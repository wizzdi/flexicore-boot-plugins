package com.flexicore.license;

import com.flexicore.license.app.App;
import com.flexicore.license.model.LicenseRequest;
import com.flexicore.license.model.LicenseRequestToProduct;
import com.flexicore.license.model.LicensingProduct;
import com.flexicore.license.request.LicenseRequestToProductCreate;
import com.flexicore.license.request.LicenseRequestToProductFiltering;
import com.flexicore.license.request.LicenseRequestToProductUpdate;
import com.flexicore.model.SecurityTenant;
import com.wizzdi.flexicore.security.request.BasicPropertiesFilter;
import com.wizzdi.flexicore.security.request.SecurityTenantFilter;
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

public class LicenseRequestToProductControllerTest {

    private LicenseRequestToProduct licenseRequestToProduct;
    @Autowired
    private LicenseRequest licenseRequest;
    @Autowired
    private LicensingProduct licensingProduct;
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
    public void testLicenseRequestToProductCreate() {
        String name = UUID.randomUUID().toString();
        ParameterizedTypeReference<PaginationResponse<SecurityTenant>> t=new ParameterizedTypeReference<PaginationResponse<SecurityTenant>>() {};

        ResponseEntity<PaginationResponse<SecurityTenant>> tenantResponse = this.restTemplate.exchange("/securityTenant/getAll", HttpMethod.POST, new HttpEntity<>(new SecurityTenantFilter()), t);
        Assertions.assertEquals(200, tenantResponse.getStatusCodeValue());
        PaginationResponse<SecurityTenant> body = tenantResponse.getBody();
        Assertions.assertNotNull(body);
        List<SecurityTenant> tenants = body.getList();
        Assertions.assertFalse(tenants.isEmpty());
        LicenseRequestToProductCreate request = new LicenseRequestToProductCreate()
                .setLicensingProductId(licensingProduct.getId())
                .setLicenseRequestId(licenseRequest.getId())
                .setName(name);
        ResponseEntity<LicenseRequestToProduct> licenseRequestToProductResponse = this.restTemplate.postForEntity("/licenseRequestToProducts/createLicenseRequestToProduct", request, LicenseRequestToProduct.class);
        Assertions.assertEquals(200, licenseRequestToProductResponse.getStatusCodeValue());
        licenseRequestToProduct = licenseRequestToProductResponse.getBody();
        assertLicenseRequestToProduct(request, licenseRequestToProduct);

    }

    @Test
    @Order(2)
    public void testListAllLicenseRequestToProducts() {
        LicenseRequestToProductFiltering request = new LicenseRequestToProductFiltering();
        request.setBasicPropertiesFilter(new BasicPropertiesFilter().setNameLike(licenseRequestToProduct.getName()));
        ParameterizedTypeReference<PaginationResponse<LicenseRequestToProduct>> t = new ParameterizedTypeReference<PaginationResponse<LicenseRequestToProduct>>() {
        };

        ResponseEntity<PaginationResponse<LicenseRequestToProduct>> licenseRequestToProductResponse = this.restTemplate.exchange("/licenseRequestToProducts/getAllLicenseRequestToProducts", HttpMethod.POST, new HttpEntity<>(request), t);
        Assertions.assertEquals(200, licenseRequestToProductResponse.getStatusCodeValue());
        PaginationResponse<LicenseRequestToProduct> body = licenseRequestToProductResponse.getBody();
        Assertions.assertNotNull(body);
        List<LicenseRequestToProduct> licenseRequestToProducts = body.getList();
        Assertions.assertNotEquals(0, licenseRequestToProducts.size());
        Assertions.assertTrue(licenseRequestToProducts.stream().anyMatch(f -> f.getId().equals(licenseRequestToProduct.getId())));


    }

    public void assertLicenseRequestToProduct(LicenseRequestToProductCreate request, LicenseRequestToProduct licenseRequestToProduct) {
        Assertions.assertNotNull(licenseRequestToProduct);
        Assertions.assertEquals(request.getName(), licenseRequestToProduct.getName());

    }

    @Test
    @Order(3)
    public void testLicenseRequestToProductUpdate() {
        String name = UUID.randomUUID().toString();
        LicenseRequestToProductUpdate request = new LicenseRequestToProductUpdate()
                .setId(licenseRequestToProduct.getId())
                .setName(name);
        ResponseEntity<LicenseRequestToProduct> licenseRequestToProductResponse = this.restTemplate.exchange("/licenseRequestToProducts/updateLicenseRequestToProduct", HttpMethod.PUT, new HttpEntity<>(request), LicenseRequestToProduct.class);
        Assertions.assertEquals(200, licenseRequestToProductResponse.getStatusCodeValue());
        licenseRequestToProduct = licenseRequestToProductResponse.getBody();
        assertLicenseRequestToProduct(request, licenseRequestToProduct);

    }

}
