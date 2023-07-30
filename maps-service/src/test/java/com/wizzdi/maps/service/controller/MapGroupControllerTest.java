package com.wizzdi.maps.service.controller;

import com.flexicore.request.AuthenticationRequest;
import com.flexicore.response.AuthenticationResponse;
import com.wizzdi.flexicore.security.response.PaginationResponse;
import com.wizzdi.maps.model.MapGroup;
import com.wizzdi.maps.service.App;
import com.wizzdi.maps.service.request.MapGroupCreate;
import com.wizzdi.maps.service.request.MapGroupFilter;
import com.wizzdi.maps.service.request.MapGroupUpdate;
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
public class MapGroupControllerTest {

  private MapGroup testMapGroup;
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
  public void testMapGroupCreate() {
    MapGroupCreate request = new MapGroupCreate().setName(UUID.randomUUID().toString());

    request.setExternalId("test-string");

    ResponseEntity<MapGroup> response =
        this.restTemplate.postForEntity("/MapGroup/createMapGroup", request, MapGroup.class);
    Assertions.assertEquals(200, response.getStatusCodeValue());
    testMapGroup = response.getBody();
    assertMapGroup(request, testMapGroup);
  }

  @Test
  @Order(2)
  public void testListAllMapGroups() {
    MapGroupFilter request = new MapGroupFilter();
    ParameterizedTypeReference<PaginationResponse<MapGroup>> t =
        new ParameterizedTypeReference<>() {};

    ResponseEntity<PaginationResponse<MapGroup>> response =
        this.restTemplate.exchange(
            "/MapGroup/getAllMapGroups", HttpMethod.POST, new HttpEntity<>(request), t);
    Assertions.assertEquals(200, response.getStatusCodeValue());
    PaginationResponse<MapGroup> body = response.getBody();
    Assertions.assertNotNull(body);
    List<MapGroup> MapGroups = body.getList();
    Assertions.assertNotEquals(0, MapGroups.size());
    Assertions.assertTrue(MapGroups.stream().anyMatch(f -> f.getId().equals(testMapGroup.getId())));
  }

  public void assertMapGroup(MapGroupCreate request, MapGroup testMapGroup) {
    Assertions.assertNotNull(testMapGroup);

    if (request.getExternalId() != null) {
      Assertions.assertEquals(request.getExternalId(), testMapGroup.getExternalId());
    }
  }

  @Test
  @Order(3)
  public void testMapGroupUpdate() {
    MapGroupUpdate request =
        new MapGroupUpdate().setId(testMapGroup.getId()).setName(UUID.randomUUID().toString());
    ResponseEntity<MapGroup> response =
        this.restTemplate.exchange(
            "/MapGroup/updateMapGroup", HttpMethod.PUT, new HttpEntity<>(request), MapGroup.class);
    Assertions.assertEquals(200, response.getStatusCodeValue());
    testMapGroup = response.getBody();
    assertMapGroup(request, testMapGroup);
  }
}
