package com.wizzdi.flexicore.pricing;

import com.wizzdi.flexicore.pricing.app.App;
import com.wizzdi.flexicore.pricing.model.price.Frequency;
import com.wizzdi.flexicore.pricing.request.FrequencyCreate;
import com.wizzdi.flexicore.pricing.request.FrequencyFiltering;
import com.wizzdi.flexicore.pricing.request.FrequencyUpdate;
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

public class FrequencyControllerTest {

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
    private Frequency frequency;
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
    public void testFrequencyCreate() {
        String name = UUID.randomUUID().toString();
        FrequencyCreate request = new FrequencyCreate()
                .setName(name);
        ResponseEntity<Frequency> frequencyResponse = this.restTemplate.postForEntity("/plugins/Frequency/createFrequency", request, Frequency.class);
        Assertions.assertEquals(200, frequencyResponse.getStatusCodeValue());
        frequency = frequencyResponse.getBody();
        assertFrequency(request, frequency);

    }

    @Test
    @Order(2)
    public void testListAllFrequencies() {
        FrequencyFiltering request=new FrequencyFiltering();
        ParameterizedTypeReference<PaginationResponse<Frequency>> t= new ParameterizedTypeReference<>() {
        };

        ResponseEntity<PaginationResponse<Frequency>> frequencyResponse = this.restTemplate.exchange("/plugins/Frequency/getAllFrequencies", HttpMethod.POST, new HttpEntity<>(request), t);
        Assertions.assertEquals(200, frequencyResponse.getStatusCodeValue());
        PaginationResponse<Frequency> body = frequencyResponse.getBody();
        Assertions.assertNotNull(body);
        List<Frequency> frequencys = body.getList();
        Assertions.assertNotEquals(0,frequencys.size());
        Assertions.assertTrue(frequencys.stream().anyMatch(f->f.getId().equals(frequency.getId())));


    }

    public void assertFrequency(FrequencyCreate request, Frequency frequency) {
        Assertions.assertNotNull(frequency);
        Assertions.assertEquals(request.getName(), frequency.getName());
    }

    @Test
    @Order(3)
    public void testFrequencyUpdate(){
        String name = UUID.randomUUID().toString();
        FrequencyUpdate request = new FrequencyUpdate()
                .setId(frequency.getId())
                .setName(name);
        ResponseEntity<Frequency> frequencyResponse = this.restTemplate.exchange("/plugins/Frequency/updateFrequency",HttpMethod.PUT, new HttpEntity<>(request), Frequency.class);
        Assertions.assertEquals(200, frequencyResponse.getStatusCodeValue());
        frequency = frequencyResponse.getBody();
        assertFrequency(request, frequency);

    }

}
