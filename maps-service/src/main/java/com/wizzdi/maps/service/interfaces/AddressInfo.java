package com.wizzdi.maps.service.interfaces;

public record AddressInfo(CountryInfo countryInfo,String state,  CityInfo cityInfo,String suburb,
                         String road, String houseNumber, String displayName, String postCode, String uniqueId,
                          double lat, double lon) {

    public record CountryInfo(String countryName,String countryCode){};
    public record CityInfo(String cityName,String cityISO){};


}
