package com.flexicore.ui;


import com.flexicore.ui.app.App;
import com.flexicore.ui.model.ConfigurationPreset;
import com.flexicore.ui.request.ConfigurationPresetCreate;
import com.flexicore.ui.request.ConfigurationPresetFiltering;
import com.flexicore.ui.request.ConfigurationPresetUpdate;
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

public class ConfigurationPresetControllerTest {

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
    private ConfigurationPreset configurationPreset;
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
    public void testConfigurationPresetCreate() {
        String name = UUID.randomUUID().toString();
        ConfigurationPresetCreate request = new ConfigurationPresetCreate()
                .setName(name);
        ResponseEntity<ConfigurationPreset> configurationPresetResponse = this.restTemplate.postForEntity("/plugins/ConfigurationPresets/createConfigurationPreset", request, ConfigurationPreset.class);
        Assertions.assertEquals(200, configurationPresetResponse.getStatusCodeValue());
        configurationPreset = configurationPresetResponse.getBody();
        assertConfigurationPreset(request, configurationPreset);

    }

    @Test
    @Order(2)
    public void testGetAllConfigurationPresets() {
        ConfigurationPresetFiltering request=new ConfigurationPresetFiltering();
        ParameterizedTypeReference<PaginationResponse<ConfigurationPreset>> t= new ParameterizedTypeReference<>() {
        };

        ResponseEntity<PaginationResponse<ConfigurationPreset>> configurationPresetResponse = this.restTemplate.exchange("/plugins/ConfigurationPresets/getAllConfigurationPresets", HttpMethod.POST, new HttpEntity<>(request), t);
        Assertions.assertEquals(200, configurationPresetResponse.getStatusCodeValue());
        PaginationResponse<ConfigurationPreset> body = configurationPresetResponse.getBody();
        Assertions.assertNotNull(body);
        List<ConfigurationPreset> configurationPresets = body.getList();
        Assertions.assertNotEquals(0,configurationPresets.size());
        Assertions.assertTrue(configurationPresets.stream().anyMatch(f->f.getId().equals(configurationPreset.getId())));


    }

    public void assertConfigurationPreset(ConfigurationPresetCreate request, ConfigurationPreset configurationPreset) {
        Assertions.assertNotNull(configurationPreset);
        Assertions.assertEquals(request.getName(), configurationPreset.getName());
    }

    @Test
    @Order(3)
    public void testConfigurationPresetUpdate(){
        String name = UUID.randomUUID().toString();
        ConfigurationPresetUpdate request = new ConfigurationPresetUpdate()
                .setId(configurationPreset.getId())
                .setName(name);
        ResponseEntity<ConfigurationPreset> configurationPresetResponse = this.restTemplate.exchange("/plugins/ConfigurationPresets/updateConfigurationPreset",HttpMethod.PUT, new HttpEntity<>(request), ConfigurationPreset.class);
        Assertions.assertEquals(200, configurationPresetResponse.getStatusCodeValue());
        configurationPreset = configurationPresetResponse.getBody();
        assertConfigurationPreset(request, configurationPreset);

    }

}
