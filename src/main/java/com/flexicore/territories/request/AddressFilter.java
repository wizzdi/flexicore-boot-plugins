package com.flexicore.territories.request;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.flexicore.annotations.TypeRetention;
import com.flexicore.model.territories.*;
import com.wizzdi.flexicore.security.request.BasicPropertiesFilter;
import com.wizzdi.flexicore.security.request.PaginationFilter;

import java.util.HashSet;
import java.util.List;
import java.util.Set;


public class AddressFilter extends PaginationFilter {

    private Set<Integer> floors;
    private Set<String> streetsIds = new HashSet<>();
    @JsonIgnore
    @TypeRetention(Street.class)
    private List<Street> streets;
    private boolean streetsExclude;

    private Set<Integer> numbers;
    private Set<String> zipCodes;
    private Set<String> externalIds = new HashSet<>();
    private Set<String> stateIds=new HashSet<>();
    @JsonIgnore
    private List<State> states;
    private boolean statesExclude;

    private Set<String> countryIds=new HashSet<>();
    @JsonIgnore
    private List<Country> countries;
    private boolean countriesExclude;

    private Set<String> citiesIds=new HashSet<>();
    @JsonIgnore
    private List<City> cities;
    private boolean citiesExclude;

    @JsonIgnore
    private List<Neighbourhood> neighbourhoods;
    private boolean neighbourhoodsExclude;

    private Set<String> neighbourhoodIds=new HashSet<>();
    private BasicPropertiesFilter basicPropertiesFilter;


    public Set<Integer> getFloors() {
        return floors;
    }

    public <T extends AddressFilter> T setFloors(Set<Integer> floors) {
        this.floors = floors;
        return (T) this;
    }

    public Set<String> getStreetsIds() {
        return streetsIds;
    }

    public <T extends AddressFilter> T setStreetsIds(Set<String> streetsIds) {
        this.streetsIds = streetsIds;
        return (T) this;
    }

    @JsonIgnore
    public List<Street> getStreets() {
        return streets;
    }

    public <T extends AddressFilter> T setStreets(List<Street> streets) {
        this.streets = streets;
        return (T) this;
    }


    public Set<String> getZipCodes() {
        return zipCodes;
    }

    public <T extends AddressFilter> T setZipCodes(Set<String> zipCodes) {
        this.zipCodes = zipCodes;
        return (T) this;
    }

    public Set<String> getExternalIds() {
        return externalIds;
    }

    public <T extends AddressFilter> T setExternalIds(Set<String> externalIds) {
        this.externalIds = externalIds;
        return (T) this;
    }

    public BasicPropertiesFilter getBasicPropertiesFilter() {
        return basicPropertiesFilter;
    }

    public <T extends AddressFilter> T setBasicPropertiesFilter(BasicPropertiesFilter basicPropertiesFilter) {
        this.basicPropertiesFilter = basicPropertiesFilter;
        return (T) this;
    }

    public Set<String> getCitiesIds() {
        return citiesIds;
    }

    public <T extends AddressFilter> T setCitiesIds(Set<String> citiesIds) {
        this.citiesIds = citiesIds;
        return (T) this;
    }

    @JsonIgnore
    public List<City> getCities() {
        return cities;
    }

    public <T extends AddressFilter> T setCities(List<City> cities) {
        this.cities = cities;
        return (T) this;
    }

    @JsonIgnore
    public List<Neighbourhood> getNeighbourhoods() {
        return neighbourhoods;
    }

    public <T extends AddressFilter> T setNeighbourhoods(List<Neighbourhood> neighbourhoods) {
        this.neighbourhoods = neighbourhoods;
        return (T) this;
    }

    public Set<String> getNeighbourhoodIds() {
        return neighbourhoodIds;
    }

    public <T extends AddressFilter> T setNeighbourhoodIds(Set<String> neighbourhoodIds) {
        this.neighbourhoodIds = neighbourhoodIds;
        return (T) this;
    }

    public Set<String> getStateIds() {
        return stateIds;
    }

    public <T extends AddressFilter> T setStateIds(Set<String> stateIds) {
        this.stateIds = stateIds;
        return (T) this;
    }

    @JsonIgnore
    public List<State> getStates() {
        return states;
    }

    public <T extends AddressFilter> T setStates(List<State> states) {
        this.states = states;
        return (T) this;
    }

    public Set<String> getCountryIds() {
        return countryIds;
    }

    public <T extends AddressFilter> T setCountryIds(Set<String> countryIds) {
        this.countryIds = countryIds;
        return (T) this;
    }

    @JsonIgnore
    public List<Country> getCountries() {
        return countries;
    }

    public <T extends AddressFilter> T setCountries(List<Country> countries) {
        this.countries = countries;
        return (T) this;
    }

    public boolean isStreetsExclude() {
        return streetsExclude;
    }

    public <T extends AddressFilter> T setStreetsExclude(boolean streetsExclude) {
        this.streetsExclude = streetsExclude;
        return (T) this;
    }

    public boolean isStatesExclude() {
        return statesExclude;
    }

    public <T extends AddressFilter> T setStatesExclude(boolean statesExclude) {
        this.statesExclude = statesExclude;
        return (T) this;
    }

    public boolean isCountriesExclude() {
        return countriesExclude;
    }

    public <T extends AddressFilter> T setCountriesExclude(boolean countriesExclude) {
        this.countriesExclude = countriesExclude;
        return (T) this;
    }

    public boolean isCitiesExclude() {
        return citiesExclude;
    }

    public <T extends AddressFilter> T setCitiesExclude(boolean citiesExclude) {
        this.citiesExclude = citiesExclude;
        return (T) this;
    }

    public boolean isNeighbourhoodsExclude() {
        return neighbourhoodsExclude;
    }

    public <T extends AddressFilter> T setNeighbourhoodsExclude(boolean neighbourhoodsExclude) {
        this.neighbourhoodsExclude = neighbourhoodsExclude;
        return (T) this;
    }
}
