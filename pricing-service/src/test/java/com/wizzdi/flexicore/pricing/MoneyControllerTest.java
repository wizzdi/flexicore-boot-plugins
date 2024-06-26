package com.wizzdi.flexicore.pricing;

import com.wizzdi.flexicore.pricing.app.App;
import com.wizzdi.flexicore.pricing.model.price.Money;
import com.wizzdi.flexicore.pricing.request.MoneyCreate;
import com.wizzdi.flexicore.pricing.request.MoneyFiltering;
import com.wizzdi.flexicore.pricing.request.MoneyUpdate;
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

public class MoneyControllerTest {

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
    private Money money;
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
    public void testMoneyCreate() {
        String name = UUID.randomUUID().toString();
        MoneyCreate request = new MoneyCreate()
                .setName(name);
        ResponseEntity<Money> moneyResponse = this.restTemplate.postForEntity("/plugins/Money/createMoney", request, Money.class);
        Assertions.assertEquals(200, moneyResponse.getStatusCodeValue());
        money = moneyResponse.getBody();
        assertMoney(request, money);

    }

    @Test
    @Order(2)
    public void testListAllMoney() {
        MoneyFiltering request=new MoneyFiltering();
        ParameterizedTypeReference<PaginationResponse<Money>> t= new ParameterizedTypeReference<>() {
        };

        ResponseEntity<PaginationResponse<Money>> moneyResponse = this.restTemplate.exchange("/plugins/Money/getAllMoney", HttpMethod.POST, new HttpEntity<>(request), t);
        Assertions.assertEquals(200, moneyResponse.getStatusCodeValue());
        PaginationResponse<Money> body = moneyResponse.getBody();
        Assertions.assertNotNull(body);
        List<Money> moneys = body.getList();
        Assertions.assertNotEquals(0,moneys.size());
        Assertions.assertTrue(moneys.stream().anyMatch(f->f.getId().equals(money.getId())));


    }

    public void assertMoney(MoneyCreate request, Money money) {
        Assertions.assertNotNull(money);
        Assertions.assertEquals(request.getName(), money.getName());
    }

    @Test
    @Order(3)
    public void testMoneyUpdate(){
        String name = UUID.randomUUID().toString();
        MoneyUpdate request = new MoneyUpdate()
                .setId(money.getId())
                .setName(name);
        ResponseEntity<Money> moneyResponse = this.restTemplate.exchange("/plugins/Money/updateMoney",HttpMethod.PUT, new HttpEntity<>(request), Money.class);
        Assertions.assertEquals(200, moneyResponse.getStatusCodeValue());
        money = moneyResponse.getBody();
        assertMoney(request, money);

    }

}
