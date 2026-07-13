package com.flexicore.ui;


import com.flexicore.ui.app.App;
import com.flexicore.ui.model.GridPreset;
import com.flexicore.ui.model.GridPresetOperationField;
import com.flexicore.ui.model.GridPresetOperationType;
import com.flexicore.ui.request.GridPresetCopy;
import com.flexicore.ui.request.GridPresetCreate;
import com.flexicore.ui.request.GridPresetFiltering;
import com.flexicore.ui.request.GridPresetUpdate;
import com.flexicore.ui.rest.GridPresetController;
import com.flexicore.ui.service.GridPresetOperationFieldService;
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
import java.util.Map;
import java.util.UUID;

@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT, classes = App.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@Testcontainers
@ActiveProfiles("test")
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class GridPresetControllerTest {

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

    private GridPreset gridPreset;
    private String unfilteredDynamicExecutionId;
    @Autowired
    private TestRestTemplate restTemplate;
    @Autowired
    private GridPresetService gridPresetService;
    @Autowired
    private GridPresetOperationFieldService gridPresetOperationFieldService;

    @BeforeAll
    public void init() {
        restTemplate.getRestTemplate().setInterceptors(
                Collections.singletonList((request, body, execution) -> {
                    request.getHeaders().add("authenticationKey", "fake");
                    return execution.execute(request, body);
                }));
    }

    @Test
    @Order(1)
    public void testGridPresetCreateCreatesManagedDynamicExecution() {
        String name = UUID.randomUUID().toString();
        GridPresetCreate request = new GridPresetCreate()
                .setDynamicInvokerCanonicalName(GridPresetController.class.getCanonicalName())
                .setDynamicInvokerMethodName("getAllGridPresets")
                .setJsonNode(Map.of("test", "test"))
                .setName(name);
        ResponseEntity<GridPreset> response = this.restTemplate.postForEntity(
                "/plugins/GridPresets/createGridPreset", request, GridPreset.class);
        Assertions.assertEquals(200, response.getStatusCodeValue());
        gridPreset = response.getBody();
        assertGridPreset(request, gridPreset);
        unfilteredDynamicExecutionId = getDynamicExecutionId(gridPreset);
        Assertions.assertNotNull(unfilteredDynamicExecutionId);
        Assertions.assertEquals("createGridPreset", gridPreset.getCreateOperationMethodName());
        Assertions.assertEquals("updateGridPreset", gridPreset.getUpdateOperationMethodName());
        Assertions.assertFalse(gridPreset.getCreateOperationFields().isEmpty());
        Assertions.assertFalse(gridPreset.getUpdateOperationFields().isEmpty());
        Assertions.assertTrue(gridPreset.getCreateOperationFields().stream().allMatch(GridPresetOperationField::isVisible));
        Assertions.assertTrue(gridPreset.getUpdateOperationFields().stream().allMatch(GridPresetOperationField::isVisible));
        assertPersistedOperationFields(gridPreset);
    }

    @Test
    @Order(2)
    public void testUnfilteredPresetReusesDynamicExecution() {
        GridPresetCreate request = new GridPresetCreate()
                .setDynamicInvokerCanonicalName(GridPresetController.class.getCanonicalName())
                .setDynamicInvokerMethodName("getAllGridPresets")
                .setName(UUID.randomUUID().toString());
        ResponseEntity<GridPreset> response = this.restTemplate.postForEntity(
                "/plugins/GridPresets/createGridPreset", request, GridPreset.class);
        Assertions.assertEquals(200, response.getStatusCodeValue());
        Assertions.assertNotNull(response.getBody());
        Assertions.assertEquals(getDynamicExecutionId(gridPreset), getDynamicExecutionId(response.getBody()));
    }

    @Test
    @Order(3)
    public void testGetAllGridPresets() {
        GridPresetFiltering request = new GridPresetFiltering()
                .setDynamicInvokerCanonicalNames(Collections.singleton(GridPresetController.class.getCanonicalName()));
        ParameterizedTypeReference<PaginationResponse<GridPreset>> type = new ParameterizedTypeReference<>() {
        };

        ResponseEntity<PaginationResponse<GridPreset>> response = this.restTemplate.exchange(
                "/plugins/GridPresets/getAllGridPresets", HttpMethod.POST, new HttpEntity<>(request), type);
        Assertions.assertEquals(200, response.getStatusCodeValue());
        PaginationResponse<GridPreset> body = response.getBody();
        Assertions.assertNotNull(body);
        Assertions.assertTrue(body.getList().stream().anyMatch(f -> f.getId().equals(gridPreset.getId())));
    }

    @Test
    @Order(4)
    public void testGridPresetUpdateFilteringAndOperationOrder() {
        String oldExecutionId = getDynamicExecutionId(gridPreset);
        String name = UUID.randomUUID().toString();
        GridPresetUpdate request = new GridPresetUpdate()
                .setId(gridPreset.getId())
                .setFiltering(Map.of("dynamicInvokerCanonicalNames", List.of(GridPresetController.class.getCanonicalName())))
                .setCreateOperationFields(List.of(
                        new GridPresetOperationField("name", true),
                        new GridPresetOperationField("description", false)))
                .setUpdateOperationFields(List.of(
                        new GridPresetOperationField("id", false),
                        new GridPresetOperationField("name", true)))
                .setName(name);
        ResponseEntity<GridPreset> response = this.restTemplate.exchange(
                "/plugins/GridPresets/updateGridPreset", HttpMethod.PUT, new HttpEntity<>(request), GridPreset.class);
        Assertions.assertEquals(200, response.getStatusCodeValue());
        gridPreset = response.getBody();
        Assertions.assertNotNull(gridPreset);
        Assertions.assertEquals(name, gridPreset.getName());
        Assertions.assertEquals(request.getFiltering(), gridPreset.getFiltering());
        assertOperationFields(gridPreset.getCreateOperationFields(),
                List.of("name", "description"), List.of(true, false));
        assertOperationFields(gridPreset.getUpdateOperationFields(),
                List.of("id", "name"), List.of(false, true));
        assertPersistedOperationFields(gridPreset);
        Assertions.assertNotEquals(oldExecutionId, getDynamicExecutionId(gridPreset));
    }

    @Test
    @Order(5)
    public void testFilteredCopyGetsIndependentDynamicExecution() {
        GridPresetCopy request = new GridPresetCopy()
                .setId(gridPreset.getId())
                .setName(UUID.randomUUID().toString());
        ResponseEntity<GridPreset> response = this.restTemplate.postForEntity(
                "/plugins/GridPresets/copyGridPreset", request, GridPreset.class);
        Assertions.assertEquals(200, response.getStatusCodeValue());
        Assertions.assertNotNull(response.getBody());
        Assertions.assertEquals(gridPreset.getFiltering(), response.getBody().getFiltering());
        Assertions.assertNotEquals(getDynamicExecutionId(gridPreset), getDynamicExecutionId(response.getBody()));
    }

    @Test
    @Order(6)
    public void testOmittedFilteringAndFieldOrderRemainUnchanged() {
        Map<String, Object> filtering = gridPreset.getFiltering();
        List<String> createFieldSignature = operationFieldSignature(gridPreset.getCreateOperationFields());
        List<String> updateFieldSignature = operationFieldSignature(gridPreset.getUpdateOperationFields());
        String executionId = getDynamicExecutionId(gridPreset);

        GridPresetUpdate request = new GridPresetUpdate()
                .setId(gridPreset.getId())
                .setDescription(UUID.randomUUID().toString());
        ResponseEntity<GridPreset> response = this.restTemplate.exchange(
                "/plugins/GridPresets/updateGridPreset", HttpMethod.PUT, new HttpEntity<>(request), GridPreset.class);
        Assertions.assertEquals(200, response.getStatusCodeValue());
        gridPreset = response.getBody();
        Assertions.assertNotNull(gridPreset);
        Assertions.assertEquals(filtering, gridPreset.getFiltering());
        Assertions.assertEquals(createFieldSignature, operationFieldSignature(gridPreset.getCreateOperationFields()));
        Assertions.assertEquals(updateFieldSignature, operationFieldSignature(gridPreset.getUpdateOperationFields()));
        Assertions.assertEquals(executionId, getDynamicExecutionId(gridPreset));
    }

    @Test
    @Order(7)
    public void testExplicitEmptyFilteringAndFieldOrderClearValues() {
        GridPresetUpdate request = new GridPresetUpdate()
                .setId(gridPreset.getId())
                .setFiltering(Collections.emptyMap())
                .setCreateOperationFields(Collections.emptyList())
                .setUpdateOperationFields(Collections.emptyList());
        ResponseEntity<GridPreset> response = this.restTemplate.exchange(
                "/plugins/GridPresets/updateGridPreset", HttpMethod.PUT, new HttpEntity<>(request), GridPreset.class);
        Assertions.assertEquals(200, response.getStatusCodeValue());
        gridPreset = response.getBody();
        Assertions.assertNotNull(gridPreset);
        Assertions.assertTrue(gridPreset.getFiltering().isEmpty());
        Assertions.assertTrue(gridPreset.getCreateOperationFields().isEmpty());
        Assertions.assertTrue(gridPreset.getUpdateOperationFields().isEmpty());
        assertPersistedOperationFields(gridPreset);
        Assertions.assertEquals(unfilteredDynamicExecutionId, getDynamicExecutionId(gridPreset));
    }


    private void assertPersistedOperationFields(GridPreset preset) {
        GridPreset persisted = gridPresetService.findByIdOrNull(GridPreset.class, preset.getId());
        Assertions.assertNotNull(persisted);
        List<GridPresetOperationField> fields = gridPresetOperationFieldService.listByGridPresets(List.of(persisted));
        Assertions.assertEquals(
                preset.getCreateOperationFields().size() + preset.getUpdateOperationFields().size(),
                fields.size());
        for (GridPresetOperationField field : fields) {
            Assertions.assertNotNull(field.getId());
            Assertions.assertNotNull(field.getGridPreset());
            Assertions.assertEquals(preset.getId(), field.getGridPreset().getId());
            Assertions.assertNotNull(field.getOperationType());
        }
        assertPriorities(fields.stream()
                .filter(f -> f.getOperationType() == GridPresetOperationType.CREATE)
                .toList());
        assertPriorities(fields.stream()
                .filter(f -> f.getOperationType() == GridPresetOperationType.UPDATE)
                .toList());
    }

    private void assertPriorities(List<GridPresetOperationField> fields) {
        for (int i = 0; i < fields.size(); i++) {
            Assertions.assertEquals(i, fields.get(i).getPriority());
        }
    }

    private void assertOperationFields(List<GridPresetOperationField> fields,
                                       List<String> paths,
                                       List<Boolean> visibility) {
        Assertions.assertEquals(paths.size(), fields.size());
        for (int i = 0; i < paths.size(); i++) {
            Assertions.assertEquals(paths.get(i), fields.get(i).getFieldPath());
            Assertions.assertEquals(visibility.get(i), fields.get(i).isVisible());
            Assertions.assertEquals(i, fields.get(i).getPriority());
        }
    }

    private List<String> operationFieldSignature(List<GridPresetOperationField> fields) {
        return fields.stream()
                .map(f -> f.getFieldPath() + "|" + f.isVisible() + "|" + f.getPriority())
                .toList();
    }

    private String getDynamicExecutionId(GridPreset preset) {
        GridPreset persisted = gridPresetService.findByIdOrNull(GridPreset.class, preset.getId());
        Assertions.assertNotNull(persisted);
        Assertions.assertNotNull(persisted.getDynamicExecution());
        return persisted.getDynamicExecution().getId();
    }

    private void assertGridPreset(GridPresetCreate request, GridPreset preset) {
        Assertions.assertNotNull(preset);
        Assertions.assertEquals(request.getName(), preset.getName());
        Assertions.assertEquals(request.getDynamicInvokerCanonicalName(), preset.getDynamicInvokerCanonicalName());
        Assertions.assertEquals(request.getDynamicInvokerMethodName(), preset.getDynamicInvokerMethodName());
        Assertions.assertEquals(GridPreset.class.getCanonicalName(), preset.getRelatedClassCanonicalName());
        Assertions.assertNotNull(preset.getFiltering());
        for (Map.Entry<String, Object> entry : request.any().entrySet()) {
            Assertions.assertEquals(entry.getValue(), preset.any().get(entry.getKey()));
        }
    }
}
