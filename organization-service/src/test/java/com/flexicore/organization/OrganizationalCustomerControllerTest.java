package com.flexicore.organization;

import com.flexicore.organization.app.App;
import com.flexicore.organization.model.OrganizationalCustomer;
import com.flexicore.organization.request.OrganizationalCustomerCreate;
import com.flexicore.organization.request.OrganizationalCustomerFiltering;
import com.flexicore.organization.request.OrganizationalCustomerUpdate;
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

public class OrganizationalCustomerControllerTest {

    private OrganizationalCustomer organizationalCustomer;
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
    public void testOrganizationalCustomerCreate() {
        String name = UUID.randomUUID().toString();
        OrganizationalCustomerCreate request = new OrganizationalCustomerCreate()
                .setName(name);
        ResponseEntity<OrganizationalCustomer> organizationalCustomerResponse = this.restTemplate.postForEntity("/plugins/OrganizationalCustomer/createOrganizationalCustomer", request, OrganizationalCustomer.class);
        Assertions.assertEquals(200, organizationalCustomerResponse.getStatusCodeValue());
        organizationalCustomer = organizationalCustomerResponse.getBody();
        assertOrganizationalCustomer(request, organizationalCustomer);

    }

    @Test
    @Order(2)
    public void testListAllOrganizationalCustomers() {
        OrganizationalCustomerFiltering request=new OrganizationalCustomerFiltering();
        ParameterizedTypeReference<PaginationResponse<OrganizationalCustomer>> t= new ParameterizedTypeReference<>() {
        };

        ResponseEntity<PaginationResponse<OrganizationalCustomer>> organizationalCustomerResponse = this.restTemplate.exchange("/plugins/OrganizationalCustomer/getAllOrganizationalCustomers", HttpMethod.POST, new HttpEntity<>(request), t);
        Assertions.assertEquals(200, organizationalCustomerResponse.getStatusCodeValue());
        PaginationResponse<OrganizationalCustomer> body = organizationalCustomerResponse.getBody();
        Assertions.assertNotNull(body);
        List<OrganizationalCustomer> organizationalCustomers = body.getList();
        Assertions.assertNotEquals(0,organizationalCustomers.size());
        Assertions.assertTrue(organizationalCustomers.stream().anyMatch(f->f.getId().equals(organizationalCustomer.getId())));


    }

    public void assertOrganizationalCustomer(OrganizationalCustomerCreate request, OrganizationalCustomer organizationalCustomer) {
        Assertions.assertNotNull(organizationalCustomer);
        Assertions.assertEquals(request.getName(), organizationalCustomer.getName());
    }

    @Test
    @Order(3)
    public void testOrganizationalCustomerUpdate(){
        String name = UUID.randomUUID().toString();
        OrganizationalCustomerUpdate request = new OrganizationalCustomerUpdate()
                .setId(organizationalCustomer.getId())
                .setName(name);
        ResponseEntity<OrganizationalCustomer> organizationalCustomerResponse = this.restTemplate.exchange("/plugins/OrganizationalCustomer/updateOrganizationalCustomer",HttpMethod.PUT, new HttpEntity<>(request), OrganizationalCustomer.class);
        Assertions.assertEquals(200, organizationalCustomerResponse.getStatusCodeValue());
        organizationalCustomer = organizationalCustomerResponse.getBody();
        assertOrganizationalCustomer(request, organizationalCustomer);

    }

}
