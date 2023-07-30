package com.flexicore.scheduling.controller;

import com.flexicore.request.AuthenticationRequest;
import com.flexicore.response.AuthenticationResponse;
import com.flexicore.scheduling.model.ScheduleToAction;
import com.flexicore.scheduling.App;
import com.flexicore.scheduling.request.ScheduleToActionCreate;
import com.flexicore.scheduling.request.ScheduleToActionFilter;
import com.flexicore.scheduling.request.ScheduleToActionUpdate;
import com.wizzdi.flexicore.security.response.PaginationResponse;
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
public class ScheduleToActionControllerTest {

  private ScheduleToAction testScheduleToAction;
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
  public void testScheduleToActionCreate() {
    String name = UUID.randomUUID().toString();
    ScheduleToActionCreate request = new ScheduleToActionCreate().setName(name);

    ResponseEntity<ScheduleToAction> response =
        this.restTemplate.postForEntity(
            "/ScheduleToAction/createScheduleToAction", request, ScheduleToAction.class);
    Assertions.assertEquals(200, response.getStatusCodeValue());
    testScheduleToAction = response.getBody();
    assertScheduleToAction(request, testScheduleToAction);
  }

  @Test
  @Order(2)
  public void testListAllScheduleToActions() {
    ScheduleToActionFilter request = new ScheduleToActionFilter();
    ParameterizedTypeReference<PaginationResponse<ScheduleToAction>> t =
        new ParameterizedTypeReference<>() {};

    ResponseEntity<PaginationResponse<ScheduleToAction>> response =
        this.restTemplate.exchange(
            "/ScheduleToAction/getAllScheduleToActions",
            HttpMethod.POST,
            new HttpEntity<>(request),
            t);
    Assertions.assertEquals(200, response.getStatusCodeValue());
    PaginationResponse<ScheduleToAction> body = response.getBody();
    Assertions.assertNotNull(body);
    List<ScheduleToAction> ScheduleToActions = body.getList();
    Assertions.assertNotEquals(0, ScheduleToActions.size());
    Assertions.assertTrue(
        ScheduleToActions.stream().anyMatch(f -> f.getId().equals(testScheduleToAction.getId())));
  }

  public void assertScheduleToAction(
      ScheduleToActionCreate request, ScheduleToAction testScheduleToAction) {
    Assertions.assertNotNull(testScheduleToAction);
  }

  @Test
  @Order(3)
  public void testScheduleToActionUpdate() {
    String name = UUID.randomUUID().toString();
    ScheduleToActionUpdate request =
        new ScheduleToActionUpdate().setId(testScheduleToAction.getId()).setName(name);
    ResponseEntity<ScheduleToAction> response =
        this.restTemplate.exchange(
            "/ScheduleToAction/updateScheduleToAction",
            HttpMethod.PUT,
            new HttpEntity<>(request),
            ScheduleToAction.class);
    Assertions.assertEquals(200, response.getStatusCodeValue());
    testScheduleToAction = response.getBody();
    assertScheduleToAction(request, testScheduleToAction);
  }
}
