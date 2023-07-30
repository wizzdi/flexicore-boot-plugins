package com.wizzdi.maps.service.controller;

import com.flexicore.request.AuthenticationRequest;
import com.flexicore.response.AuthenticationResponse;
import com.wizzdi.flexicore.security.response.PaginationResponse;
import com.wizzdi.maps.model.MapGroup;
import com.wizzdi.maps.model.MapGroupToMappedPOI;
import com.wizzdi.maps.model.MappedPOI;
import com.wizzdi.maps.service.App;
import com.wizzdi.maps.service.request.MapGroupToMappedPOICreate;
import com.wizzdi.maps.service.request.MapGroupToMappedPOIFilter;
import com.wizzdi.maps.service.request.MapGroupToMappedPOIUpdate;
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
public class MapGroupToMappedPOIControllerTest {

  private MapGroupToMappedPOI testMapGroupToMappedPOI;
  @Autowired private TestRestTemplate restTemplate;

  @Autowired private MapGroup mapGroup;

  @Autowired private MappedPOI mappedPOI;

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
  public void testMapGroupToMappedPOICreate() {
    MapGroupToMappedPOICreate request =
        new MapGroupToMappedPOICreate().setName(UUID.randomUUID().toString());

    request.setMappedPOIId(this.mappedPOI.getId());

    request.setMapGroupId(this.mapGroup.getId());

    ResponseEntity<MapGroupToMappedPOI> response =
        this.restTemplate.postForEntity(
            "/MapGroupToMappedPOI/createMapGroupToMappedPOI", request, MapGroupToMappedPOI.class);
    Assertions.assertEquals(200, response.getStatusCodeValue());
    testMapGroupToMappedPOI = response.getBody();
    assertMapGroupToMappedPOI(request, testMapGroupToMappedPOI);
  }

  @Test
  @Order(2)
  public void testListAllMapGroupToMappedPOIs() {
    MapGroupToMappedPOIFilter request = new MapGroupToMappedPOIFilter();
    ParameterizedTypeReference<PaginationResponse<MapGroupToMappedPOI>> t =
        new ParameterizedTypeReference<>() {};

    ResponseEntity<PaginationResponse<MapGroupToMappedPOI>> response =
        this.restTemplate.exchange(
            "/MapGroupToMappedPOI/getAllMapGroupToMappedPOIs",
            HttpMethod.POST,
            new HttpEntity<>(request),
            t);
    Assertions.assertEquals(200, response.getStatusCodeValue());
    PaginationResponse<MapGroupToMappedPOI> body = response.getBody();
    Assertions.assertNotNull(body);
    List<MapGroupToMappedPOI> MapGroupToMappedPOIs = body.getList();
    Assertions.assertNotEquals(0, MapGroupToMappedPOIs.size());
    Assertions.assertTrue(
        MapGroupToMappedPOIs.stream()
            .anyMatch(f -> f.getId().equals(testMapGroupToMappedPOI.getId())));
  }

  public void assertMapGroupToMappedPOI(
      MapGroupToMappedPOICreate request, MapGroupToMappedPOI testMapGroupToMappedPOI) {
    Assertions.assertNotNull(testMapGroupToMappedPOI);

    if (request.getMappedPOIId() != null) {

      Assertions.assertNotNull(testMapGroupToMappedPOI.getMappedPOI());
      Assertions.assertEquals(
          request.getMappedPOIId(), testMapGroupToMappedPOI.getMappedPOI().getId());
    }

    if (request.getMapGroupId() != null) {

      Assertions.assertNotNull(testMapGroupToMappedPOI.getMapGroup());
      Assertions.assertEquals(
          request.getMapGroupId(), testMapGroupToMappedPOI.getMapGroup().getId());
    }
  }

  @Test
  @Order(3)
  public void testMapGroupToMappedPOIUpdate() {
    MapGroupToMappedPOIUpdate request =
        new MapGroupToMappedPOIUpdate()
            .setId(testMapGroupToMappedPOI.getId())
            .setName(UUID.randomUUID().toString());
    ResponseEntity<MapGroupToMappedPOI> response =
        this.restTemplate.exchange(
            "/MapGroupToMappedPOI/updateMapGroupToMappedPOI",
            HttpMethod.PUT,
            new HttpEntity<>(request),
            MapGroupToMappedPOI.class);
    Assertions.assertEquals(200, response.getStatusCodeValue());
    testMapGroupToMappedPOI = response.getBody();
    assertMapGroupToMappedPOI(request, testMapGroupToMappedPOI);
  }
}
