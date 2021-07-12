package com.wizzdi.flexicore.billing;

import com.wizzdi.flexicore.billing.app.App;
import com.wizzdi.flexicore.billing.request.PaymentCreate;
import com.wizzdi.flexicore.billing.request.PaymentFiltering;
import com.wizzdi.flexicore.billing.request.PaymentUpdate;
import com.wizzdi.flexicore.billing.model.payment.Payment;
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
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.Collections;
import java.util.List;
import java.util.UUID;

@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT, classes = App.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@ActiveProfiles("test")

public class PaymentControllerTest {

    private Payment payment;
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
    public void testPaymentCreate() {
        String name = UUID.randomUUID().toString();
        PaymentCreate request = new PaymentCreate()
                .setName(name);
        ResponseEntity<Payment> paymentResponse = this.restTemplate.postForEntity("/plugins/Payment/createPayment", request, Payment.class);
        Assertions.assertEquals(200, paymentResponse.getStatusCodeValue());
        payment = paymentResponse.getBody();
        assertPayment(request, payment);

    }

    @Test
    @Order(2)
    public void testListAllPayments() {
        PaymentFiltering request=new PaymentFiltering();
        ParameterizedTypeReference<PaginationResponse<Payment>> t= new ParameterizedTypeReference<>() {
        };

        ResponseEntity<PaginationResponse<Payment>> paymentResponse = this.restTemplate.exchange("/plugins/Payment/getAllPayments", HttpMethod.POST, new HttpEntity<>(request), t);
        Assertions.assertEquals(200, paymentResponse.getStatusCodeValue());
        PaginationResponse<Payment> body = paymentResponse.getBody();
        Assertions.assertNotNull(body);
        List<Payment> payments = body.getList();
        Assertions.assertNotEquals(0,payments.size());
        Assertions.assertTrue(payments.stream().anyMatch(f->f.getId().equals(payment.getId())));


    }

    public void assertPayment(PaymentCreate request, Payment payment) {
        Assertions.assertNotNull(payment);
        Assertions.assertEquals(request.getName(), payment.getName());
    }

    @Test
    @Order(3)
    public void testPaymentUpdate(){
        String name = UUID.randomUUID().toString();
        PaymentUpdate request = new PaymentUpdate()
                .setId(payment.getId())
                .setName(name);
        ResponseEntity<Payment> paymentResponse = this.restTemplate.exchange("/plugins/Payment/updatePayment",HttpMethod.PUT, new HttpEntity<>(request), Payment.class);
        Assertions.assertEquals(200, paymentResponse.getStatusCodeValue());
        payment = paymentResponse.getBody();
        assertPayment(request, payment);

    }

}
