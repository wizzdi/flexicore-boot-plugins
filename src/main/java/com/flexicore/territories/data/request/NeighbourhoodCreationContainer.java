package com.flexicore.territories.data.request;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.flexicore.model.territories.City;
import com.flexicore.model.territories.Neighbourhood;
import com.flexicore.model.territories.Street;

public class NeighbourhoodCreationContainer {

    private String externalId;
    private String cityId;
    @JsonIgnore
    private City city;
    private String name;
    private String description;


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

    public String getName() {
        return name;
    }

    public NeighbourhoodCreationContainer setName(String name) {
        this.name = name;
        return this;
    }

    public String getDescription() {
        return description;
    }

    public NeighbourhoodCreationContainer setDescription(String description) {
        this.description = description;
        return this;
    }
}