package com.flexicore.rules.controller;

import com.flexicore.request.AuthenticationRequest;
import com.flexicore.response.AuthenticationResponse;
import com.flexicore.rules.App;
import com.flexicore.rules.model.DataSource;
import com.flexicore.rules.request.DataSourceCreate;
import com.flexicore.rules.request.DataSourceFilter;
import com.flexicore.rules.request.DataSourceUpdate;
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
public class DataSourceControllerTest {

  private DataSource testDataSource;
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
  public void testDataSourceCreate() {
    String name = UUID.randomUUID().toString();
    DataSourceCreate request = new DataSourceCreate().setName(name);

    ResponseEntity<DataSource> response =
        this.restTemplate.postForEntity("/DataSource/createDataSource", request, DataSource.class);
    Assertions.assertEquals(200, response.getStatusCodeValue());
    testDataSource = response.getBody();
    assertDataSource(request, testDataSource);
  }

  @Test
  @Order(2)
  public void testListAllDataSources() {
    DataSourceFilter request = new DataSourceFilter();
    ParameterizedTypeReference<PaginationResponse<DataSource>> t =
        new ParameterizedTypeReference<>() {};

    ResponseEntity<PaginationResponse<DataSource>> response =
        this.restTemplate.exchange(
            "/DataSource/getAllDataSources", HttpMethod.POST, new HttpEntity<>(request), t);
    Assertions.assertEquals(200, response.getStatusCodeValue());
    PaginationResponse<DataSource> body = response.getBody();
    Assertions.assertNotNull(body);
    List<DataSource> DataSources = body.getList();
    Assertions.assertNotEquals(0, DataSources.size());
    Assertions.assertTrue(
        DataSources.stream().anyMatch(f -> f.getId().equals(testDataSource.getId())));
  }

  public void assertDataSource(DataSourceCreate request, DataSource testDataSource) {
    Assertions.assertNotNull(testDataSource);
  }

  @Test
  @Order(3)
  public void testDataSourceUpdate() {
    String name = UUID.randomUUID().toString();
    DataSourceUpdate request = new DataSourceUpdate().setId(testDataSource.getId()).setName(name);
    ResponseEntity<DataSource> response =
        this.restTemplate.exchange(
            "/DataSource/updateDataSource",
            HttpMethod.PUT,
            new HttpEntity<>(request),
            DataSource.class);
    Assertions.assertEquals(200, response.getStatusCodeValue());
    testDataSource = response.getBody();
    assertDataSource(request, testDataSource);
  }
}
