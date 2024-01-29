package com.wizzdi.flexicore.pricing;

import com.wizzdi.flexicore.pricing.app.App;
import com.wizzdi.flexicore.pricing.model.price.PriceListItem;
import com.wizzdi.flexicore.pricing.request.PriceListItemCreate;
import com.wizzdi.flexicore.pricing.request.PriceListItemFiltering;
import com.wizzdi.flexicore.pricing.request.PriceListItemUpdate;
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

public class PriceListItemControllerTest {

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
    private PriceListItem priceListItem;
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
    public void testPriceListItemCreate() {
        String name = UUID.randomUUID().toString();
        PriceListItemCreate request = new PriceListItemCreate()
                .setName(name);
        ResponseEntity<PriceListItem> priceListItemResponse = this.restTemplate.postForEntity("/plugins/PriceListItem/createPriceListItem", request, PriceListItem.class);
        Assertions.assertEquals(200, priceListItemResponse.getStatusCodeValue());
        priceListItem = priceListItemResponse.getBody();
        assertPriceListItem(request, priceListItem);

    }

    @Test
    @Order(2)
    public void testListAllPriceListItems() {
        PriceListItemFiltering request=new PriceListItemFiltering();
        ParameterizedTypeReference<PaginationResponse<PriceListItem>> t= new ParameterizedTypeReference<>() {
        };

        ResponseEntity<PaginationResponse<PriceListItem>> priceListItemResponse = this.restTemplate.exchange("/plugins/PriceListItem/getAllPriceListItems", HttpMethod.POST, new HttpEntity<>(request), t);
        Assertions.assertEquals(200, priceListItemResponse.getStatusCodeValue());
        PaginationResponse<PriceListItem> body = priceListItemResponse.getBody();
        Assertions.assertNotNull(body);
        List<PriceListItem> priceListItems = body.getList();
        Assertions.assertNotEquals(0,priceListItems.size());
        Assertions.assertTrue(priceListItems.stream().anyMatch(f->f.getId().equals(priceListItem.getId())));


    }

    public void assertPriceListItem(PriceListItemCreate request, PriceListItem priceListItem) {
        Assertions.assertNotNull(priceListItem);
        Assertions.assertEquals(request.getName(), priceListItem.getName());
    }

    @Test
    @Order(3)
    public void testPriceListItemUpdate(){
        String name = UUID.randomUUID().toString();
        PriceListItemUpdate request = new PriceListItemUpdate()
                .setId(priceListItem.getId())
                .setName(name);
        ResponseEntity<PriceListItem> priceListItemResponse = this.restTemplate.exchange("/plugins/PriceListItem/updatePriceListItem",HttpMethod.PUT, new HttpEntity<>(request), PriceListItem.class);
        Assertions.assertEquals(200, priceListItemResponse.getStatusCodeValue());
        priceListItem = priceListItemResponse.getBody();
        assertPriceListItem(request, priceListItem);

    }

}
