package com.flexicore.rules.controller;



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
public class ScenarioTriggerTypeControllerTest {

  private ScenarioTriggerType testScenarioTriggerType;
  @Autowired private TestRestTemplate restTemplate;

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
  public void testScenarioTriggerTypeCreate() {
    String name = UUID.randomUUID().toString();
    ScenarioTriggerTypeCreate request = new ScenarioTriggerTypeCreate().setName(name);

    request.setEventCanonicalName("test-string");

    ResponseEntity<ScenarioTriggerType> response =
        this.restTemplate.postForEntity(
            "/ScenarioTriggerType/createScenarioTriggerType", request, ScenarioTriggerType.class);
    Assertions.assertTrue(response.getStatusCode().is2xxSuccessful() );
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
    Assertions.assertTrue(response.getStatusCode().is2xxSuccessful() );
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
    Assertions.assertTrue(response.getStatusCode().is2xxSuccessful() );
    testScenarioTriggerType = response.getBody();
    assertScenarioTriggerType(request, testScenarioTriggerType);
  }
}
