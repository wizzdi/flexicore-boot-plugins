package com.wizzdi.maps.service.controller;

import com.flexicore.request.AuthenticationRequest;
import com.flexicore.response.AuthenticationResponse;
import com.wizzdi.flexicore.security.response.PaginationResponse;
import com.wizzdi.maps.model.Building;
import com.wizzdi.maps.model.MappedPOI;
import com.wizzdi.maps.service.App;
import com.wizzdi.maps.service.request.BuildingCreate;
import com.wizzdi.maps.service.request.BuildingFilter;
import com.wizzdi.maps.service.request.BuildingUpdate;
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
public class BuildingControllerTest {

  private Building testBuilding;
  @Autowired private TestRestTemplate restTemplate;

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
  public void testBuildingCreate() {
    BuildingCreate request = new BuildingCreate().setName(UUID.randomUUID().toString());

    request.setMappedPOIId(this.mappedPOI.getId());

    request.setExternalId("test-string");

    ResponseEntity<Building> response =
        this.restTemplate.postForEntity("/Building/createBuilding", request, Building.class);
    Assertions.assertEquals(200, response.getStatusCodeValue());
    testBuilding = response.getBody();
    assertBuilding(request, testBuilding);
  }

  @Test
  @Order(2)
  public void testListAllBuildings() {
    BuildingFilter request = new BuildingFilter();
    ParameterizedTypeReference<PaginationResponse<Building>> t =
        new ParameterizedTypeReference<>() {};

    ResponseEntity<PaginationResponse<Building>> response =
        this.restTemplate.exchange(
            "/Building/getAllBuildings", HttpMethod.POST, new HttpEntity<>(request), t);
    Assertions.assertEquals(200, response.getStatusCodeValue());
    PaginationResponse<Building> body = response.getBody();
    Assertions.assertNotNull(body);
    List<Building> Buildings = body.getList();
    Assertions.assertNotEquals(0, Buildings.size());
    Assertions.assertTrue(Buildings.stream().anyMatch(f -> f.getId().equals(testBuilding.getId())));
  }

  public void assertBuilding(BuildingCreate request, Building testBuilding) {
    Assertions.assertNotNull(testBuilding);

    if (request.getMappedPOIId() != null) {

      Assertions.assertNotNull(testBuilding.getMappedPOI());
      Assertions.assertEquals(request.getMappedPOIId(), testBuilding.getMappedPOI().getId());
    }

    if (request.getExternalId() != null) {
      Assertions.assertEquals(request.getExternalId(), testBuilding.getExternalId());
    }
  }

  @Test
  @Order(3)
  public void testBuildingUpdate() {
    BuildingUpdate request =
        new BuildingUpdate().setId(testBuilding.getId()).setName(UUID.randomUUID().toString());
    ResponseEntity<Building> response =
        this.restTemplate.exchange(
            "/Building/updateBuilding", HttpMethod.PUT, new HttpEntity<>(request), Building.class);
    Assertions.assertEquals(200, response.getStatusCodeValue());
    testBuilding = response.getBody();
    assertBuilding(request, testBuilding);
  }
}
