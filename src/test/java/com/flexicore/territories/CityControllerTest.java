package com.flexicore.territories;

import com.flexicore.model.territories.Country;
import com.flexicore.model.territories.City;
import com.flexicore.model.territories.State;
import com.flexicore.territories.app.App;
import com.flexicore.territories.request.*;
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
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.Collections;
import java.util.List;
import java.util.UUID;

@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT, classes = App.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@ActiveProfiles("test")

public class CityControllerTest {

    private City city;
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

    public Country countryCreate() {
        String name = UUID.randomUUID().toString();
        CountryCreate request = new CountryCreate()
                .setName(name);
        ResponseEntity<Country> countryResponse = this.restTemplate.postForEntity("/plugins/country/createCountry", request, Country.class);
        return countryResponse.getBody();

    }
    public State stateCreate(Country country) {
        String name = UUID.randomUUID().toString();
        StateCreate request = new StateCreate()
                .setCountryId(country.getId())
                .setName(name);
        ResponseEntity<State> stateResponse = this.restTemplate.postForEntity("/plugins/state/createState", request, State.class);
        return stateResponse.getBody();

    }

    @Test
    @Order(1)
    public void testCityCreate() {
        Country country = countryCreate();
        State state = stateCreate(country);
        String name = UUID.randomUUID().toString();
        CityCreate request = new CityCreate()
                .setCountryId(country.getId())
                .setStateId(state.getId())
                .setName(name);
        ResponseEntity<City> cityResponse = this.restTemplate.postForEntity("/plugins/city/createCity", request, City.class);
        Assertions.assertEquals(200, cityResponse.getStatusCodeValue());
        city = cityResponse.getBody();
        assertCity(request, city);

    }

    @Test
    @Order(2)
    public void testGetAllCities() {
        CityFilter request=new CityFilter();
        ParameterizedTypeReference<PaginationResponse<City>> t= new ParameterizedTypeReference<>() {
        };

        ResponseEntity<PaginationResponse<City>> cityResponse = this.restTemplate.exchange("/plugins/city/getAllCities", HttpMethod.POST, new HttpEntity<>(request), t);
        Assertions.assertEquals(200, cityResponse.getStatusCodeValue());
        PaginationResponse<City> body = cityResponse.getBody();
        Assertions.assertNotNull(body);
        List<City> citys = body.getList();
        Assertions.assertNotEquals(0,citys.size());
        Assertions.assertTrue(citys.stream().anyMatch(f->f.getId().equals(city.getId())));


    }

    public void assertCity(CityCreate request, City city) {
        Assertions.assertNotNull(city);
        if(request.getName()!=null){
            Assertions.assertEquals(request.getName(), city.getName());
        }
        if(request.getCountryId()!=null){
            Assertions.assertEquals(request.getCountryId(), city.getCountry().getId());
        }
        if(request.getExternalId()!=null){
            Assertions.assertEquals(request.getExternalId(), city.getExternalId());
        }
        if(request.getStateId()!=null){
            Assertions.assertEquals(request.getStateId(), city.getState().getId());
        }

    }

    @Test
    @Order(3)
    public void testCityUpdate(){
        String name = UUID.randomUUID().toString();
        CityUpdate request = new CityUpdate()
                .setId(city.getId())
                .setName(name);
        ResponseEntity<City> cityResponse = this.restTemplate.exchange("/plugins/city/updateCity",HttpMethod.PUT, new HttpEntity<>(request), City.class);
        Assertions.assertEquals(200, cityResponse.getStatusCodeValue());
        city = cityResponse.getBody();
        assertCity(request, city);

    }

}
