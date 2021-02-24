package com.flexicore.territories.request;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.flexicore.model.territories.Street;
import com.wizzdi.flexicore.security.request.BasicPropertiesFilter;
import com.wizzdi.flexicore.security.request.PaginationFilter;

import java.util.HashSet;
import java.util.List;
import java.util.Set;


public class AddressFilter extends PaginationFilter {

    private Set<Integer> floors;
    private Set<String> streetsIds = new HashSet<>();
    @JsonIgnore
    private List<Street> streets;
    private Set<Integer> numbers;
    private Set<String> zipCodes;
    private Set<String> externalIds = new HashSet<>();
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

    public Set<Integer> getNumbers() {
        return numbers;
    }

    public <T extends AddressFilter> T setNumbers(Set<Integer> numbers) {
        this.numbers = numbers;
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
}