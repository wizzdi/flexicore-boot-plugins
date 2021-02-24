package com.flexicore.territories;

import com.flexicore.model.territories.City;
import com.flexicore.model.territories.Country;
import com.flexicore.model.territories.Street;
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

public class StreetControllerTest {

    private Street street;
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


    public City cityCreate(State state, Country country) {
        String name = UUID.randomUUID().toString();
        CityCreate request = new CityCreate()
                .setCountryId(country.getId())
                .setStateId(state.getId())
                .setName(name);
        ResponseEntity<City> cityResponse = this.restTemplate.postForEntity("/plugins/city/createCity", request, City.class);
      return cityResponse.getBody();

    }
    @Test
    @Order(1)
    public void testStreetCreate() {
        Country country = countryCreate();
        State state = stateCreate(country);

        City city = cityCreate(state,country);
        String name = UUID.randomUUID().toString();
        StreetCreate request = new StreetCreate()
                .setCityId(city.getId())
                .setName(name);
        ResponseEntity<Street> streetResponse = this.restTemplate.postForEntity("/plugins/street/createStreet", request, Street.class);
        Assertions.assertEquals(200, streetResponse.getStatusCodeValue());
        street = streetResponse.getBody();
        assertStreet(request, street);

    }

    @Test
    @Order(2)
    public void testGetAllStreets() {
        StreetFilter request=new StreetFilter();
        ParameterizedTypeReference<PaginationResponse<Street>> t= new ParameterizedTypeReference<>() {
        };

        ResponseEntity<PaginationResponse<Street>> streetResponse = this.restTemplate.exchange("/plugins/street/getAllStreets", HttpMethod.POST, new HttpEntity<>(request), t);
        Assertions.assertEquals(200, streetResponse.getStatusCodeValue());
        PaginationResponse<Street> body = streetResponse.getBody();
        Assertions.assertNotNull(body);
        List<Street> streets = body.getList();
        Assertions.assertNotEquals(0,streets.size());
        Assertions.assertTrue(streets.stream().anyMatch(f->f.getId().equals(street.getId())));


    }

    public void assertStreet(StreetCreate request, Street street) {
        Assertions.assertNotNull(street);
        if(request.getName()!=null){
            Assertions.assertEquals(request.getName(), street.getName());
        }
        if(request.getCityId()!=null){
            Assertions.assertEquals(request.getCityId(), street.getCity().getId());
        }
        if(request.getExternalId()!=null){
            Assertions.assertEquals(request.getExternalId(), street.getExternalId());
        }
    }

    @Test
    @Order(3)
    public void testStreetUpdate(){
        String name = UUID.randomUUID().toString();
        StreetUpdate request = new StreetUpdate()
                .setId(street.getId())
                .setName(name);
        ResponseEntity<Street> streetResponse = this.restTemplate.exchange("/plugins/street/updateStreet",HttpMethod.PUT, new HttpEntity<>(request), Street.class);
        Assertions.assertEquals(200, streetResponse.getStatusCodeValue());
        street = streetResponse.getBody();
        assertStreet(request, street);

    }

}
