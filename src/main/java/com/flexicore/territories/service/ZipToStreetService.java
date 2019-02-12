package com.flexicore.territories.service;

import com.flexicore.annotations.plugins.PluginInfo;

import java.util.List;
import java.util.logging.Logger;

import com.flexicore.model.territories.Street;
import com.flexicore.model.territories.Zip;
import com.flexicore.territories.data.ZipToStreetRepository;

import javax.inject.Inject;
import javax.ws.rs.BadRequestException;
import javax.ws.rs.core.Context;

import com.flexicore.model.Baseclass;
import com.flexicore.territories.interfaces.IZipToStreetService;
import com.flexicore.security.SecurityContext;
import com.flexicore.territories.data.request.ZipToStreetCreationContainer;
import com.flexicore.model.territories.ZipToStreet;
import com.flexicore.model.QueryInformationHolder;
import com.flexicore.territories.data.request.ZipToStreetUpdateContainer;

@PluginInfo(version = 1)
public class ZipToStreetService implements IZipToStreetService {

    @Inject
    @PluginInfo(version = 1)
    private ZipToStreetRepository repository;
    @Inject
    private Logger logger;

    @Override
    public <T extends Baseclass> T getByIdOrNull(java.lang.String id,
                                                 Class<T> c, List<String> batch, SecurityContext securityContext) {
        return repository.getByIdOrNull(id, c, batch, securityContext);
    }

    public void validate(ZipToStreetCreationContainer creationContainer, SecurityContext securityContext) {
        Zip leftside = getByIdOrNull(creationContainer.getZipId(),
                Zip.class, null, securityContext);
        if (leftside == null) {
            throw new BadRequestException("no Zip with id "
                    + creationContainer.getZipId());
        }
        creationContainer.setZip(leftside);
        Street rightside = getByIdOrNull(
                creationContainer.getStreetId(), Street.class, null,
                securityContext);
        if (rightside == null) {
            throw new BadRequestException("no Street with id "
                    + creationContainer.getStreetId());
        }
        creationContainer.setStreet(rightside);

    }

    @Override
    public ZipToStreet createZipToStreet(
            ZipToStreetCreationContainer creationContainer,
            com.flexicore.security.SecurityContext securityContext) {
        ZipToStreet ziptostreet = createZipToStreetNoMerge(creationContainer, securityContext);
        repository.merge(ziptostreet);
        return ziptostreet;
    }

    private ZipToStreet createZipToStreetNoMerge(ZipToStreetCreationContainer creationContainer, SecurityContext securityContext) {
        ZipToStreet ziptostreet = ZipToStreet.s().CreateUnchecked(
                "ZipToStreet", securityContext);
        ziptostreet.Init();
        updateZipToStreetNoMerge(ziptostreet, creationContainer);
        return ziptostreet;
    }

    private boolean updateZipToStreetNoMerge(ZipToStreet zipToStreet, ZipToStreetCreationContainer creationContainer) {
        boolean update = false;
        if (creationContainer.getStreet() != null && (zipToStreet.getRightside() == null || !zipToStreet.getRightside().getId().equals(creationContainer.getStreet().getId()))) {
            zipToStreet.setRightside(creationContainer.getStreet());
            update = true;
        }

        if (creationContainer.getZip() != null && (zipToStreet.getLeftside() == null || !zipToStreet.getLeftside().getId().equals(creationContainer.getZip().getId()))) {
            zipToStreet.setLeftside(creationContainer.getZip());
            update = true;
        }
        return update;

    }

    @Override
    public void deleteZipToStreet(String ziptostreetid,
                                  SecurityContext securityContext) {
        ZipToStreet ziptostreet = getByIdOrNull(ziptostreetid,
                ZipToStreet.class, null, securityContext);
        repository.remove(ziptostreet);
    }

    @Override
    public List<ZipToStreet> listAllZipToStreets(
            SecurityContext securityContext,
            com.flexicore.territories.data.request.ZipToStreetFiltering filtering) {
        QueryInformationHolder<ZipToStreet> queryInfo = new QueryInformationHolder<>(
                filtering, ZipToStreet.class, securityContext);
        return repository.getAllFiltered(queryInfo);
    }

    @Override
    public ZipToStreet updateZipToStreet(
            ZipToStreetUpdateContainer updateContainer,
            com.flexicore.security.SecurityContext securityContext) {
        ZipToStreet ziptostreet = updateContainer.getZipToStreet();
        if (updateZipToStreetNoMerge(ziptostreet, updateContainer)) {
            repository.merge(ziptostreet);
        }
        return ziptostreet;
    }
}