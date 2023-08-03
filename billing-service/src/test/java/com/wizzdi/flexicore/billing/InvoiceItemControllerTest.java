package com.wizzdi.flexicore.billing;

import com.wizzdi.flexicore.billing.app.App;
import com.wizzdi.flexicore.billing.model.payment.InvoiceItem;
import com.wizzdi.flexicore.billing.request.InvoiceItemCreate;
import com.wizzdi.flexicore.billing.request.InvoiceItemFiltering;
import com.wizzdi.flexicore.billing.request.InvoiceItemUpdate;
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
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.Collections;
import java.util.List;
import java.util.UUID;

@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT, classes = App.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@Testcontainers
@ActiveProfiles("test")
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE) // deactivate the default behaviour

public class InvoiceItemControllerTest {

    private final static PostgreSQLContainer postgresqlContainer = new PostgreSQLContainer("postgres:15")

            .withDatabaseName("flexicore-test")
            .withUsername("flexicore")
            .withPassword("flexicore");

    static {
        postgresqlContainer.start();
    }

    @DynamicPropertySource
    static void setProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgresqlContainer::getJdbcUrl);
        registry.add("spring.datasource.username", postgresqlContainer::getUsername);
        registry.add("spring.datasource.password", postgresqlContainer::getPassword);
    }
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
