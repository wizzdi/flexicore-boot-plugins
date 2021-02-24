package com.flexicore.territories;

import com.flexicore.model.territories.*;
import com.flexicore.territories.app.App;
import com.flexicore.territories.reponse.AddressImportResponse;
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

public class AddressControllerTest {

    private Address address;
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

    public Street streetCreate(City city) {
        String name = UUID.randomUUID().toString();
        StreetCreate request = new StreetCreate()
                .setCityId(city.getId())
                .setName(name);
        ResponseEntity<Street> streetResponse = this.restTemplate.postForEntity("/plugins/street/createStreet", request, Street.class);
      return streetResponse.getBody();

    }

    public Neighbourhood neighbourhoodCreate(Country country, State state, City city) {
        String name = UUID.randomUUID().toString();
        NeighbourhoodCreate request = new NeighbourhoodCreate()
                .setCityId(city.getId())
                .setName(name);
        ResponseEntity<Neighbourhood> neighbourhoodResponse = this.restTemplate.postForEntity("/plugins/neighbourhood/createNeighbourhood", request, Neighbourhood.class);
      return neighbourhoodResponse.getBody();

    }
    @Test
    @Order(1)
    public void testAddressCreate() {
        Country country = countryCreate();
        State state = stateCreate(country);

        City city = cityCreate(state,country);
        Street street = streetCreate( city);
        Neighbourhood neighbourhood = neighbourhoodCreate(country, state, city);
        String name = UUID.randomUUID().toString();
        AddressCreate request = new AddressCreate()
                .setExternalId(name)
                .setFloor(1)
                .setNumber(3)
                .setZipCode(name)
                .setStreetId(street.getId())
                .setNeighbourhoodId(neighbourhood.getId())
                .setName(name);
        ResponseEntity<Address> addressResponse = this.restTemplate.postForEntity("/plugins/address/createAddress", request, Address.class);
        Assertions.assertEquals(200, addressResponse.getStatusCodeValue());
        address = addressResponse.getBody();
        assertAddress(request, address);

    }

    @Test
    @Order(2)
    public void testGetAllAddresses() {
        AddressFilter request=new AddressFilter();
        ParameterizedTypeReference<PaginationResponse<Address>> t= new ParameterizedTypeReference<>() {};

        ResponseEntity<PaginationResponse<Address>> addressResponse = this.restTemplate.exchange("/plugins/address/getAllAddresses", HttpMethod.POST, new HttpEntity<>(request), t);
        Assertions.assertEquals(200, addressResponse.getStatusCodeValue());
        PaginationResponse<Address> body = addressResponse.getBody();
        Assertions.assertNotNull(body);
        List<Address> addresss = body.getList();
        Assertions.assertNotEquals(0,addresss.size());
        Assertions.assertTrue(addresss.stream().anyMatch(f->f.getId().equals(address.getId())));


    }

    public void assertAddress(AddressCreate request, Address address) {
        Assertions.assertNotNull(address);
        if(request.getName()!=null){
            Assertions.assertEquals(request.getName(), address.getName());

        }
        if(request.getNeighbourhoodId()!=null){
            Assertions.assertEquals(request.getNeighbourhoodId(), address.getNeighbourhood().getId());

        }
        if(request.getStreetId()!=null){
            Assertions.assertEquals(request.getStreetId(), address.getStreet().getId());

        }
        if(request.getZipCode()!=null){
            Assertions.assertEquals(request.getZipCode(), address.getZipCode());
        }
        if(request.getFloor()!=null){
            Assertions.assertEquals(request.getFloor(), address.getFloorForAddress());

        }
        if(request.getNumber()!=null){
            Assertions.assertEquals(request.getNumber(), address.getNumber());

        }


    }

    @Test
    @Order(3)
    public void testAddressUpdate(){
        String name = UUID.randomUUID().toString();
        AddressUpdate request = new AddressUpdate()
                .setId(address.getId())
                .setName(name);
        ResponseEntity<Address> addressResponse = this.restTemplate.exchange("/plugins/address/updateAddress",HttpMethod.PUT, new HttpEntity<>(request), Address.class);
        Assertions.assertEquals(200, addressResponse.getStatusCodeValue());
        address = addressResponse.getBody();
        assertAddress(request, address);

    }

    @Test
    @Order(3)
    public void testImportAddresses(){
        String name = UUID.randomUUID().toString();
        AddressImportRequest request = new AddressImportRequest()
                .setUrl("https://data.gov.il/dataset/321/resource/d04feead-6431-427f-81bc-d6a24151c1fb/download/d04feead-6431-427f-81bc-d6a24151c1fb.xml");

        ResponseEntity<AddressImportResponse> addressResponse = this.restTemplate.exchange("/plugins/address/importAddresses",HttpMethod.POST, new HttpEntity<>(request), AddressImportResponse.class);
        Assertions.assertEquals(200, addressResponse.getStatusCodeValue());
        AddressImportResponse importResponse = addressResponse.getBody();
        Assertions.assertNotNull(importResponse);
        Assertions.assertTrue(importResponse.getCreatedStreet() > 0);

    }

}
