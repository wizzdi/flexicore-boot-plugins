package com.flexicore.rules.controller;



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
public class ScenarioToDataSourceControllerTest {

  private ScenarioToDataSource testScenarioToDataSource;
  @Autowired private TestRestTemplate restTemplate;

  @Autowired private DataSource dataSource;

  @Autowired private Scenario scenario;

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
    Assertions.assertTrue(response.getStatusCode().is2xxSuccessful() );
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
    Assertions.assertTrue(response.getStatusCode().is2xxSuccessful() );
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

    if (request.getEnabled() != null) {
      Assertions.assertEquals(request.getEnabled(), testScenarioToDataSource.isEnabled());
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
    Assertions.assertTrue(response.getStatusCode().is2xxSuccessful() );
    testScenarioToDataSource = response.getBody();
    assertScenarioToDataSource(request, testScenarioToDataSource);
  }
}
