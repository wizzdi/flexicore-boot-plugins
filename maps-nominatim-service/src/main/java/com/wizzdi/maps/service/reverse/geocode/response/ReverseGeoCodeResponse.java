package com.wizzdi.maps.service.reverse.geocode.response;

import com.fasterxml.jackson.annotation.JsonProperty;

public class ReverseGeoCodeResponse {

    @JsonProperty("place_id")
    private String placeId;
    private String license;
    @JsonProperty("osm_type")
    private String osmType;
    @JsonProperty("osm_id")
    private String osmId;

    private double lat;
    private double lon;
    @JsonProperty("display_name")
    private String displayName;
    private Address address;
    private double[] boundingBox;

    public String getPlaceId() {
        return placeId;
    }

    public <T extends ReverseGeoCodeResponse> T setPlaceId(String placeId) {
        this.placeId = placeId;
        return (T) this;
    }

    public String getLicense() {
        return license;
    }

    public <T extends ReverseGeoCodeResponse> T setLicense(String license) {
        this.license = license;
        return (T) this;
    }

    public String getOsmType() {
        return osmType;
    }

    public <T extends ReverseGeoCodeResponse> T setOsmType(String osmType) {
        this.osmType = osmType;
        return (T) this;
    }

    public String getOsmId() {
        return osmId;
    }

    public <T extends ReverseGeoCodeResponse> T setOsmId(String osmId) {
        this.osmId = osmId;
        return (T) this;
    }

    public double getLat() {
        return lat;
    }

    public <T extends ReverseGeoCodeResponse> T setLat(double lat) {
        this.lat = lat;
        return (T) this;
    }

    public double getLon() {
        return lon;
    }

    public <T extends ReverseGeoCodeResponse> T setLon(double lon) {
        this.lon = lon;
        return (T) this;
    }

    public String getDisplayName() {
        return displayName;
    }

    public <T extends ReverseGeoCodeResponse> T setDisplayName(String displayName) {
        this.displayName = displayName;
        return (T) this;
    }

    public Address getAddress() {
        return address;
    }

    public <T extends ReverseGeoCodeResponse> T setAddress(Address address) {
        this.address = address;
        return (T) this;
    }

    public double[] getBoundingBox() {
        return boundingBox;
    }

    public <T extends ReverseGeoCodeResponse> T setBoundingBox(double[] boundingBox) {
        this.boundingBox = boundingBox;
        return (T) this;
    }
}
