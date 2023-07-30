package com.flexicore.license;

import com.flexicore.license.app.App;
import com.flexicore.license.model.LicenseRequest;
import com.flexicore.license.request.LicenseRequestCreate;
import com.flexicore.license.request.LicenseRequestFiltering;
import com.flexicore.license.request.LicenseRequestUpdate;
import com.flexicore.license.service.LicenseRequestService;
import com.flexicore.license.service.LicenseRequestToProductService;
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
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.Collections;
import java.util.List;
import java.util.UUID;

@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT, classes = App.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@ActiveProfiles("test")

public class LicensedControllerTest {

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
    public void testWithoutLicense() {

        ResponseEntity<String> licenseRequestResponse = this.restTemplate.getForEntity("/licenseTest/test", String.class);
        Assertions.assertEquals(403, licenseRequestResponse.getStatusCodeValue());


    }



}
