package com.wizzdi.flexicore.pricing;

import com.wizzdi.flexicore.pricing.app.App;
import com.wizzdi.flexicore.pricing.model.price.Price;
import com.wizzdi.flexicore.pricing.request.PriceCreate;
import com.wizzdi.flexicore.pricing.request.PriceFiltering;
import com.wizzdi.flexicore.pricing.request.PriceUpdate;
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

public class PriceControllerTest {

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
    private Price price;
    @Autowired
    private TestRestTemplate restTemplate;

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
    public void testPriceCreate() {
        String name = UUID.randomUUID().toString();
        PriceCreate request = new PriceCreate()
                .setName(name);
        ResponseEntity<Price> priceResponse = this.restTemplate.postForEntity("/plugins/Price/createPrice", request, Price.class);
        Assertions.assertEquals(200, priceResponse.getStatusCodeValue());
        price = priceResponse.getBody();
        assertPrice(request, price);

    }

    @Test
    @Order(2)
    public void testListAllPrices() {
        PriceFiltering request=new PriceFiltering();
        ParameterizedTypeReference<PaginationResponse<Price>> t= new ParameterizedTypeReference<>() {
        };

        ResponseEntity<PaginationResponse<Price>> priceResponse = this.restTemplate.exchange("/plugins/Price/getAllPrice", HttpMethod.POST, new HttpEntity<>(request), t);
        Assertions.assertEquals(200, priceResponse.getStatusCodeValue());
        PaginationResponse<Price> body = priceResponse.getBody();
        Assertions.assertNotNull(body);
        List<Price> prices = body.getList();
        Assertions.assertNotEquals(0,prices.size());
        Assertions.assertTrue(prices.stream().anyMatch(f->f.getId().equals(price.getId())));


    }

    public void assertPrice(PriceCreate request, Price price) {
        Assertions.assertNotNull(price);
        Assertions.assertEquals(request.getName(), price.getName());
    }

    @Test
    @Order(3)
    public void testPriceUpdate(){
        String name = UUID.randomUUID().toString();
        PriceUpdate request = new PriceUpdate()
                .setId(price.getId())
                .setName(name);
        ResponseEntity<Price> priceResponse = this.restTemplate.exchange("/plugins/Price/updatePrice",HttpMethod.PUT, new HttpEntity<>(request), Price.class);
        Assertions.assertEquals(200, priceResponse.getStatusCodeValue());
        price = priceResponse.getBody();
        assertPrice(request, price);

    }

}
