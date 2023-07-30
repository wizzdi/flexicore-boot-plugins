package com.flexicore.rules.controller;

import com.flexicore.request.AuthenticationRequest;
import com.flexicore.response.AuthenticationResponse;
import com.flexicore.rules.App;
import com.flexicore.rules.model.Scenario;
import com.flexicore.rules.request.ScenarioCreate;
import com.flexicore.rules.request.ScenarioFilter;
import com.flexicore.rules.request.ScenarioUpdate;
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
public class ScenarioControllerTest {

  private Scenario testScenario;
  @Autowired private TestRestTemplate restTemplate;

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
  public void testScenarioCreate() {
    String name = UUID.randomUUID().toString();
    ScenarioCreate request = new ScenarioCreate().setName(name);

    request.setScenarioHint("test-string");

    ResponseEntity<Scenario> response =
        this.restTemplate.postForEntity("/Scenario/createScenario", request, Scenario.class);
    Assertions.assertEquals(200, response.getStatusCodeValue());
    testScenario = response.getBody();
    assertScenario(request, testScenario);
  }

  @Test
  @Order(2)
  public void testListAllScenarios() {
    ScenarioFilter request = new ScenarioFilter();
    ParameterizedTypeReference<PaginationResponse<Scenario>> t =
        new ParameterizedTypeReference<>() {};

    ResponseEntity<PaginationResponse<Scenario>> response =
        this.restTemplate.exchange(
            "/Scenario/getAllScenarios", HttpMethod.POST, new HttpEntity<>(request), t);
    Assertions.assertEquals(200, response.getStatusCodeValue());
    PaginationResponse<Scenario> body = response.getBody();
    Assertions.assertNotNull(body);
    List<Scenario> Scenarios = body.getList();
    Assertions.assertNotEquals(0, Scenarios.size());
    Assertions.assertTrue(Scenarios.stream().anyMatch(f -> f.getId().equals(testScenario.getId())));
  }

  public void assertScenario(ScenarioCreate request, Scenario testScenario) {
    Assertions.assertNotNull(testScenario);

    if (request.getScenarioHint() != null) {
      Assertions.assertEquals(request.getScenarioHint(), testScenario.getScenarioHint());
    }
  }

  @Test
  @Order(3)
  public void testScenarioUpdate() {
    String name = UUID.randomUUID().toString();
    ScenarioUpdate request = new ScenarioUpdate().setId(testScenario.getId()).setName(name);
    ResponseEntity<Scenario> response =
        this.restTemplate.exchange(
            "/Scenario/updateScenario", HttpMethod.PUT, new HttpEntity<>(request), Scenario.class);
    Assertions.assertEquals(200, response.getStatusCodeValue());
    testScenario = response.getBody();
    assertScenario(request, testScenario);
  }
}
