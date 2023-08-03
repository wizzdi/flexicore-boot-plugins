package com.wizzdi.flexicore.pricing;

import com.wizzdi.flexicore.pricing.app.App;
import com.wizzdi.flexicore.pricing.model.price.OneTimePrice;
import com.wizzdi.flexicore.pricing.request.OneTimePriceCreate;
import com.wizzdi.flexicore.pricing.request.OneTimePriceFiltering;
import com.wizzdi.flexicore.pricing.request.OneTimePriceUpdate;
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

public class OneTimePriceControllerTest {

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
    private OneTimePrice oneTimePrice;
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
    public void testOneTimePriceCreate() {
        String name = UUID.randomUUID().toString();
        OneTimePriceCreate request = new OneTimePriceCreate()
                .setName(name);
        ResponseEntity<OneTimePrice> oneTimePriceResponse = this.restTemplate.postForEntity("/plugins/OneTimePrice/createOneTimePrice", request, OneTimePrice.class);
        Assertions.assertEquals(200, oneTimePriceResponse.getStatusCodeValue());
        oneTimePrice = oneTimePriceResponse.getBody();
        assertOneTimePrice(request, oneTimePrice);

    }

    @Test
    @Order(2)
    public void testListAllOneTimePrices() {
        OneTimePriceFiltering request=new OneTimePriceFiltering();
        ParameterizedTypeReference<PaginationResponse<OneTimePrice>> t= new ParameterizedTypeReference<>() {
        };

        ResponseEntity<PaginationResponse<OneTimePrice>> oneTimePriceResponse = this.restTemplate.exchange("/plugins/OneTimePrice/getAllOneTimePrice", HttpMethod.POST, new HttpEntity<>(request), t);
        Assertions.assertEquals(200, oneTimePriceResponse.getStatusCodeValue());
        PaginationResponse<OneTimePrice> body = oneTimePriceResponse.getBody();
        Assertions.assertNotNull(body);
        List<OneTimePrice> oneTimePrices = body.getList();
        Assertions.assertNotEquals(0,oneTimePrices.size());
        Assertions.assertTrue(oneTimePrices.stream().anyMatch(f->f.getId().equals(oneTimePrice.getId())));


    }

    public void assertOneTimePrice(OneTimePriceCreate request, OneTimePrice oneTimePrice) {
        Assertions.assertNotNull(oneTimePrice);
        Assertions.assertEquals(request.getName(), oneTimePrice.getName());
    }

    @Test
    @Order(3)
    public void testOneTimePriceUpdate(){
        String name = UUID.randomUUID().toString();
        OneTimePriceUpdate request = new OneTimePriceUpdate()
                .setId(oneTimePrice.getId())
                .setName(name);
        ResponseEntity<OneTimePrice> oneTimePriceResponse = this.restTemplate.exchange("/plugins/OneTimePrice/updateOneTimePrice",HttpMethod.PUT, new HttpEntity<>(request), OneTimePrice.class);
        Assertions.assertEquals(200, oneTimePriceResponse.getStatusCodeValue());
        oneTimePrice = oneTimePriceResponse.getBody();
        assertOneTimePrice(request, oneTimePrice);

    }

}
