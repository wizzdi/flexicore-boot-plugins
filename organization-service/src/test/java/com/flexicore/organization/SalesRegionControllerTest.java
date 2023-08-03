package com.flexicore.organization;

import com.flexicore.organization.app.App;
import com.flexicore.organization.model.SalesRegion;
import com.flexicore.organization.request.SalesRegionCreate;
import com.flexicore.organization.request.SalesRegionFiltering;
import com.flexicore.organization.request.SalesRegionUpdate;
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

public class SalesRegionControllerTest {

    private SalesRegion salesRegion;
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
    public void testSalesRegionCreate() {
        String name = UUID.randomUUID().toString();
        SalesRegionCreate request = new SalesRegionCreate()
                .setName(name);
        ResponseEntity<SalesRegion> salesRegionResponse = this.restTemplate.postForEntity("/plugins/SalesRegion/createSalesRegion", request, SalesRegion.class);
        Assertions.assertEquals(200, salesRegionResponse.getStatusCodeValue());
        salesRegion = salesRegionResponse.getBody();
        assertSalesRegion(request, salesRegion);

    }

    @Test
    @Order(2)
    public void testListAllSalesRegions() {
        SalesRegionFiltering request=new SalesRegionFiltering();
        ParameterizedTypeReference<PaginationResponse<SalesRegion>> t= new ParameterizedTypeReference<>() {
        };

        ResponseEntity<PaginationResponse<SalesRegion>> salesRegionResponse = this.restTemplate.exchange("/plugins/SalesRegion/getAllSalesRegions", HttpMethod.POST, new HttpEntity<>(request), t);
        Assertions.assertEquals(200, salesRegionResponse.getStatusCodeValue());
        PaginationResponse<SalesRegion> body = salesRegionResponse.getBody();
        Assertions.assertNotNull(body);
        List<SalesRegion> salesRegions = body.getList();
        Assertions.assertNotEquals(0,salesRegions.size());
        Assertions.assertTrue(salesRegions.stream().anyMatch(f->f.getId().equals(salesRegion.getId())));


    }

    public void assertSalesRegion(SalesRegionCreate request, SalesRegion salesRegion) {
        Assertions.assertNotNull(salesRegion);
        Assertions.assertEquals(request.getName(), salesRegion.getName());
    }

    @Test
    @Order(3)
    public void testSalesRegionUpdate(){
        String name = UUID.randomUUID().toString();
        SalesRegionUpdate request = new SalesRegionUpdate()
                .setId(salesRegion.getId())
                .setName(name);
        ResponseEntity<SalesRegion> salesRegionResponse = this.restTemplate.exchange("/plugins/SalesRegion/updateSalesRegion",HttpMethod.PUT, new HttpEntity<>(request), SalesRegion.class);
        Assertions.assertEquals(200, salesRegionResponse.getStatusCodeValue());
        salesRegion = salesRegionResponse.getBody();
        assertSalesRegion(request, salesRegion);

    }

}
