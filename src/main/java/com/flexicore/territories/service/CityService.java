package com.flexicore.territories.service;

import com.flexicore.annotations.plugins.PluginInfo;

import java.util.List;
import java.util.logging.Logger;

import com.flexicore.model.territories.Country;
import com.flexicore.territories.data.CityRepository;

import javax.inject.Inject;
import javax.ws.rs.BadRequestException;
import javax.ws.rs.core.Context;

import com.flexicore.model.Baseclass;
import com.flexicore.territories.interfaces.ICityService;
import com.flexicore.security.SecurityContext;
import com.flexicore.model.QueryInformationHolder;
import com.flexicore.model.territories.City;
import com.flexicore.territories.data.request.CityUpdateContainer;
import com.flexicore.territories.data.request.CityCreationContainer;

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
    public List<City> listAllCities(
            SecurityContext securityContext,
            com.flexicore.territories.data.request.CityFiltering filtering) {
        QueryInformationHolder<City> queryInfo = new QueryInformationHolder<>(
                filtering, City.class, securityContext);
        return repository.getAllFiltered(queryInfo);
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

    private boolean updateCityNoMerge(CityCreationContainer creationContainer, City city) {
        boolean update = false;
        if (creationContainer.getName() != null && !city.getName().equals(creationContainer.getName())) {
            city.setName(creationContainer.getName());
            update = true;
        }
        if (creationContainer.getDescription() != null && !city.getDescription().equals(creationContainer.getDescription())) {
            city.setDescription(creationContainer.getDescription());
            update = true;
        }
        if (creationContainer.getCountry() != null && (city.getCountry() == null || !city.getCountry().getId().equals(creationContainer.getCountry().getId()))) {
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