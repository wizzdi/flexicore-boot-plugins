package com.flexicore.license.service;


import com.flexicore.license.data.LicenseRequestToFeatureRepository;
import com.flexicore.license.model.LicenseRequestToFeature;
import com.flexicore.license.model.LicensingFeature;
import com.flexicore.license.model.LicensingFeature_;
import com.flexicore.license.request.LicenseRequestToFeatureCreate;
import com.flexicore.license.request.LicenseRequestToFeatureFiltering;
import com.flexicore.license.request.LicenseRequestToFeatureUpdate;
import com.flexicore.model.Baseclass;
import com.flexicore.model.Basic;
import com.wizzdi.flexicore.security.configuration.SecurityContext;
import com.wizzdi.flexicore.boot.base.interfaces.Plugin;
import com.wizzdi.flexicore.security.response.PaginationResponse;
import com.wizzdi.flexicore.security.service.BaseclassService;
import org.springframework.stereotype.Component;
import org.pf4j.Extension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.http.HttpStatus;

import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import jakarta.persistence.metamodel.SingularAttribute;
import java.util.List;
import java.util.Set;
import java.util.UUID;


@Extension
@Component
public class LicenseRequestToFeatureService implements Plugin {


    @Autowired

    private LicenseRequestToFeatureRepository repository;

    @Autowired

    private LicenseRequestToEntityService licenseRequestToEntityService;



   public <T extends Baseclass> List<T> listByIds(Class<T> c, Set<String> ids, SecurityContext securityContext) {
        return repository.listByIds(c, ids, securityContext);
    }

    public <T extends Baseclass> T getByIdOrNull(String id, Class<T> c, SecurityContext securityContext) {
        return repository.getByIdOrNull(id, c, securityContext);
    }

    public <D extends Basic, E extends Baseclass, T extends D> T getByIdOrNull(String id, Class<T> c, SingularAttribute<D, E> baseclassAttribute, SecurityContext securityContext) {
        return repository.getByIdOrNull(id, c, baseclassAttribute, securityContext);
    }

    public <D extends Basic, E extends Baseclass, T extends D> List<T> listByIds(Class<T> c, Set<String> ids, SingularAttribute<D, E> baseclassAttribute, SecurityContext securityContext) {
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

    public LicenseRequestToFeature createLicenseRequestToFeature(LicenseRequestToFeatureCreate pluginCreationContainer, SecurityContext securityContext) {
        LicenseRequestToFeature licenseRequestToFeature = createLicenseRequestToFeatureNoMerge(pluginCreationContainer, securityContext);
        repository.merge(licenseRequestToFeature);
        licenseRequestUpdateEventEvent.publishEvent(new LicenseRequestUpdateEvent().setLicenseRequest(licenseRequestToFeature.getLicenseRequest()).setSecurityContext(securityContext));
        return licenseRequestToFeature;


    }

    public LicenseRequestToFeature createLicenseRequestToFeatureNoMerge(LicenseRequestToFeatureCreate licenseRequestToFeatureCreate, SecurityContext securityContext) {
        LicenseRequestToFeature licenseRequestToFeature = new LicenseRequestToFeature();
        licenseRequestToFeature.setId(UUID.randomUUID().toString());
        updateLicenseRequestToFeatureNoMerge(licenseRequestToFeature, licenseRequestToFeatureCreate);
        BaseclassService.createSecurityObjectNoMerge(licenseRequestToFeature,securityContext);
        return licenseRequestToFeature;
    }

    public boolean updateLicenseRequestToFeatureNoMerge(LicenseRequestToFeature licenseRequestToFeature, LicenseRequestToFeatureCreate licenseRequestToFeatureCreate) {
        boolean update = licenseRequestToEntityService.updateLicenseRequestToEntityNoMerge(licenseRequestToFeature, licenseRequestToFeatureCreate);
        if(licenseRequestToFeatureCreate.getLicensingFeature()!=null && (licenseRequestToFeature.getLicensingEntity()==null || !licenseRequestToFeatureCreate.getLicensingFeature().getId().equals(licenseRequestToFeature.getLicensingEntity().getId()))){
            licenseRequestToFeature.setLicensingEntity(licenseRequestToFeatureCreate.getLicensingFeature());
            update=true;
        }

        return update;
    }


    public LicenseRequestToFeature updateLicenseRequestToFeature(LicenseRequestToFeatureUpdate licenseRequestToFeatureUpdate, SecurityContext securityContext) {
        LicenseRequestToFeature licenseRequestToFeature = licenseRequestToFeatureUpdate.getLicenseRequestToFeature();
        if (updateLicenseRequestToFeatureNoMerge(licenseRequestToFeature, licenseRequestToFeatureUpdate)) {
            repository.merge(licenseRequestToFeature);
            licenseRequestUpdateEventEvent.publishEvent(new LicenseRequestUpdateEvent().setLicenseRequest(licenseRequestToFeature.getLicenseRequest()).setSecurityContext(securityContext));

        }
        return licenseRequestToFeature;
    }

    public List<LicenseRequestToFeature> listAllLicenseRequestToFeatures(LicenseRequestToFeatureFiltering licenseRequestToFeatureFiltering, SecurityContext securityContext) {
        return repository.listAllLicenseRequestToFeatures(licenseRequestToFeatureFiltering, securityContext);
    }

    public void validate(LicenseRequestToFeatureCreate licenseRequestToFeatureCreate, SecurityContext securityContext) {
        licenseRequestToEntityService.validate(licenseRequestToFeatureCreate, securityContext);
        String licensingFeatureId=licenseRequestToFeatureCreate.getLicensingFeatureId();
        LicensingFeature licensingFeature=licensingFeatureId!=null?getByIdOrNull(licensingFeatureId,LicensingFeature.class, securityContext):null;
        if(licensingFeature==null && licensingFeatureId!=null){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"No License feature with id "+licensingFeatureId);
        }
        licenseRequestToFeatureCreate.setLicensingFeature(licensingFeature);

    }

    public void validate(LicenseRequestToFeatureFiltering licenseRequestToFeatureFiltering, SecurityContext securityContext) {
        licenseRequestToEntityService.validate(licenseRequestToFeatureFiltering, securityContext);
    }

    public PaginationResponse<LicenseRequestToFeature> getAllLicenseRequestToFeatures(LicenseRequestToFeatureFiltering licenseRequestToFeatureFiltering, SecurityContext securityContext) {
        List<LicenseRequestToFeature> list = listAllLicenseRequestToFeatures(licenseRequestToFeatureFiltering, securityContext);
        long count = repository.countAllLicenseRequestToFeatures(licenseRequestToFeatureFiltering, securityContext);
        return new PaginationResponse<>(list, licenseRequestToFeatureFiltering, count);
    }


}
