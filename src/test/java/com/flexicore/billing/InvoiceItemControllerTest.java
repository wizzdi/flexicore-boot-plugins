package com.flexicore.billing;

import com.flexicore.billing.app.App;
import com.flexicore.billing.model.InvoiceItem;
import com.flexicore.billing.request.InvoiceItemCreate;
import com.flexicore.billing.request.InvoiceItemFiltering;
import com.flexicore.billing.request.InvoiceItemUpdate;
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

public class InvoiceItemControllerTest {

    private InvoiceItem invoiceItem;
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
    public void testInvoiceItemCreate() {
        String name = UUID.randomUUID().toString();
        InvoiceItemCreate request = new InvoiceItemCreate()
                .setName(name);
        ResponseEntity<InvoiceItem> invoiceItemResponse = this.restTemplate.postForEntity("/plugins/InvoiceItem/createInvoiceItem", request, InvoiceItem.class);
        Assertions.assertEquals(200, invoiceItemResponse.getStatusCodeValue());
        invoiceItem = invoiceItemResponse.getBody();
        assertInvoiceItem(request, invoiceItem);

    }

    @Test
    @Order(2)
    public void testListAllInvoiceItems() {
        InvoiceItemFiltering request=new InvoiceItemFiltering();
        ParameterizedTypeReference<PaginationResponse<InvoiceItem>> t= new ParameterizedTypeReference<>() {
        };

        ResponseEntity<PaginationResponse<InvoiceItem>> invoiceItemResponse = this.restTemplate.exchange("/plugins/InvoiceItem/getAllInvoiceItems", HttpMethod.POST, new HttpEntity<>(request), t);
        Assertions.assertEquals(200, invoiceItemResponse.getStatusCodeValue());
        PaginationResponse<InvoiceItem> body = invoiceItemResponse.getBody();
        Assertions.assertNotNull(body);
        List<InvoiceItem> invoiceItems = body.getList();
        Assertions.assertNotEquals(0,invoiceItems.size());
        Assertions.assertTrue(invoiceItems.stream().anyMatch(f->f.getId().equals(invoiceItem.getId())));


    }

    public void assertInvoiceItem(InvoiceItemCreate request, InvoiceItem invoiceItem) {
        Assertions.assertNotNull(invoiceItem);
        Assertions.assertEquals(request.getName(), invoiceItem.getName());
    }

    @Test
    @Order(3)
    public void testInvoiceItemUpdate(){
        String name = UUID.randomUUID().toString();
        InvoiceItemUpdate request = new InvoiceItemUpdate()
                .setId(invoiceItem.getId())
                .setName(name);
        ResponseEntity<InvoiceItem> invoiceItemResponse = this.restTemplate.exchange("/plugins/InvoiceItem/updateInvoiceItem",HttpMethod.PUT, new HttpEntity<>(request), InvoiceItem.class);
        Assertions.assertEquals(200, invoiceItemResponse.getStatusCodeValue());
        invoiceItem = invoiceItemResponse.getBody();
        assertInvoiceItem(request, invoiceItem);

    }

}
