package com.flexicore.rules.controller;



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
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT, classes = App.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@Testcontainers
@ActiveProfiles("test")
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE) // deactivate the default behaviour
public class JSFunctionControllerTest {

  private JSFunction testJSFunction;
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
  public void testJSFunctionCreate() {
    String name = UUID.randomUUID().toString();
    JSFunctionCreate request = new JSFunctionCreate().setName(name);

    request.setReturnType("test-string");

    request.setMethodName("test-string");

    ResponseEntity<JSFunction> response =
        this.restTemplate.postForEntity("/JSFunction/createJSFunction", request, JSFunction.class);
    Assertions.assertTrue(response.getStatusCode().is2xxSuccessful() );
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
    Assertions.assertTrue(response.getStatusCode().is2xxSuccessful() );
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
    Assertions.assertTrue(response.getStatusCode().is2xxSuccessful() );
    testJSFunction = response.getBody();
    assertJSFunction(request, testJSFunction);
  }
}
