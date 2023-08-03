package com.flexicore.rules.controller;



import com.flexicore.rules.App;
import com.flexicore.rules.model.JSFunction;
import com.flexicore.rules.model.JSFunctionParameter;
import com.flexicore.rules.request.JSFunctionParameterCreate;
import com.flexicore.rules.request.JSFunctionParameterFilter;
import com.flexicore.rules.request.JSFunctionParameterUpdate;
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
public class JSFunctionParameterControllerTest {

  private JSFunctionParameter testJSFunctionParameter;
  @Autowired private TestRestTemplate restTemplate;

  @Autowired private JSFunction jSFunction;

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
  public void testJSFunctionParameterCreate() {
    String name = UUID.randomUUID().toString();
    JSFunctionParameterCreate request = new JSFunctionParameterCreate().setName(name);

    request.setOrdinal(10);

    request.setParameterType("test-string");

    request.setJsFunctionId(this.jSFunction.getId());

    ResponseEntity<JSFunctionParameter> response =
        this.restTemplate.postForEntity(
            "/JSFunctionParameter/createJSFunctionParameter", request, JSFunctionParameter.class);
    Assertions.assertTrue(response.getStatusCode().is2xxSuccessful() );
    testJSFunctionParameter = response.getBody();
    assertJSFunctionParameter(request, testJSFunctionParameter);
  }

  @Test
  @Order(2)
  public void testListAllJSFunctionParameters() {
    JSFunctionParameterFilter request = new JSFunctionParameterFilter();
    ParameterizedTypeReference<PaginationResponse<JSFunctionParameter>> t =
        new ParameterizedTypeReference<>() {};

    ResponseEntity<PaginationResponse<JSFunctionParameter>> response =
        this.restTemplate.exchange(
            "/JSFunctionParameter/getAllJSFunctionParameters",
            HttpMethod.POST,
            new HttpEntity<>(request),
            t);
    Assertions.assertTrue(response.getStatusCode().is2xxSuccessful() );
    PaginationResponse<JSFunctionParameter> body = response.getBody();
    Assertions.assertNotNull(body);
    List<JSFunctionParameter> JSFunctionParameters = body.getList();
    Assertions.assertNotEquals(0, JSFunctionParameters.size());
    Assertions.assertTrue(
        JSFunctionParameters.stream()
            .anyMatch(f -> f.getId().equals(testJSFunctionParameter.getId())));
  }

  public void assertJSFunctionParameter(
      JSFunctionParameterCreate request, JSFunctionParameter testJSFunctionParameter) {
    Assertions.assertNotNull(testJSFunctionParameter);

    if (request.getOrdinal() != null) {
      Assertions.assertEquals(request.getOrdinal(), testJSFunctionParameter.getOrdinal());
    }

    if (request.getParameterType() != null) {
      Assertions.assertEquals(
          request.getParameterType(), testJSFunctionParameter.getParameterType());
    }

    if (request.getJsFunctionId() != null) {

      Assertions.assertNotNull(testJSFunctionParameter.getJsFunction());
      Assertions.assertEquals(
          request.getJsFunctionId(), testJSFunctionParameter.getJsFunction().getId());
    }
  }

  @Test
  @Order(3)
  public void testJSFunctionParameterUpdate() {
    String name = UUID.randomUUID().toString();
    JSFunctionParameterUpdate request =
        new JSFunctionParameterUpdate().setId(testJSFunctionParameter.getId()).setName(name);
    ResponseEntity<JSFunctionParameter> response =
        this.restTemplate.exchange(
            "/JSFunctionParameter/updateJSFunctionParameter",
            HttpMethod.PUT,
            new HttpEntity<>(request),
            JSFunctionParameter.class);
    Assertions.assertTrue(response.getStatusCode().is2xxSuccessful() );
    testJSFunctionParameter = response.getBody();
    assertJSFunctionParameter(request, testJSFunctionParameter);
  }
}
