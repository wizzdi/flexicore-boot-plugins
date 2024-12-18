package com.flexicore.ui.component.rest;

import com.flexicore.model.Clazz;
import com.flexicore.model.PermissionGroup;

import com.flexicore.model.PermissionGroupToBaseclass;
import com.flexicore.ui.component.model.UIComponent;
import com.flexicore.ui.component.request.UIComponentRegistrationContainer;
import com.flexicore.ui.component.request.UIComponentsRegistrationContainer;
import com.flexicore.ui.component.rest.app.App;
import com.wizzdi.flexicore.security.request.PermissionGroupFilter;
import com.wizzdi.flexicore.security.request.PermissionGroupToBaseclassFilter;
import com.wizzdi.flexicore.security.response.PaginationResponse;
import com.wizzdi.flexicore.security.service.PermissionGroupToBaseclassService;
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

import java.util.*;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT, classes = App.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@Testcontainers
@ActiveProfiles("test")
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE) // deactivate the default behaviour
public class UIComponentControllerTest {

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

    private static final String ADMIN = "admin";
    private static final String USER = "user";
    @Autowired
    private TestRestTemplate restTemplate;

    private List<UIComponent> uiComponents;
    @Autowired
    private PermissionGroupToBaseclassService permissionGroupToBaseclassService;
    private final AtomicReference<String> authenticationKey = new AtomicReference<>(null);

    @BeforeAll
    public void init() {
        authenticationKey.set(ADMIN);
        restTemplate.getRestTemplate().setInterceptors(
                Collections.singletonList((request, body, execution) -> {
                    request.getHeaders()
                            .add("authenticationKey", authenticationKey.get());
                    return execution.execute(request, body);
                }));
    }


    @Test
    @Order(1)
    public void testRegisterNewUIComponent() {

        UIComponentsRegistrationContainer uiComponentsRegistrationContainer = new UIComponentsRegistrationContainer();
        List<UIComponentRegistrationContainer> uiComponentRegistrationContainers = new ArrayList<>();
        Map<String, String> componentToPermissionGroups = new HashMap<>();
        componentToPermissionGroups.put("test1", "a,b,c");
        componentToPermissionGroups.put("test2", "b,c");
        componentToPermissionGroups.put("test3", "c");

        for (String externalId : Arrays.asList("test1", "test2", "test3")) {
            UIComponentRegistrationContainer uiComponentRegistrationContainer = new UIComponentRegistrationContainer();
            uiComponentRegistrationContainer.setExternalId(externalId);
            uiComponentRegistrationContainer.setDescription(externalId);
            uiComponentRegistrationContainer.setName(externalId);
            uiComponentRegistrationContainer.setGroups(componentToPermissionGroups.get(externalId));
        }
        uiComponentsRegistrationContainer.setComponentsToRegister(uiComponentRegistrationContainers);
        ParameterizedTypeReference<List<UIComponent>> t = new ParameterizedTypeReference<List<UIComponent>>() {
        };
        ResponseEntity<List<UIComponent>> uiComponentResponse = this.restTemplate.exchange("/uiComponent/registerAndGetAllowedUIComponents", HttpMethod.POST, new HttpEntity<>(uiComponentsRegistrationContainer), t);

        Assertions.assertEquals(200, uiComponentResponse.getStatusCodeValue());
        uiComponents = uiComponentResponse.getBody();
        Assertions.assertNotNull(uiComponents);
        assertUIComponents(uiComponentRegistrationContainers, uiComponents);

    }

    private void assertUIComponents(List<UIComponentRegistrationContainer> uiComponentRegistrationContainers, List<UIComponent> uiComponents) {
        Set<String> expected = uiComponentRegistrationContainers.stream().map(f -> f.getExternalId()).collect(Collectors.toSet());
        Set<String> actual = uiComponents.stream().map(f -> f.getExternalId()).collect(Collectors.toSet());
        Assertions.assertTrue(expected.containsAll(actual));
        Assertions.assertTrue(actual.containsAll(expected));
        Map<String, UIComponent> uiComponentMap = uiComponents.stream().collect(Collectors.toMap(f -> f.getExternalId(), f -> f, (a, b) -> a));
        for (UIComponentRegistrationContainer uiComponentRegistrationContainer : uiComponentRegistrationContainers) {
            UIComponent uiComponent = uiComponentMap.get(uiComponentRegistrationContainer.getExternalId());
            List<PermissionGroupToBaseclass> permissionGroupToBaseclasses = permissionGroupToBaseclassService.listAllPermissionGroupToBaseclass(new PermissionGroupToBaseclassFilter().setSecuredIds(Collections.singleton(uiComponent.getSecurityId())).setClazzes(Collections.singletonList(Clazz.ofClass(uiComponent.getClass()))), null);

            Set<String> actualGroups = permissionGroupToBaseclasses.stream().map(f -> f.getPermissionGroup().getExternalId()).collect(Collectors.toSet());
            Set<String> expectedGroups = Stream.of(uiComponentRegistrationContainer.getGroups().split(",")).collect(Collectors.toSet());
            Assertions.assertTrue(expectedGroups.containsAll(actualGroups));
            Assertions.assertTrue(actualGroups.containsAll(expectedGroups));
        }

    }


