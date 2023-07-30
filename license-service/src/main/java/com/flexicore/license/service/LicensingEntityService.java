package com.flexicore.license.service;


import com.flexicore.license.data.LicensingEntityRepository;
import com.flexicore.license.model.LicensingEntity;
import com.flexicore.license.request.LicensingEntityCreate;
import com.flexicore.license.request.LicensingEntityFiltering;
import com.flexicore.license.request.LicensingEntityUpdate;
import com.flexicore.model.Baseclass;
import com.flexicore.model.Basic;
import com.flexicore.security.SecurityContextBase;
import com.wizzdi.flexicore.boot.base.interfaces.Plugin;
import com.wizzdi.flexicore.security.response.PaginationResponse;
import com.wizzdi.flexicore.security.service.BaseclassService;
import com.wizzdi.flexicore.security.service.BasicService;
import org.springframework.stereotype.Component;
import org.pf4j.Extension;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.transaction.annotation.Transactional;

import jakarta.persistence.metamodel.SingularAttribute;
import java.util.List;
import java.util.Set;


@Extension
@Component
public class LicensingEntityService implements Plugin {


    @Autowired

    private LicensingEntityRepository repository;

    @Autowired
    private BasicService basicService;



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
    public LicensingEntity createLicensingEntity(LicensingEntityCreate pluginCreationContainer, SecurityContextBase securityContext) {
        LicensingEntity licensingEntity = createLicensingEntityNoMerge(pluginCreationContainer, securityContext);
        repository.merge(licensingEntity);
        return licensingEntity;


    }

    public LicensingEntity createLicensingEntityNoMerge(LicensingEntityCreate licensingEntityCreate, SecurityContextBase securityContext) {
        LicensingEntity licensingEntity = new LicensingEntity();
        licensingEntity.setId(Baseclass.getBase64ID());
        updateLicensingEntityNoMerge(licensingEntity, licensingEntityCreate);
        BaseclassService.createSecurityObjectNoMerge(licensingEntity,securityContext);
        return licensingEntity;
    }

    public boolean updateLicensingEntityNoMerge(LicensingEntity licensingEntity, LicensingEntityCreate licensingEntityCreate) {
        boolean update = basicService.updateBasicNoMerge(licensingEntityCreate, licensingEntity);
        if (licensingEntityCreate.getCanonicalName() != null && !licensingEntityCreate.getCanonicalName().equals(licensingEntity.getCanonicalName())) {
            licensingEntity.setCanonicalName(licensingEntityCreate.getCanonicalName());
            update = true;
        }
        return update;
    }


    public LicensingEntity updateLicensingEntity(LicensingEntityUpdate licensingEntityUpdate, SecurityContextBase securityContext) {
        LicensingEntity licensingEntity = licensingEntityUpdate.getLicensingEntity();
        if (updateLicensingEntityNoMerge(licensingEntity, licensingEntityUpdate)) {
            repository.merge(licensingEntity);
        }
        return licensingEntity;
    }

    public List<LicensingEntity> listAllLicensingEntities(LicensingEntityFiltering licensingEntityFiltering, SecurityContextBase securityContext) {
        return repository.listAllLicensingEntities(licensingEntityFiltering, securityContext);
    }

    public void validate(LicensingEntityCreate licensingEntityCreate, SecurityContextBase securityContext) {
        basicService.validate(licensingEntityCreate, securityContext);

    }

    public void validate(LicensingEntityFiltering licensingEntityFiltering, SecurityContextBase securityContext) {
        basicService.validate(licensingEntityFiltering,securityContext);
    }

    public PaginationResponse<LicensingEntity> getAllLicensingEntities(LicensingEntityFiltering licensingEntityFiltering, SecurityContextBase securityContext) {
        List<LicensingEntity> list = listAllLicensingEntities(licensingEntityFiltering, securityContext);
        long count = repository.countAllLicensingEntities(licensingEntityFiltering, securityContext);
        return new PaginationResponse<>(list, licensingEntityFiltering, count);
    }


}