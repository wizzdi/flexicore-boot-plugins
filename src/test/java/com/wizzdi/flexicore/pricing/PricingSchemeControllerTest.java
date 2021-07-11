package com.wizzdi.flexicore.pricing;

import com.wizzdi.flexicore.pricing.app.App;
import com.wizzdi.flexicore.pricing.model.price.PricingScheme;
import com.wizzdi.flexicore.pricing.request.PricingSchemeCreate;
import com.wizzdi.flexicore.pricing.request.PricingSchemeFiltering;
import com.wizzdi.flexicore.pricing.request.PricingSchemeUpdate;
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

public class PricingSchemeControllerTest {

    private PricingScheme pricingScheme;
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
    public void testPricingSchemeCreate() {
        String name = UUID.randomUUID().toString();
        PricingSchemeCreate request = new PricingSchemeCreate()
                .setName(name);
        ResponseEntity<PricingScheme> pricingSchemeResponse = this.restTemplate.postForEntity("/plugins/PricingScheme/createPricingScheme", request, PricingScheme.class);
        Assertions.assertEquals(200, pricingSchemeResponse.getStatusCodeValue());
        pricingScheme = pricingSchemeResponse.getBody();
        assertPricingScheme(request, pricingScheme);

    }

    @Test
    @Order(2)
    public void testListAllPricingSchemes() {
        PricingSchemeFiltering request=new PricingSchemeFiltering();
        ParameterizedTypeReference<PaginationResponse<PricingScheme>> t= new ParameterizedTypeReference<>() {
        };

        ResponseEntity<PaginationResponse<PricingScheme>> pricingSchemeResponse = this.restTemplate.exchange("/plugins/PricingScheme/getAllCurrencies", HttpMethod.POST, new HttpEntity<>(request), t);
        Assertions.assertEquals(200, pricingSchemeResponse.getStatusCodeValue());
        PaginationResponse<PricingScheme> body = pricingSchemeResponse.getBody();
        Assertions.assertNotNull(body);
        List<PricingScheme> pricingSchemes = body.getList();
        Assertions.assertNotEquals(0,pricingSchemes.size());
        Assertions.assertTrue(pricingSchemes.stream().anyMatch(f->f.getId().equals(pricingScheme.getId())));


    }

    public void assertPricingScheme(PricingSchemeCreate request, PricingScheme pricingScheme) {
        Assertions.assertNotNull(pricingScheme);
        Assertions.assertEquals(request.getName(), pricingScheme.getName());
    }

    @Test
    @Order(3)
    public void testPricingSchemeUpdate(){
        String name = UUID.randomUUID().toString();
        PricingSchemeUpdate request = new PricingSchemeUpdate()
                .setId(pricingScheme.getId())
                .setName(name);
        ResponseEntity<PricingScheme> pricingSchemeResponse = this.restTemplate.exchange("/plugins/PricingScheme/updatePricingScheme",HttpMethod.PUT, new HttpEntity<>(request), PricingScheme.class);
        Assertions.assertEquals(200, pricingSchemeResponse.getStatusCodeValue());
        pricingScheme = pricingSchemeResponse.getBody();
        assertPricingScheme(request, pricingScheme);

    }

}
