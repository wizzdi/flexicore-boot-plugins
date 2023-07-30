package com.flexicore.rules.controller;

import com.flexicore.request.AuthenticationRequest;
import com.flexicore.response.AuthenticationResponse;
import com.flexicore.rules.App;
import com.flexicore.rules.model.Scenario;
import com.flexicore.rules.model.ScenarioToTrigger;
import com.flexicore.rules.model.ScenarioTrigger;
import com.flexicore.rules.request.ScenarioToTriggerCreate;
import com.flexicore.rules.request.ScenarioToTriggerFilter;
import com.flexicore.rules.request.ScenarioToTriggerUpdate;
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
import org.springframework.test.context.junit.jupiter.SpringExtension;

@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT, classes = App.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@ActiveProfiles("test")
public class ScenarioToTriggerControllerTest {

  private ScenarioToTrigger testScenarioToTrigger;
  @Autowired private TestRestTemplate restTemplate;

  @Autowired private ScenarioTrigger scenarioTrigger;

  @Autowired private Scenario scenario;

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
  public void testScenarioToTriggerCreate() {
    String name = UUID.randomUUID().toString();
    ScenarioToTriggerCreate request = new ScenarioToTriggerCreate().setName(name);

    request.setFiring(true);

    request.setScenarioTriggerId(this.scenarioTrigger.getId());

    request.setScenarioId(this.scenario.getId());

    request.setOrdinal(10);

    request.setEnabled(true);

    ResponseEntity<ScenarioToTrigger> response =
        this.restTemplate.postForEntity(
            "/ScenarioToTrigger/createScenarioToTrigger", request, ScenarioToTrigger.class);
    Assertions.assertEquals(200, response.getStatusCodeValue());
    testScenarioToTrigger = response.getBody();
    assertScenarioToTrigger(request, testScenarioToTrigger);
  }

  @Test
  @Order(2)
  public void testListAllScenarioToTriggers() {
    ScenarioToTriggerFilter request = new ScenarioToTriggerFilter();
    ParameterizedTypeReference<PaginationResponse<ScenarioToTrigger>> t =
        new ParameterizedTypeReference<>() {};

    ResponseEntity<PaginationResponse<ScenarioToTrigger>> response =
        this.restTemplate.exchange(
            "/ScenarioToTrigger/getAllScenarioToTriggers",
            HttpMethod.POST,
            new HttpEntity<>(request),
            t);
    Assertions.assertEquals(200, response.getStatusCodeValue());
    PaginationResponse<ScenarioToTrigger> body = response.getBody();
    Assertions.assertNotNull(body);
    List<ScenarioToTrigger> ScenarioToTriggers = body.getList();
    Assertions.assertNotEquals(0, ScenarioToTriggers.size());
    Assertions.assertTrue(
        ScenarioToTriggers.stream().anyMatch(f -> f.getId().equals(testScenarioToTrigger.getId())));
  }

  public void assertScenarioToTrigger(
      ScenarioToTriggerCreate request, ScenarioToTrigger testScenarioToTrigger) {
    Assertions.assertNotNull(testScenarioToTrigger);

    if (request.getFiring() != null) {
      Assertions.assertEquals(request.getFiring(), testScenarioToTrigger.isFiring());
    }

    if (request.getScenarioTriggerId() != null) {

      Assertions.assertNotNull(testScenarioToTrigger.getScenarioTrigger());
      Assertions.assertEquals(
          request.getScenarioTriggerId(), testScenarioToTrigger.getScenarioTrigger().getId());
    }

    if (request.getScenarioId() != null) {

      Assertions.assertNotNull(testScenarioToTrigger.getScenario());
      Assertions.assertEquals(request.getScenarioId(), testScenarioToTrigger.getScenario().getId());
    }

    if (request.getOrdinal() != null) {
      Assertions.assertEquals(request.getOrdinal(), testScenarioToTrigger.getOrdinal());
    }

    if (request.getEnabled() != null) {
      Assertions.assertEquals(request.getEnabled(), testScenarioToTrigger.isEnabled());
    }
  }

  @Test
  @Order(3)
  public void testScenarioToTriggerUpdate() {
    String name = UUID.randomUUID().toString();
    ScenarioToTriggerUpdate request =
        new ScenarioToTriggerUpdate().setId(testScenarioToTrigger.getId()).setName(name);
    ResponseEntity<ScenarioToTrigger> response =
        this.restTemplate.exchange(
            "/ScenarioToTrigger/updateScenarioToTrigger",
            HttpMethod.PUT,
            new HttpEntity<>(request),
            ScenarioToTrigger.class);
    Assertions.assertEquals(200, response.getStatusCodeValue());
    testScenarioToTrigger = response.getBody();
    assertScenarioToTrigger(request, testScenarioToTrigger);
  }
}
