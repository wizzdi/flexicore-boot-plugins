package com.wizzdi.maps.service.service;

import com.flexicore.model.territories.*;
import com.flexicore.security.SecurityContextBase;
import com.flexicore.territories.request.*;
import com.flexicore.territories.service.*;
import com.wizzdi.flexicore.boot.base.interfaces.Plugin;
import com.wizzdi.maps.service.interfaces.AddressInfo;
import com.wizzdi.maps.service.interfaces.ReverseGeoHashProvider;
import org.pf4j.Extension;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.Optional;

@Extension
@Component
public class ReverseGeoHashService implements Plugin {

    @Autowired
    private CountryService countryService;
    @Autowired
    private CityService cityService;
    @Autowired
    private StreetService streetService;
    @Autowired
    private StateService stateService;
    @Autowired
    private NeighbourhoodService neighbourhoodService;

    @Autowired
    private AddressService addressService;

    @Autowired
    private ObjectProvider<ReverseGeoHashProvider> reverseGeoHashProviders;
    public Address getAddress(double lat, double lon, SecurityContextBase securityContextBase){
        return reverseGeoHashProviders.stream().map(f->f.getAddress(lat, lon)).filter(f->f!=null).findFirst().map(f->getAddress(f,securityContextBase)).orElse(null);
    }

    public Address getAddress(AddressInfo addressInfo,SecurityContextBase securityContextBase){

        AddressInfo.CountryInfo countryInfo = addressInfo.countryInfo();
        Country country= countryService.listAllCountries(securityContextBase,new CountryFilter().setCountryCodes(Collections.singleton(countryInfo.countryCode()))).stream().findFirst()
                .orElseGet(()->countryService.createCountry(new CountryCreate().setCountryCode(countryInfo.countryCode()).setName(countryInfo.countryName()),securityContextBase));
        AddressInfo.CityInfo cityInfo = addressInfo.cityInfo();
        State state =Optional.ofNullable(addressInfo.state()).map(f->stateService.listAllStates(securityContextBase,new StateFilter().setCountries(Collections.singletonList(country)).setExternalIds(Collections.singleton(f))).stream().findFirst().orElseGet(()->stateService.createState(new StateCreate().setCountry(country).setExternalId(f).setName(f),securityContextBase))).orElse(null);

        City city= cityService.listAllCities(securityContextBase,new CityFilter().setExternalIds(Collections.singleton(cityInfo.cityISO()))).stream().findFirst()
                .orElseGet(()->cityService.createCity(new CityCreate().setState(state).setCountry(country).setExternalId(cityInfo.cityISO()).setName(cityInfo.cityName()),securityContextBase));

        Street street = streetService.listAllStreets(securityContextBase,new StreetFilter().setCities(Collections.singletonList(city)).setExternalIds(Collections.singleton(addressInfo.road()))).stream().findFirst()
                .orElseGet(()->streetService.createStreet(new StreetCreate().setCity(city).setExternalId(addressInfo.road()).setName(addressInfo.road()),securityContextBase));
        Neighbourhood neighbourhood=Optional.ofNullable(addressInfo.suburb()).map(f->neighbourhoodService.listAllNeighbourhoods(securityContextBase,new NeighbourhoodFilter().setCities(Collections.singletonList(city)).setExternalIds(Collections.singleton(f))).stream().findFirst().orElseGet(()->neighbourhoodService.createNeighbourhood(new NeighbourhoodCreate().setCity(city).setExternalId(f).setName(f),securityContextBase))).orElse(null);
        return addressService.listAllAddresses(securityContextBase,new AddressFilter().setExternalIds(Collections.singleton(addressInfo.uniqueId()))).stream().findFirst()
                .orElseGet(()->addressService.createAddress(new AddressCreate().setExternalId(addressInfo.uniqueId()).setStreet(street).setNeighbourhood(neighbourhood).setHouseNumber(addressInfo.houseNumber()).setZipCode(addressInfo.postCode()).setName(addressInfo.displayName()),securityContextBase));

    }
}
