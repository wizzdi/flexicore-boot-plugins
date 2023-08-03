package com.flexicore.rules.controller;



import com.flexicore.rules.App;
import com.flexicore.rules.model.ScenarioSavableEvent;
import com.flexicore.rules.model.ScenarioTrigger;
import com.flexicore.rules.request.ScenarioSavableEventCreate;
import com.flexicore.rules.request.ScenarioSavableEventFilter;
import com.flexicore.rules.request.ScenarioSavableEventUpdate;
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
public class ScenarioSavableEventControllerTest {

  private ScenarioSavableEvent testScenarioSavableEvent;
  @Autowired private TestRestTemplate restTemplate;

  @Autowired private ScenarioTrigger scenarioTrigger;

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
  public void testScenarioSavableEventCreate() {
    String name = UUID.randomUUID().toString();
    ScenarioSavableEventCreate request = new ScenarioSavableEventCreate().setName(name);

    request.setScenarioTriggerId(this.scenarioTrigger.getId());

    ResponseEntity<ScenarioSavableEvent> response =
        this.restTemplate.exchange(
            "/ScenarioSavableEvent/createScenarioSavableEvent",
            HttpMethod.POST,
            new HttpEntity<>(request),
            ScenarioSavableEvent.class);
    Assertions.assertTrue(response.getStatusCode().is2xxSuccessful() );
    testScenarioSavableEvent = response.getBody();
    assertScenarioSavableEvent(request, testScenarioSavableEvent);
  }

  @Test
  @Order(2)
  public void testListAllScenarioSavableEvents() {
    ScenarioSavableEventFilter request = new ScenarioSavableEventFilter();
    ParameterizedTypeReference<PaginationResponse<ScenarioSavableEvent>> t =
        new ParameterizedTypeReference<>() {};

    ResponseEntity<PaginationResponse<ScenarioSavableEvent>> response =
        this.restTemplate.exchange(
            "/ScenarioSavableEvent/getAllScenarioSavableEvents",
            HttpMethod.POST,
            new HttpEntity<>(request),
            t);
    Assertions.assertTrue(response.getStatusCode().is2xxSuccessful() );
    PaginationResponse<ScenarioSavableEvent> body = response.getBody();
    Assertions.assertNotNull(body);
    List<ScenarioSavableEvent> ScenarioSavableEvents = body.getList();
    Assertions.assertNotEquals(0, ScenarioSavableEvents.size());
    Assertions.assertTrue(
        ScenarioSavableEvents.stream()
            .anyMatch(f -> f.getId().equals(testScenarioSavableEvent.getId())));
  }

  public void assertScenarioSavableEvent(
      ScenarioSavableEventCreate request, ScenarioSavableEvent testScenarioSavableEvent) {
    Assertions.assertNotNull(testScenarioSavableEvent);

    if (request.getScenarioTriggerId() != null) {

      Assertions.assertNotNull(testScenarioSavableEvent.getScenarioTrigger());
      Assertions.assertEquals(
          request.getScenarioTriggerId(), testScenarioSavableEvent.getScenarioTrigger().getId());
    }
  }

  @Test
  @Order(3)
  public void testScenarioSavableEventUpdate() {
    String name = UUID.randomUUID().toString();
    ScenarioSavableEventUpdate request =
        new ScenarioSavableEventUpdate().setId(testScenarioSavableEvent.getId()).setName(name);
    ResponseEntity<ScenarioSavableEvent> response =
        this.restTemplate.exchange(
            "/ScenarioSavableEvent/updateScenarioSavableEvent",
            HttpMethod.PUT,
            new HttpEntity<>(request),
            ScenarioSavableEvent.class);
    Assertions.assertTrue(response.getStatusCode().is2xxSuccessful() );
    testScenarioSavableEvent = response.getBody();
    assertScenarioSavableEvent(request, testScenarioSavableEvent);
  }
}
