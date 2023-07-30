package com.wizzdi.maps.service.controller;

import com.flexicore.request.AuthenticationRequest;
import com.flexicore.response.AuthenticationResponse;
import com.wizzdi.flexicore.security.response.PaginationResponse;
import com.wizzdi.maps.model.MappedPOI;
import com.wizzdi.maps.service.App;
import com.wizzdi.maps.service.request.GeoHashRequest;
import com.wizzdi.maps.service.request.MappedPOICreate;
import com.wizzdi.maps.service.request.MappedPOIFilter;
import com.wizzdi.maps.service.request.MappedPOIUpdate;
import com.wizzdi.maps.service.response.GeoHashResponse;
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

import java.util.Collections;
import java.util.List;
import java.util.UUID;

@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT, classes = App.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@ActiveProfiles("test")
public class GeoHashControllerTest {

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
  @Order(2)
  public void testAreaLowPrecision() {
    GeoHashRequest request = new GeoHashRequest()
            .setMappedPOIFilter(new MappedPOIFilter())
            .setPrecision(3);
    ParameterizedTypeReference<PaginationResponse<GeoHashResponse>> t =
        new ParameterizedTypeReference<>() {};

    ResponseEntity<PaginationResponse<GeoHashResponse>> response =
        this.restTemplate.exchange(
            "/GeoHash/getAllGeoHashAreas", HttpMethod.POST, new HttpEntity<>(request), t);
    Assertions.assertEquals(200, response.getStatusCodeValue());
    PaginationResponse<GeoHashResponse> body = response.getBody();
    Assertions.assertNotNull(body);
    List<GeoHashResponse> areas = body.getList();
    Assertions.assertNotEquals(0, areas.size());
    Assertions.assertEquals(2,areas.size());
    GeoHashResponse area = areas.get(0);
    Assertions.assertEquals(1, area.getCount());


  }
  @Test
  @Order(2)
  public void testAreaHighPrecision() {
    GeoHashRequest request = new GeoHashRequest()
            .setMappedPOIFilter(new MappedPOIFilter())
            .setPrecision(12);
    ParameterizedTypeReference<PaginationResponse<GeoHashResponse>> t =
            new ParameterizedTypeReference<>() {};

    ResponseEntity<PaginationResponse<GeoHashResponse>> response =
            this.restTemplate.exchange(
                    "/GeoHash/getAllGeoHashAreas", HttpMethod.POST, new HttpEntity<>(request), t);
    Assertions.assertEquals(200, response.getStatusCodeValue());
    PaginationResponse<GeoHashResponse> body = response.getBody();
    Assertions.assertNotNull(body);
    List<GeoHashResponse> areas = body.getList();
    Assertions.assertNotEquals(0, areas.size());
    Assertions.assertEquals(3,areas.size());
    for (GeoHashResponse area : areas) {
      Assertions.assertEquals(1, area.getCount());

    }


  }

}
