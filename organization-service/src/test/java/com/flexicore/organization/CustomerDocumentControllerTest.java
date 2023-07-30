package com.flexicore.organization;

import com.flexicore.organization.app.App;
import com.flexicore.organization.model.CustomerDocument;
import com.flexicore.organization.request.CustomerDocumentCreate;
import com.flexicore.organization.request.CustomerDocumentFiltering;
import com.flexicore.organization.request.CustomerDocumentUpdate;
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

public class CustomerDocumentControllerTest {

    private CustomerDocument customerDocument;
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
    public void testCustomerDocumentCreate() {
        String name = UUID.randomUUID().toString();
        CustomerDocumentCreate request = new CustomerDocumentCreate()
                .setName(name);
        ResponseEntity<CustomerDocument> customerDocumentResponse = this.restTemplate.postForEntity("/plugins/CustomerDocument/createCustomerDocument", request, CustomerDocument.class);
        Assertions.assertEquals(200, customerDocumentResponse.getStatusCodeValue());
        customerDocument = customerDocumentResponse.getBody();
        assertCustomerDocument(request, customerDocument);

    }

    @Test
    @Order(2)
    public void testListAllCustomerDocuments() {
        CustomerDocumentFiltering request=new CustomerDocumentFiltering();
        ParameterizedTypeReference<PaginationResponse<CustomerDocument>> t= new ParameterizedTypeReference<>() {
        };

        ResponseEntity<PaginationResponse<CustomerDocument>> customerDocumentResponse = this.restTemplate.exchange("/plugins/CustomerDocument/getAllCustomerDocuments", HttpMethod.POST, new HttpEntity<>(request), t);
        Assertions.assertEquals(200, customerDocumentResponse.getStatusCodeValue());
        PaginationResponse<CustomerDocument> body = customerDocumentResponse.getBody();
        Assertions.assertNotNull(body);
        List<CustomerDocument> customerDocuments = body.getList();
        Assertions.assertNotEquals(0,customerDocuments.size());
        Assertions.assertTrue(customerDocuments.stream().anyMatch(f->f.getId().equals(customerDocument.getId())));


    }

    public void assertCustomerDocument(CustomerDocumentCreate request, CustomerDocument customerDocument) {
        Assertions.assertNotNull(customerDocument);
        Assertions.assertEquals(request.getName(), customerDocument.getName());
    }

    @Test
    @Order(3)
    public void testCustomerDocumentUpdate(){
        String name = UUID.randomUUID().toString();
        CustomerDocumentUpdate request = new CustomerDocumentUpdate()
                .setId(customerDocument.getId())
                .setName(name);
        ResponseEntity<CustomerDocument> customerDocumentResponse = this.restTemplate.exchange("/plugins/CustomerDocument/updateCustomerDocument",HttpMethod.PUT, new HttpEntity<>(request), CustomerDocument.class);
        Assertions.assertEquals(200, customerDocumentResponse.getStatusCodeValue());
        customerDocument = customerDocumentResponse.getBody();
        assertCustomerDocument(request, customerDocument);

    }

}
