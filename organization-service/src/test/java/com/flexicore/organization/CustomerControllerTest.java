package com.flexicore.organization;

import com.flexicore.organization.app.App;
import com.flexicore.organization.model.Customer;
import com.flexicore.organization.request.CustomerCreate;
import com.flexicore.organization.request.CustomerFiltering;
import com.flexicore.organization.request.CustomerUpdate;
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

public class CustomerControllerTest {

    private Customer customer;
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
    public void testCustomerCreate() {
        String name = UUID.randomUUID().toString();
        CustomerCreate request = new CustomerCreate()
                .setName(name);
        ResponseEntity<Customer> customerResponse = this.restTemplate.postForEntity("/plugins/Customer/createCustomer", request, Customer.class);
        Assertions.assertEquals(200, customerResponse.getStatusCodeValue());
        customer = customerResponse.getBody();
        assertCustomer(request, customer);

    }

    @Test
    @Order(2)
    public void testListAllCustomers() {
        CustomerFiltering request=new CustomerFiltering();
        ParameterizedTypeReference<PaginationResponse<Customer>> t= new ParameterizedTypeReference<>() {
        };

        ResponseEntity<PaginationResponse<Customer>> customerResponse = this.restTemplate.exchange("/plugins/Customer/getAllCustomers", HttpMethod.POST, new HttpEntity<>(request), t);
        Assertions.assertEquals(200, customerResponse.getStatusCodeValue());
        PaginationResponse<Customer> body = customerResponse.getBody();
        Assertions.assertNotNull(body);
        List<Customer> customers = body.getList();
        Assertions.assertNotEquals(0,customers.size());
        Assertions.assertTrue(customers.stream().anyMatch(f->f.getId().equals(customer.getId())));


    }

    public void assertCustomer(CustomerCreate request, Customer customer) {
        Assertions.assertNotNull(customer);
        Assertions.assertEquals(request.getName(), customer.getName());
    }

    @Test
    @Order(3)
    public void testCustomerUpdate(){
        String name = UUID.randomUUID().toString();
        CustomerUpdate request = new CustomerUpdate()
                .setId(customer.getId())
                .setName(name);
        ResponseEntity<Customer> customerResponse = this.restTemplate.exchange("/plugins/Customer/updateCustomer",HttpMethod.PUT, new HttpEntity<>(request), Customer.class);
        Assertions.assertEquals(200, customerResponse.getStatusCodeValue());
        customer = customerResponse.getBody();
        assertCustomer(request, customer);

    }

}
