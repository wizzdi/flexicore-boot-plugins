package com.wizzdi.flexicore.pricing;

import com.wizzdi.flexicore.pricing.app.App;
import com.wizzdi.flexicore.pricing.model.price.Currency;
import com.wizzdi.flexicore.pricing.request.CurrencyCreate;
import com.wizzdi.flexicore.pricing.request.CurrencyFiltering;
import com.wizzdi.flexicore.pricing.request.CurrencyUpdate;
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

public class CurrencyControllerTest {

    private Currency currency;
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
    public void testCurrencyCreate() {
        String name = UUID.randomUUID().toString();
        CurrencyCreate request = new CurrencyCreate()
                .setName(name);
        ResponseEntity<Currency> currencyResponse = this.restTemplate.postForEntity("/plugins/Currency/createCurrency", request, Currency.class);
        Assertions.assertEquals(200, currencyResponse.getStatusCodeValue());
        currency = currencyResponse.getBody();
        assertCurrency(request, currency);

    }

    @Test
    @Order(2)
    public void testListAllCurrencys() {
        CurrencyFiltering request=new CurrencyFiltering();
        ParameterizedTypeReference<PaginationResponse<Currency>> t= new ParameterizedTypeReference<>() {
        };

        ResponseEntity<PaginationResponse<Currency>> currencyResponse = this.restTemplate.exchange("/plugins/Currency/getAllCurrencies", HttpMethod.POST, new HttpEntity<>(request), t);
        Assertions.assertEquals(200, currencyResponse.getStatusCodeValue());
        PaginationResponse<Currency> body = currencyResponse.getBody();
        Assertions.assertNotNull(body);
        List<Currency> currencys = body.getList();
        Assertions.assertNotEquals(0,currencys.size());
        Assertions.assertTrue(currencys.stream().anyMatch(f->f.getId().equals(currency.getId())));


    }

    public void assertCurrency(CurrencyCreate request, Currency currency) {
        Assertions.assertNotNull(currency);
        Assertions.assertEquals(request.getName(), currency.getName());
    }

    @Test
    @Order(3)
    public void testCurrencyUpdate(){
        String name = UUID.randomUUID().toString();
        CurrencyUpdate request = new CurrencyUpdate()
                .setId(currency.getId())
                .setName(name);
        ResponseEntity<Currency> currencyResponse = this.restTemplate.exchange("/plugins/Currency/updateCurrency",HttpMethod.PUT, new HttpEntity<>(request), Currency.class);
        Assertions.assertEquals(200, currencyResponse.getStatusCodeValue());
        currency = currencyResponse.getBody();
        assertCurrency(request, currency);

    }

}
