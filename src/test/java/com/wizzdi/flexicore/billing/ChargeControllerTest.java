package com.wizzdi.flexicore.billing;

import com.wizzdi.flexicore.billing.app.App;
import com.wizzdi.flexicore.billing.request.ChargeCreate;
import com.wizzdi.flexicore.billing.request.ChargeFiltering;
import com.wizzdi.flexicore.billing.request.ChargeUpdate;
import com.wizzdi.flexicore.billing.model.billing.Charge;
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

public class ChargeControllerTest {

    private Charge charge;
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
    public void testChargeCreate() {
        String name = UUID.randomUUID().toString();
        ChargeCreate request = new ChargeCreate()
                .setName(name);
        ResponseEntity<Charge> chargeResponse = this.restTemplate.postForEntity("/plugins/Charge/createCharge", request, Charge.class);
        Assertions.assertEquals(200, chargeResponse.getStatusCodeValue());
        charge = chargeResponse.getBody();
        assertCharge(request, charge);

    }

    @Test
    @Order(2)
    public void testListAllCharges() {
        ChargeFiltering request=new ChargeFiltering();
        ParameterizedTypeReference<PaginationResponse<Charge>> t= new ParameterizedTypeReference<>() {
        };

        ResponseEntity<PaginationResponse<Charge>> chargeResponse = this.restTemplate.exchange("/plugins/Charge/getAllCharges", HttpMethod.POST, new HttpEntity<>(request), t);
        Assertions.assertEquals(200, chargeResponse.getStatusCodeValue());
        PaginationResponse<Charge> body = chargeResponse.getBody();
        Assertions.assertNotNull(body);
        List<Charge> charges = body.getList();
        Assertions.assertNotEquals(0,charges.size());
        Assertions.assertTrue(charges.stream().anyMatch(f->f.getId().equals(charge.getId())));


    }

    public void assertCharge(ChargeCreate request, Charge charge) {
        Assertions.assertNotNull(charge);
        Assertions.assertEquals(request.getName(), charge.getName());
    }

    @Test
    @Order(3)
    public void testChargeUpdate(){
        String name = UUID.randomUUID().toString();
        ChargeUpdate request = new ChargeUpdate()
                .setId(charge.getId())
                .setName(name);
        ResponseEntity<Charge> chargeResponse = this.restTemplate.exchange("/plugins/Charge/updateCharge",HttpMethod.PUT, new HttpEntity<>(request), Charge.class);
        Assertions.assertEquals(200, chargeResponse.getStatusCodeValue());
        charge = chargeResponse.getBody();
        assertCharge(request, charge);

    }

}
