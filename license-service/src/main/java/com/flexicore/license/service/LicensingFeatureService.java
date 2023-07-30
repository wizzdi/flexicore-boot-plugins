package com.flexicore.license.service;


import com.flexicore.license.data.LicensingFeatureRepository;
import com.flexicore.license.model.LicensingFeature;
import com.flexicore.license.model.LicensingProduct;
import com.flexicore.license.model.LicensingProduct_;
import com.flexicore.license.request.LicensingFeatureCreate;
import com.flexicore.license.request.LicensingFeatureFiltering;
import com.flexicore.license.request.LicensingFeatureUpdate;
import com.flexicore.model.Baseclass;
import com.flexicore.model.Basic;
import com.flexicore.security.SecurityContextBase;
import com.wizzdi.flexicore.boot.base.interfaces.Plugin;
import com.wizzdi.flexicore.security.response.PaginationResponse;
import com.wizzdi.flexicore.security.service.BaseclassService;
import org.springframework.stereotype.Component;
import org.pf4j.Extension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;

import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import jakarta.persistence.metamodel.SingularAttribute;
import java.util.*;
import java.util.stream.Collectors;


@Extension
@Component
public class LicensingFeatureService implements Plugin {


    @Autowired

    private LicensingFeatureRepository repository;

    @Autowired

    private LicensingEntityService licensingEntityService;



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
    public LicensingFeature createLicensingFeature(LicensingFeatureCreate pluginCreationContainer, SecurityContextBase securityContext) {
        LicensingFeature licensingFeature = createLicensingFeatureNoMerge(pluginCreationContainer, securityContext);
        repository.merge(licensingFeature);
        return licensingFeature;


    }

    public LicensingFeature createLicensingFeatureNoMerge(LicensingFeatureCreate licensingFeatureCreate, SecurityContextBase securityContext) {
        LicensingFeature licensingFeature = new LicensingFeature();
        licensingFeature.setId(Baseclass.getBase64ID());
        updateLicensingFeatureNoMerge(licensingFeature, licensingFeatureCreate);
        BaseclassService.createSecurityObjectNoMerge(licensingFeature,securityContext);
        return licensingFeature;
    }

    public boolean updateLicensingFeatureNoMerge(LicensingFeature licensingFeature, LicensingFeatureCreate licensingFeatureCreate) {
        boolean update = licensingEntityService.updateLicensingEntityNoMerge(licensingFeature, licensingFeatureCreate);
        if(licensingFeatureCreate.getLicensingProduct()!=null && (licensingFeature.getProduct()==null || !licensingFeatureCreate.getLicensingProduct().getId().equals(licensingFeature.getProduct().getId()))){
            licensingFeature.setProduct(licensingFeatureCreate.getLicensingProduct());
            update=true;
        }
        return update;
    }


    public LicensingFeature updateLicensingFeature(LicensingFeatureUpdate licensingFeatureUpdate, SecurityContextBase securityContext) {
        LicensingFeature licensingFeature = licensingFeatureUpdate.getLicensingFeature();
        if (updateLicensingFeatureNoMerge(licensingFeature, licensingFeatureUpdate)) {
            repository.merge(licensingFeature);
        }
        return licensingFeature;
    }

    public List<LicensingFeature> listAllLicensingFeatures(LicensingFeatureFiltering licensingFeatureFiltering, SecurityContextBase securityContext) {
        return repository.listAllLicensingFeatures(licensingFeatureFiltering, securityContext);
    }

    public void validate(LicensingFeatureCreate licensingFeatureCreate, SecurityContextBase securityContext) {
        licensingEntityService.validate(licensingFeatureCreate, securityContext);

    }

    public void validate(LicensingFeatureFiltering licensingFeatureFiltering, SecurityContextBase securityContext) {
        licensingEntityService.validate(licensingFeatureFiltering,securityContext);
        Set<String> productIds=licensingFeatureFiltering.getLicensingProductsIds();
        Map<String, LicensingProduct> productMap=productIds.isEmpty()?new HashMap<>():listByIds(LicensingProduct.class,productIds, LicensingProduct_.security,securityContext).parallelStream().collect(Collectors.toMap(f->f.getId(), f->f));
        productIds.removeAll(productMap.keySet());
        if(!productIds.isEmpty()){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"No LicensingProduct with ids "+productIds);
        }
        licensingFeatureFiltering.setLicensingProducts(new ArrayList<>(productMap.values()));
    }

    public PaginationResponse<LicensingFeature> getAllLicensingFeatures(LicensingFeatureFiltering licensingFeatureFiltering, SecurityContextBase securityContext) {
        List<LicensingFeature> list = listAllLicensingFeatures(licensingFeatureFiltering, securityContext);
        long count = repository.countAllLicensingFeatures(licensingFeatureFiltering, securityContext);
        return new PaginationResponse<>(list, licensingFeatureFiltering, count);
    }

}