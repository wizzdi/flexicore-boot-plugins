package com.wizzdi.maps.service.interfaces;

public interface ReverseGeoHashProvider {

    AddressInfo getAddress(double lat, double lon);

}
