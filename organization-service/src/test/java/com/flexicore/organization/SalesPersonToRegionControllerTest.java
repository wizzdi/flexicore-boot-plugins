package com.flexicore.organization;

import com.flexicore.organization.app.App;
import com.flexicore.organization.model.SalesPersonToRegion;
import com.flexicore.organization.request.SalesPersonToRegionCreate;
import com.flexicore.organization.request.SalesPersonToRegionFiltering;
import com.flexicore.organization.request.SalesPersonToRegionUpdate;
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

public class SalesPersonToRegionControllerTest {

    private SalesPersonToRegion salesPersonToRegion;
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
    public void testSalesPersonToRegionCreate() {
        String name = UUID.randomUUID().toString();
        SalesPersonToRegionCreate request = new SalesPersonToRegionCreate()
                .setName(name);
        ResponseEntity<SalesPersonToRegion> salesPersonToRegionResponse = this.restTemplate.postForEntity("/plugins/SalesPersonToRegion/createSalesPersonToRegion", request, SalesPersonToRegion.class);
        Assertions.assertEquals(200, salesPersonToRegionResponse.getStatusCodeValue());
        salesPersonToRegion = salesPersonToRegionResponse.getBody();
        assertSalesPersonToRegion(request, salesPersonToRegion);

    }



    public void assertSalesPersonToRegion(SalesPersonToRegionCreate request, SalesPersonToRegion salesPersonToRegion) {
        Assertions.assertNotNull(salesPersonToRegion);
        Assertions.assertEquals(request.getName(), salesPersonToRegion.getName());
    }


}
