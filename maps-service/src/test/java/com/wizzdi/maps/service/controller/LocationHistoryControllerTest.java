package com.wizzdi.maps.service.controller;



import com.wizzdi.flexicore.security.response.PaginationResponse;
import com.wizzdi.maps.model.LocationHistory;
import com.wizzdi.maps.model.MappedPOI;
import com.wizzdi.maps.model.Room;
import com.wizzdi.maps.service.App;
import com.wizzdi.maps.service.request.LocationHistoryCreate;
import com.wizzdi.maps.service.request.LocationHistoryFilter;
import com.wizzdi.maps.service.request.LocationHistoryUpdate;
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
public class LocationHistoryControllerTest {

  private LocationHistory testLocationHistory;
  @Autowired private TestRestTemplate restTemplate;

  @Autowired private Room room;

  @Autowired private MappedPOI mappedPOI;

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
  public void testLocationHistoryCreate() {
    String name = UUID.randomUUID().toString();
    LocationHistoryCreate request = new LocationHistoryCreate().setName(name);

    request.setDateAtLocation(OffsetDateTime.now());

    request.setY(10D);

    request.setZ(10D);

    request.setX(10D);

    request.setRoomId(this.room.getId());

    request.setMappedPOIId(this.mappedPOI.getId());

    request.setLon(10D);

    request.setLat(10D);

    ResponseEntity<LocationHistory> response =
        this.restTemplate.postForEntity(
            "/LocationHistory/createLocationHistory", request, LocationHistory.class);
    Assertions.assertTrue(response.getStatusCode().is2xxSuccessful() );
    testLocationHistory = response.getBody();
    assertLocationHistory(request, testLocationHistory);
  }

  @Test
  @Order(2)
  public void testListAllLocationHistories() {
    LocationHistoryFilter request = new LocationHistoryFilter();
    ParameterizedTypeReference<PaginationResponse<LocationHistory>> t =
        new ParameterizedTypeReference<>() {};

    ResponseEntity<PaginationResponse<LocationHistory>> response =
        this.restTemplate.exchange(
            "/LocationHistory/getAllLocationHistories",
            HttpMethod.POST,
            new HttpEntity<>(request),
            t);
    Assertions.assertTrue(response.getStatusCode().is2xxSuccessful() );
    PaginationResponse<LocationHistory> body = response.getBody();
    Assertions.assertNotNull(body);
    List<LocationHistory> LocationHistories = body.getList();
    Assertions.assertNotEquals(0, LocationHistories.size());
    Assertions.assertTrue(
        LocationHistories.stream().anyMatch(f -> f.getId().equals(testLocationHistory.getId())));
  }

  public void assertLocationHistory(
      LocationHistoryCreate request, LocationHistory testLocationHistory) {
    Assertions.assertNotNull(testLocationHistory);

    if (request.getDateAtLocation() != null) {

      Assertions.assertEquals(request.getDateAtLocation().atZoneSameInstant(ZoneId.systemDefault()), testLocationHistory.getDateAtLocation().atZoneSameInstant(ZoneId.systemDefault()));
    }

    if (request.getY() != null) {

      Assertions.assertEquals(request.getY(), testLocationHistory.getY());
    }

    if (request.getZ() != null) {

      Assertions.assertEquals(request.getZ(), testLocationHistory.getZ());
    }

    if (request.getX() != null) {

      Assertions.assertEquals(request.getX(), testLocationHistory.getX());
    }

    if (request.getRoomId() != null) {

      Assertions.assertNotNull(testLocationHistory.getRoom());
      Assertions.assertEquals(request.getRoomId(), testLocationHistory.getRoom().getId());
    }

    if (request.getMappedPOIId() != null) {

      Assertions.assertNotNull(testLocationHistory.getMappedPOI());
      Assertions.assertEquals(request.getMappedPOIId(), testLocationHistory.getMappedPOI().getId());
    }

    if (request.getLon() != null) {

      Assertions.assertEquals(request.getLon(), testLocationHistory.getLon());
    }

    if (request.getLat() != null) {

      Assertions.assertEquals(request.getLat(), testLocationHistory.getLat());
    }
  }

  @Test
  @Order(3)
  public void testLocationHistoryUpdate() {
    String name = UUID.randomUUID().toString();
    LocationHistoryUpdate request =
        new LocationHistoryUpdate().setId(testLocationHistory.getId()).setName(name);
    ResponseEntity<LocationHistory> response =
        this.restTemplate.exchange(
            "/LocationHistory/updateLocationHistory",
            HttpMethod.PUT,
            new HttpEntity<>(request),
            LocationHistory.class);
    Assertions.assertTrue(response.getStatusCode().is2xxSuccessful() );
    testLocationHistory = response.getBody();
    assertLocationHistory(request, testLocationHistory);
  }
}
