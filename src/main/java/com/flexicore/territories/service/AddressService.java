package com.flexicore.territories.service;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import com.flexicore.annotations.plugins.PluginInfo;
import com.flexicore.data.jsoncontainers.PaginationResponse;

import java.io.IOException;
import java.io.InputStream;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.flexicore.model.territories.*;
import com.flexicore.territories.data.AddressRepository;

import javax.inject.Inject;
import javax.ws.rs.BadRequestException;

import com.flexicore.model.Baseclass;
import com.flexicore.territories.request.*;
import com.flexicore.territories.interfaces.IAddressService;
import com.flexicore.security.SecurityContext;
import com.flexicore.model.QueryInformationHolder;
import com.flexicore.territories.reponse.AddressImportResponse;
import com.flexicore.territories.request.AddressImportRequest;
import com.flexicore.territories.request.StreetEntry;
import com.google.api.client.http.HttpStatusCodes;
import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;

@PluginInfo(version = 1)
public class AddressService implements IAddressService {

    @Inject
    @PluginInfo(version = 1)
    private AddressRepository repository;
    @Inject
    private Logger logger;

    @Inject
    @PluginInfo(version = 1)
    private CityService cityService;
    @Inject
    @PluginInfo(version = 1)
    private StreetService streetService;

    @Inject
    @PluginInfo(version = 1)
    private CountryService countryService;

    @Override
    public <T extends Baseclass> T getByIdOrNull(java.lang.String id,
                                                 Class<T> c, List<String> batch, SecurityContext securityContext) {
        return repository.getByIdOrNull(id, c, batch, securityContext);
    }

    @Override
    public Address updateAddress(AddressUpdateContainer updateContainer,
                                 com.flexicore.security.SecurityContext securityContext) {
        Address address = updateContainer.getAddress();
        if (updateAddressNoMerge(address, updateContainer)) {
            repository.merge(address);

        }
        return address;
    }
    @Override
    public void validate(AddressCreationContainer addressCreationContainer, SecurityContext securityContext) {
        String streetId = addressCreationContainer.getStreetId();
        Street street = streetId !=null?getByIdOrNull(streetId, Street.class, null, securityContext):null;
        if (street == null&& streetId !=null) {
            throw new BadRequestException("no Street with id " + streetId);
        }
        addressCreationContainer.setStreet(street);

        String zipId = addressCreationContainer.getZipId();
        Zip zip = zipId !=null?getByIdOrNull(zipId, Zip.class, null, securityContext):null;
        if (zip == null&& zipId !=null) {
            throw new BadRequestException("no Zip with id " + zipId);
        }
        addressCreationContainer.setZip(zip);

    }

    private boolean updateAddressNoMerge(Address address, AddressCreationContainer creationContainer) {
        boolean update = false;
        if (creationContainer.getName() != null && !creationContainer.getName().equals(address.getName())) {
            address.setName(creationContainer.getName());
            update = true;
        }
        if (creationContainer.getDescription() != null && !creationContainer.getDescription().equals(address.getDescription())) {
            address.setDescription(creationContainer.getDescription());
            update = true;
        }
        if (creationContainer.getFloor() != null && !creationContainer.getFloor().equals(address.getFloorForAddress())) {
            address.setFloorForAddress(creationContainer.getFloor());
            update = true;
        }

        if (creationContainer.getNumber() != null && !creationContainer.getNumber().equals(address.getNumber())) {
            address.setNumber(creationContainer.getNumber());
            update = true;
        }
        if (creationContainer.getNeighbourhood() != null && (address.getNeighbourhood() == null || !creationContainer.getNeighbourhood().getId().equals(address.getNeighbourhood().getId()))) {
            address.setNeighbourhood(creationContainer.getNeighbourhood());
            update = true;
        }
        if (creationContainer.getStreet() != null && (address.getStreet() == null || !creationContainer.getStreet().getId().equals(address.getStreet().getId()))) {
            address.setStreet(creationContainer.getStreet());
            update = true;
        }

        if (creationContainer.getZip() != null && (address.getZip() == null || !creationContainer.getZip().getId().equals(address.getZip().getId()))) {
            address.setZip(creationContainer.getZip());
            update = true;
        }
        return update;
    }

    @Override
    public PaginationResponse<Address> listAllAddresses(SecurityContext securityContext, AddressFiltering filtering) {
        QueryInformationHolder<Address> queryInfo = new QueryInformationHolder<>(filtering, Address.class, securityContext);
        return new PaginationResponse<>(repository.getAllFiltered(queryInfo), filtering, repository.countAllFiltered(queryInfo));
    }

    @Override
    public Address createAddress(AddressCreationContainer creationContainer,
                                 com.flexicore.security.SecurityContext securityContext) {
        Address address = createAddressNoMerge(creationContainer, securityContext);
        repository.merge(address);
        return address;
    }

    private Address createAddressNoMerge(AddressCreationContainer creationContainer, SecurityContext securityContext) {

        Address address = Address.s().CreateUnchecked(creationContainer.getName(), securityContext);
        address.Init();
        updateAddressNoMerge(address, creationContainer);
        return address;
    }


    public AddressImportResponse importAddresses(SecurityContext securityContext, AddressImportRequest addressImportRequest) {
        AddressImportResponse addressImportResponse=new AddressImportResponse();
        XmlMapper xmlMapper=new XmlMapper();
        xmlMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES,false);

