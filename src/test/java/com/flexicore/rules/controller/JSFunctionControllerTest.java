package com.flexicore.rules.controller;

import com.flexicore.request.AuthenticationRequest;
import com.flexicore.response.AuthenticationResponse;
import com.flexicore.rules.App;
import com.flexicore.rules.model.JSFunction;
import com.flexicore.rules.request.JSFunctionCreate;
import com.flexicore.rules.request.JSFunctionFilter;
import com.flexicore.rules.request.JSFunctionUpdate;
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
public class JSFunctionControllerTest {

  private JSFunction testJSFunction;
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
  public void testJSFunctionCreate() {
    String name = UUID.randomUUID().toString();
    JSFunctionCreate request = new JSFunctionCreate().setName(name);

    request.setReturnType("test-string");

    request.setMethodName("test-string");

    ResponseEntity<JSFunction> response =
        this.restTemplate.postForEntity("/JSFunction/createJSFunction", request, JSFunction.class);
    Assertions.assertEquals(200, response.getStatusCodeValue());
    testJSFunction = response.getBody();
    assertJSFunction(request, testJSFunction);
  }

  @Test
  @Order(2)
  public void testListAllJSFunctions() {
    JSFunctionFilter request = new JSFunctionFilter();
    ParameterizedTypeReference<PaginationResponse<JSFunction>> t =
        new ParameterizedTypeReference<>() {};

    ResponseEntity<PaginationResponse<JSFunction>> response =
        this.restTemplate.exchange(
            "/JSFunction/getAllJSFunctions", HttpMethod.POST, new HttpEntity<>(request), t);
    Assertions.assertEquals(200, response.getStatusCodeValue());
    PaginationResponse<JSFunction> body = response.getBody();
    Assertions.assertNotNull(body);
    List<JSFunction> JSFunctions = body.getList();
    Assertions.assertNotEquals(0, JSFunctions.size());
    Assertions.assertTrue(
        JSFunctions.stream().anyMatch(f -> f.getId().equals(testJSFunction.getId())));
  }

  public void assertJSFunction(JSFunctionCreate request, JSFunction testJSFunction) {
    Assertions.assertNotNull(testJSFunction);

    if (request.getReturnType() != null) {
      Assertions.assertEquals(request.getReturnType(), testJSFunction.getReturnType());
    }

    if (request.getMethodName() != null) {
      Assertions.assertEquals(request.getMethodName(), testJSFunction.getMethodName());
    }
  }

  @Test
  @Order(3)
  public void testJSFunctionUpdate() {
    String name = UUID.randomUUID().toString();
    JSFunctionUpdate request = new JSFunctionUpdate().setId(testJSFunction.getId()).setName(name);
    ResponseEntity<JSFunction> response =
        this.restTemplate.exchange(
            "/JSFunction/updateJSFunction",
            HttpMethod.PUT,
            new HttpEntity<>(request),
            JSFunction.class);
    Assertions.assertEquals(200, response.getStatusCodeValue());
    testJSFunction = response.getBody();
    assertJSFunction(request, testJSFunction);
  }
}
