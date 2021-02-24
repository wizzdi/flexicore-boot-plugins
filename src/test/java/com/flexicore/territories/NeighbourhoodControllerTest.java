package com.flexicore.territories;

import com.flexicore.model.territories.City;
import com.flexicore.model.territories.Neighbourhood;
import com.flexicore.model.territories.Country;
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

public class NeighbourhoodControllerTest {

    private Neighbourhood neighbourhood;
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
    public void testNeighbourhoodCreate() {
        Country country = countryCreate();
        State state = stateCreate(country);

        City city = cityCreate(state,country);
        String name = UUID.randomUUID().toString();
        NeighbourhoodCreate request = new NeighbourhoodCreate()
                .setCityId(city.getId())
                .setName(name);
        ResponseEntity<Neighbourhood> neighbourhoodResponse = this.restTemplate.postForEntity("/plugins/neighbourhood/createNeighbourhood", request, Neighbourhood.class);
        Assertions.assertEquals(200, neighbourhoodResponse.getStatusCodeValue());
        neighbourhood = neighbourhoodResponse.getBody();
        assertNeighbourhood(request, neighbourhood);

    }

    @Test
    @Order(2)
    public void testGetAllNeighbourhoods() {
        NeighbourhoodFilter request=new NeighbourhoodFilter();
        ParameterizedTypeReference<PaginationResponse<Neighbourhood>> t= new ParameterizedTypeReference<>() {
        };

        ResponseEntity<PaginationResponse<Neighbourhood>> neighbourhoodResponse = this.restTemplate.exchange("/plugins/neighbourhood/getAllNeighbourhoods", HttpMethod.POST, new HttpEntity<>(request), t);
        Assertions.assertEquals(200, neighbourhoodResponse.getStatusCodeValue());
        PaginationResponse<Neighbourhood> body = neighbourhoodResponse.getBody();
        Assertions.assertNotNull(body);
        List<Neighbourhood> neighbourhoods = body.getList();
        Assertions.assertNotEquals(0,neighbourhoods.size());
        Assertions.assertTrue(neighbourhoods.stream().anyMatch(f->f.getId().equals(neighbourhood.getId())));


    }

    public void assertNeighbourhood(NeighbourhoodCreate request, Neighbourhood neighbourhood) {
        Assertions.assertNotNull(neighbourhood);
        if(request.getName()!=null){
            Assertions.assertEquals(request.getName(), neighbourhood.getName());
        }
        if(request.getExternalId()!=null){
            Assertions.assertEquals(request.getExternalId(), neighbourhood.getExternalId());
        }
        if(request.getCityId()!=null){
            Assertions.assertEquals(request.getCityId(), neighbourhood.getCity().getId());
        }

    }

    @Test
    @Order(3)
    public void testNeighbourhoodUpdate(){
        String name = UUID.randomUUID().toString();
        NeighbourhoodUpdate request = new NeighbourhoodUpdate()
                .setId(neighbourhood.getId())
                .setName(name);
        ResponseEntity<Neighbourhood> neighbourhoodResponse = this.restTemplate.exchange("/plugins/neighbourhood/updateNeighbourhood",HttpMethod.PUT, new HttpEntity<>(request), Neighbourhood.class);
        Assertions.assertEquals(200, neighbourhoodResponse.getStatusCodeValue());
        neighbourhood = neighbourhoodResponse.getBody();
        assertNeighbourhood(request, neighbourhood);

    }

}
