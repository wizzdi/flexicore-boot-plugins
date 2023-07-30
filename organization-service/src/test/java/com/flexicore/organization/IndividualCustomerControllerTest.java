package com.flexicore.organization;

import com.flexicore.organization.app.App;
import com.flexicore.organization.model.IndividualCustomer;
import com.flexicore.organization.request.IndividualCustomerCreate;
import com.flexicore.organization.request.IndividualCustomerFiltering;
import com.flexicore.organization.request.IndividualCustomerUpdate;
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

public class IndividualCustomerControllerTest {

    private IndividualCustomer individualCustomer;
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
    public void testIndividualCustomerCreate() {
        String name = UUID.randomUUID().toString();
        IndividualCustomerCreate request = new IndividualCustomerCreate()
                .setName(name);
        ResponseEntity<IndividualCustomer> individualCustomerResponse = this.restTemplate.postForEntity("/plugins/IndividualCustomer/createIndividualCustomer", request, IndividualCustomer.class);
        Assertions.assertEquals(200, individualCustomerResponse.getStatusCodeValue());
        individualCustomer = individualCustomerResponse.getBody();
        assertIndividualCustomer(request, individualCustomer);

    }

    @Test
    @Order(2)
    public void testListAllIndividualCustomers() {
        IndividualCustomerFiltering request=new IndividualCustomerFiltering();
        ParameterizedTypeReference<PaginationResponse<IndividualCustomer>> t= new ParameterizedTypeReference<>() {
        };

        ResponseEntity<PaginationResponse<IndividualCustomer>> individualCustomerResponse = this.restTemplate.exchange("/plugins/IndividualCustomer/getAllIndividualCustomers", HttpMethod.POST, new HttpEntity<>(request), t);
        Assertions.assertEquals(200, individualCustomerResponse.getStatusCodeValue());
        PaginationResponse<IndividualCustomer> body = individualCustomerResponse.getBody();
        Assertions.assertNotNull(body);
        List<IndividualCustomer> individualCustomers = body.getList();
        Assertions.assertNotEquals(0,individualCustomers.size());
        Assertions.assertTrue(individualCustomers.stream().anyMatch(f->f.getId().equals(individualCustomer.getId())));


    }

    public void assertIndividualCustomer(IndividualCustomerCreate request, IndividualCustomer individualCustomer) {
        Assertions.assertNotNull(individualCustomer);
        Assertions.assertEquals(request.getName(), individualCustomer.getName());
    }

    @Test
    @Order(3)
    public void testIndividualCustomerUpdate(){
        String name = UUID.randomUUID().toString();
        IndividualCustomerUpdate request = new IndividualCustomerUpdate()
                .setId(individualCustomer.getId())
                .setName(name);
        ResponseEntity<IndividualCustomer> individualCustomerResponse = this.restTemplate.exchange("/plugins/IndividualCustomer/updateIndividualCustomer",HttpMethod.PUT, new HttpEntity<>(request), IndividualCustomer.class);
        Assertions.assertEquals(200, individualCustomerResponse.getStatusCodeValue());
        individualCustomer = individualCustomerResponse.getBody();
        assertIndividualCustomer(request, individualCustomer);

    }

}
