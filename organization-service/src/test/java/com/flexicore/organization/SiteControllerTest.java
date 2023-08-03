package com.flexicore.organization;

import com.flexicore.organization.app.App;

import com.flexicore.organization.model.Site;
import com.flexicore.organization.request.SiteCreate;
import com.flexicore.organization.request.SiteFiltering;
import com.flexicore.organization.request.SiteUpdate;
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

public class SiteControllerTest {

    private Site site;
    @Autowired
    private TestRestTemplate restTemplate;

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
    public void testSiteCreate() {
        String name = UUID.randomUUID().toString();
        SiteCreate request = new SiteCreate()
                .setName(name);
        ResponseEntity<Site> siteResponse = this.restTemplate.postForEntity("/plugins/Site/createSite", request, Site.class);
        Assertions.assertEquals(200, siteResponse.getStatusCodeValue());
        site = siteResponse.getBody();
        assertSite(request, site);

    }

    @Test
    @Order(2)
    public void testListAllSites() {
        SiteFiltering request=new SiteFiltering();
        ParameterizedTypeReference<PaginationResponse<Site>> t= new ParameterizedTypeReference<>() {
        };

        ResponseEntity<PaginationResponse<Site>> siteResponse = this.restTemplate.exchange("/plugins/Site/getAllSites", HttpMethod.POST, new HttpEntity<>(request), t);
        Assertions.assertEquals(200, siteResponse.getStatusCodeValue());
        PaginationResponse<Site> body = siteResponse.getBody();
        Assertions.assertNotNull(body);
        List<Site> sites = body.getList();
        Assertions.assertNotEquals(0,sites.size());
        Assertions.assertTrue(sites.stream().anyMatch(f->f.getId().equals(site.getId())));


    }

    public void assertSite(SiteCreate request, Site site) {
        Assertions.assertNotNull(site);
        Assertions.assertEquals(request.getName(), site.getName());
    }

    @Test
    @Order(3)
    public void testSiteUpdate(){
        String name = UUID.randomUUID().toString();
        SiteUpdate request = new SiteUpdate()
                .setId(site.getId())
                .setName(name);
        ResponseEntity<Site> siteResponse = this.restTemplate.exchange("/plugins/Site/updateSite",HttpMethod.PUT, new HttpEntity<>(request), Site.class);
        Assertions.assertEquals(200, siteResponse.getStatusCodeValue());
        site = siteResponse.getBody();
        assertSite(request, site);

    }

}
