package com.flexicore.rules.controller;

import com.flexicore.request.AuthenticationRequest;
import com.flexicore.response.AuthenticationResponse;
import com.flexicore.rules.App;
import com.flexicore.rules.model.JsFunction;
import com.flexicore.rules.request.JsFunctionCreate;
import com.flexicore.rules.request.JsFunctionFilter;
import com.flexicore.rules.request.JsFunctionUpdate;
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
public class JsFunctionControllerTest {

  private JsFunction testJsFunction;
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
  public void testJsFunctionCreate() {
    String name = UUID.randomUUID().toString();
    JsFunctionCreate request = new JsFunctionCreate().setName(name);

    request.setReturnType("test-string");

    request.setMethodName("test-string");

    ResponseEntity<JsFunction> response =
        this.restTemplate.postForEntity("/JsFunction/createJsFunction", request, JsFunction.class);
    Assertions.assertEquals(200, response.getStatusCodeValue());
    testJsFunction = response.getBody();
    assertJsFunction(request, testJsFunction);
  }

  @Test
  @Order(2)
  public void testListAllJsFunctions() {
    JsFunctionFilter request = new JsFunctionFilter();
    ParameterizedTypeReference<PaginationResponse<JsFunction>> t =
        new ParameterizedTypeReference<>() {};

    ResponseEntity<PaginationResponse<JsFunction>> response =
        this.restTemplate.exchange(
            "/JsFunction/getAllJsFunctions", HttpMethod.POST, new HttpEntity<>(request), t);
    Assertions.assertEquals(200, response.getStatusCodeValue());
    PaginationResponse<JsFunction> body = response.getBody();
    Assertions.assertNotNull(body);
    List<JsFunction> JsFunctions = body.getList();
    Assertions.assertNotEquals(0, JsFunctions.size());
    Assertions.assertTrue(
        JsFunctions.stream().anyMatch(f -> f.getId().equals(testJsFunction.getId())));
  }

  public void assertJsFunction(JsFunctionCreate request, JsFunction testJsFunction) {
    Assertions.assertNotNull(testJsFunction);

    if (request.getReturnType() != null) {

      Assertions.assertEquals(request.getReturnType(), testJsFunction.getReturnType());
    }

    if (request.getMethodName() != null) {

      Assertions.assertEquals(request.getMethodName(), testJsFunction.getMethodName());
    }
  }

  @Test
  @Order(3)
  public void testJsFunctionUpdate() {
    String name = UUID.randomUUID().toString();
    JsFunctionUpdate request = new JsFunctionUpdate().setId(testJsFunction.getId()).setName(name);
    ResponseEntity<JsFunction> response =
        this.restTemplate.exchange(
            "/JsFunction/updateJsFunction",
            HttpMethod.PUT,
            new HttpEntity<>(request),
            JsFunction.class);
    Assertions.assertEquals(200, response.getStatusCodeValue());
    testJsFunction = response.getBody();
    assertJsFunction(request, testJsFunction);
  }
}
