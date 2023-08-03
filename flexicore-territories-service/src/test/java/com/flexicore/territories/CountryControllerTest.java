package com.flexicore.territories;

import com.flexicore.model.territories.Country;
import com.flexicore.territories.app.App;

import com.flexicore.territories.reponse.ImportCountriesResponse;
import com.flexicore.territories.request.CountryCreate;
import com.flexicore.territories.request.CountryFilter;
import com.flexicore.territories.request.CountryUpdate;
import com.flexicore.territories.request.ImportCountriesRequest;
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

public class CountryControllerTest {

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


    private Country country;
    @Autowired
    private TestRestTemplate restTemplate;

    @BeforeAll
    private void init() {
        restTemplate.getRestTemplate().setInterceptors(
                Collections.singletonList((request, body, execution) -> {
                    request.getHeaders()
                            .add("authenticationKey", "fake");
                    return execution.execute(request, body);
                }));

    }

    @Test
    @Order(1)
    public void testCountryCreate() {
        String name = UUID.randomUUID().toString();
        CountryCreate request = new CountryCreate()
                .setName(name);
        ResponseEntity<Country> countryResponse = this.restTemplate.postForEntity("/plugins/country/createCountry", request, Country.class);
        Assertions.assertEquals(200, countryResponse.getStatusCodeValue());
        country = countryResponse.getBody();
        assertCountry(request, country);

    }

    @Test
    @Order(2)
    public void testGetAllCountries() {
        CountryFilter request=new CountryFilter();
        ParameterizedTypeReference<PaginationResponse<Country>> t= new ParameterizedTypeReference<>() {
        };

        ResponseEntity<PaginationResponse<Country>> countryResponse = this.restTemplate.exchange("/plugins/country/getAllCountries", HttpMethod.POST, new HttpEntity<>(request), t);
        Assertions.assertEquals(200, countryResponse.getStatusCodeValue());
        PaginationResponse<Country> body = countryResponse.getBody();
        Assertions.assertNotNull(body);
        List<Country> countrys = body.getList();
        Assertions.assertNotEquals(0,countrys.size());
        Assertions.assertTrue(countrys.stream().anyMatch(f->f.getId().equals(country.getId())));


    }

    public void assertCountry(CountryCreate request, Country country) {
        Assertions.assertNotNull(country);
        if(request.getName()!=null){
            Assertions.assertEquals(request.getName(), country.getName());
        }
        if(request.getCountryCode()!=null){
            Assertions.assertEquals(request.getCountryCode(), country.getCountryCode());
        }

    }

    @Test
    @Order(3)
    public void testCountryUpdate(){
        String name = UUID.randomUUID().toString();
        CountryUpdate request = new CountryUpdate()
                .setId(country.getId())
                .setName(name);
        ResponseEntity<Country> countryResponse = this.restTemplate.exchange("/plugins/country/updateCountry",HttpMethod.PUT, new HttpEntity<>(request), Country.class);
        Assertions.assertEquals(200, countryResponse.getStatusCodeValue());
        country = countryResponse.getBody();
        assertCountry(request, country);

    }

    @Test
    @Order(4)
    public void testImportCountries(){
        ImportCountriesRequest request = new ImportCountriesRequest();
        ResponseEntity<ImportCountriesResponse> countryResponse = this.restTemplate.exchange("/plugins/country/importCountries",HttpMethod.POST, new HttpEntity<>(request), ImportCountriesResponse.class);
        Assertions.assertEquals(200, countryResponse.getStatusCodeValue());
        ImportCountriesResponse country = countryResponse.getBody();
        Assertions.assertNotNull(country);
        Assertions.assertTrue(country.getCreatedCountries()>0);

    }

}
