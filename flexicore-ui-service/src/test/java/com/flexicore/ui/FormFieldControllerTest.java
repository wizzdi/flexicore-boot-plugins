package com.flexicore.ui;


import com.flexicore.ui.app.App;
import com.flexicore.ui.model.Form;
import com.flexicore.ui.model.FormField;
import com.flexicore.ui.request.FormFieldCreate;
import com.flexicore.ui.request.FormFieldFiltering;
import com.flexicore.ui.request.FormFieldUpdate;
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

public class FormFieldControllerTest {

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
    private FormField formField;
    @Autowired
    private TestRestTemplate restTemplate;
    @Autowired
    private Form form;

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
    public void testFormFieldCreate() {
        String name = UUID.randomUUID().toString();
        FormFieldCreate request = new FormFieldCreate()
                .setPresetId(form.getId())
                .setName(name);
        ResponseEntity<FormField> formFieldResponse = this.restTemplate.postForEntity("/plugins/FormFields/createFormField", request, FormField.class);
        Assertions.assertEquals(200, formFieldResponse.getStatusCodeValue());
        formField = formFieldResponse.getBody();
        assertFormField(request, formField);

    }

    @Test
    @Order(2)
    public void testGetAllFormFields() {
        FormFieldFiltering request=new FormFieldFiltering();
        ParameterizedTypeReference<PaginationResponse<FormField>> t= new ParameterizedTypeReference<>() {
        };

        ResponseEntity<PaginationResponse<FormField>> formFieldResponse = this.restTemplate.exchange("/plugins/FormFields/getAllFormFields", HttpMethod.POST, new HttpEntity<>(request), t);
        Assertions.assertEquals(200, formFieldResponse.getStatusCodeValue());
        PaginationResponse<FormField> body = formFieldResponse.getBody();
        Assertions.assertNotNull(body);
        List<FormField> formFields = body.getList();
        Assertions.assertNotEquals(0,formFields.size());
        Assertions.assertTrue(formFields.stream().anyMatch(f->f.getId().equals(formField.getId())));


    }

    public void assertFormField(FormFieldCreate request, FormField formField) {
        Assertions.assertNotNull(formField);
        if(request.getName()!=null){
            Assertions.assertEquals(request.getName(), formField.getName());
        }
        if(request.getPresetId()!=null){
            Assertions.assertEquals(request.getPresetId(), formField.getPreset().getId());
        }
    }

    @Test
    @Order(3)
    public void testFormFieldUpdate(){
        String name = UUID.randomUUID().toString();
        FormFieldUpdate request = new FormFieldUpdate()
                .setId(formField.getId())
                .setName(name);
        ResponseEntity<FormField> formFieldResponse = this.restTemplate.exchange("/plugins/FormFields/updateFormField",HttpMethod.PUT, new HttpEntity<>(request), FormField.class);
        Assertions.assertEquals(200, formFieldResponse.getStatusCodeValue());
        formField = formFieldResponse.getBody();
        assertFormField(request, formField);

    }

}
