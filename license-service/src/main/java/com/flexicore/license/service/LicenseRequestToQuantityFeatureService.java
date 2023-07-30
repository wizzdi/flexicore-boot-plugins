package com.flexicore.license.service;


import com.flexicore.license.data.LicenseRequestToQuantityFeatureRepository;
import com.flexicore.license.model.LicenseRequestToQuantityFeature;
import com.flexicore.license.request.LicenseRequestToQuantityFeatureCreate;
import com.flexicore.license.request.LicenseRequestToQuantityFeatureFiltering;
import com.flexicore.license.request.LicenseRequestToQuantityFeatureUpdate;
import com.flexicore.model.Baseclass;
import com.flexicore.model.Basic;
import com.flexicore.security.SecurityContextBase;
import com.wizzdi.flexicore.boot.base.interfaces.Plugin;
import com.wizzdi.flexicore.security.response.PaginationResponse;
import com.wizzdi.flexicore.security.service.BaseclassService;
import org.springframework.stereotype.Component;
import org.pf4j.Extension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;

import org.springframework.transaction.annotation.Transactional;

import jakarta.persistence.metamodel.SingularAttribute;
import java.util.List;
import java.util.Set;


@Extension
@Component
public class LicenseRequestToQuantityFeatureService implements Plugin {


    @Autowired

    private LicenseRequestToQuantityFeatureRepository repository;

    @Autowired

    private LicenseRequestToFeatureService licenseRequestToFeatureService;



   public <T extends Baseclass> List<T> listByIds(Class<T> c, Set<String> ids, SecurityContextBase securityContext) {
        return repository.listByIds(c, ids, securityContext);
    }

    public <T extends Baseclass> T getByIdOrNull(String id, Class<T> c, SecurityContextBase securityContext) {
        return repository.getByIdOrNull(id, c, securityContext);
    }

    public <D extends Basic, E extends Baseclass, T extends D> T getByIdOrNull(String id, Class<T> c, SingularAttribute<D, E> baseclassAttribute, SecurityContextBase securityContext) {
        return repository.getByIdOrNull(id, c, baseclassAttribute, securityContext);
    }

    public <D extends Basic, E extends Baseclass, T extends D> List<T> listByIds(Class<T> c, Set<String> ids, SingularAttribute<D, E> baseclassAttribute, SecurityContextBase securityContext) {
        return repository.listByIds(c, ids, baseclassAttribute, securityContext);
    }

    public <D extends Basic, T extends D> List<T> findByIds(Class<T> c, Set<String> ids, SingularAttribute<D, String> idAttribute) {
        return repository.findByIds(c, ids, idAttribute);
    }

    public <T extends Basic> List<T> findByIds(Class<T> c, Set<String> requested) {
        return repository.findByIds(c, requested);
    }

    public <T> T findByIdOrNull(Class<T> type, String id) {
        return repository.findByIdOrNull(type, id);
    }

    @Transactional
    public void merge(Object base) {
        repository.merge(base);
    }

    @Transactional
    public void massMerge(List<?> toMerge) {
        repository.massMerge(toMerge);
    }
    @Autowired
    private ApplicationEventPublisher licenseRequestUpdateEventEvent;

    public LicenseRequestToQuantityFeature createLicenseRequestToQuantityFeature(LicenseRequestToQuantityFeatureCreate pluginCreationContainer, SecurityContextBase securityContext) {
        LicenseRequestToQuantityFeature licenseRequestToQuantityFeature = createLicenseRequestToQuantityFeatureNoMerge(pluginCreationContainer, securityContext);
        repository.merge(licenseRequestToQuantityFeature);
        licenseRequestUpdateEventEvent.publishEvent(new LicenseRequestUpdateEvent().setLicenseRequest(licenseRequestToQuantityFeature.getLicenseRequest()).setSecurityContextBase(securityContext));
        return licenseRequestToQuantityFeature;


    }

