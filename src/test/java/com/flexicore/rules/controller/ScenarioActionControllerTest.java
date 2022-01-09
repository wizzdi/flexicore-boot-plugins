package com.flexicore.rules.controller;

import com.flexicore.request.AuthenticationRequest;
import com.flexicore.response.AuthenticationResponse;
import com.flexicore.rules.App;
import com.flexicore.rules.model.ScenarioAction;
import com.flexicore.rules.request.ScenarioActionCreate;
import com.flexicore.rules.request.ScenarioActionFilter;
import com.flexicore.rules.request.ScenarioActionUpdate;
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
public class ScenarioActionControllerTest {

  private ScenarioAction testScenarioAction;
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
  public void testScenarioActionCreate() {
    String name = UUID.randomUUID().toString();
    ScenarioActionCreate request = new ScenarioActionCreate().setName(name);

    ResponseEntity<ScenarioAction> response =
        this.restTemplate.postForEntity(
            "/ScenarioAction/createScenarioAction", request, ScenarioAction.class);
    Assertions.assertEquals(200, response.getStatusCodeValue());
    testScenarioAction = response.getBody();
    assertScenarioAction(request, testScenarioAction);
  }

  @Test
  @Order(2)
  public void testListAllScenarioActions() {
    ScenarioActionFilter request = new ScenarioActionFilter();
    ParameterizedTypeReference<PaginationResponse<ScenarioAction>> t =
        new ParameterizedTypeReference<>() {};

    ResponseEntity<PaginationResponse<ScenarioAction>> response =
        this.restTemplate.exchange(
            "/ScenarioAction/getAllScenarioActions", HttpMethod.POST, new HttpEntity<>(request), t);
    Assertions.assertEquals(200, response.getStatusCodeValue());
    PaginationResponse<ScenarioAction> body = response.getBody();
    Assertions.assertNotNull(body);
    List<ScenarioAction> ScenarioActions = body.getList();
    Assertions.assertNotEquals(0, ScenarioActions.size());
    Assertions.assertTrue(
        ScenarioActions.stream().anyMatch(f -> f.getId().equals(testScenarioAction.getId())));
  }

  public void assertScenarioAction(
      ScenarioActionCreate request, ScenarioAction testScenarioAction) {
    Assertions.assertNotNull(testScenarioAction);
  }

  @Test
  @Order(3)
  public void testScenarioActionUpdate() {
    String name = UUID.randomUUID().toString();
    ScenarioActionUpdate request =
        new ScenarioActionUpdate().setId(testScenarioAction.getId()).setName(name);
    ResponseEntity<ScenarioAction> response =
        this.restTemplate.exchange(
            "/ScenarioAction/updateScenarioAction",
            HttpMethod.PUT,
            new HttpEntity<>(request),
            ScenarioAction.class);
    Assertions.assertEquals(200, response.getStatusCodeValue());
    testScenarioAction = response.getBody();
    assertScenarioAction(request, testScenarioAction);
  }
}
