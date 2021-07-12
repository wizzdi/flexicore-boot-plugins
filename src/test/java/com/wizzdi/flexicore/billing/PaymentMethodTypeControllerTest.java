package com.wizzdi.flexicore.billing;

import com.wizzdi.flexicore.billing.app.App;
import com.wizzdi.flexicore.billing.model.payment.PaymentMethodType;
import com.wizzdi.flexicore.billing.request.PaymentMethodTypeCreate;
import com.wizzdi.flexicore.billing.request.PaymentMethodTypeFiltering;
import com.wizzdi.flexicore.billing.request.PaymentMethodTypeUpdate;
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

public class PaymentMethodTypeControllerTest {

    private PaymentMethodType paymentMethodType;
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
    public void testPaymentMethodTypeCreate() {
        String name = UUID.randomUUID().toString();
        PaymentMethodTypeCreate request = new PaymentMethodTypeCreate()
                .setName(name);
        ResponseEntity<PaymentMethodType> paymentMethodTypeResponse = this.restTemplate.postForEntity("/plugins/PaymentMethodType/createPaymentMethodType", request, PaymentMethodType.class);
        Assertions.assertEquals(200, paymentMethodTypeResponse.getStatusCodeValue());
        paymentMethodType = paymentMethodTypeResponse.getBody();
        assertPaymentMethodType(request, paymentMethodType);

    }

    @Test
    @Order(2)
    public void testListAllPaymentMethodTypes() {
        PaymentMethodTypeFiltering request=new PaymentMethodTypeFiltering();
        ParameterizedTypeReference<PaginationResponse<PaymentMethodType>> t= new ParameterizedTypeReference<>() {
        };

        ResponseEntity<PaginationResponse<PaymentMethodType>> paymentMethodTypeResponse = this.restTemplate.exchange("/plugins/PaymentMethodType/getAllPaymentMethodTypes", HttpMethod.POST, new HttpEntity<>(request), t);
        Assertions.assertEquals(200, paymentMethodTypeResponse.getStatusCodeValue());
        PaginationResponse<PaymentMethodType> body = paymentMethodTypeResponse.getBody();
        Assertions.assertNotNull(body);
        List<PaymentMethodType> paymentMethodTypes = body.getList();
        Assertions.assertNotEquals(0,paymentMethodTypes.size());
        Assertions.assertTrue(paymentMethodTypes.stream().anyMatch(f->f.getId().equals(paymentMethodType.getId())));


    }

    public void assertPaymentMethodType(PaymentMethodTypeCreate request, PaymentMethodType paymentMethodType) {
        Assertions.assertNotNull(paymentMethodType);
        Assertions.assertEquals(request.getName(), paymentMethodType.getName());
    }

    @Test
    @Order(3)
    public void testPaymentMethodTypeUpdate(){
        String name = UUID.randomUUID().toString();
        PaymentMethodTypeUpdate request = new PaymentMethodTypeUpdate()
                .setId(paymentMethodType.getId())
                .setName(name);
        ResponseEntity<PaymentMethodType> paymentMethodTypeResponse = this.restTemplate.exchange("/plugins/PaymentMethodType/updatePaymentMethodType",HttpMethod.PUT, new HttpEntity<>(request), PaymentMethodType.class);
        Assertions.assertEquals(200, paymentMethodTypeResponse.getStatusCodeValue());
        paymentMethodType = paymentMethodTypeResponse.getBody();
        assertPaymentMethodType(request, paymentMethodType);

    }

}
