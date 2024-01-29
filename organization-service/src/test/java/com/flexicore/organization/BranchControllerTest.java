package com.flexicore.organization;

import com.flexicore.organization.app.App;
import com.flexicore.organization.model.Branch;
import com.flexicore.organization.request.BranchCreate;
import com.flexicore.organization.request.BranchFiltering;
import com.flexicore.organization.request.BranchUpdate;
import com.wizzdi.flexicore.security.response.PaginationResponse;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.Collections;
import java.util.List;
import java.util.UUID;

@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT, classes = App.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@Testcontainers
@ActiveProfiles("test")
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE) // deactivate the default behaviour

public class BranchControllerTest {

    private Branch branch;
    @Autowired
    private TestRestTemplate restTemplate;


  private final static PostgreSQLContainer postgresqlContainer = new PostgreSQLContainer("postgres:15")

            .withDatabaseName("flexicore-test")
            .withUsername("flexicore")
            .withPassword("flexicore");

    static {
        postgresqlContainer.start();
    }

    @DynamicPropertySource
    static void setProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgresqlContainer::getJdbcUrl);
        registry.add("spring.datasource.username", postgresqlContainer::getUsername);
        registry.add("spring.datasource.password", postgresqlContainer::getPassword);
    }

    @BeforeAll
    public void init() {
        restTemplate.getRestTemplate().setInterceptors(
                Collections.singletonList((request, body, execution) -> {
                    request.getHeaders()
                            .add("authenticationKey", "fake");
                    return execution.execute(request, body);
                }));

    }

    @Test
    @Order(1)
    public void testBranchCreate() {
        String name = UUID.randomUUID().toString();
        BranchCreate request = new BranchCreate()
                .setName(name);
        ResponseEntity<Branch> branchResponse = this.restTemplate.postForEntity("/plugins/Branch/createBranch", request, Branch.class);
        Assertions.assertEquals(200, branchResponse.getStatusCodeValue());
        branch = branchResponse.getBody();
        assertBranch(request, branch);

    }

    @Test
    @Order(2)
    public void testListAllBranchs() {
        BranchFiltering request=new BranchFiltering();
        ParameterizedTypeReference<PaginationResponse<Branch>> t= new ParameterizedTypeReference<>() {
        };

        ResponseEntity<PaginationResponse<Branch>> branchResponse = this.restTemplate.exchange("/plugins/Branch/getAllBranches", HttpMethod.POST, new HttpEntity<>(request), t);
        Assertions.assertEquals(200, branchResponse.getStatusCodeValue());
        PaginationResponse<Branch> body = branchResponse.getBody();
        Assertions.assertNotNull(body);
        List<Branch> branchs = body.getList();
        Assertions.assertNotEquals(0,branchs.size());
        Assertions.assertTrue(branchs.stream().anyMatch(f->f.getId().equals(branch.getId())));


    }

    public void assertBranch(BranchCreate request, Branch branch) {
        Assertions.assertNotNull(branch);
        Assertions.assertEquals(request.getName(), branch.getName());
    }

    @Test
    @Order(3)
    public void testBranchUpdate(){
        String name = UUID.randomUUID().toString();
        BranchUpdate request = new BranchUpdate()
                .setId(branch.getId())
                .setName(name);
        ResponseEntity<Branch> branchResponse = this.restTemplate.exchange("/plugins/Branch/updateBranch",HttpMethod.PUT, new HttpEntity<>(request), Branch.class);
        Assertions.assertEquals(200, branchResponse.getStatusCodeValue());
        branch = branchResponse.getBody();
        assertBranch(request, branch);

    }

}
