package com.flexicore.rules.controller;



import com.flexicore.rules.App;
import com.flexicore.rules.model.Scenario;
import com.flexicore.rules.model.ScenarioAction;
import com.flexicore.rules.model.ScenarioToAction;
import com.flexicore.rules.request.ScenarioToActionCreate;
import com.flexicore.rules.request.ScenarioToActionFilter;
import com.flexicore.rules.request.ScenarioToActionUpdate;
import com.wizzdi.flexicore.security.response.PaginationResponse;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
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

@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT, classes = App.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@Testcontainers
@ActiveProfiles("test")
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE) // deactivate the default behaviour
public class ScenarioToActionControllerTest {

  private ScenarioToAction testScenarioToAction;
  @Autowired private TestRestTemplate restTemplate;

  @Autowired private ScenarioAction scenarioAction;

  @Autowired private Scenario scenario;

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
  public void testScenarioToActionCreate() {
    String name = UUID.randomUUID().toString();
    ScenarioToActionCreate request = new ScenarioToActionCreate().setName(name);

    request.setEnabled(true);

    request.setScenarioActionId(this.scenarioAction.getId());

    request.setScenarioId(this.scenario.getId());

    ResponseEntity<ScenarioToAction> response =
        this.restTemplate.postForEntity(
            "/ScenarioToAction/createScenarioToAction", request, ScenarioToAction.class);
    Assertions.assertTrue(response.getStatusCode().is2xxSuccessful() );
    testScenarioToAction = response.getBody();
    assertScenarioToAction(request, testScenarioToAction);
  }

  @Test
  @Order(2)
  public void testListAllScenarioToActions() {
    ScenarioToActionFilter request = new ScenarioToActionFilter();
    ParameterizedTypeReference<PaginationResponse<ScenarioToAction>> t =
        new ParameterizedTypeReference<>() {};

    ResponseEntity<PaginationResponse<ScenarioToAction>> response =
        this.restTemplate.exchange(
            "/ScenarioToAction/getAllScenarioToActions",
            HttpMethod.POST,
            new HttpEntity<>(request),
            t);
    Assertions.assertTrue(response.getStatusCode().is2xxSuccessful() );
    PaginationResponse<ScenarioToAction> body = response.getBody();
    Assertions.assertNotNull(body);
    List<ScenarioToAction> ScenarioToActions = body.getList();
    Assertions.assertNotEquals(0, ScenarioToActions.size());
    Assertions.assertTrue(
        ScenarioToActions.stream().anyMatch(f -> f.getId().equals(testScenarioToAction.getId())));
  }

  public void assertScenarioToAction(
      ScenarioToActionCreate request, ScenarioToAction testScenarioToAction) {
    Assertions.assertNotNull(testScenarioToAction);

    if (request.getEnabled() != null) {
      Assertions.assertEquals(request.getEnabled(), testScenarioToAction.isEnabled());
    }

    if (request.getScenarioActionId() != null) {

      Assertions.assertNotNull(testScenarioToAction.getScenarioAction());
      Assertions.assertEquals(
          request.getScenarioActionId(), testScenarioToAction.getScenarioAction().getId());
    }

    if (request.getScenarioId() != null) {

      Assertions.assertNotNull(testScenarioToAction.getScenario());
      Assertions.assertEquals(request.getScenarioId(), testScenarioToAction.getScenario().getId());
    }
  }

  @Test
  @Order(3)
  public void testScenarioToActionUpdate() {
    String name = UUID.randomUUID().toString();
    ScenarioToActionUpdate request =
        new ScenarioToActionUpdate().setId(testScenarioToAction.getId()).setName(name);
    ResponseEntity<ScenarioToAction> response =
        this.restTemplate.exchange(
            "/ScenarioToAction/updateScenarioToAction",
            HttpMethod.PUT,
            new HttpEntity<>(request),
            ScenarioToAction.class);
    Assertions.assertTrue(response.getStatusCode().is2xxSuccessful() );
    testScenarioToAction = response.getBody();
    assertScenarioToAction(request, testScenarioToAction);
  }
}
