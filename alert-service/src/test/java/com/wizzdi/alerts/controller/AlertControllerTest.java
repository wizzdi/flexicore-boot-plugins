package com.wizzdi.alerts.controller;

import com.wizzdi.alerts.Alert;
import com.wizzdi.alerts.AlertLevel;
import com.wizzdi.alerts.app.App;
import com.wizzdi.alerts.request.AlertCreate;
import com.wizzdi.alerts.request.AlertFilter;
import com.wizzdi.alerts.request.AlertUpdate;

import com.wizzdi.flexicore.security.response.PaginationResponse;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Testcontainers;

@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT, classes = App.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@Testcontainers
@ActiveProfiles("test")
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE) // deactivate the default behaviour
public class AlertControllerTest {
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

  private Alert testAlert;
  @Autowired private TestRestTemplate restTemplate;

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
  @Disabled
  public void testAlertCreate() {
    AlertCreate request = new AlertCreate().setName(UUID.randomUUID().toString());

    request.setAlertCategory("test-string");

    request.setAlertLevel(AlertLevel.WARNING);

    request.setAlertContent("test-string");

    request.setRelatedId("test-string");

    request.setRelatedType("test-string");

    ResponseEntity<Alert> response =
        this.restTemplate.postForEntity("/Alert/createAlert", request, Alert.class);
    Assertions.assertEquals(200, response.getStatusCodeValue());
    testAlert = response.getBody();
    assertAlert(request, testAlert);
  }

  @Test
  @Order(2)
  @Disabled
  public void testListAllAlerts() {
    AlertFilter request = new AlertFilter();
    ParameterizedTypeReference<PaginationResponse<Alert>> t = new ParameterizedTypeReference<>() {};

    ResponseEntity<PaginationResponse<Alert>> response =
        this.restTemplate.exchange(
            "/Alert/getAllAlerts", HttpMethod.POST, new HttpEntity<>(request), t);
    Assertions.assertEquals(200, response.getStatusCodeValue());
    PaginationResponse<Alert> body = response.getBody();
    Assertions.assertNotNull(body);
    List<Alert> Alerts = body.getList();
    Assertions.assertNotEquals(0, Alerts.size());
    Assertions.assertTrue(Alerts.stream().anyMatch(f -> f.getId().equals(testAlert.getId())));
  }

  public void assertAlert(AlertCreate request, Alert testAlert) {
    Assertions.assertNotNull(testAlert);

    if (request.getAlertCategory() != null) {
      Assertions.assertEquals(request.getAlertCategory(), testAlert.getAlertCategory());
    }

    if (request.getAlertLevel() != null) {
      Assertions.assertEquals(request.getAlertLevel(), testAlert.getAlertLevel());
    }

    if (request.getAlertContent() != null) {
      Assertions.assertEquals(request.getAlertContent(), testAlert.getAlertContent());
    }

    if (request.getRelatedId() != null) {
      Assertions.assertEquals(request.getRelatedId(), testAlert.getRelatedId());
    }

    if (request.getRelatedType() != null) {
      Assertions.assertEquals(request.getRelatedType(), testAlert.getRelatedType());
    }
  }

  @Test
  @Order(3)
  @Disabled
  public void testAlertUpdate() {
    AlertUpdate request =
        new AlertUpdate().setId(testAlert.getId()).setName(UUID.randomUUID().toString());
    ResponseEntity<Alert> response =
        this.restTemplate.exchange(
            "/Alert/updateAlert", HttpMethod.PUT, new HttpEntity<>(request), Alert.class);
    Assertions.assertEquals(200, response.getStatusCodeValue());
    testAlert = response.getBody();
    assertAlert(request, testAlert);
  }
}
