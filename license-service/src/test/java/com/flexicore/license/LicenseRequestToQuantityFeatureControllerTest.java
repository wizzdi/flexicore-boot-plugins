package com.flexicore.license;

import com.flexicore.license.app.App;
import com.flexicore.license.model.LicenseRequest;
import com.flexicore.license.model.LicenseRequestToQuantityFeature;
import com.flexicore.license.model.LicensingFeature;
import com.flexicore.license.model.LicensingProduct;
import com.flexicore.license.request.LicenseRequestToQuantityFeatureCreate;
import com.flexicore.license.request.LicenseRequestToQuantityFeatureFiltering;
import com.flexicore.license.request.LicenseRequestToQuantityFeatureUpdate;
import com.flexicore.license.service.LicensingProductService;
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

public class LicenseRequestToQuantityFeatureControllerTest {

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
    private LicenseRequestToQuantityFeature licenseRequestToQuantityFeature;
    @Autowired
    private LicenseRequest licenseRequest;
    @Autowired
    private LicensingFeature licensingFeature;
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
    public void testLicenseRequestToQuantityFeatureCreate() {
        String name = UUID.randomUUID().toString();
        ParameterizedTypeReference<PaginationResponse<SecurityTenant>> t=new ParameterizedTypeReference<PaginationResponse<SecurityTenant>>() {};

        ResponseEntity<PaginationResponse<SecurityTenant>> tenantResponse = this.restTemplate.exchange("/securityTenant/getAll", HttpMethod.POST, new HttpEntity<>(new SecurityTenantFilter()), t);
        Assertions.assertEquals(200, tenantResponse.getStatusCodeValue());
        PaginationResponse<SecurityTenant> body = tenantResponse.getBody();
        Assertions.assertNotNull(body);
        List<SecurityTenant> tenants = body.getList();
        Assertions.assertFalse(tenants.isEmpty());
        LicenseRequestToQuantityFeatureCreate request = new LicenseRequestToQuantityFeatureCreate()
                .setQuantityLimit(10)
                .setLicensingFeatureId(licensingFeature.getId())
                .setLicenseRequestId(licenseRequest.getId())
                .setName(name);
        ResponseEntity<LicenseRequestToQuantityFeature> licenseRequestToQuantityFeatureResponse = this.restTemplate.postForEntity("/licenseRequestToQuantityFeatures/createLicenseRequestToQuantityFeature", request, LicenseRequestToQuantityFeature.class);
        Assertions.assertEquals(200, licenseRequestToQuantityFeatureResponse.getStatusCodeValue());
        licenseRequestToQuantityFeature = licenseRequestToQuantityFeatureResponse.getBody();
        assertLicenseRequestToQuantityFeature(request, licenseRequestToQuantityFeature);

    }

    @Test
    @Order(2)
    public void testListAllLicenseRequestToQuantityFeatures() {
        LicenseRequestToQuantityFeatureFiltering request = new LicenseRequestToQuantityFeatureFiltering();
        request.setBasicPropertiesFilter(new BasicPropertiesFilter().setNameLike(licenseRequestToQuantityFeature.getName()));
        ParameterizedTypeReference<PaginationResponse<LicenseRequestToQuantityFeature>> t = new ParameterizedTypeReference<PaginationResponse<LicenseRequestToQuantityFeature>>() {
        };

        ResponseEntity<PaginationResponse<LicenseRequestToQuantityFeature>> licenseRequestToQuantityFeatureResponse = this.restTemplate.exchange("/licenseRequestToQuantityFeatures/getAllLicenseRequestToQuantityFeatures", HttpMethod.POST, new HttpEntity<>(request), t);
        Assertions.assertEquals(200, licenseRequestToQuantityFeatureResponse.getStatusCodeValue());
        PaginationResponse<LicenseRequestToQuantityFeature> body = licenseRequestToQuantityFeatureResponse.getBody();
        Assertions.assertNotNull(body);
        List<LicenseRequestToQuantityFeature> licenseRequestToQuantityFeatures = body.getList();
        Assertions.assertNotEquals(0, licenseRequestToQuantityFeatures.size());
        Assertions.assertTrue(licenseRequestToQuantityFeatures.stream().anyMatch(f -> f.getId().equals(licenseRequestToQuantityFeature.getId())));


    }

    public void assertLicenseRequestToQuantityFeature(LicenseRequestToQuantityFeatureCreate request, LicenseRequestToQuantityFeature licenseRequestToQuantityFeature) {
        Assertions.assertNotNull(licenseRequestToQuantityFeature);
        Assertions.assertEquals(request.getName(), licenseRequestToQuantityFeature.getName());

    }

    @Test
    @Order(3)
    public void testLicenseRequestToQuantityFeatureUpdate() {
        String name = UUID.randomUUID().toString();
        LicenseRequestToQuantityFeatureUpdate request = new LicenseRequestToQuantityFeatureUpdate()
                .setId(licenseRequestToQuantityFeature.getId())
                .setName(name);
        ResponseEntity<LicenseRequestToQuantityFeature> licenseRequestToQuantityFeatureResponse = this.restTemplate.exchange("/licenseRequestToQuantityFeatures/updateLicenseRequestToQuantityFeature", HttpMethod.PUT, new HttpEntity<>(request), LicenseRequestToQuantityFeature.class);
        Assertions.assertEquals(200, licenseRequestToQuantityFeatureResponse.getStatusCodeValue());
        licenseRequestToQuantityFeature = licenseRequestToQuantityFeatureResponse.getBody();
        assertLicenseRequestToQuantityFeature(request, licenseRequestToQuantityFeature);

    }

}
