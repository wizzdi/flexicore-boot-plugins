package com.flexicore.rules.controller;

import com.flexicore.request.AuthenticationRequest;
import com.flexicore.response.AuthenticationResponse;
import com.flexicore.rules.App;
import com.flexicore.rules.model.JsFunction;
import com.flexicore.rules.model.JsFunctionParameter;
import com.flexicore.rules.request.JsFunctionParameterCreate;
import com.flexicore.rules.request.JsFunctionParameterFilter;
import com.flexicore.rules.request.JsFunctionParameterUpdate;
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
public class JsFunctionParameterControllerTest {

  private JsFunctionParameter testJsFunctionParameter;
  @Autowired private TestRestTemplate restTemplate;

  @Autowired private JsFunction jsFunction;

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
  public void testJsFunctionParameterCreate() {
    String name = UUID.randomUUID().toString();
    JsFunctionParameterCreate request = new JsFunctionParameterCreate().setName(name);

    request.setOrdinal("test-string");

    request.setJsFunctionId(this.jsFunction.getId());

    request.setParameterType("test-string");

    ResponseEntity<JsFunctionParameter> response =
        this.restTemplate.postForEntity(
            "/JsFunctionParameter/createJsFunctionParameter", request, JsFunctionParameter.class);
    Assertions.assertEquals(200, response.getStatusCodeValue());
    testJsFunctionParameter = response.getBody();
    assertJsFunctionParameter(request, testJsFunctionParameter);
  }

  @Test
  @Order(2)
  public void testListAllJsFunctionParameters() {
    JsFunctionParameterFilter request = new JsFunctionParameterFilter();
    ParameterizedTypeReference<PaginationResponse<JsFunctionParameter>> t =
        new ParameterizedTypeReference<>() {};

    ResponseEntity<PaginationResponse<JsFunctionParameter>> response =
        this.restTemplate.exchange(
            "/JsFunctionParameter/getAllJsFunctionParameters",
            HttpMethod.POST,
            new HttpEntity<>(request),
            t);
    Assertions.assertEquals(200, response.getStatusCodeValue());
    PaginationResponse<JsFunctionParameter> body = response.getBody();
    Assertions.assertNotNull(body);
    List<JsFunctionParameter> JsFunctionParameters = body.getList();
    Assertions.assertNotEquals(0, JsFunctionParameters.size());
    Assertions.assertTrue(
        JsFunctionParameters.stream()
            .anyMatch(f -> f.getId().equals(testJsFunctionParameter.getId())));
  }

  public void assertJsFunctionParameter(
      JsFunctionParameterCreate request, JsFunctionParameter testJsFunctionParameter) {
    Assertions.assertNotNull(testJsFunctionParameter);

    if (request.getOrdinal() != null) {

      Assertions.assertEquals(request.getOrdinal(), testJsFunctionParameter.getOrdinal());
    }

    if (request.getJsFunctionId() != null) {

      Assertions.assertNotNull(testJsFunctionParameter.getJsFunction());
      Assertions.assertEquals(
          request.getJsFunctionId(), testJsFunctionParameter.getJsFunction().getId());
    }

    if (request.getParameterType() != null) {

      Assertions.assertEquals(
          request.getParameterType(), testJsFunctionParameter.getParameterType());
    }
  }

  @Test
  @Order(3)
  public void testJsFunctionParameterUpdate() {
    String name = UUID.randomUUID().toString();
    JsFunctionParameterUpdate request =
        new JsFunctionParameterUpdate().setId(testJsFunctionParameter.getId()).setName(name);
    ResponseEntity<JsFunctionParameter> response =
        this.restTemplate.exchange(
            "/JsFunctionParameter/updateJsFunctionParameter",
            HttpMethod.PUT,
            new HttpEntity<>(request),
            JsFunctionParameter.class);
    Assertions.assertEquals(200, response.getStatusCodeValue());
    testJsFunctionParameter = response.getBody();
    assertJsFunctionParameter(request, testJsFunctionParameter);
  }
}
