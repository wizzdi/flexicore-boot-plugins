package com.wizzdi.maps.service.reverse.geocode.response;

import com.fasterxml.jackson.annotation.JsonProperty;

public class Address {
    @JsonProperty("house_number")
    private String houseNumber;
    private String road;
    private String suburb;
    private String residential;
    private String city;
    @JsonProperty("state_district")
    private String stateDistrict;
    private String state;
    @JsonProperty("ISO3166-2-lvl4")
    private String iso;
    @JsonProperty("postcode")
    private String postCode;
    private String country;
    @JsonProperty("country_code")
    private String countryCode;

    public String getHouseNumber() {
        return houseNumber;
    }

    public <T extends Address> T setHouseNumber(String houseNumber) {
        this.houseNumber = houseNumber;
        return (T) this;
    }

    public String getRoad() {
        return road;
    }

    public <T extends Address> T setRoad(String road) {
        this.road = road;
        return (T) this;
    }

    public String getSuburb() {
        return suburb;
    }

    public <T extends Address> T setSuburb(String suburb) {
        this.suburb = suburb;
        return (T) this;
    }

    public String getResidential() {
        return residential;
    }

    public <T extends Address> T setResidential(String residential) {
        this.residential = residential;
        return (T) this;
    }

    public String getCity() {
        return city;
    }

    public <T extends Address> T setCity(String city) {
        this.city = city;
        return (T) this;
    }

    public String getStateDistrict() {
        return stateDistrict;
    }

    public <T extends Address> T setStateDistrict(String stateDistrict) {
        this.stateDistrict = stateDistrict;
        return (T) this;
    }

    public String getState() {
        return state;
    }

    public <T extends Address> T setState(String state) {
        this.state = state;
        return (T) this;
    }

    public String getIso() {
        return iso;
    }

    public <T extends Address> T setIso(String iso) {
        this.iso = iso;
        return (T) this;
    }

    public String getPostCode() {
        return postCode;
    }

    public <T extends Address> T setPostCode(String postCode) {
        this.postCode = postCode;
        return (T) this;
    }

    public String getCountry() {
        return country;
    }

    public <T extends Address> T setCountry(String country) {
        this.country = country;
        return (T) this;
    }

    public String getCountryCode() {
        return countryCode;
    }

    public <T extends Address> T setCountryCode(String countryCode) {
        this.countryCode = countryCode;
        return (T) this;
    }
}