    public LicenseRequestToQuantityFeature createLicenseRequestToQuantityFeatureNoMerge(LicenseRequestToQuantityFeatureCreate licenseRequestToQuantityFeatureCreate, SecurityContextBase securityContext) {
        LicenseRequestToQuantityFeature licenseRequestToQuantityFeature = new LicenseRequestToQuantityFeature();
        licenseRequestToQuantityFeature.setId(Baseclass.getBase64ID());
        updateLicenseRequestToQuantityFeatureNoMerge(licenseRequestToQuantityFeature, licenseRequestToQuantityFeatureCreate);
        BaseclassService.createSecurityObjectNoMerge(licenseRequestToQuantityFeature,securityContext);
        return licenseRequestToQuantityFeature;
    }

    private boolean updateLicenseRequestToQuantityFeatureNoMerge(LicenseRequestToQuantityFeature licenseRequestToQuantityFeature, LicenseRequestToQuantityFeatureCreate licenseRequestToQuantityFeatureCreate) {
        boolean update = licenseRequestToFeatureService.updateLicenseRequestToFeatureNoMerge(licenseRequestToQuantityFeature, licenseRequestToQuantityFeatureCreate);
        if (licenseRequestToQuantityFeatureCreate.getQuantityLimit() != null && !licenseRequestToQuantityFeatureCreate.getQuantityLimit().equals(licenseRequestToQuantityFeature.getQuantityLimit())) {
            licenseRequestToQuantityFeature.setQuantityLimit(licenseRequestToQuantityFeatureCreate.getQuantityLimit());
            update = true;
        }

        return update;
    }


    public LicenseRequestToQuantityFeature updateLicenseRequestToQuantityFeature(LicenseRequestToQuantityFeatureUpdate licenseRequestToQuantityFeatureUpdate, SecurityContextBase securityContext) {
        LicenseRequestToQuantityFeature licenseRequestToQuantityFeature = licenseRequestToQuantityFeatureUpdate.getLicenseRequestToQuantityFeature();
        if (updateLicenseRequestToQuantityFeatureNoMerge(licenseRequestToQuantityFeature, licenseRequestToQuantityFeatureUpdate)) {
            repository.merge(licenseRequestToQuantityFeature);
            licenseRequestUpdateEventEvent.publishEvent(new LicenseRequestUpdateEvent().setLicenseRequest(licenseRequestToQuantityFeature.getLicenseRequest()).setSecurityContextBase(securityContext));

        }
        return licenseRequestToQuantityFeature;
    }

    public List<LicenseRequestToQuantityFeature> listAllLicenseRequestToQuantityFeatures(LicenseRequestToQuantityFeatureFiltering licenseRequestToQuantityFeatureFiltering, SecurityContextBase securityContext) {
        return repository.listAllLicenseRequestToQuantityFeatures(licenseRequestToQuantityFeatureFiltering, securityContext);
    }

    public void validate(LicenseRequestToQuantityFeatureCreate licenseRequestToQuantityFeatureCreate, SecurityContextBase securityContext) {
        licenseRequestToFeatureService.validate(licenseRequestToQuantityFeatureCreate, securityContext);


    }

    public void validate(LicenseRequestToQuantityFeatureFiltering licenseRequestToQuantityFeatureFiltering, SecurityContextBase securityContext) {
        licenseRequestToFeatureService.validate(licenseRequestToQuantityFeatureFiltering, securityContext);
    }

    public PaginationResponse<LicenseRequestToQuantityFeature> getAllLicenseRequestToQuantityFeatures(LicenseRequestToQuantityFeatureFiltering licenseRequestToQuantityFeatureFiltering, SecurityContextBase securityContext) {
        List<LicenseRequestToQuantityFeature> list = listAllLicenseRequestToQuantityFeatures(licenseRequestToQuantityFeatureFiltering, securityContext);
        long count = repository.countAllLicenseRequestToQuantityFeatures(licenseRequestToQuantityFeatureFiltering, securityContext);
        return new PaginationResponse<>(list, licenseRequestToQuantityFeatureFiltering, count);
    }


}