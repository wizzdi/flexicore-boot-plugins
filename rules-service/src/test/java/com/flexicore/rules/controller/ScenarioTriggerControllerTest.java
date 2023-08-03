package com.flexicore.rules.controller;



import com.flexicore.rules.App;
import com.flexicore.rules.model.ScenarioTrigger;
import com.flexicore.rules.model.ScenarioTriggerType;
import com.flexicore.rules.request.ScenarioTriggerCreate;
import com.flexicore.rules.request.ScenarioTriggerFilter;
import com.flexicore.rules.request.ScenarioTriggerUpdate;
import com.wizzdi.flexicore.security.response.PaginationResponse;
import java.time.OffsetDateTime;
import java.time.ZoneId;
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
public class ScenarioTriggerControllerTest {

  private ScenarioTrigger testScenarioTrigger;
  @Autowired private TestRestTemplate restTemplate;

  @Autowired private ScenarioTriggerType scenarioTriggerType;

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
  public void testScenarioTriggerCreate() {
    String name = UUID.randomUUID().toString();
    ScenarioTriggerCreate request = new ScenarioTriggerCreate().setName(name);

    request.setLastEventId("test-string");


    request.setValidFrom(OffsetDateTime.now());

    request.setCooldownIntervalMs(10L);

    request.setActiveTill(OffsetDateTime.now());

    request.setActiveMs(10L);

    request.setScenarioTriggerTypeId(this.scenarioTriggerType.getId());

    request.setValidTill(OffsetDateTime.now());

    ResponseEntity<ScenarioTrigger> response =
            this.restTemplate.postForEntity(
                    "/ScenarioTrigger/createScenarioTrigger", request, ScenarioTrigger.class);
    Assertions.assertTrue(response.getStatusCode().is2xxSuccessful() );
    testScenarioTrigger = response.getBody();
    assertScenarioTrigger(request, testScenarioTrigger);
  }

  @Test
  @Order(2)
  public void testListAllScenarioTriggers() {
    ScenarioTriggerFilter request = new ScenarioTriggerFilter();
    ParameterizedTypeReference<PaginationResponse<ScenarioTrigger>> t =
            new ParameterizedTypeReference<>() {};

    ResponseEntity<PaginationResponse<ScenarioTrigger>> response =
            this.restTemplate.exchange(
                    "/ScenarioTrigger/getAllScenarioTriggers",
                    HttpMethod.POST,
                    new HttpEntity<>(request),
                    t);
    Assertions.assertTrue(response.getStatusCode().is2xxSuccessful() );
    PaginationResponse<ScenarioTrigger> body = response.getBody();
    Assertions.assertNotNull(body);
    List<ScenarioTrigger> ScenarioTriggers = body.getList();
    Assertions.assertNotEquals(0, ScenarioTriggers.size());
    Assertions.assertTrue(
            ScenarioTriggers.stream().anyMatch(f -> f.getId().equals(testScenarioTrigger.getId())));
  }

  public void assertScenarioTrigger(
          ScenarioTriggerCreate request, ScenarioTrigger testScenarioTrigger) {
    Assertions.assertNotNull(testScenarioTrigger);

    if (request.getLastEventId() != null) {
      Assertions.assertEquals(request.getLastEventId(), testScenarioTrigger.getLastEventId());
    }


    if (request.getValidFrom() != null) {
      Assertions.assertEquals(request.getValidFrom().atZoneSameInstant(ZoneId.systemDefault()), testScenarioTrigger.getValidFrom().atZoneSameInstant(ZoneId.systemDefault()));
    }

    if (request.getCooldownIntervalMs() != null) {
      Assertions.assertEquals(
              request.getCooldownIntervalMs(), testScenarioTrigger.getCooldownIntervalMs());
    }

    if (request.getActiveTill() != null) {
      Assertions.assertEquals(request.getActiveTill().atZoneSameInstant(ZoneId.systemDefault()), testScenarioTrigger.getActiveTill().atZoneSameInstant(ZoneId.systemDefault()));
    }

    if (request.getActiveMs() != null) {
      Assertions.assertEquals(request.getActiveMs(), testScenarioTrigger.getActiveMs());
    }

    if (request.getScenarioTriggerTypeId() != null) {

      Assertions.assertNotNull(testScenarioTrigger.getScenarioTriggerType());
      Assertions.assertEquals(
              request.getScenarioTriggerTypeId(), testScenarioTrigger.getScenarioTriggerType().getId());
    }

    if (request.getValidTill() != null) {
      Assertions.assertEquals(request.getValidTill().atZoneSameInstant(ZoneId.systemDefault()), testScenarioTrigger.getValidTill().atZoneSameInstant(ZoneId.systemDefault()));
    }
  }

  @Test
  @Order(3)
  public void testScenarioTriggerUpdate() {
    String name = UUID.randomUUID().toString();
    ScenarioTriggerUpdate request =
            new ScenarioTriggerUpdate().setId(testScenarioTrigger.getId()).setName(name);
    ResponseEntity<ScenarioTrigger> response =
            this.restTemplate.exchange(
                    "/ScenarioTrigger/updateScenarioTrigger",
                    HttpMethod.PUT,
                    new HttpEntity<>(request),
                    ScenarioTrigger.class);
    Assertions.assertTrue(response.getStatusCode().is2xxSuccessful() );
    testScenarioTrigger = response.getBody();
    assertScenarioTrigger(request, testScenarioTrigger);
  }
}
