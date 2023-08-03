package com.flexicore.scheduling.controller;



import com.flexicore.scheduling.model.ScheduleToAction;
import com.flexicore.scheduling.App;
import com.flexicore.scheduling.request.ScheduleToActionCreate;
import com.flexicore.scheduling.request.ScheduleToActionFilter;
import com.flexicore.scheduling.request.ScheduleToActionUpdate;
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
public class ScheduleToActionControllerTest {

  private ScheduleToAction testScheduleToAction;
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
  public void testScheduleToActionCreate() {
    String name = UUID.randomUUID().toString();
    ScheduleToActionCreate request = new ScheduleToActionCreate().setName(name);

    ResponseEntity<ScheduleToAction> response =
        this.restTemplate.postForEntity(
            "/ScheduleToAction/createScheduleToAction", request, ScheduleToAction.class);
    Assertions.assertTrue(response.getStatusCode().is2xxSuccessful() );
    testScheduleToAction = response.getBody();
    assertScheduleToAction(request, testScheduleToAction);
  }

  @Test
  @Order(2)
  public void testListAllScheduleToActions() {
    ScheduleToActionFilter request = new ScheduleToActionFilter();
    ParameterizedTypeReference<PaginationResponse<ScheduleToAction>> t =
        new ParameterizedTypeReference<>() {};

    ResponseEntity<PaginationResponse<ScheduleToAction>> response =
        this.restTemplate.exchange(
            "/ScheduleToAction/getAllScheduleToActions",
            HttpMethod.POST,
            new HttpEntity<>(request),
            t);
    Assertions.assertTrue(response.getStatusCode().is2xxSuccessful() );
    PaginationResponse<ScheduleToAction> body = response.getBody();
    Assertions.assertNotNull(body);
    List<ScheduleToAction> ScheduleToActions = body.getList();
    Assertions.assertNotEquals(0, ScheduleToActions.size());
    Assertions.assertTrue(
        ScheduleToActions.stream().anyMatch(f -> f.getId().equals(testScheduleToAction.getId())));
  }

  public void assertScheduleToAction(
      ScheduleToActionCreate request, ScheduleToAction testScheduleToAction) {
    Assertions.assertNotNull(testScheduleToAction);
  }

  @Test
  @Order(3)
  public void testScheduleToActionUpdate() {
    String name = UUID.randomUUID().toString();
    ScheduleToActionUpdate request =
        new ScheduleToActionUpdate().setId(testScheduleToAction.getId()).setName(name);
    ResponseEntity<ScheduleToAction> response =
        this.restTemplate.exchange(
            "/ScheduleToAction/updateScheduleToAction",
            HttpMethod.PUT,
            new HttpEntity<>(request),
            ScheduleToAction.class);
    Assertions.assertTrue(response.getStatusCode().is2xxSuccessful() );
    testScheduleToAction = response.getBody();
    assertScheduleToAction(request, testScheduleToAction);
  }
}
