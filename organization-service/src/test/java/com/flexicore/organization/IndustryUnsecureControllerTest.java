package com.flexicore.organization;

import com.flexicore.organization.app.App;
import com.flexicore.organization.model.Industry;
import com.flexicore.organization.request.IndustryCreate;
import com.flexicore.organization.request.IndustryFiltering;
import com.flexicore.organization.service.IndustryService;
import com.flexicore.security.SecurityContextBase;
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

public class IndustryUnsecureControllerTest {

    private Industry industry;
    @Autowired
    private TestRestTemplate restTemplate;
    @Autowired
    private IndustryService industryService;
    @Autowired
    private SecurityContextBase adminSecurityContext;

    @BeforeAll
    private void init() {


    }

    @Test
    @Order(1)
    public void testIndustryCreate() {
        String name = UUID.randomUUID().toString();
        IndustryCreate request = new IndustryCreate()
                .setName(name);
        industry=industryService.createIndustry(request,adminSecurityContext);
        assertIndustry(request, industry);

    }

    @Test
    @Order(2)
    public void testListAllIndustries() {
        IndustryFiltering request=new IndustryFiltering();
        ParameterizedTypeReference<PaginationResponse<Industry>> t= new ParameterizedTypeReference<>() {
        };

        ResponseEntity<PaginationResponse<Industry>> industryResponse = this.restTemplate.exchange("/plugins/UnsecureIndustry/getAllIndustries", HttpMethod.POST, new HttpEntity<>(request), t);
        Assertions.assertEquals(200, industryResponse.getStatusCodeValue());
        PaginationResponse<Industry> body = industryResponse.getBody();
        Assertions.assertNotNull(body);
        List<Industry> industrys = body.getList();
        Assertions.assertNotEquals(0,industrys.size());
        Assertions.assertTrue(industrys.stream().anyMatch(f->f.getId().equals(industry.getId())));


    }

    public void assertIndustry(IndustryCreate request, Industry industry) {
        Assertions.assertNotNull(industry);
        Assertions.assertEquals(request.getName(), industry.getName());
    }

}
