package com.flexicore.rules.controller;

import com.flexicore.request.AuthenticationRequest;
import com.flexicore.response.AuthenticationResponse;
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
import org.springframework.test.context.junit.jupiter.SpringExtension;

@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT, classes = App.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@ActiveProfiles("test")
public class ScenarioSavableEventControllerTest {

  private ScenarioSavableEvent testScenarioSavableEvent;
  @Autowired private TestRestTemplate restTemplate;

  @Autowired private ScenarioTrigger scenarioTrigger;

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
  public void testScenarioSavableEventCreate() {
    String name = UUID.randomUUID().toString();
    ScenarioSavableEventCreate request = new ScenarioSavableEventCreate().setName(name);

    request.setScenarioTriggerId(this.scenarioTrigger.getId());

    ResponseEntity<ScenarioSavableEvent> response =
        this.restTemplate.postForEntity(
            "/ScenarioSavableEvent/createScenarioSavableEvent",
            request,
            ScenarioSavableEvent.class);
    Assertions.assertEquals(200, response.getStatusCodeValue());
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
    Assertions.assertEquals(200, response.getStatusCodeValue());
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
    Assertions.assertEquals(200, response.getStatusCodeValue());
    testScenarioSavableEvent = response.getBody();
    assertScenarioSavableEvent(request, testScenarioSavableEvent);
  }
}
