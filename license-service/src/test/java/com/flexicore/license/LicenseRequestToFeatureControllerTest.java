package com.flexicore.license;

import com.flexicore.license.app.App;
import com.flexicore.license.model.LicenseRequest;
import com.flexicore.license.model.LicenseRequestToFeature;
import com.flexicore.license.model.LicensingFeature;
import com.flexicore.license.request.LicenseRequestToFeatureCreate;
import com.flexicore.license.request.LicenseRequestToFeatureFiltering;
import com.flexicore.license.request.LicenseRequestToFeatureUpdate;
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

public class LicenseRequestToFeatureControllerTest {

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
    @Autowired
    private LicenseRequest licenseRequest;
    @Autowired
    private LicensingFeature licensingFeature;
    private LicenseRequestToFeature licenseRequestToFeature;
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
    public void testLicenseRequestToFeatureCreate() {
        String name = UUID.randomUUID().toString();
        ParameterizedTypeReference<PaginationResponse<SecurityTenant>> t=new ParameterizedTypeReference<PaginationResponse<SecurityTenant>>() {};

        ResponseEntity<PaginationResponse<SecurityTenant>> tenantResponse = this.restTemplate.exchange("/securityTenant/getAll", HttpMethod.POST, new HttpEntity<>(new SecurityTenantFilter()), t);
        Assertions.assertEquals(200, tenantResponse.getStatusCodeValue());
        PaginationResponse<SecurityTenant> body = tenantResponse.getBody();
        Assertions.assertNotNull(body);
        List<SecurityTenant> tenants = body.getList();
        Assertions.assertFalse(tenants.isEmpty());
        LicenseRequestToFeatureCreate request = new LicenseRequestToFeatureCreate()
                .setLicensingFeatureId(licensingFeature.getId())
                .setLicenseRequestId(licenseRequest.getId())
                .setName(name);
        ResponseEntity<LicenseRequestToFeature> licenseRequestToFeatureResponse = this.restTemplate.postForEntity("/licenseRequestToFeatures/createLicenseRequestToFeature", request, LicenseRequestToFeature.class);
        Assertions.assertEquals(200, licenseRequestToFeatureResponse.getStatusCodeValue());
        licenseRequestToFeature = licenseRequestToFeatureResponse.getBody();
        assertLicenseRequestToFeature(request, licenseRequestToFeature);

    }

    @Test
    @Order(2)
    public void testListAllLicenseRequestToFeatures() {
        LicenseRequestToFeatureFiltering request = new LicenseRequestToFeatureFiltering();
        request.setBasicPropertiesFilter(new BasicPropertiesFilter().setNameLike(licenseRequestToFeature.getName()));
        ParameterizedTypeReference<PaginationResponse<LicenseRequestToFeature>> t = new ParameterizedTypeReference<PaginationResponse<LicenseRequestToFeature>>() {
        };

        ResponseEntity<PaginationResponse<LicenseRequestToFeature>> licenseRequestToFeatureResponse = this.restTemplate.exchange("/licenseRequestToFeatures/getAllLicenseRequestToFeatures", HttpMethod.POST, new HttpEntity<>(request), t);
        Assertions.assertEquals(200, licenseRequestToFeatureResponse.getStatusCodeValue());
        PaginationResponse<LicenseRequestToFeature> body = licenseRequestToFeatureResponse.getBody();
        Assertions.assertNotNull(body);
        List<LicenseRequestToFeature> licenseRequestToFeatures = body.getList();
        Assertions.assertNotEquals(0, licenseRequestToFeatures.size());
        Assertions.assertTrue(licenseRequestToFeatures.stream().anyMatch(f -> f.getId().equals(licenseRequestToFeature.getId())));


    }

    public void assertLicenseRequestToFeature(LicenseRequestToFeatureCreate request, LicenseRequestToFeature licenseRequestToFeature) {
        Assertions.assertNotNull(licenseRequestToFeature);
        Assertions.assertEquals(request.getName(), licenseRequestToFeature.getName());

    }

    @Test
    @Order(3)
    public void testLicenseRequestToFeatureUpdate() {
        String name = UUID.randomUUID().toString();
        LicenseRequestToFeatureUpdate request = new LicenseRequestToFeatureUpdate()
                .setId(licenseRequestToFeature.getId())
                .setName(name);
        ResponseEntity<LicenseRequestToFeature> licenseRequestToFeatureResponse = this.restTemplate.exchange("/licenseRequestToFeatures/updateLicenseRequestToFeature", HttpMethod.PUT, new HttpEntity<>(request), LicenseRequestToFeature.class);
        Assertions.assertEquals(200, licenseRequestToFeatureResponse.getStatusCodeValue());
        licenseRequestToFeature = licenseRequestToFeatureResponse.getBody();
        assertLicenseRequestToFeature(request, licenseRequestToFeature);

    }

}
