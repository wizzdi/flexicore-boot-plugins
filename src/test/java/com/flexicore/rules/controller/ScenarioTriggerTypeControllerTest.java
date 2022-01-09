package com.flexicore.rules.controller;

import com.flexicore.request.AuthenticationRequest;
import com.flexicore.response.AuthenticationResponse;
import com.flexicore.rules.App;
import com.flexicore.rules.model.ScenarioTriggerType;
import com.flexicore.rules.request.ScenarioTriggerTypeCreate;
import com.flexicore.rules.request.ScenarioTriggerTypeFilter;
import com.flexicore.rules.request.ScenarioTriggerTypeUpdate;
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
public class ScenarioTriggerTypeControllerTest {

  private ScenarioTriggerType testScenarioTriggerType;
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
  public void testScenarioTriggerTypeCreate() {
    String name = UUID.randomUUID().toString();
    ScenarioTriggerTypeCreate request = new ScenarioTriggerTypeCreate().setName(name);

    request.setEventCanonicalName("test-string");

    ResponseEntity<ScenarioTriggerType> response =
        this.restTemplate.postForEntity(
            "/ScenarioTriggerType/createScenarioTriggerType", request, ScenarioTriggerType.class);
    Assertions.assertEquals(200, response.getStatusCodeValue());
    testScenarioTriggerType = response.getBody();
    assertScenarioTriggerType(request, testScenarioTriggerType);
  }

  @Test
  @Order(2)
  public void testListAllScenarioTriggerTypes() {
    ScenarioTriggerTypeFilter request = new ScenarioTriggerTypeFilter();
    ParameterizedTypeReference<PaginationResponse<ScenarioTriggerType>> t =
        new ParameterizedTypeReference<>() {};

    ResponseEntity<PaginationResponse<ScenarioTriggerType>> response =
        this.restTemplate.exchange(
            "/ScenarioTriggerType/getAllScenarioTriggerTypes",
            HttpMethod.POST,
            new HttpEntity<>(request),
            t);
    Assertions.assertEquals(200, response.getStatusCodeValue());
    PaginationResponse<ScenarioTriggerType> body = response.getBody();
    Assertions.assertNotNull(body);
    List<ScenarioTriggerType> ScenarioTriggerTypes = body.getList();
    Assertions.assertNotEquals(0, ScenarioTriggerTypes.size());
    Assertions.assertTrue(
        ScenarioTriggerTypes.stream()
            .anyMatch(f -> f.getId().equals(testScenarioTriggerType.getId())));
  }

  public void assertScenarioTriggerType(
      ScenarioTriggerTypeCreate request, ScenarioTriggerType testScenarioTriggerType) {
    Assertions.assertNotNull(testScenarioTriggerType);

    if (request.getEventCanonicalName() != null) {

      Assertions.assertEquals(
          request.getEventCanonicalName(), testScenarioTriggerType.getEventCanonicalName());
    }
  }

  @Test
  @Order(3)
  public void testScenarioTriggerTypeUpdate() {
    String name = UUID.randomUUID().toString();
    ScenarioTriggerTypeUpdate request =
        new ScenarioTriggerTypeUpdate().setId(testScenarioTriggerType.getId()).setName(name);
    ResponseEntity<ScenarioTriggerType> response =
        this.restTemplate.exchange(
            "/ScenarioTriggerType/updateScenarioTriggerType",
            HttpMethod.PUT,
            new HttpEntity<>(request),
            ScenarioTriggerType.class);
    Assertions.assertEquals(200, response.getStatusCodeValue());
    testScenarioTriggerType = response.getBody();
    assertScenarioTriggerType(request, testScenarioTriggerType);
  }
}