        try {
            String url = addressImportRequest.getUrl();
            HttpResponse<InputStream> response=Unirest.get(url).asBinary();
            if(HttpStatusCodes.isSuccess(response.getStatus())){
                InputStream is=response.getBody();
                try {
                    StreetEntry[] data=xmlMapper.readValue(is,  StreetEntry[].class);
                    CountryFiltering countryFiltering = new CountryFiltering();
                    countryFiltering.setNameLike("%Israel%");

                    Country israel=countryService.listAllCountries(securityContext, countryFiltering).parallelStream().findFirst().orElse(null);
                    if(israel==null){
                        israel=countryService.createCountry(new CountryCreationContainer().setName("Israel"),securityContext);
                    }
                    Map<String, City> existingCities=cityService.listAllCities(securityContext,new CityFiltering()).parallelStream().filter(f->f.getExternalId()!=null).collect(Collectors.toMap(f->f.getExternalId(),f->f,(a,b)->a));
                    Map<String, Map<String,Street>> existingStreets=streetService.listAllStreets(securityContext,new StreetFiltering()).parallelStream().filter(f->f.getExternalId()!=null&&f.getCity()!=null&&f.getCity().getExternalId()!=null).collect(Collectors.groupingBy(f->f.getCity().getExternalId(),Collectors.toMap(f->f.getExternalId(),f->f,(a,b)->a)));
                    List<Object> toMerge=new ArrayList<>();


                    for (StreetEntry streetEntry : data) {
                        String cityId = streetEntry.getCityId() + "";
                        City city=existingCities.get(cityId);
                        CityCreationContainer cityCreationContainer=new CityCreationContainer()
                                .setExternalId(cityId)
                                .setCountry(israel)
                                .setName(streetEntry.getCityName());
                        if(city==null){
                            city=cityService.createCityNoMerge(cityCreationContainer,securityContext);
                            toMerge.add(city);
                            existingCities.put(cityId,city);
                            addressImportResponse.setCreatedCities(addressImportResponse.getCreatedCities()+1);
                        }
                        else{
                            if(cityService.updateCityNoMerge(cityCreationContainer,city)){
                                toMerge.add(city);
                                addressImportResponse.setUpdatedCities(addressImportResponse.getUpdatedCities()+1);
                            }
                            else{
                                addressImportResponse.setUnchangedCities(addressImportResponse.getUnchangedCities()+1);

                            }
                        }

                        String streetId = streetEntry.getStreetId() + "";
                        Street street=existingStreets.computeIfAbsent(cityId,f->new HashMap<>()).get(streetId);
                        StreetCreationContainer streetCreationContainer=new StreetCreationContainer()
                                .setExternalId(streetId)
                                .setCity(city)
                                .setName(streetEntry.getStreetName());
                        if(street==null){
                            street=streetService.createStreetNoMerge(streetCreationContainer,securityContext);
                            toMerge.add(street);
                            existingStreets.computeIfAbsent(cityId,f->new HashMap<>()).put(streetId,street);
                            addressImportResponse.setCreatedStreet(addressImportResponse.getCreatedStreet()+1);
                        }
                        else{
                            if(streetService.updateStreetNoMerge(street, streetCreationContainer)){
                                toMerge.add(street);
                                addressImportResponse.setUpdatedStreet(addressImportResponse.getUpdatedStreet()+1);
                            }
                            else{
                                addressImportResponse.setUnchangedStreet(addressImportResponse.getUnchangedStreet()+1);

                            }
                        }
                    }

                   Set<String> cities= Stream.of(data).map(f->f.getCityId()+"").collect(Collectors.toSet());
                    Map<String,Set<String>> streets= Stream.of(data).collect(Collectors.groupingBy(f->f.getCityId()+"",Collectors.mapping(f->f.getStreetId()+"",Collectors.toSet())));


                    List<City> citiesToDel=existingCities.values().parallelStream().filter(f->!cities.contains(f.getExternalId())).collect(Collectors.toList());
                    List<Street> streetsToDel=existingStreets.values().parallelStream().flatMap(f->f.values().stream()).filter(f->deleteStreet(f,streets)).collect(Collectors.toList());
                    for (City city : citiesToDel) {
                        city.setSoftDelete(true);
                        toMerge.add(city);
                        addressImportResponse.setDeletedCities(addressImportResponse.getDeletedCities()+1);
                    }

                    for (Street street : streetsToDel) {
                        street.setSoftDelete(true);
                        toMerge.add(street);
                        addressImportResponse.setDeletedStreet(addressImportResponse.getDeletedStreet()+1);

                    }
                    repository.massMerge(toMerge);
                    addressImportResponse.setOk(true);


                } catch (IOException e) {
                    logger.log(Level.SEVERE,"unable to parse data",e);
                }
            }
            else{
                logger.severe("Request for db file from "+url +" failed with status "+response.getStatus());
            }
        } catch (UnirestException e) {
            logger.log(Level.SEVERE,"unable to get address db",e);
        }
        return addressImportResponse;
    }

    private boolean deleteStreet(Street f, Map<String, Set<String>> streets) {
       Set<String> cityStreets=streets.get(f.getCity().getExternalId());
       return cityStreets==null||!cityStreets.contains(f.getExternalId());
    }
}