package com.wizzdi.flexicore.billing;

import com.wizzdi.flexicore.billing.app.App;
import com.wizzdi.flexicore.billing.model.payment.PaymentMethod;
import com.wizzdi.flexicore.billing.request.PaymentMethodCreate;
import com.wizzdi.flexicore.billing.request.PaymentMethodFiltering;
import com.wizzdi.flexicore.billing.request.PaymentMethodUpdate;
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

public class PaymentMethodControllerTest {

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
    private PaymentMethod paymentMethod;
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
    public void testPaymentMethodCreate() {
        String name = UUID.randomUUID().toString();
        PaymentMethodCreate request = new PaymentMethodCreate()
                .setName(name);
        ResponseEntity<PaymentMethod> paymentMethodResponse = this.restTemplate.postForEntity("/plugins/PaymentMethod/createPaymentMethod", request, PaymentMethod.class);
        Assertions.assertEquals(200, paymentMethodResponse.getStatusCodeValue());
        paymentMethod = paymentMethodResponse.getBody();
        assertPaymentMethod(request, paymentMethod);

    }

    @Test
    @Order(2)
    public void testListAllPaymentMethods() {
        PaymentMethodFiltering request=new PaymentMethodFiltering();
        ParameterizedTypeReference<PaginationResponse<PaymentMethod>> t= new ParameterizedTypeReference<>() {
        };

        ResponseEntity<PaginationResponse<PaymentMethod>> paymentMethodResponse = this.restTemplate.exchange("/plugins/PaymentMethod/getAllPaymentMethods", HttpMethod.POST, new HttpEntity<>(request), t);
        Assertions.assertEquals(200, paymentMethodResponse.getStatusCodeValue());
        PaginationResponse<PaymentMethod> body = paymentMethodResponse.getBody();
        Assertions.assertNotNull(body);
        List<PaymentMethod> paymentMethods = body.getList();
        Assertions.assertNotEquals(0,paymentMethods.size());
        Assertions.assertTrue(paymentMethods.stream().anyMatch(f->f.getId().equals(paymentMethod.getId())));


    }

    public void assertPaymentMethod(PaymentMethodCreate request, PaymentMethod paymentMethod) {
        Assertions.assertNotNull(paymentMethod);
        Assertions.assertEquals(request.getName(), paymentMethod.getName());
    }

    @Test
    @Order(3)
    public void testPaymentMethodUpdate(){
        String name = UUID.randomUUID().toString();
        PaymentMethodUpdate request = new PaymentMethodUpdate()
                .setId(paymentMethod.getId())
                .setName(name);
        ResponseEntity<PaymentMethod> paymentMethodResponse = this.restTemplate.exchange("/plugins/PaymentMethod/updatePaymentMethod",HttpMethod.PUT, new HttpEntity<>(request), PaymentMethod.class);
        Assertions.assertEquals(200, paymentMethodResponse.getStatusCodeValue());
        paymentMethod = paymentMethodResponse.getBody();
        assertPaymentMethod(request, paymentMethod);

    }

}
