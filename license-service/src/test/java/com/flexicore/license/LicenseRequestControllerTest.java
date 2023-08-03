package com.flexicore.license;

import com.flexicore.license.app.App;
import com.wizzdi.flexicore.security.request.BasicPropertiesFilter;
import com.wizzdi.flexicore.security.request.SecurityTenantFilter;
import com.wizzdi.flexicore.security.response.PaginationResponse;
import com.flexicore.license.model.LicenseRequest;
import com.flexicore.license.request.LicenseRequestCreate;
import com.flexicore.license.request.LicenseRequestFiltering;
import com.flexicore.license.request.LicenseRequestUpdate;
import com.flexicore.model.SecurityTenant;
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

public class LicenseRequestControllerTest {

    private LicenseRequest licenseRequest;
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
    public void testLicenseRequestCreate() {
        String name = UUID.randomUUID().toString();
        ParameterizedTypeReference<PaginationResponse<SecurityTenant>> t=new ParameterizedTypeReference<PaginationResponse<SecurityTenant>>() {};

        ResponseEntity<PaginationResponse<SecurityTenant>> tenantResponse = this.restTemplate.exchange("/securityTenant/getAll", HttpMethod.POST, new HttpEntity<>(new SecurityTenantFilter()), t);
        Assertions.assertEquals(200, tenantResponse.getStatusCodeValue());
        PaginationResponse<SecurityTenant> body = tenantResponse.getBody();
        Assertions.assertNotNull(body);
        List<SecurityTenant> tenants = body.getList();
        Assertions.assertFalse(tenants.isEmpty());
        LicenseRequestCreate request = new LicenseRequestCreate()
                .setLicensedTenantId(tenants.get(0).getId())
                .setName(name);
        ResponseEntity<LicenseRequest> licenseRequestResponse = this.restTemplate.postForEntity("/licenseRequests/createLicenseRequest", request, LicenseRequest.class);
        Assertions.assertEquals(200, licenseRequestResponse.getStatusCodeValue());
        licenseRequest = licenseRequestResponse.getBody();
        assertLicenseRequest(request, licenseRequest);

    }

    @Test
    @Order(2)
    public void testListAllLicenseRequests() {
        LicenseRequestFiltering request = new LicenseRequestFiltering();
        request.setBasicPropertiesFilter(new BasicPropertiesFilter().setNameLike(licenseRequest.getName()));
        ParameterizedTypeReference<PaginationResponse<LicenseRequest>> t = new ParameterizedTypeReference<PaginationResponse<LicenseRequest>>() {
        };

        ResponseEntity<PaginationResponse<LicenseRequest>> licenseRequestResponse = this.restTemplate.exchange("/licenseRequests/getAllLicenseRequests", HttpMethod.POST, new HttpEntity<>(request), t);
        Assertions.assertEquals(200, licenseRequestResponse.getStatusCodeValue());
        PaginationResponse<LicenseRequest> body = licenseRequestResponse.getBody();
        Assertions.assertNotNull(body);
        List<LicenseRequest> licenseRequests = body.getList();
        Assertions.assertNotEquals(0, licenseRequests.size());
        Assertions.assertTrue(licenseRequests.stream().anyMatch(f -> f.getId().equals(licenseRequest.getId())));


    }

    public void assertLicenseRequest(LicenseRequestCreate request, LicenseRequest licenseRequest) {
        Assertions.assertNotNull(licenseRequest);
        Assertions.assertEquals(request.getName(), licenseRequest.getName());

    }

    @Test
    @Order(3)
    public void testLicenseRequestUpdate() {
        String name = UUID.randomUUID().toString();
        LicenseRequestUpdate request = new LicenseRequestUpdate()
                .setId(licenseRequest.getId())
                .setName(name);
        ResponseEntity<LicenseRequest> licenseRequestResponse = this.restTemplate.exchange("/licenseRequests/updateLicenseRequest", HttpMethod.PUT, new HttpEntity<>(request), LicenseRequest.class);
        Assertions.assertEquals(200, licenseRequestResponse.getStatusCodeValue());
        licenseRequest = licenseRequestResponse.getBody();
        assertLicenseRequest(request, licenseRequest);

    }

}
