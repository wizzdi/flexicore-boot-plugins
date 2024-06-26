package com.wizzdi.flexicore.pricing;

import com.wizzdi.flexicore.pricing.app.App;
import com.wizzdi.flexicore.pricing.model.product.PricedProduct;
import com.wizzdi.flexicore.pricing.request.PricedProductCreate;
import com.wizzdi.flexicore.pricing.request.PricedProductFiltering;
import com.wizzdi.flexicore.pricing.request.PricedProductUpdate;
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

public class PricedProductControllerTest {

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
    private PricedProduct pricedProduct;
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
    public void testPricedProductCreate() {
        String name = UUID.randomUUID().toString();
        PricedProductCreate request = new PricedProductCreate()
                .setName(name);
        ResponseEntity<PricedProduct> pricedProductResponse = this.restTemplate.postForEntity("/plugins/PricedProduct/createPricedProduct", request, PricedProduct.class);
        Assertions.assertEquals(200, pricedProductResponse.getStatusCodeValue());
        pricedProduct = pricedProductResponse.getBody();
        assertPricedProduct(request, pricedProduct);

    }

    @Test
    @Order(2)
    public void testListAllPricedProducts() {
        PricedProductFiltering request=new PricedProductFiltering();
        ParameterizedTypeReference<PaginationResponse<PricedProduct>> t= new ParameterizedTypeReference<>() {
        };

        ResponseEntity<PaginationResponse<PricedProduct>> pricedProductResponse = this.restTemplate.exchange("/plugins/PricedProduct/getAllPricedProducts", HttpMethod.POST, new HttpEntity<>(request), t);
        Assertions.assertEquals(200, pricedProductResponse.getStatusCodeValue());
        PaginationResponse<PricedProduct> body = pricedProductResponse.getBody();
        Assertions.assertNotNull(body);
        List<PricedProduct> pricedProducts = body.getList();
        Assertions.assertNotEquals(0,pricedProducts.size());
        Assertions.assertTrue(pricedProducts.stream().anyMatch(f->f.getId().equals(pricedProduct.getId())));


    }

    public void assertPricedProduct(PricedProductCreate request, PricedProduct pricedProduct) {
        Assertions.assertNotNull(pricedProduct);
        Assertions.assertEquals(request.getName(), pricedProduct.getName());
    }

    @Test
    @Order(3)
    public void testPricedProductUpdate(){
        String name = UUID.randomUUID().toString();
        PricedProductUpdate request = new PricedProductUpdate()
                .setId(pricedProduct.getId())
                .setName(name);
        ResponseEntity<PricedProduct> pricedProductResponse = this.restTemplate.exchange("/plugins/PricedProduct/updatePricedProduct",HttpMethod.PUT, new HttpEntity<>(request), PricedProduct.class);
        Assertions.assertEquals(200, pricedProductResponse.getStatusCodeValue());
        pricedProduct = pricedProductResponse.getBody();
        assertPricedProduct(request, pricedProduct);

    }

}
