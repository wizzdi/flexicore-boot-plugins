package com.flexicore.organization;

import com.flexicore.organization.app.App;
import com.flexicore.organization.model.Supplier;
import com.flexicore.organization.request.SupplierCreate;
import com.flexicore.organization.request.SupplierFiltering;
import com.flexicore.organization.request.SupplierUpdate;
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

public class SupplierControllerTest {

    private Supplier supplier;
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
    public void testSupplierCreate() {
        String name = UUID.randomUUID().toString();
        SupplierCreate request = new SupplierCreate()
                .setName(name);
        ResponseEntity<Supplier> supplierResponse = this.restTemplate.postForEntity("/plugins/Supplier/createSupplier", request, Supplier.class);
        Assertions.assertEquals(200, supplierResponse.getStatusCodeValue());
        supplier = supplierResponse.getBody();
        assertSupplier(request, supplier);

    }

    @Test
    @Order(2)
    public void testListAllSuppliers() {
        SupplierFiltering request=new SupplierFiltering();
        ParameterizedTypeReference<PaginationResponse<Supplier>> t= new ParameterizedTypeReference<>() {
        };

        ResponseEntity<PaginationResponse<Supplier>> supplierResponse = this.restTemplate.exchange("/plugins/Supplier/getAllSuppliers", HttpMethod.POST, new HttpEntity<>(request), t);
        Assertions.assertEquals(200, supplierResponse.getStatusCodeValue());
        PaginationResponse<Supplier> body = supplierResponse.getBody();
        Assertions.assertNotNull(body);
        List<Supplier> suppliers = body.getList();
        Assertions.assertNotEquals(0,suppliers.size());
        Assertions.assertTrue(suppliers.stream().anyMatch(f->f.getId().equals(supplier.getId())));


    }

    public void assertSupplier(SupplierCreate request, Supplier supplier) {
        Assertions.assertNotNull(supplier);
        Assertions.assertEquals(request.getName(), supplier.getName());
    }

    @Test
    @Order(3)
    public void testSupplierUpdate(){
        String name = UUID.randomUUID().toString();
        SupplierUpdate request = new SupplierUpdate()
                .setId(supplier.getId())
                .setName(name);
        ResponseEntity<Supplier> supplierResponse = this.restTemplate.exchange("/plugins/Supplier/updateSupplier",HttpMethod.PUT, new HttpEntity<>(request), Supplier.class);
        Assertions.assertEquals(200, supplierResponse.getStatusCodeValue());
        supplier = supplierResponse.getBody();
        assertSupplier(request, supplier);

    }

}
