package com.flexicore.billing;

import com.flexicore.billing.app.App;
import com.flexicore.billing.model.PriceListToService;
import com.flexicore.billing.request.PriceListToServiceCreate;
import com.flexicore.billing.request.PriceListToServiceFiltering;
import com.flexicore.billing.request.PriceListToServiceUpdate;
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

public class PriceListToServiceControllerTest {

    private PriceListToService priceListToService;
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
    public void testPriceListToServiceCreate() {
        String name = UUID.randomUUID().toString();
        PriceListToServiceCreate request = new PriceListToServiceCreate()
                .setName(name);
        ResponseEntity<PriceListToService> priceListToServiceResponse = this.restTemplate.postForEntity("/plugins/PriceListToService/createPriceListToService", request, PriceListToService.class);
        Assertions.assertEquals(200, priceListToServiceResponse.getStatusCodeValue());
        priceListToService = priceListToServiceResponse.getBody();
        assertPriceListToService(request, priceListToService);

    }

    @Test
    @Order(2)
    public void testListAllPriceListToServices() {
        PriceListToServiceFiltering request=new PriceListToServiceFiltering();
        ParameterizedTypeReference<PaginationResponse<PriceListToService>> t= new ParameterizedTypeReference<>() {
        };

        ResponseEntity<PaginationResponse<PriceListToService>> priceListToServiceResponse = this.restTemplate.exchange("/plugins/PriceListToService/getAllPriceListToServices", HttpMethod.POST, new HttpEntity<>(request), t);
        Assertions.assertEquals(200, priceListToServiceResponse.getStatusCodeValue());
        PaginationResponse<PriceListToService> body = priceListToServiceResponse.getBody();
        Assertions.assertNotNull(body);
        List<PriceListToService> priceListToServices = body.getList();
        Assertions.assertNotEquals(0,priceListToServices.size());
        Assertions.assertTrue(priceListToServices.stream().anyMatch(f->f.getId().equals(priceListToService.getId())));


    }

    public void assertPriceListToService(PriceListToServiceCreate request, PriceListToService priceListToService) {
        Assertions.assertNotNull(priceListToService);
        Assertions.assertEquals(request.getName(), priceListToService.getName());
    }

    @Test
    @Order(3)
    public void testPriceListToServiceUpdate(){
        String name = UUID.randomUUID().toString();
        PriceListToServiceUpdate request = new PriceListToServiceUpdate()
                .setId(priceListToService.getId())
                .setName(name);
        ResponseEntity<PriceListToService> priceListToServiceResponse = this.restTemplate.exchange("/plugins/PriceListToService/updatePriceListToService",HttpMethod.PUT, new HttpEntity<>(request), PriceListToService.class);
        Assertions.assertEquals(200, priceListToServiceResponse.getStatusCodeValue());
        priceListToService = priceListToServiceResponse.getBody();
        assertPriceListToService(request, priceListToService);

    }

}
