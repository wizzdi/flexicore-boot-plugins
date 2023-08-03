package com.flexicore.rules.controller;



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
public class DataSourceControllerTest {

  private DataSource testDataSource;
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
  public void testDataSourceCreate() {
    String name = UUID.randomUUID().toString();
    DataSourceCreate request = new DataSourceCreate().setName(name);

    ResponseEntity<DataSource> response =
        this.restTemplate.postForEntity("/DataSource/createDataSource", request, DataSource.class);
    Assertions.assertTrue(response.getStatusCode().is2xxSuccessful() );
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
    Assertions.assertTrue(response.getStatusCode().is2xxSuccessful() );
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
    Assertions.assertTrue(response.getStatusCode().is2xxSuccessful() );
    testDataSource = response.getBody();
    assertDataSource(request, testDataSource);
  }
}
