package com.flexicore.organization;

import com.flexicore.organization.app.App;
import com.flexicore.organization.model.SalesPerson;
import com.flexicore.organization.request.SalesPersonCreate;
import com.flexicore.organization.request.SalesPersonFiltering;
import com.flexicore.organization.request.SalesPersonUpdate;
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

public class SalesPersonControllerTest {

    private SalesPerson salesPerson;
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
    public void testSalesPersonCreate() {
        String name = UUID.randomUUID().toString();
        SalesPersonCreate request = new SalesPersonCreate()
                .setName(name);
        ResponseEntity<SalesPerson> salesPersonResponse = this.restTemplate.postForEntity("/plugins/SalesPerson/createSalesPerson", request, SalesPerson.class);
        Assertions.assertEquals(200, salesPersonResponse.getStatusCodeValue());
        salesPerson = salesPersonResponse.getBody();
        assertSalesPerson(request, salesPerson);

    }

    @Test
    @Order(2)
    public void testListAllSalesPersons() {
        SalesPersonFiltering request=new SalesPersonFiltering();
        ParameterizedTypeReference<PaginationResponse<SalesPerson>> t= new ParameterizedTypeReference<>() {
        };

        ResponseEntity<PaginationResponse<SalesPerson>> salesPersonResponse = this.restTemplate.exchange("/plugins/SalesPerson/getAllSalesPeople", HttpMethod.POST, new HttpEntity<>(request), t);
        Assertions.assertEquals(200, salesPersonResponse.getStatusCodeValue());
        PaginationResponse<SalesPerson> body = salesPersonResponse.getBody();
        Assertions.assertNotNull(body);
        List<SalesPerson> salesPersons = body.getList();
        Assertions.assertNotEquals(0,salesPersons.size());
        Assertions.assertTrue(salesPersons.stream().anyMatch(f->f.getId().equals(salesPerson.getId())));


    }

    public void assertSalesPerson(SalesPersonCreate request, SalesPerson salesPerson) {
        Assertions.assertNotNull(salesPerson);
        Assertions.assertEquals(request.getName(), salesPerson.getName());
    }

    @Test
    @Order(3)
    public void testSalesPersonUpdate(){
        String name = UUID.randomUUID().toString();
        SalesPersonUpdate request = new SalesPersonUpdate()
                .setId(salesPerson.getId())
                .setName(name);
        ResponseEntity<SalesPerson> salesPersonResponse = this.restTemplate.exchange("/plugins/SalesPerson/updateSalesPerson",HttpMethod.PUT, new HttpEntity<>(request), SalesPerson.class);
        Assertions.assertEquals(200, salesPersonResponse.getStatusCodeValue());
        salesPerson = salesPersonResponse.getBody();
        assertSalesPerson(request, salesPerson);

    }

}
