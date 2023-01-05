package com.wizzdi.maps.service.reverse.geocode;

import com.wizzdi.flexicore.boot.base.interfaces.Plugin;
import com.wizzdi.maps.service.interfaces.AddressInfo;
import com.wizzdi.maps.service.interfaces.ReverseGeoHashProvider;
import com.wizzdi.maps.service.reverse.geocode.response.Address;
import com.wizzdi.maps.service.reverse.geocode.response.ReverseGeoCodeResponse;
import org.pf4j.Extension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.Optional;

@Component
@Extension
public class NominatimReverseGeoHashProvider implements ReverseGeoHashProvider, Plugin {

    @Autowired
    @Qualifier("nominatimRestTemplate")
    private RestTemplate nominatimRestTemplate;
    @Override
    public AddressInfo getAddress(double lat, double lon) {
        ResponseEntity<ReverseGeoCodeResponse> response = nominatimRestTemplate.getForEntity("/reverse?lat={lat}&lon={lon}&format=json", ReverseGeoCodeResponse.class, lat, lon);
        if(!response.getStatusCode().is2xxSuccessful()){
            return null;
        }
        ReverseGeoCodeResponse body = response.getBody();
        if(body==null){
            return null;
        }
        return toAddressInfo(body);
    }

    private AddressInfo toAddressInfo(ReverseGeoCodeResponse body) {
        Address address = body.getAddress();
        if(address ==null){
            return null;
        }
        return new AddressInfo(new AddressInfo.CountryInfo(address.getCountry(),address.getCountryCode()),address.getState(),new AddressInfo.CityInfo(address.getCity(), address.getIso()), address.getSuburb(), address.getRoad(),address.getHouseNumber(),body.getDisplayName(),address.getPostCode(),body.getOsmId(),body.getLat(),body.getLon());
    }
}
