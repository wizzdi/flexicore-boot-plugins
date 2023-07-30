package com.wizzdi.flexicore.pricing;

import com.wizzdi.flexicore.pricing.app.App;
import com.wizzdi.flexicore.pricing.model.price.RecurringPrice;
import com.wizzdi.flexicore.pricing.request.RecurringPriceCreate;
import com.wizzdi.flexicore.pricing.request.RecurringPriceFiltering;
import com.wizzdi.flexicore.pricing.request.RecurringPriceUpdate;
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

public class RecurringPriceControllerTest {

    private RecurringPrice recurringPrice;
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
    public void testRecurringPriceCreate() {
        String name = UUID.randomUUID().toString();
        RecurringPriceCreate request = new RecurringPriceCreate()
                .setName(name);
        ResponseEntity<RecurringPrice> recurringPriceResponse = this.restTemplate.postForEntity("/plugins/RecurringPrice/createRecurringPrice", request, RecurringPrice.class);
        Assertions.assertEquals(200, recurringPriceResponse.getStatusCodeValue());
        recurringPrice = recurringPriceResponse.getBody();
        assertRecurringPrice(request, recurringPrice);

    }

    @Test
    @Order(2)
    public void testListAllRecurringPrices() {
        RecurringPriceFiltering request=new RecurringPriceFiltering();
        ParameterizedTypeReference<PaginationResponse<RecurringPrice>> t= new ParameterizedTypeReference<>() {
        };

        ResponseEntity<PaginationResponse<RecurringPrice>> recurringPriceResponse = this.restTemplate.exchange("/plugins/RecurringPrice/getAllCurrencies", HttpMethod.POST, new HttpEntity<>(request), t);
        Assertions.assertEquals(200, recurringPriceResponse.getStatusCodeValue());
        PaginationResponse<RecurringPrice> body = recurringPriceResponse.getBody();
        Assertions.assertNotNull(body);
        List<RecurringPrice> recurringPrices = body.getList();
        Assertions.assertNotEquals(0,recurringPrices.size());
        Assertions.assertTrue(recurringPrices.stream().anyMatch(f->f.getId().equals(recurringPrice.getId())));


    }

    public void assertRecurringPrice(RecurringPriceCreate request, RecurringPrice recurringPrice) {
        Assertions.assertNotNull(recurringPrice);
        Assertions.assertEquals(request.getName(), recurringPrice.getName());
    }

    @Test
    @Order(3)
    public void testRecurringPriceUpdate(){
        String name = UUID.randomUUID().toString();
        RecurringPriceUpdate request = new RecurringPriceUpdate()
                .setId(recurringPrice.getId())
                .setName(name);
        ResponseEntity<RecurringPrice> recurringPriceResponse = this.restTemplate.exchange("/plugins/RecurringPrice/updateRecurringPrice",HttpMethod.PUT, new HttpEntity<>(request), RecurringPrice.class);
        Assertions.assertEquals(200, recurringPriceResponse.getStatusCodeValue());
        recurringPrice = recurringPriceResponse.getBody();
        assertRecurringPrice(request, recurringPrice);

    }

}
