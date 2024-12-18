package com.wizzdi.maps.service.controller;



import com.wizzdi.flexicore.security.configuration.SecurityContext;
import com.wizzdi.flexicore.security.request.BasicPropertiesFilter;
import com.wizzdi.flexicore.security.response.PaginationResponse;
import com.wizzdi.maps.model.MappedPOI;
import com.wizzdi.maps.service.App;
import com.wizzdi.maps.service.request.GeoHashRequest;
import com.wizzdi.maps.service.request.MappedPOICreate;
import com.wizzdi.maps.service.request.MappedPOIFilter;
import com.wizzdi.maps.service.request.MappedPOIUpdate;
import com.wizzdi.maps.service.response.GeoHashResponse;
import com.wizzdi.maps.service.service.MappedPOIService;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.context.annotation.Lazy;
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

import java.util.*;

@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT, classes = App.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@Testcontainers
@ActiveProfiles("test")
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE) // deactivate the default behaviour
public class GeoHashControllerTest {

  @Autowired private TestRestTemplate restTemplate;
  @Autowired
  private MappedPOIService mappedPOIService;
  @Autowired
  @Lazy
  private SecurityContext adminSecurityContext;

  private Set<String> ids=new HashSet<>();
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
    ids.add(mappedPOIService.createMappedPOI(new MappedPOICreate().setLat(34.3D).setLon(32.3D).setName("test1"),adminSecurityContext).getId());
    ids.add( mappedPOIService.createMappedPOI(new MappedPOICreate().setLat(34.5D).setLon(32.4D).setName("test2"),adminSecurityContext).getId());


  }

  @Test
  @Order(2)
  public void testAreaLowPrecision() {
    GeoHashRequest request = new GeoHashRequest()
            .setMappedPOIFilter(new MappedPOIFilter().setBasicPropertiesFilter(new BasicPropertiesFilter().setOnlyIds(ids)))
            .setPrecision(1);
    ParameterizedTypeReference<PaginationResponse<GeoHashResponse>> t =
        new ParameterizedTypeReference<>() {};

    ResponseEntity<PaginationResponse<GeoHashResponse>> response =
        this.restTemplate.exchange(
            "/GeoHash/getAllGeoHashAreas", HttpMethod.POST, new HttpEntity<>(request), t);
    Assertions.assertTrue(response.getStatusCode().is2xxSuccessful() );
    PaginationResponse<GeoHashResponse> body = response.getBody();
    Assertions.assertNotNull(body);
    List<GeoHashResponse> areas = body.getList();
    Assertions.assertEquals(1,areas.size());
    GeoHashResponse area = areas.get(0);
    Assertions.assertEquals(2, area.getCount());


  }
  @Test
  @Order(2)
  public void testAreaHighPrecision() {
    GeoHashRequest request = new GeoHashRequest()
            .setMappedPOIFilter(new MappedPOIFilter().setBasicPropertiesFilter(new BasicPropertiesFilter().setOnlyIds(ids)))
            .setPrecision(12);
    ParameterizedTypeReference<PaginationResponse<GeoHashResponse>> t =
            new ParameterizedTypeReference<>() {};

    ResponseEntity<PaginationResponse<GeoHashResponse>> response =
            this.restTemplate.exchange(
                    "/GeoHash/getAllGeoHashAreas", HttpMethod.POST, new HttpEntity<>(request), t);
    Assertions.assertTrue(response.getStatusCode().is2xxSuccessful() );
    PaginationResponse<GeoHashResponse> body = response.getBody();
    Assertions.assertNotNull(body);
    List<GeoHashResponse> areas = body.getList();
    Assertions.assertEquals(2,areas.size());
    for (GeoHashResponse area : areas) {
      Assertions.assertEquals(1, area.getCount());

    }


  }

}
