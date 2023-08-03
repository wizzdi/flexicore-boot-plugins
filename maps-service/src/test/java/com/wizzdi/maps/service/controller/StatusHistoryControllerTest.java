package com.wizzdi.maps.service.controller;



import com.wizzdi.flexicore.security.response.PaginationResponse;
import com.wizzdi.maps.model.MapIcon;
import com.wizzdi.maps.model.MappedPOI;
import com.wizzdi.maps.model.StatusHistory;
import com.wizzdi.maps.service.App;
import com.wizzdi.maps.service.request.StatusHistoryCreate;
import com.wizzdi.maps.service.request.StatusHistoryFilter;
import com.wizzdi.maps.service.request.StatusHistoryUpdate;
import java.time.OffsetDateTime;
import java.time.ZoneId;
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
public class StatusHistoryControllerTest {

  private StatusHistory testStatusHistory;
  @Autowired private TestRestTemplate restTemplate;

  @Autowired private MappedPOI mappedPOI;

  @Autowired private MapIcon mapIcon;

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
  public void testStatusHistoryCreate() {
    String name = UUID.randomUUID().toString();
    StatusHistoryCreate request = new StatusHistoryCreate().setName(name);

    request.setMappedPOIId(this.mappedPOI.getId());

    request.setMapIconId(this.mapIcon.getId());

    request.setDateAtStatus(OffsetDateTime.now());

    ResponseEntity<StatusHistory> response =
        this.restTemplate.postForEntity(
            "/StatusHistory/createStatusHistory", request, StatusHistory.class);
    Assertions.assertTrue(response.getStatusCode().is2xxSuccessful() );
    testStatusHistory = response.getBody();
    assertStatusHistory(request, testStatusHistory);
  }

  @Test
  @Order(2)
  public void testListAllStatusHistories() {
    StatusHistoryFilter request = new StatusHistoryFilter();
    ParameterizedTypeReference<PaginationResponse<StatusHistory>> t =
        new ParameterizedTypeReference<>() {};

    ResponseEntity<PaginationResponse<StatusHistory>> response =
        this.restTemplate.exchange(
            "/StatusHistory/getAllStatusHistories", HttpMethod.POST, new HttpEntity<>(request), t);
    Assertions.assertTrue(response.getStatusCode().is2xxSuccessful() );
    PaginationResponse<StatusHistory> body = response.getBody();
    Assertions.assertNotNull(body);
    List<StatusHistory> StatusHistories = body.getList();
    Assertions.assertNotEquals(0, StatusHistories.size());
    Assertions.assertTrue(
        StatusHistories.stream().anyMatch(f -> f.getId().equals(testStatusHistory.getId())));
  }

  public void assertStatusHistory(StatusHistoryCreate request, StatusHistory testStatusHistory) {
    Assertions.assertNotNull(testStatusHistory);

    if (request.getMappedPOIId() != null) {

      Assertions.assertNotNull(testStatusHistory.getMappedPOI());
      Assertions.assertEquals(request.getMappedPOIId(), testStatusHistory.getMappedPOI().getId());
    }

    if (request.getMapIconId() != null) {

      Assertions.assertNotNull(testStatusHistory.getMapIcon());
      Assertions.assertEquals(request.getMapIconId(), testStatusHistory.getMapIcon().getId());
    }

    if (request.getDateAtStatus() != null) {
      Assertions.assertEquals(request.getDateAtStatus().atZoneSameInstant(ZoneId.systemDefault()), testStatusHistory.getDateAtStatus().atZoneSameInstant(ZoneId.systemDefault()));
    }
  }

  @Test
  @Order(3)
  public void testStatusHistoryUpdate() {
    String name = UUID.randomUUID().toString();
    StatusHistoryUpdate request =
        new StatusHistoryUpdate().setId(testStatusHistory.getId()).setName(name);
    ResponseEntity<StatusHistory> response =
        this.restTemplate.exchange(
            "/StatusHistory/updateStatusHistory",
            HttpMethod.PUT,
            new HttpEntity<>(request),
            StatusHistory.class);
    Assertions.assertTrue(response.getStatusCode().is2xxSuccessful() );
    testStatusHistory = response.getBody();
    assertStatusHistory(request, testStatusHistory);
  }
}
