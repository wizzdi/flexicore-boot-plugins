package com.wizzdi.flexicore.billing;

import com.wizzdi.flexicore.billing.app.App;
import com.wizzdi.flexicore.billing.model.payment.Invoice;
import com.wizzdi.flexicore.billing.request.InvoiceCreate;
import com.wizzdi.flexicore.billing.request.InvoiceFiltering;
import com.wizzdi.flexicore.billing.request.InvoiceUpdate;
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

public class InvoiceControllerTest {

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
    private Invoice invoice;
    @Autowired
    private TestRestTemplate restTemplate;

    @BeforeAll
    public void init() {
        restTemplate.getRestTemplate().setInterceptors(
                Collections.singletonList((request, body, execution) -> {
                    request.getHeaders()
                            .add("authenticationKey", "fake");
                    return execution.execute(request, body);
                }));

    }

    @Test
    @Order(1)
    public void testInvoiceCreate() {
        String name = UUID.randomUUID().toString();
        InvoiceCreate request = new InvoiceCreate()
                .setName(name);
        ResponseEntity<Invoice> invoiceResponse = this.restTemplate.postForEntity("/plugins/Invoice/createInvoice", request, Invoice.class);
        Assertions.assertEquals(200, invoiceResponse.getStatusCodeValue());
        invoice = invoiceResponse.getBody();
        assertInvoice(request, invoice);

    }

    @Test
    @Order(2)
    public void testListAllInvoices() {
        InvoiceFiltering request=new InvoiceFiltering();
        ParameterizedTypeReference<PaginationResponse<Invoice>> t= new ParameterizedTypeReference<>() {
        };

        ResponseEntity<PaginationResponse<Invoice>> invoiceResponse = this.restTemplate.exchange("/plugins/Invoice/getAllInvoices", HttpMethod.POST, new HttpEntity<>(request), t);
        Assertions.assertEquals(200, invoiceResponse.getStatusCodeValue());
        PaginationResponse<Invoice> body = invoiceResponse.getBody();
        Assertions.assertNotNull(body);
        List<Invoice> invoices = body.getList();
        Assertions.assertNotEquals(0,invoices.size());
        Assertions.assertTrue(invoices.stream().anyMatch(f->f.getId().equals(invoice.getId())));


    }

    public void assertInvoice(InvoiceCreate request, Invoice invoice) {
        Assertions.assertNotNull(invoice);
        Assertions.assertEquals(request.getName(), invoice.getName());
    }

    @Test
    @Order(3)
    public void testInvoiceUpdate(){
        String name = UUID.randomUUID().toString();
        InvoiceUpdate request = new InvoiceUpdate()
                .setId(invoice.getId())
                .setName(name);
        ResponseEntity<Invoice> invoiceResponse = this.restTemplate.exchange("/plugins/Invoice/updateInvoice",HttpMethod.PUT, new HttpEntity<>(request), Invoice.class);
        Assertions.assertEquals(200, invoiceResponse.getStatusCodeValue());
        invoice = invoiceResponse.getBody();
        assertInvoice(request, invoice);

    }

}
