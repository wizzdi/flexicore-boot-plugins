package com.wizzdi.maps.service.controller;



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
public class MapGroupControllerTest {

  private MapGroup testMapGroup;
  @Autowired private TestRestTemplate restTemplate;

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
  public void testMapGroupCreate() {
    MapGroupCreate request = new MapGroupCreate().setName(UUID.randomUUID().toString());

    request.setExternalId("test-string");

    ResponseEntity<MapGroup> response =
        this.restTemplate.postForEntity("/MapGroup/createMapGroup", request, MapGroup.class);
    Assertions.assertTrue(response.getStatusCode().is2xxSuccessful() );
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
    Assertions.assertTrue(response.getStatusCode().is2xxSuccessful() );
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
    Assertions.assertTrue(response.getStatusCode().is2xxSuccessful() );
    testMapGroup = response.getBody();
    assertMapGroup(request, testMapGroup);
  }
}
