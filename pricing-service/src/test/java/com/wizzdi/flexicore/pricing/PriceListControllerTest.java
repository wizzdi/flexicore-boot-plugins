package com.wizzdi.flexicore.pricing;

import com.wizzdi.flexicore.pricing.app.App;
import com.wizzdi.flexicore.pricing.model.price.PriceList;
import com.wizzdi.flexicore.pricing.request.PriceListCreate;
import com.wizzdi.flexicore.pricing.request.PriceListFiltering;
import com.wizzdi.flexicore.pricing.request.PriceListUpdate;
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

public class PriceListControllerTest {

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
    private PriceList priceList;
    @Autowired
    private TestRestTemplate restTemplate;

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
    public void testPriceListCreate() {
        String name = UUID.randomUUID().toString();
        PriceListCreate request = new PriceListCreate()
                .setName(name);
        ResponseEntity<PriceList> priceListResponse = this.restTemplate.postForEntity("/plugins/PriceList/createPriceList", request, PriceList.class);
        Assertions.assertEquals(200, priceListResponse.getStatusCodeValue());
        priceList = priceListResponse.getBody();
        assertPriceList(request, priceList);

    }

    @Test
    @Order(2)
    public void testListAllPriceLists() {
        PriceListFiltering request=new PriceListFiltering();
        ParameterizedTypeReference<PaginationResponse<PriceList>> t= new ParameterizedTypeReference<>() {
        };

        ResponseEntity<PaginationResponse<PriceList>> priceListResponse = this.restTemplate.exchange("/plugins/PriceList/getAllPriceLists", HttpMethod.POST, new HttpEntity<>(request), t);
        Assertions.assertEquals(200, priceListResponse.getStatusCodeValue());
        PaginationResponse<PriceList> body = priceListResponse.getBody();
        Assertions.assertNotNull(body);
        List<PriceList> priceLists = body.getList();
        Assertions.assertNotEquals(0,priceLists.size());
        Assertions.assertTrue(priceLists.stream().anyMatch(f->f.getId().equals(priceList.getId())));


    }

    public void assertPriceList(PriceListCreate request, PriceList priceList) {
        Assertions.assertNotNull(priceList);
        Assertions.assertEquals(request.getName(), priceList.getName());
    }

    @Test
    @Order(3)
    public void testPriceListUpdate(){
        String name = UUID.randomUUID().toString();
        PriceListUpdate request = new PriceListUpdate()
                .setId(priceList.getId())
                .setName(name);
        ResponseEntity<PriceList> priceListResponse = this.restTemplate.exchange("/plugins/PriceList/updatePriceList",HttpMethod.PUT, new HttpEntity<>(request), PriceList.class);
        Assertions.assertEquals(200, priceListResponse.getStatusCodeValue());
        priceList = priceListResponse.getBody();
        assertPriceList(request, priceList);

    }

}
