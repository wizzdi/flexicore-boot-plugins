package com.flexicore.territories.request;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.flexicore.model.territories.City;
import com.flexicore.request.BaseclassCreate;

public class NeighbourhoodCreationContainer extends BaseclassCreate {

    private String externalId;
    private String cityId;
    @JsonIgnore
    private City city;



    public String getExternalId() {
        return externalId;
    }

    public NeighbourhoodCreationContainer setExternalId(String externalId) {
        this.externalId = externalId;
        return this;
    }

    public String getCityId() {
        return cityId;
    }

    public NeighbourhoodCreationContainer setCityId(String cityId) {
        this.cityId = cityId;
        return this;
    }

    @JsonIgnore
    public City getCity() {
        return city;
    }

    public NeighbourhoodCreationContainer setCity(City city) {
        this.city = city;
        return this;
    }


}