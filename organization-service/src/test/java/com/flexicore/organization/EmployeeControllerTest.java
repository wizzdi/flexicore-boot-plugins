package com.flexicore.organization;

import com.flexicore.organization.app.App;
import com.flexicore.organization.model.Employee;
import com.flexicore.organization.model.Organization;
import com.flexicore.organization.request.EmployeeCreate;
import com.flexicore.organization.request.EmployeeFiltering;
import com.flexicore.organization.request.EmployeeUpdate;
import com.wizzdi.flexicore.security.response.PaginationResponse;
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

import java.util.Collections;
import java.util.List;
import java.util.UUID;

@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT, classes = App.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@Testcontainers
@ActiveProfiles("test")
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE) // deactivate the default behaviour

public class EmployeeControllerTest {

    private Employee employee;
    @Autowired
    private TestRestTemplate restTemplate;
    @Autowired
    private Organization organization;

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
    public void testEmployeeCreate() {
        String name = UUID.randomUUID().toString();
        EmployeeCreate request = new EmployeeCreate()
                .setOrganizationId(organization.getId())
                .setName(name);
        ResponseEntity<Employee> employeeResponse = this.restTemplate.postForEntity("/plugins/Employee/createEmployee", request, Employee.class);
        Assertions.assertEquals(200, employeeResponse.getStatusCodeValue());
        employee = employeeResponse.getBody();
        assertEmployee(request, employee);

    }

    @Test
    @Order(2)
    public void testListAllEmployees() {
        EmployeeFiltering request=new EmployeeFiltering();
        ParameterizedTypeReference<PaginationResponse<Employee>> t= new ParameterizedTypeReference<>() {
        };

        ResponseEntity<PaginationResponse<Employee>> employeeResponse = this.restTemplate.exchange("/plugins/Employee/listAllEmployees", HttpMethod.POST, new HttpEntity<>(request), t);
        Assertions.assertEquals(200, employeeResponse.getStatusCodeValue());
        PaginationResponse<Employee> body = employeeResponse.getBody();
        Assertions.assertNotNull(body);
        List<Employee> employees = body.getList();
        Assertions.assertNotEquals(0,employees.size());
        Assertions.assertTrue(employees.stream().anyMatch(f->f.getId().equals(employee.getId())));


    }

    public void assertEmployee(EmployeeCreate request, Employee employee) {
        Assertions.assertNotNull(employee);
        if(request.getName()!=null){
            Assertions.assertEquals(request.getName(), employee.getName());
        }
        if(request.getOrganizationId()!=null){
            Assertions.assertEquals(request.getOrganizationId(), employee.getOrganization().getId());

        }
    }

    @Test
    @Order(3)
    public void testEmployeeUpdate(){
        String name = UUID.randomUUID().toString();
        EmployeeUpdate request = new EmployeeUpdate()
                .setId(employee.getId())
                .setName(name);
        ResponseEntity<Employee> employeeResponse = this.restTemplate.exchange("/plugins/Employee/updateEmployee",HttpMethod.PUT, new HttpEntity<>(request), Employee.class);
        Assertions.assertEquals(200, employeeResponse.getStatusCodeValue());
        employee = employeeResponse.getBody();
        assertEmployee(request, employee);

    }

}
