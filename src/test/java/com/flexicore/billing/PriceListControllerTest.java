package com.flexicore.billing;

import com.flexicore.billing.app.App;
import com.flexicore.billing.model.PriceList;
import com.flexicore.billing.request.PriceListCreate;
import com.flexicore.billing.request.PriceListFiltering;
import com.flexicore.billing.request.PriceListUpdate;
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

public class PriceListControllerTest {

    private PriceList priceList;
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
    public void testPriceListCreate() {
        String name = UUID.randomUUID().toString();
        PriceListCreate request = new PriceListCreate()
                .setName(name);
        ResponseEntity<PriceList> priceListResponse = this.restTemplate.postForEntity("/plugins/PriceList/createPriceList", request, PriceList.class);
        Assertions.assertEquals(200, priceListResponse.getStatusCodeValue());
        priceList = priceListResponse.getBody();
        assertPriceList(request, priceList);

    }

    @Test
    @Order(2)
    public void testListAllPriceLists() {
        PriceListFiltering request=new PriceListFiltering();
        ParameterizedTypeReference<PaginationResponse<PriceList>> t= new ParameterizedTypeReference<>() {
        };

        ResponseEntity<PaginationResponse<PriceList>> priceListResponse = this.restTemplate.exchange("/plugins/PriceList/getAllPriceLists", HttpMethod.POST, new HttpEntity<>(request), t);
        Assertions.assertEquals(200, priceListResponse.getStatusCodeValue());
        PaginationResponse<PriceList> body = priceListResponse.getBody();
        Assertions.assertNotNull(body);
        List<PriceList> priceLists = body.getList();
        Assertions.assertNotEquals(0,priceLists.size());
        Assertions.assertTrue(priceLists.stream().anyMatch(f->f.getId().equals(priceList.getId())));


    }

    public void assertPriceList(PriceListCreate request, PriceList priceList) {
        Assertions.assertNotNull(priceList);
        Assertions.assertEquals(request.getName(), priceList.getName());
    }

    @Test
    @Order(3)
    public void testPriceListUpdate(){
        String name = UUID.randomUUID().toString();
        PriceListUpdate request = new PriceListUpdate()
                .setId(priceList.getId())
                .setName(name);
        ResponseEntity<PriceList> priceListResponse = this.restTemplate.exchange("/plugins/PriceList/updatePriceList",HttpMethod.PUT, new HttpEntity<>(request), PriceList.class);
        Assertions.assertEquals(200, priceListResponse.getStatusCodeValue());
        priceList = priceListResponse.getBody();
        assertPriceList(request, priceList);

    }

}
