package com.wizzdi.maps.service.controller;


import com.wizzdi.flexicore.security.response.PaginationResponse;
import com.wizzdi.maps.model.Building;
import com.wizzdi.maps.model.MappedPOI;
import com.wizzdi.maps.service.App;
import com.wizzdi.maps.service.request.BuildingCreate;
import com.wizzdi.maps.service.request.BuildingFilter;
import com.wizzdi.maps.service.request.BuildingUpdate;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.util.Collections;
import java.util.List;
import java.util.UUID;

@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT, classes = App.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@Testcontainers
@ActiveProfiles("test")
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE) // deactivate the default behaviour

public class BuildingControllerTest {
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

  @Autowired
  private MappedPOI mappedPOI;
  private Building testBuilding;
  @Autowired
  private TestRestTemplate restTemplate;


  @Test
  @Order(1)
  public void testBuildingCreate() {
    BuildingCreate request = new BuildingCreate().setName(UUID.randomUUID().toString());

    request.setMappedPOIId(this.mappedPOI.getId());

    request.setExternalId("test-string");

    ResponseEntity<Building> response =
        this.restTemplate.postForEntity("/Building/createBuilding", request, Building.class);
    Assertions.assertTrue(response.getStatusCode().is2xxSuccessful() );
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
    Assertions.assertTrue(response.getStatusCode().is2xxSuccessful() );
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
    Assertions.assertTrue(response.getStatusCode().is2xxSuccessful() );
    testBuilding = response.getBody();
    assertBuilding(request, testBuilding);
  }
}
