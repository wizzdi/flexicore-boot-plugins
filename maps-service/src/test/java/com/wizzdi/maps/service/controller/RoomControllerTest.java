package com.wizzdi.maps.service.controller;



import com.wizzdi.flexicore.security.response.PaginationResponse;
import com.wizzdi.maps.model.Building;
import com.wizzdi.maps.model.BuildingFloor;
import com.wizzdi.maps.model.Room;
import com.wizzdi.maps.service.App;
import com.wizzdi.maps.service.request.RoomCreate;
import com.wizzdi.maps.service.request.RoomFilter;
import com.wizzdi.maps.service.request.RoomUpdate;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestMethodOrder;
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
import org.springframework.web.bind.annotation.*;

@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT, classes = App.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@Testcontainers
@ActiveProfiles("test")
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE) // deactivate the default behaviour
public class RoomControllerTest {

  private Room testRoom;
  @Autowired private TestRestTemplate restTemplate;

  @Autowired private Building building;
  @Autowired
  private BuildingFloor buildingFloor;

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
  public void testRoomCreate() {
    RoomCreate request = new RoomCreate().setName(UUID.randomUUID().toString());

  //  request.setBuildingId(this.building.());

    request.setZ(10D);

    request.setX(10D);

    request.setExternalId("test-string");

    request.setY(10D);
    request.setBuildingFloorId(buildingFloor.getId());

    ResponseEntity<Room> response =
        this.restTemplate.postForEntity("/Room/createRoom", request, Room.class);
    Assertions.assertTrue(response.getStatusCode().is2xxSuccessful() );
    testRoom = response.getBody();
    assertRoom(request, testRoom);
  }

  @Test
  @Order(2)
  public void testListAllRooms() {
    RoomFilter request = new RoomFilter();
    ParameterizedTypeReference<PaginationResponse<Room>> t = new ParameterizedTypeReference<>() {};

    ResponseEntity<PaginationResponse<Room>> response =
        this.restTemplate.exchange(
            "/Room/getAllRooms", HttpMethod.POST, new HttpEntity<>(request), t);
    Assertions.assertTrue(response.getStatusCode().is2xxSuccessful() );
    PaginationResponse<Room> body = response.getBody();
    Assertions.assertNotNull(body);
    List<Room> Rooms = body.getList();
    Assertions.assertNotEquals(0, Rooms.size());
    Assertions.assertTrue(Rooms.stream().anyMatch(f -> f.getId().equals(testRoom.getId())));
  }

  public void assertRoom(RoomCreate request, Room testRoom) {
    Assertions.assertNotNull(testRoom);

//    if (request.getBuildingId() != null) {
//
//      Assertions.assertNotNull(testRoom.getBuilding());
//      Assertions.assertEquals(request.getBuildingId(), testRoom.getBuilding().getId());
//    }

    if (request.getZ() != null) {
      Assertions.assertEquals(request.getZ(), testRoom.getZ());
    }

    if (request.getX() != null) {
      Assertions.assertEquals(request.getX(), testRoom.getX());
    }

    if (request.getExternalId() != null) {
      Assertions.assertEquals(request.getExternalId(), testRoom.getExternalId());
    }

    if (request.getY() != null) {
      Assertions.assertEquals(request.getY(), testRoom.getY());
    }
  }

  @Test
  @Order(3)
  public void testRoomUpdate() {
    RoomUpdate request =
        new RoomUpdate().setId(testRoom.getId()).setName(UUID.randomUUID().toString());
    ResponseEntity<Room> response =
        this.restTemplate.exchange(
            "/Room/updateRoom", HttpMethod.PUT, new HttpEntity<>(request), Room.class);
    Assertions.assertTrue(response.getStatusCode().is2xxSuccessful() );
    testRoom = response.getBody();
    assertRoom(request, testRoom);
  }
}
