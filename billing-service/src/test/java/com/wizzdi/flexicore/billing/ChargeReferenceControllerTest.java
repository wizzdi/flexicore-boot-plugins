package com.wizzdi.flexicore.billing;

import com.wizzdi.flexicore.billing.app.App;
import com.wizzdi.flexicore.billing.request.ChargeReferenceCreate;
import com.wizzdi.flexicore.billing.request.ChargeReferenceFiltering;
import com.wizzdi.flexicore.billing.request.ChargeReferenceUpdate;
import com.wizzdi.flexicore.billing.model.billing.ChargeReference;
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

public class ChargeReferenceControllerTest {

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
    private ChargeReference chargeReference;
    @Autowired
    private TestRestTemplate restTemplate;

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
    public void testChargeReferenceCreate() {
        String name = UUID.randomUUID().toString();
        ChargeReferenceCreate request = new ChargeReferenceCreate()
                .setName(name);
        ResponseEntity<ChargeReference> chargeReferenceResponse = this.restTemplate.postForEntity("/plugins/ChargeReference/createChargeReference", request, ChargeReference.class);
        Assertions.assertEquals(200, chargeReferenceResponse.getStatusCodeValue());
        chargeReference = chargeReferenceResponse.getBody();
        assertChargeReference(request, chargeReference);

    }

    @Test
    @Order(2)
    public void testListAllChargeReferences() {
        ChargeReferenceFiltering request=new ChargeReferenceFiltering();
        ParameterizedTypeReference<PaginationResponse<ChargeReference>> t= new ParameterizedTypeReference<>() {
        };

        ResponseEntity<PaginationResponse<ChargeReference>> chargeReferenceResponse = this.restTemplate.exchange("/plugins/ChargeReference/getAllChargeReferences", HttpMethod.POST, new HttpEntity<>(request), t);
        Assertions.assertEquals(200, chargeReferenceResponse.getStatusCodeValue());
        PaginationResponse<ChargeReference> body = chargeReferenceResponse.getBody();
        Assertions.assertNotNull(body);
        List<ChargeReference> chargeReferences = body.getList();
        Assertions.assertNotEquals(0,chargeReferences.size());
        Assertions.assertTrue(chargeReferences.stream().anyMatch(f->f.getId().equals(chargeReference.getId())));


    }

    public void assertChargeReference(ChargeReferenceCreate request, ChargeReference chargeReference) {
        Assertions.assertNotNull(chargeReference);
        Assertions.assertEquals(request.getName(), chargeReference.getName());
    }

    @Test
    @Order(3)
    public void testChargeReferenceUpdate(){
        String name = UUID.randomUUID().toString();
        ChargeReferenceUpdate request = new ChargeReferenceUpdate()
                .setId(chargeReference.getId())
                .setName(name);
        ResponseEntity<ChargeReference> chargeReferenceResponse = this.restTemplate.exchange("/plugins/ChargeReference/updateChargeReference",HttpMethod.PUT, new HttpEntity<>(request), ChargeReference.class);
        Assertions.assertEquals(200, chargeReferenceResponse.getStatusCodeValue());
        chargeReference = chargeReferenceResponse.getBody();
        assertChargeReference(request, chargeReference);

    }

}