    @Test
    @Order(2)
    public void testRegisterExistingUIComponent() {

        UIComponentsRegistrationContainer uiComponentsRegistrationContainer = new UIComponentsRegistrationContainer();
        List<UIComponentRegistrationContainer> uiComponentRegistrationContainers = new ArrayList<>();
        Map<String, String> componentToPermissionGroups = new HashMap<>();
        componentToPermissionGroups.put("test1", "a,b,c");
        componentToPermissionGroups.put("test2", "b,c");
        componentToPermissionGroups.put("test3", "c");

        for (String externalId : Arrays.asList("test1", "test2", "test3")) {
            UIComponentRegistrationContainer uiComponentRegistrationContainer = new UIComponentRegistrationContainer();
            uiComponentRegistrationContainer.setExternalId(externalId);
            uiComponentRegistrationContainer.setDescription(externalId);
            uiComponentRegistrationContainer.setName(externalId);
            uiComponentRegistrationContainer.setGroups(componentToPermissionGroups.get(externalId));
        }
        uiComponentsRegistrationContainer.setComponentsToRegister(uiComponentRegistrationContainers);
        ParameterizedTypeReference<List<UIComponent>> t = new ParameterizedTypeReference<List<UIComponent>>() {
        };
        ResponseEntity<List<UIComponent>> uiComponentResponse = this.restTemplate.exchange("/uiComponent/registerAndGetAllowedUIComponents", HttpMethod.POST, new HttpEntity<>(uiComponentsRegistrationContainer), t);

        Assertions.assertEquals(200, uiComponentResponse.getStatusCodeValue());
        List<UIComponent> uiComponents = uiComponentResponse.getBody();
        Assertions.assertNotNull(uiComponents);
        Map<String, String> expected = this.uiComponents.stream().collect(Collectors.toMap(f -> f.getExternalId(), f -> f.getId()));
        Map<String, String> actual = uiComponents.stream().collect(Collectors.toMap(f -> f.getExternalId(), f -> f.getId()));
        Assertions.assertEquals(actual, expected);


    }


    @Test
    @Order(3)
    public void testRegistrationAsDifferentUser() {
        this.authenticationKey.set(USER);
        UIComponentsRegistrationContainer uiComponentsRegistrationContainer = new UIComponentsRegistrationContainer();
        List<UIComponentRegistrationContainer> uiComponentRegistrationContainers = new ArrayList<>();
        Map<String, String> componentToPermissionGroups = new HashMap<>();
        componentToPermissionGroups.put("test1", "a,b,c");
        componentToPermissionGroups.put("test2", "b,c");
        componentToPermissionGroups.put("test3", "c");

        for (String externalId : Arrays.asList("test1", "test2", "test3")) {
            UIComponentRegistrationContainer uiComponentRegistrationContainer = new UIComponentRegistrationContainer();
            uiComponentRegistrationContainer.setExternalId(externalId);
            uiComponentRegistrationContainer.setDescription(externalId);
            uiComponentRegistrationContainer.setName(externalId);
            uiComponentRegistrationContainer.setGroups(componentToPermissionGroups.get(externalId));
        }
        uiComponentsRegistrationContainer.setComponentsToRegister(uiComponentRegistrationContainers);
        ParameterizedTypeReference<List<UIComponent>> t = new ParameterizedTypeReference<List<UIComponent>>() {
        };
        ResponseEntity<List<UIComponent>> uiComponentResponse = this.restTemplate.exchange("/uiComponent/registerAndGetAllowedUIComponents", HttpMethod.POST, new HttpEntity<>(uiComponentsRegistrationContainer), t);

        Assertions.assertEquals(200, uiComponentResponse.getStatusCodeValue());
        List<UIComponent> uiComponents = uiComponentResponse.getBody();
        Assertions.assertNotNull(uiComponents);
        Assertions.assertTrue(uiComponents.isEmpty());

    }


}
