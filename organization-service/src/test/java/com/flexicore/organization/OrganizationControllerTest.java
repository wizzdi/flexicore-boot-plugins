package com.flexicore.organization;

import com.flexicore.organization.app.App;
import com.flexicore.organization.model.Organization;
import com.flexicore.organization.request.OrganizationCreate;
import com.flexicore.organization.request.OrganizationFiltering;
import com.flexicore.organization.service.OrganizationService;
import com.flexicore.security.SecurityContextBase;
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

public class OrganizationControllerTest {

    private Organization organization;
    @Autowired
    private TestRestTemplate restTemplate;
    @Autowired
    private OrganizationService organizationService;
    @Autowired
    private SecurityContextBase adminSecurityContext;

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
    private void init() {
        restTemplate.getRestTemplate().setInterceptors(
                Collections.singletonList((request, body, execution) -> {
                    request.getHeaders()
                            .add("authenticationKey", "fake");
                    return execution.execute(request, body);
                }));

    }

    @Test
    @Order(1)
    public void testOrganizationCreate() {
        String name = UUID.randomUUID().toString();
        OrganizationCreate request = new OrganizationCreate()
                .setName(name);
        organization=organizationService.createOrganization(request,adminSecurityContext);
        assertOrganization(request, organization);

    }

    @Test
    @Order(2)
    public void testListAllOrganizations() {
        OrganizationFiltering request=new OrganizationFiltering();
        ParameterizedTypeReference<PaginationResponse<Organization>> t= new ParameterizedTypeReference<>() {
        };

        ResponseEntity<PaginationResponse<Organization>> organizationResponse = this.restTemplate.exchange("/plugins/Organization/getAllOrganizations", HttpMethod.POST, new HttpEntity<>(request), t);
        Assertions.assertEquals(200, organizationResponse.getStatusCodeValue());
        PaginationResponse<Organization> body = organizationResponse.getBody();
        Assertions.assertNotNull(body);
        List<Organization> organizations = body.getList();
        Assertions.assertNotEquals(0,organizations.size());
        Assertions.assertTrue(organizations.stream().anyMatch(f->f.getId().equals(organization.getId())));


    }

    public void assertOrganization(OrganizationCreate request, Organization organization) {
        Assertions.assertNotNull(organization);
        Assertions.assertEquals(request.getName(), organization.getName());
    }

}
