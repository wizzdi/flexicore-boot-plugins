package com.flexicore.territories.service;

import com.flexicore.annotations.plugins.PluginInfo;

import java.util.List;
import java.util.logging.Logger;

import com.flexicore.data.jsoncontainers.PaginationResponse;
import com.flexicore.model.territories.Country;
import com.flexicore.territories.data.CityRepository;

import javax.inject.Inject;
import javax.ws.rs.BadRequestException;

import com.flexicore.model.Baseclass;
import com.flexicore.territories.request.CityFiltering;
import com.flexicore.territories.interfaces.ICityService;
import com.flexicore.security.SecurityContext;
import com.flexicore.model.territories.City;
import com.flexicore.territories.request.CityUpdateContainer;
import com.flexicore.territories.request.CityCreationContainer;

@PluginInfo(version = 1)
public class CityService implements ICityService {

    @Inject
    @PluginInfo(version = 1)
    private CityRepository repository;
    @Inject
    private Logger logger;

    @Override
    public <T extends Baseclass> T getByIdOrNull(java.lang.String id,
                                                 Class<T> c, List<String> batch, SecurityContext securityContext) {
        return repository.getByIdOrNull(id, c, batch, securityContext);
    }

    @Override
    public List<City> listAllCities(SecurityContext securityContext, CityFiltering filtering) {
        return repository.listAllCities(securityContext,filtering);
    }

    @Override
    public PaginationResponse<City> getAllCities(SecurityContext securityContext, CityFiltering filtering) {
        List<City> list=repository.listAllCities(securityContext,filtering);
        long count=repository.countAllCities(securityContext,filtering);
        return new PaginationResponse<>(list,filtering,count);
    }

    @Override
    public City updateCity(CityUpdateContainer updateContainer,
                           com.flexicore.security.SecurityContext securityContext) {
        City city = updateContainer.getCity();
        if (updateCityNoMerge(updateContainer, city)) {
          repository.merge(city);
        }
        return city;
    }

    public void validate(CityCreationContainer creationContainer, SecurityContext securityContext) {
        Country country = getByIdOrNull(creationContainer.getCountryId(), Country.class, null, securityContext);
        if (country == null) {
            throw new BadRequestException("no Country with id " + creationContainer.getCountryId());
        }
        creationContainer.setCountry(country);
    }

    public void validate(CityFiltering creationContainer, SecurityContext securityContext) {
        Country country = getByIdOrNull(creationContainer.getCountryId(), Country.class, null, securityContext);
        if (country == null) {
            throw new BadRequestException("no Country with id " + creationContainer.getCountryId());
        }
        creationContainer.setCountry(country);
    }

    @Override
    public City createCity(CityCreationContainer creationContainer,
                           com.flexicore.security.SecurityContext securityContext) {
        City city = createCityNoMerge(creationContainer, securityContext);
        repository.merge(city);
        return city;
    }

    public City createCityNoMerge(CityCreationContainer creationContainer, SecurityContext securityContext) {
        City city = City.s().CreateUnchecked("City", securityContext);
        city.Init();
        updateCityNoMerge(creationContainer, city);
        return city;
    }

    public boolean updateCityNoMerge(CityCreationContainer creationContainer, City city) {
        boolean update = false;
        if(city.isSoftDelete()){
            city.setSoftDelete(false);
            update=true;
        }
        if (creationContainer.getExternalId() != null && !creationContainer.getExternalId().equals(city.getExternalId())) {
            city.setExternalId(creationContainer.getExternalId());
            update = true;
        }

        if (creationContainer.getName() != null && !creationContainer.getName().equals(city.getName())) {
            city.setName(creationContainer.getName());
            update = true;
        }
        if (creationContainer.getDescription() != null && !creationContainer.getDescription().equals(city.getDescription())) {
            city.setDescription(creationContainer.getDescription());
            update = true;
        }
        if (creationContainer.getCountry() != null && (city.getCountry() == null || !creationContainer.getCountry().getId().equals(city.getCountry().getId()))) {
            city.setCountry(creationContainer.getCountry());
            update = true;
        }
        return update;

    }

    @Override
    public void deleteCity(String cityid, SecurityContext securityContext) {
        City city = getByIdOrNull(cityid, City.class, null, securityContext);
        repository.remove(city);
    }
}