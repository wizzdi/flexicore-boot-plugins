package com.flexicore.territories.service;

import com.flexicore.annotations.plugins.PluginInfo;

import java.util.List;
import java.util.logging.Logger;

import com.flexicore.data.jsoncontainers.PaginationResponse;
import com.flexicore.model.territories.City;
import com.flexicore.territories.data.StreetRepository;

import javax.inject.Inject;
import javax.ws.rs.BadRequestException;
import javax.ws.rs.core.Context;

import com.flexicore.model.Baseclass;
import com.flexicore.territories.data.request.StreetFiltering;
import com.flexicore.territories.interfaces.IStreetService;
import com.flexicore.security.SecurityContext;
import com.flexicore.model.QueryInformationHolder;
import com.flexicore.model.territories.Street;
import com.flexicore.territories.data.request.StreetUpdateContainer;
import com.flexicore.territories.data.request.StreetCreationContainer;

@PluginInfo(version = 1)
public class StreetService implements IStreetService {

    @Inject
    @PluginInfo(version = 1)
    private StreetRepository repository;
    @Inject
    private Logger logger;

    @Override
    public <T extends Baseclass> T getByIdOrNull(java.lang.String id,
                                                 Class<T> c, List<String> batch, SecurityContext securityContext) {
        return repository.getByIdOrNull(id, c, batch, securityContext);
    }

    @Override
    public List<Street> listAllStreets(SecurityContext securityContext, StreetFiltering filtering) {
        return repository.listAllStreets(securityContext,filtering);

    }

    @Override
    public PaginationResponse<Street> getAllStreets(SecurityContext securityContext, StreetFiltering filtering) {
       List<Street> list=repository.listAllStreets(securityContext,filtering);
       long count=repository.countAllStreets(securityContext,filtering);
       return new PaginationResponse<>(list,filtering,count);
    }

    @Override
    public Street updateStreet(StreetUpdateContainer updateContainer,
                               com.flexicore.security.SecurityContext securityContext) {
        Street street = updateContainer.getStreet();
        if (updateStreetNoMerge(street, updateContainer)) {
            repository.merge(street);
        }
        return street;
    }

    public void validate(StreetUpdateContainer updateContainer, SecurityContext securityContext) {
        City city = updateContainer.getCityId() != null ? getByIdOrNull(updateContainer.getCityId(), City.class, null, securityContext) : null;
        if (city == null && updateContainer.getCityId() != null) {
            throw new BadRequestException("no City with id " + updateContainer.getCityId());
        }
        updateContainer.setCity(city);
    }

    public void validate(StreetFiltering streetFiltering, SecurityContext securityContext) {
        City city = streetFiltering.getCityId() != null ? getByIdOrNull(streetFiltering.getCityId(), City.class, null, securityContext) : null;
        if (city == null && streetFiltering.getCityId() != null) {
            throw new BadRequestException("no City with id " + streetFiltering.getCityId());
        }
        streetFiltering.setCity(city);
    }

    public Street createStreetNoMerge(StreetCreationContainer streetCreationContainer, SecurityContext securityContext) {
        Street street = Street.s().CreateUnchecked("Street", securityContext);
        street.Init();
        updateStreetNoMerge(street, streetCreationContainer);
        return street;

    }

    public boolean updateStreetNoMerge(Street street, StreetCreationContainer streetCreationContainer) {

        boolean update = false;
        if(street.isSoftDelete()){
            street.setSoftDelete(false);
            update=true;
        }
        if (streetCreationContainer.getExternalId() != null && !streetCreationContainer.getExternalId().equals(street.getExternalId())) {
            street.setExternalId(streetCreationContainer.getExternalId());
            update = true;
        }
        if (streetCreationContainer.getName() != null && !streetCreationContainer.getName().equals(street.getName())) {
            street.setName(streetCreationContainer.getName());
            update = true;
        }
        if (streetCreationContainer.getDescription() != null && !streetCreationContainer.getDescription().equals(street.getDescription())) {
            street.setDescription(streetCreationContainer.getDescription());
            update = true;
        }
        if (streetCreationContainer.getCity() != null && (street.getCity() == null || !streetCreationContainer.getCity().getId().equals(street.getCity().getId()))) {
            street.setCity(streetCreationContainer.getCity());
            update = true;
        }
        return update;

    }

    @Override
    public Street createStreet(StreetCreationContainer creationContainer,
                               com.flexicore.security.SecurityContext securityContext) {
        Street street = createStreetNoMerge(creationContainer, securityContext);
        repository.merge(street);
        return street;
    }

    @Override
    public void deleteStreet(String streetid, SecurityContext securityContext) {
        Street street = getByIdOrNull(streetid, Street.class, null,
                securityContext);
        repository.remove(street);
    }
}