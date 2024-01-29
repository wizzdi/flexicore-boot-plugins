package com.flexicore.ui;


import com.flexicore.ui.app.App;
import com.flexicore.ui.model.PresetToPreset;
import com.flexicore.ui.request.PresetToPresetCreate;
import com.flexicore.ui.request.PresetToPresetFiltering;
import com.flexicore.ui.request.PresetToPresetUpdate;
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

public class PresetToPresetControllerTest {

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
    private PresetToPreset presetToPreset;
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
    public void testPresetToPresetCreate() {
        String name = UUID.randomUUID().toString();
        PresetToPresetCreate request = new PresetToPresetCreate()
                .setName(name);
        ResponseEntity<PresetToPreset> presetToPresetResponse = this.restTemplate.postForEntity("/plugins/PresetToPresets/createPresetToPreset", request, PresetToPreset.class);
        Assertions.assertEquals(200, presetToPresetResponse.getStatusCodeValue());
        presetToPreset = presetToPresetResponse.getBody();
        assertPresetToPreset(request, presetToPreset);

    }

    @Test
    @Order(2)
    public void testGetAllPresetToPresets() {
        PresetToPresetFiltering request=new PresetToPresetFiltering();
        ParameterizedTypeReference<PaginationResponse<PresetToPreset>> t= new ParameterizedTypeReference<>() {
        };

        ResponseEntity<PaginationResponse<PresetToPreset>> presetToPresetResponse = this.restTemplate.exchange("/plugins/PresetToPresets/getAllPresetToPresets", HttpMethod.POST, new HttpEntity<>(request), t);
        Assertions.assertEquals(200, presetToPresetResponse.getStatusCodeValue());
        PaginationResponse<PresetToPreset> body = presetToPresetResponse.getBody();
        Assertions.assertNotNull(body);
        List<PresetToPreset> presetToPresets = body.getList();
        Assertions.assertNotEquals(0,presetToPresets.size());
        Assertions.assertTrue(presetToPresets.stream().anyMatch(f->f.getId().equals(presetToPreset.getId())));


    }

    public void assertPresetToPreset(PresetToPresetCreate request, PresetToPreset presetToPreset) {
        Assertions.assertNotNull(presetToPreset);
        Assertions.assertEquals(request.getName(), presetToPreset.getName());
    }

    @Test
    @Order(3)
    public void testPresetToPresetUpdate(){
        String name = UUID.randomUUID().toString();
        PresetToPresetUpdate request = new PresetToPresetUpdate()
                .setId(presetToPreset.getId())
                .setName(name);
        ResponseEntity<PresetToPreset> presetToPresetResponse = this.restTemplate.exchange("/plugins/PresetToPresets/updatePresetToPreset",HttpMethod.PUT, new HttpEntity<>(request), PresetToPreset.class);
        Assertions.assertEquals(200, presetToPresetResponse.getStatusCodeValue());
        presetToPreset = presetToPresetResponse.getBody();
        assertPresetToPreset(request, presetToPreset);

    }

}
