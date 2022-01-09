package com.flexicore.rules.controller;

import com.flexicore.request.AuthenticationRequest;
import com.flexicore.response.AuthenticationResponse;
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
import org.springframework.test.context.junit.jupiter.SpringExtension;

@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT, classes = App.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@ActiveProfiles("test")
public class ScenarioTriggerControllerTest {

  private ScenarioTrigger testScenarioTrigger;
  @Autowired private TestRestTemplate restTemplate;

  @Autowired private ScenarioTriggerType scenarioTriggerType;

  @BeforeAll
  private void init() {
    ResponseEntity<AuthenticationResponse> authenticationResponse =
        this.restTemplate.postForEntity(
            "/FlexiCore/rest/authenticationNew/login",
            new AuthenticationRequest().setEmail("admin@flexicore.com").setPassword("admin"),
            AuthenticationResponse.class);
    String authenticationKey = authenticationResponse.getBody().getAuthenticationKey();
    restTemplate
        .getRestTemplate()
        .setInterceptors(
            Collections.singletonList(
                (request, body, execution) -> {
                  request.getHeaders().add("authenticationKey", authenticationKey);
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

    request.setValidTill(OffsetDateTime.now());

    request.setScenarioTriggerTypeId(this.scenarioTriggerType.getId());

    request.setActiveTill(OffsetDateTime.now());

    request.setActiveMs(10L);

    ResponseEntity<ScenarioTrigger> response =
        this.restTemplate.postForEntity(
            "/ScenarioTrigger/createScenarioTrigger", request, ScenarioTrigger.class);
    Assertions.assertEquals(200, response.getStatusCodeValue());
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
    Assertions.assertEquals(200, response.getStatusCodeValue());
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

    if (request.getLastActivated() != null) {

      Assertions.assertEquals(request.getLastActivated().atZoneSameInstant(ZoneId.systemDefault()), testScenarioTrigger.getLastActivated().atZoneSameInstant(ZoneId.systemDefault()));
    }

    if (request.getValidFrom() != null) {

      Assertions.assertEquals(request.getValidFrom().atZoneSameInstant(ZoneId.systemDefault()), testScenarioTrigger.getValidFrom().atZoneSameInstant(ZoneId.systemDefault()));
    }

    if (request.getCooldownIntervalMs() != null) {

      Assertions.assertEquals(
          request.getCooldownIntervalMs(), testScenarioTrigger.getCooldownIntervalMs());
    }

    if (request.getValidTill() != null) {

      Assertions.assertEquals(request.getValidTill().atZoneSameInstant(ZoneId.systemDefault()), testScenarioTrigger.getValidTill().atZoneSameInstant(ZoneId.systemDefault()));
    }

    if (request.getScenarioTriggerTypeId() != null) {

      Assertions.assertNotNull(testScenarioTrigger.getScenarioTriggerType());
      Assertions.assertEquals(
          request.getScenarioTriggerTypeId(), testScenarioTrigger.getScenarioTriggerType().getId());
    }

    if (request.getActiveTill() != null) {

      Assertions.assertEquals(request.getActiveTill().atZoneSameInstant(ZoneId.systemDefault()), testScenarioTrigger.getActiveTill().atZoneSameInstant(ZoneId.systemDefault()));
    }

    if (request.getActiveMs() != null) {

      Assertions.assertEquals(request.getActiveMs(), testScenarioTrigger.getActiveMs());
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
    Assertions.assertEquals(200, response.getStatusCodeValue());
    testScenarioTrigger = response.getBody();
    assertScenarioTrigger(request, testScenarioTrigger);
  }
}
