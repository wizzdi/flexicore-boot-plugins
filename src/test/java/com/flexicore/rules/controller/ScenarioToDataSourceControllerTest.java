package com.flexicore.rules.controller;

import com.flexicore.request.AuthenticationRequest;
import com.flexicore.response.AuthenticationResponse;
import com.flexicore.rules.App;
import com.flexicore.rules.model.DataSource;
import com.flexicore.rules.model.Scenario;
import com.flexicore.rules.model.ScenarioToDataSource;
import com.flexicore.rules.request.ScenarioToDataSourceCreate;
import com.flexicore.rules.request.ScenarioToDataSourceFilter;
import com.flexicore.rules.request.ScenarioToDataSourceUpdate;
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
public class ScenarioToDataSourceControllerTest {

  private ScenarioToDataSource testScenarioToDataSource;
  @Autowired private TestRestTemplate restTemplate;

  @Autowired private DataSource dataSource;

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
  public void testScenarioToDataSourceCreate() {
    String name = UUID.randomUUID().toString();
    ScenarioToDataSourceCreate request = new ScenarioToDataSourceCreate().setName(name);

    request.setEnabled(true);

    request.setDataSourceId(this.dataSource.getId());

    request.setOrdinal(10);

    request.setScenarioId(this.scenario.getId());

    ResponseEntity<ScenarioToDataSource> response =
        this.restTemplate.postForEntity(
            "/ScenarioToDataSource/createScenarioToDataSource",
            request,
            ScenarioToDataSource.class);
    Assertions.assertEquals(200, response.getStatusCodeValue());
    testScenarioToDataSource = response.getBody();
    assertScenarioToDataSource(request, testScenarioToDataSource);
  }

  @Test
  @Order(2)
  public void testListAllScenarioToDataSources() {
    ScenarioToDataSourceFilter request = new ScenarioToDataSourceFilter();
    ParameterizedTypeReference<PaginationResponse<ScenarioToDataSource>> t =
        new ParameterizedTypeReference<>() {};

    ResponseEntity<PaginationResponse<ScenarioToDataSource>> response =
        this.restTemplate.exchange(
            "/ScenarioToDataSource/getAllScenarioToDataSources",
            HttpMethod.POST,
            new HttpEntity<>(request),
            t);
    Assertions.assertEquals(200, response.getStatusCodeValue());
    PaginationResponse<ScenarioToDataSource> body = response.getBody();
    Assertions.assertNotNull(body);
    List<ScenarioToDataSource> ScenarioToDataSources = body.getList();
    Assertions.assertNotEquals(0, ScenarioToDataSources.size());
    Assertions.assertTrue(
        ScenarioToDataSources.stream()
            .anyMatch(f -> f.getId().equals(testScenarioToDataSource.getId())));
  }

  public void assertScenarioToDataSource(
      ScenarioToDataSourceCreate request, ScenarioToDataSource testScenarioToDataSource) {
    Assertions.assertNotNull(testScenarioToDataSource);

    if (request.isEnabled() != null) {

      Assertions.assertEquals(request.isEnabled(), testScenarioToDataSource.isEnabled());
    }

    if (request.getDataSourceId() != null) {

      Assertions.assertNotNull(testScenarioToDataSource.getDataSource());
      Assertions.assertEquals(
          request.getDataSourceId(), testScenarioToDataSource.getDataSource().getId());
    }

    if (request.getOrdinal() != null) {

      Assertions.assertEquals(request.getOrdinal(), testScenarioToDataSource.getOrdinal());
    }

    if (request.getScenarioId() != null) {

      Assertions.assertNotNull(testScenarioToDataSource.getScenario());
      Assertions.assertEquals(
          request.getScenarioId(), testScenarioToDataSource.getScenario().getId());
    }
  }

  @Test
  @Order(3)
  public void testScenarioToDataSourceUpdate() {
    String name = UUID.randomUUID().toString();
    ScenarioToDataSourceUpdate request =
        new ScenarioToDataSourceUpdate().setId(testScenarioToDataSource.getId()).setName(name);
    ResponseEntity<ScenarioToDataSource> response =
        this.restTemplate.exchange(
            "/ScenarioToDataSource/updateScenarioToDataSource",
            HttpMethod.PUT,
            new HttpEntity<>(request),
            ScenarioToDataSource.class);
    Assertions.assertEquals(200, response.getStatusCodeValue());
    testScenarioToDataSource = response.getBody();
    assertScenarioToDataSource(request, testScenarioToDataSource);
  }
}
