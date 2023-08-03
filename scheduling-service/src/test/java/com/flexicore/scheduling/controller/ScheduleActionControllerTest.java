package com.flexicore.scheduling.controller;



import com.flexicore.scheduling.model.ScheduleAction;
import com.flexicore.scheduling.App;
import com.flexicore.scheduling.request.ScheduleActionCreate;
import com.flexicore.scheduling.request.ScheduleActionFilter;
import com.flexicore.scheduling.request.ScheduleActionUpdate;
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
public class ScheduleActionControllerTest {

  private ScheduleAction testScheduleAction;
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
  public void testScheduleActionCreate() {
    String name = UUID.randomUUID().toString();
    ScheduleActionCreate request = new ScheduleActionCreate().setName(name);

    ResponseEntity<ScheduleAction> response =
        this.restTemplate.postForEntity(
            "/ScheduleAction/createScheduleAction", request, ScheduleAction.class);
    Assertions.assertTrue(response.getStatusCode().is2xxSuccessful() );
    testScheduleAction = response.getBody();
    assertScheduleAction(request, testScheduleAction);
  }

  @Test
  @Order(2)
  public void testListAllScheduleActions() {
    ScheduleActionFilter request = new ScheduleActionFilter();
    ParameterizedTypeReference<PaginationResponse<ScheduleAction>> t =
        new ParameterizedTypeReference<>() {};

    ResponseEntity<PaginationResponse<ScheduleAction>> response =
        this.restTemplate.exchange(
            "/ScheduleAction/getAllScheduleActions", HttpMethod.POST, new HttpEntity<>(request), t);
    Assertions.assertTrue(response.getStatusCode().is2xxSuccessful() );
    PaginationResponse<ScheduleAction> body = response.getBody();
    Assertions.assertNotNull(body);
    List<ScheduleAction> ScheduleActions = body.getList();
    Assertions.assertNotEquals(0, ScheduleActions.size());
    Assertions.assertTrue(
        ScheduleActions.stream().anyMatch(f -> f.getId().equals(testScheduleAction.getId())));
  }

  public void assertScheduleAction(
      ScheduleActionCreate request, ScheduleAction testScheduleAction) {
    Assertions.assertNotNull(testScheduleAction);
  }

  @Test
  @Order(3)
  public void testScheduleActionUpdate() {
    String name = UUID.randomUUID().toString();
    ScheduleActionUpdate request =
        new ScheduleActionUpdate().setId(testScheduleAction.getId()).setName(name);
    ResponseEntity<ScheduleAction> response =
        this.restTemplate.exchange(
            "/ScheduleAction/updateScheduleAction",
            HttpMethod.PUT,
            new HttpEntity<>(request),
            ScheduleAction.class);
    Assertions.assertTrue(response.getStatusCode().is2xxSuccessful() );
    testScheduleAction = response.getBody();
    assertScheduleAction(request, testScheduleAction);
  }
}
