package com.flexicore.billing;

import com.flexicore.billing.app.App;
import com.flexicore.billing.model.BusinessService;
import com.flexicore.billing.request.BusinessServiceCreate;
import com.flexicore.billing.request.BusinessServiceFiltering;
import com.flexicore.billing.request.BusinessServiceUpdate;
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

public class BusinessServiceControllerTest {

    private BusinessService businessService;
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
    public void testBusinessServiceCreate() {
        String name = UUID.randomUUID().toString();
        BusinessServiceCreate request = new BusinessServiceCreate()
                .setName(name);
        ResponseEntity<BusinessService> businessServiceResponse = this.restTemplate.postForEntity("/plugins/BusinessService/createBusinessService", request, BusinessService.class);
        Assertions.assertEquals(200, businessServiceResponse.getStatusCodeValue());
        businessService = businessServiceResponse.getBody();
        assertBusinessService(request, businessService);

    }

    @Test
    @Order(2)
    public void testListAllBusinessServices() {
        BusinessServiceFiltering request=new BusinessServiceFiltering();
        ParameterizedTypeReference<PaginationResponse<BusinessService>> t= new ParameterizedTypeReference<>() {
        };

        ResponseEntity<PaginationResponse<BusinessService>> businessServiceResponse = this.restTemplate.exchange("/plugins/BusinessService/getAllBusinessServices", HttpMethod.POST, new HttpEntity<>(request), t);
        Assertions.assertEquals(200, businessServiceResponse.getStatusCodeValue());
        PaginationResponse<BusinessService> body = businessServiceResponse.getBody();
        Assertions.assertNotNull(body);
        List<BusinessService> businessServices = body.getList();
        Assertions.assertNotEquals(0,businessServices.size());
        Assertions.assertTrue(businessServices.stream().anyMatch(f->f.getId().equals(businessService.getId())));


    }

    public void assertBusinessService(BusinessServiceCreate request, BusinessService businessService) {
        Assertions.assertNotNull(businessService);
        Assertions.assertEquals(request.getName(), businessService.getName());
    }

    @Test
    @Order(3)
    public void testBusinessServiceUpdate(){
        String name = UUID.randomUUID().toString();
        BusinessServiceUpdate request = new BusinessServiceUpdate()
                .setId(businessService.getId())
                .setName(name);
        ResponseEntity<BusinessService> businessServiceResponse = this.restTemplate.exchange("/plugins/BusinessService/updateBusinessService",HttpMethod.PUT, new HttpEntity<>(request), BusinessService.class);
        Assertions.assertEquals(200, businessServiceResponse.getStatusCodeValue());
        businessService = businessServiceResponse.getBody();
        assertBusinessService(request, businessService);

    }

}
