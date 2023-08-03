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

public class IndustryUnsecureControllerTest {

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

    private Industry industry;
    @Autowired
    private TestRestTemplate restTemplate;
    @Autowired
    private IndustryService industryService;
    @Autowired
    private SecurityContextBase adminSecurityContext;


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
