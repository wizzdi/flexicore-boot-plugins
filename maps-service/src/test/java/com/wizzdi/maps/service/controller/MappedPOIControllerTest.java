package com.wizzdi.maps.service.controller;

import com.flexicore.request.AuthenticationRequest;
import com.flexicore.response.AuthenticationResponse;
import com.wizzdi.flexicore.security.response.PaginationResponse;
import com.wizzdi.maps.model.*;
import com.wizzdi.maps.service.App;
import com.wizzdi.maps.service.request.MappedPOICreate;
import com.wizzdi.maps.service.request.MappedPOIFilter;
import com.wizzdi.maps.service.request.MappedPOIUpdate;
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
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.web.bind.annotation.*;

@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT, classes = App.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@ActiveProfiles("test")
public class MappedPOIControllerTest {

  private MappedPOI testMappedPOI;
  @Autowired private TestRestTemplate restTemplate;

  @Autowired private Room room;

  @Autowired private MapIcon mapIcon;
  @Autowired private LayerType layerType1;
  @Autowired private Layer layer1;
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
  public void testMappedPOICreate() {
    MappedPOICreate request = new MappedPOICreate().setLayerId(layer1.getId()).setName(UUID.randomUUID().toString());
    request.setRelatedId("test-string");

    request.setY(10D);



    request.setZ(10D);


    request.setMapIconId(this.mapIcon.getId());

    request.setLat(10D);
    request.setKeepLocationHistory(true);
    request.setKeepStatusHistory(true);

    request.setX(10D);
    request.setRoomId(this.room.getId());


    request.setRoomId(this.room.getId());

    request.setLon(10D);


    ResponseEntity<MappedPOI> response =
            this.restTemplate.postForEntity("/MappedPOI/createMappedPOI", request, MappedPOI.class);
    Assertions.assertEquals(200, response.getStatusCodeValue());
    testMappedPOI = response.getBody();
    assertMappedPOI(request, testMappedPOI);
  }

  @Test
  @Order(2)
  public void testListAllMappedPOIs() {
    MappedPOIFilter request = new MappedPOIFilter();
    ParameterizedTypeReference<PaginationResponse<MappedPOI>> t =
            new ParameterizedTypeReference<>() {};

    ResponseEntity<PaginationResponse<MappedPOI>> response =
            this.restTemplate.exchange(
                    "/MappedPOI/getAllMappedPOIs", HttpMethod.POST, new HttpEntity<>(request), t);
    Assertions.assertEquals(200, response.getStatusCodeValue());
    PaginationResponse<MappedPOI> body = response.getBody();
    Assertions.assertNotNull(body);
    List<MappedPOI> MappedPOIs = body.getList();
    Assertions.assertNotEquals(0, MappedPOIs.size());
    Assertions.assertTrue(
            MappedPOIs.stream().anyMatch(f -> f.getId().equals(testMappedPOI.getId())));
  }

  public void assertMappedPOI(MappedPOICreate request, MappedPOI testMappedPOI) {
    Assertions.assertNotNull(testMappedPOI);
    Assertions.assertNotNull(testMappedPOI.getLayer());
    Assertions.assertEquals(layer1.getName(),testMappedPOI.getLayer().getName());

    if (request.getRelatedId() != null) {
      Assertions.assertEquals(request.getRelatedId(), testMappedPOI.getRelatedId());
    }

    if (request.getY() != null) {

      Assertions.assertEquals(request.getY(), testMappedPOI.getY());
    }



    if (request.getZ() != null) {

      Assertions.assertEquals(request.getZ(), testMappedPOI.getZ());
    }


    if (request.getLat() != null) {

      Assertions.assertEquals(request.getLat(), testMappedPOI.getLat());
    }

    if (request.getX() != null) {

      Assertions.assertEquals(request.getX(), testMappedPOI.getX());
    }

    if (request.getRoomId() != null) {

      Assertions.assertNotNull(testMappedPOI.getRoom());
      Assertions.assertEquals(request.getRoomId(), testMappedPOI.getRoom().getId());
    }


    if (request.getLon() != null) {

      Assertions.assertEquals(request.getLon(), testMappedPOI.getLon());
    }
    if (request.getExternalId() != null) {

      Assertions.assertEquals(request.getExternalId(), testMappedPOI.getExternalId());
    }

    if (request.getKeepLocationHistory() != null) {
      Assertions.assertEquals(
              request.getKeepLocationHistory(), testMappedPOI.isKeepLocationHistory());
    }
    if (request.getKeepStatusHistory() != null) {
      Assertions.assertEquals(request.getKeepStatusHistory(), testMappedPOI.isKeepStatusHistory());
    }

  }

  @Test
  @Order(3)
  public void testMappedPOIUpdate() {
    MappedPOIUpdate request =
        new MappedPOIUpdate().setId(testMappedPOI.getId()).setName(UUID.randomUUID().toString());
    ResponseEntity<MappedPOI> response =
            this.restTemplate.exchange(
                    "/MappedPOI/updateMappedPOI",
                    HttpMethod.PUT,
                    new HttpEntity<>(request),
                    MappedPOI.class);
    Assertions.assertEquals(200, response.getStatusCodeValue());
    testMappedPOI = response.getBody();
    assertMappedPOI(request, testMappedPOI);
  }
}
