package com.wizzdi.maps.service.controller;

import com.flexicore.request.AuthenticationRequest;
import com.flexicore.response.AuthenticationResponse;
import com.wizzdi.flexicore.security.response.PaginationResponse;
import com.wizzdi.maps.model.MapIcon;
import com.wizzdi.maps.model.MappedPOI;
import com.wizzdi.maps.model.StatusHistory;
import com.wizzdi.maps.service.App;
import com.wizzdi.maps.service.request.StatusHistoryCreate;
import com.wizzdi.maps.service.request.StatusHistoryFilter;
import com.wizzdi.maps.service.request.StatusHistoryUpdate;
import java.time.OffsetDateTime;
import java.time.ZoneId;
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
public class StatusHistoryControllerTest {

  private StatusHistory testStatusHistory;
  @Autowired private TestRestTemplate restTemplate;

  @Autowired private MappedPOI mappedPOI;

  @Autowired private MapIcon mapIcon;

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
  public void testStatusHistoryCreate() {
    String name = UUID.randomUUID().toString();
    StatusHistoryCreate request = new StatusHistoryCreate().setName(name);

    request.setMappedPOIId(this.mappedPOI.getId());

    request.setMapIconId(this.mapIcon.getId());

    request.setDateAtStatus(OffsetDateTime.now());

    ResponseEntity<StatusHistory> response =
        this.restTemplate.postForEntity(
            "/StatusHistory/createStatusHistory", request, StatusHistory.class);
    Assertions.assertEquals(200, response.getStatusCodeValue());
    testStatusHistory = response.getBody();
    assertStatusHistory(request, testStatusHistory);
  }

  @Test
  @Order(2)
  public void testListAllStatusHistories() {
    StatusHistoryFilter request = new StatusHistoryFilter();
    ParameterizedTypeReference<PaginationResponse<StatusHistory>> t =
        new ParameterizedTypeReference<>() {};

    ResponseEntity<PaginationResponse<StatusHistory>> response =
        this.restTemplate.exchange(
            "/StatusHistory/getAllStatusHistories", HttpMethod.POST, new HttpEntity<>(request), t);
    Assertions.assertEquals(200, response.getStatusCodeValue());
    PaginationResponse<StatusHistory> body = response.getBody();
    Assertions.assertNotNull(body);
    List<StatusHistory> StatusHistories = body.getList();
    Assertions.assertNotEquals(0, StatusHistories.size());
    Assertions.assertTrue(
        StatusHistories.stream().anyMatch(f -> f.getId().equals(testStatusHistory.getId())));
  }

  public void assertStatusHistory(StatusHistoryCreate request, StatusHistory testStatusHistory) {
    Assertions.assertNotNull(testStatusHistory);

    if (request.getMappedPOIId() != null) {

      Assertions.assertNotNull(testStatusHistory.getMappedPOI());
      Assertions.assertEquals(request.getMappedPOIId(), testStatusHistory.getMappedPOI().getId());
    }

    if (request.getMapIconId() != null) {

      Assertions.assertNotNull(testStatusHistory.getMapIcon());
      Assertions.assertEquals(request.getMapIconId(), testStatusHistory.getMapIcon().getId());
    }

    if (request.getDateAtStatus() != null) {
      Assertions.assertEquals(request.getDateAtStatus().atZoneSameInstant(ZoneId.systemDefault()), testStatusHistory.getDateAtStatus().atZoneSameInstant(ZoneId.systemDefault()));
    }
  }

  @Test
  @Order(3)
  public void testStatusHistoryUpdate() {
    String name = UUID.randomUUID().toString();
    StatusHistoryUpdate request =
        new StatusHistoryUpdate().setId(testStatusHistory.getId()).setName(name);
    ResponseEntity<StatusHistory> response =
        this.restTemplate.exchange(
            "/StatusHistory/updateStatusHistory",
            HttpMethod.PUT,
            new HttpEntity<>(request),
            StatusHistory.class);
    Assertions.assertEquals(200, response.getStatusCodeValue());
    testStatusHistory = response.getBody();
    assertStatusHistory(request, testStatusHistory);
  }
}
