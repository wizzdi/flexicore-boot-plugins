package com.flexicore.territories;

import com.flexicore.model.territories.Country;
import com.flexicore.model.territories.State;
import com.flexicore.territories.app.App;
import com.flexicore.territories.request.CountryCreate;
import com.flexicore.territories.request.StateCreate;
import com.flexicore.territories.request.StateFilter;
import com.flexicore.territories.request.StateUpdate;
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

public class StateControllerTest {

    private State state;
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

    @Test
    @Order(1)
    public void testStateCreate() {
        Country country = countryCreate();
        String name = UUID.randomUUID().toString();
        StateCreate request = new StateCreate()
                .setCountryId(country.getId())
                .setName(name);
        ResponseEntity<State> stateResponse = this.restTemplate.postForEntity("/plugins/state/createState", request, State.class);
        Assertions.assertEquals(200, stateResponse.getStatusCodeValue());
        state = stateResponse.getBody();
        assertState(request, state);

    }

    @Test
    @Order(2)
    public void testGetAllStates() {
        StateFilter request=new StateFilter();
        ParameterizedTypeReference<PaginationResponse<State>> t= new ParameterizedTypeReference<>() {
        };

        ResponseEntity<PaginationResponse<State>> stateResponse = this.restTemplate.exchange("/plugins/state/getAllStates", HttpMethod.POST, new HttpEntity<>(request), t);
        Assertions.assertEquals(200, stateResponse.getStatusCodeValue());
        PaginationResponse<State> body = stateResponse.getBody();
        Assertions.assertNotNull(body);
        List<State> states = body.getList();
        Assertions.assertNotEquals(0,states.size());
        Assertions.assertTrue(states.stream().anyMatch(f->f.getId().equals(state.getId())));


    }

    public void assertState(StateCreate request, State state) {
        Assertions.assertNotNull(state);
        if(request.getName()!=null){
            Assertions.assertEquals(request.getName(), state.getName());
        }
        if(request.getExternalId()!=null){
            Assertions.assertEquals(request.getExternalId(), state.getExternalId());
        }
        if(request.getCountryId()!=null){
            Assertions.assertEquals(request.getCountryId(), state.getCountry().getId());
        }
    }

    @Test
    @Order(3)
    public void testStateUpdate(){
        String name = UUID.randomUUID().toString();
        StateUpdate request = new StateUpdate()
                .setId(state.getId())
                .setName(name);
        ResponseEntity<State> stateResponse = this.restTemplate.exchange("/plugins/state/updateState",HttpMethod.PUT, new HttpEntity<>(request), State.class);
        Assertions.assertEquals(200, stateResponse.getStatusCodeValue());
        state = stateResponse.getBody();
        assertState(request, state);

    }

}
