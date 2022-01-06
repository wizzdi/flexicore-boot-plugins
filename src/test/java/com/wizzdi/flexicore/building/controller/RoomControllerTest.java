package com.wizzdi.flexicore.building.controller;

import com.flexicore.request.AuthenticationRequest;
import com.flexicore.response.AuthenticationResponse;
import com.wizzdi.flexicore.building.App;
import com.wizzdi.flexicore.building.model.Room;
import com.wizzdi.flexicore.building.request.RoomCreate;
import com.wizzdi.flexicore.building.request.RoomFilter;
import com.wizzdi.flexicore.building.request.RoomUpdate;
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
public class RoomControllerTest {

  private Room testRoom;
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
  public void testRoomCreate() {
    String name = UUID.randomUUID().toString();
    RoomCreate request = new RoomCreate().setName(name);

    request.setExternalId("test-string");

    ResponseEntity<Room> response =
        this.restTemplate.postForEntity("/Room/createRoom", request, Room.class);
    Assertions.assertEquals(200, response.getStatusCodeValue());
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
    Assertions.assertEquals(200, response.getStatusCodeValue());
    PaginationResponse<Room> body = response.getBody();
    Assertions.assertNotNull(body);
    List<Room> Rooms = body.getList();
    Assertions.assertNotEquals(0, Rooms.size());
    Assertions.assertTrue(Rooms.stream().anyMatch(f -> f.getId().equals(testRoom.getId())));
  }

  public void assertRoom(RoomCreate request, Room testRoom) {
    Assertions.assertNotNull(testRoom);

    if (request.getExternalId() != null) {

      Assertions.assertEquals(request.getExternalId(), testRoom.getExternalId());
    }
  }

  @Test
  @Order(3)
  public void testRoomUpdate() {
    String name = UUID.randomUUID().toString();
    RoomUpdate request = new RoomUpdate().setId(testRoom.getId()).setName(name);
    ResponseEntity<Room> response =
        this.restTemplate.exchange(
            "/Room/updateRoom", HttpMethod.PUT, new HttpEntity<>(request), Room.class);
    Assertions.assertEquals(200, response.getStatusCodeValue());
    testRoom = response.getBody();
    assertRoom(request, testRoom);
  }
}
