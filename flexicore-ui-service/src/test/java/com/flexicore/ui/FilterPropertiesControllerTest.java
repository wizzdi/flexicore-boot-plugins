package com.flexicore.ui;


import com.flexicore.ui.app.App;
import com.flexicore.ui.model.FilterProperties;
import com.flexicore.ui.model.GridPreset;
import com.flexicore.ui.request.FilterPropertiesCreate;
import com.flexicore.ui.request.FilterPropertiesFiltering;
import com.flexicore.ui.request.FilterPropertiesUpdate;
import com.flexicore.ui.service.GridPresetService;
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

public class FilterPropertiesControllerTest {

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
    private FilterProperties filterProperties;
    @Autowired
    private TestRestTemplate restTemplate;
    @Autowired
    private GridPreset gridPreset;

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
    public void testFilterPropertiesCreate() {
        String name = UUID.randomUUID().toString();
        FilterPropertiesCreate request = new FilterPropertiesCreate()
                .setFilterPath("test")
                .setExternalize(false)
                .setBaseclassId(gridPreset.getId())
                .setName(name);
        ResponseEntity<FilterProperties> filterPropertiesResponse = this.restTemplate.postForEntity("/plugins/FilterProperties/createFilterProperties", request, FilterProperties.class);
        Assertions.assertEquals(200, filterPropertiesResponse.getStatusCodeValue());
        filterProperties = filterPropertiesResponse.getBody();
        assertFilterProperties(request, filterProperties);

    }

    @Test
    @Order(2)
    public void getAllFilterProperties() {
        FilterPropertiesFiltering request=new FilterPropertiesFiltering();
        ParameterizedTypeReference<PaginationResponse<FilterProperties>> t= new ParameterizedTypeReference<>() {
        };

        ResponseEntity<PaginationResponse<FilterProperties>> filterPropertiesResponse = this.restTemplate.exchange("/plugins/FilterProperties/getAllFilterProperties", HttpMethod.POST, new HttpEntity<>(request), t);
        Assertions.assertEquals(200, filterPropertiesResponse.getStatusCodeValue());
        PaginationResponse<FilterProperties> body = filterPropertiesResponse.getBody();
        Assertions.assertNotNull(body);
        List<FilterProperties> filterPropertiess = body.getList();
        Assertions.assertNotEquals(0,filterPropertiess.size());
        Assertions.assertTrue(filterPropertiess.stream().anyMatch(f->f.getId().equals(filterProperties.getId())));


    }

    public void assertFilterProperties(FilterPropertiesCreate request, FilterProperties filterProperties) {
        Assertions.assertNotNull(filterProperties);
        if(request.getName()!=null){
            Assertions.assertEquals(request.getName(), filterProperties.getName());
        }
        if(request.getBaseclassId()!=null){
            Assertions.assertEquals(request.getBaseclassId(), filterProperties.getRelatedBaseclass().getId());
        }
        if(request.getFilterPath()!=null){
            Assertions.assertEquals(request.getFilterPath(), filterProperties.getFilterPath());
        }
        if(request.getExternalize()!=null){
            Assertions.assertEquals(request.getExternalize(), filterProperties.isExternalize());
        }
    }

    @Test
    @Order(3)
    public void testFilterPropertiesUpdate(){
        String name = UUID.randomUUID().toString();
        FilterPropertiesUpdate request = new FilterPropertiesUpdate()
                .setId(filterProperties.getId())
                .setName(name);
        ResponseEntity<FilterProperties> filterPropertiesResponse = this.restTemplate.exchange("/plugins/FilterProperties/updateFilterProperties",HttpMethod.PUT, new HttpEntity<>(request), FilterProperties.class);
        Assertions.assertEquals(200, filterPropertiesResponse.getStatusCodeValue());
        filterProperties = filterPropertiesResponse.getBody();
        assertFilterProperties(request, filterProperties);

    }

}
