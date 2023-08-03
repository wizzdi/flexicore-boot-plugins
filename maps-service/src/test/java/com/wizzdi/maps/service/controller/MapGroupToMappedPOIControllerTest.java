package com.wizzdi.maps.service.controller;



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
public class MapGroupToMappedPOIControllerTest {

  private MapGroupToMappedPOI testMapGroupToMappedPOI;
  @Autowired private TestRestTemplate restTemplate;

  @Autowired private MapGroup mapGroup;

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
  public void testMapGroupToMappedPOICreate() {
    MapGroupToMappedPOICreate request =
        new MapGroupToMappedPOICreate().setName(UUID.randomUUID().toString());

    request.setMappedPOIId(this.mappedPOI.getId());

    request.setMapGroupId(this.mapGroup.getId());

    ResponseEntity<MapGroupToMappedPOI> response =
        this.restTemplate.postForEntity(
            "/MapGroupToMappedPOI/createMapGroupToMappedPOI", request, MapGroupToMappedPOI.class);
    Assertions.assertTrue(response.getStatusCode().is2xxSuccessful() );
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
    Assertions.assertTrue(response.getStatusCode().is2xxSuccessful() );
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
    Assertions.assertTrue(response.getStatusCode().is2xxSuccessful() );
    testMapGroupToMappedPOI = response.getBody();
    assertMapGroupToMappedPOI(request, testMapGroupToMappedPOI);
  }
}
