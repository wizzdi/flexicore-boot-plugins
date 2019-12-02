package com.flexicore.territories.request;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.flexicore.model.FilteringInformationHolder;
import com.flexicore.model.territories.City;

public class NeighbourhoodFiltering extends FilteringInformationHolder {

    private String externalId;
    private String cityId;
    @JsonIgnore
    private City city;


    public String getExternalId() {
        return externalId;
    }

    public NeighbourhoodFiltering setExternalId(String externalId) {
        this.externalId = externalId;
        return this;
    }

    public String getCityId() {
        return cityId;
    }

    public NeighbourhoodFiltering setCityId(String cityId) {
        this.cityId = cityId;
        return this;
    }

    @JsonIgnore
    public City getCity() {
        return city;
    }

    public NeighbourhoodFiltering setCity(City city) {
        this.city = city;
        return this;
    }

}