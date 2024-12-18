package com.flexicore.license.service;


import com.flexicore.license.data.LicenseRequestToEntityRepository;
import com.flexicore.license.model.LicenseRequest;
import com.flexicore.license.model.LicenseRequestToEntity;
import com.flexicore.license.model.LicenseRequest_;
import com.flexicore.license.request.LicenseRequestToEntityCreate;
import com.flexicore.license.request.LicenseRequestToEntityFiltering;
import com.flexicore.model.Baseclass;
import com.flexicore.model.Basic;
import com.wizzdi.flexicore.security.configuration.SecurityContext;
import com.wizzdi.flexicore.boot.base.interfaces.Plugin;
import com.wizzdi.flexicore.security.response.PaginationResponse;
import com.wizzdi.flexicore.security.service.BaseclassService;
import com.wizzdi.flexicore.security.service.BasicService;
import org.springframework.stereotype.Component;
import org.pf4j.Extension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;

import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import jakarta.persistence.metamodel.SingularAttribute;
import java.time.ZoneId;
import java.util.*;
import java.util.logging.Logger;
import java.util.stream.Collectors;


@Extension
@Component
public class LicenseRequestToEntityService implements Plugin {


    @Autowired

    private LicenseRequestToEntityRepository repository;

    @Autowired
    private BasicService basicService;

   private Logger logger = Logger.getLogger(getClass().getCanonicalName());


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
    public LicenseRequestToEntity createLicenseRequestToEntity(LicenseRequestToEntityCreate pluginCreationContainer, SecurityContext securityContext) {
        LicenseRequestToEntity licenseRequestToEntity = createLicenseRequestToEntityNoMerge(pluginCreationContainer, securityContext);
        repository.merge(licenseRequestToEntity);
        return licenseRequestToEntity;


    }

    public LicenseRequestToEntity createLicenseRequestToEntityNoMerge(LicenseRequestToEntityCreate licenseRequestToEntityCreate, SecurityContext securityContext) {
        LicenseRequestToEntity licenseRequestToEntity = new LicenseRequestToEntity();
        licenseRequestToEntity.setId(UUID.randomUUID().toString());
        updateLicenseRequestToEntityNoMerge(licenseRequestToEntity, licenseRequestToEntityCreate);
        BaseclassService.createSecurityObjectNoMerge(licenseRequestToEntity,securityContext);
        return licenseRequestToEntity;
    }

    public boolean updateLicenseRequestToEntityNoMerge(LicenseRequestToEntity licenseRequestToEntity, LicenseRequestToEntityCreate licenseRequestToEntityCreate) {
        boolean update = basicService.updateBasicNoMerge(licenseRequestToEntityCreate, licenseRequestToEntity);
        if(licenseRequestToEntityCreate.getDemo()!=null && !licenseRequestToEntityCreate.getDemo().equals(licenseRequestToEntity.isDemo())){
            licenseRequestToEntity.setDemo(licenseRequestToEntityCreate.getDemo());
            update=true;
        }
        if(licenseRequestToEntityCreate.getGranted()!=null && !licenseRequestToEntityCreate.getGranted().withZoneSameInstant(ZoneId.systemDefault()).toOffsetDateTime().equals(licenseRequestToEntity.getGranted())){
            licenseRequestToEntity.setGranted(licenseRequestToEntityCreate.getGranted().withZoneSameInstant(ZoneId.systemDefault()).toOffsetDateTime());
            update=true;
        }

        if(licenseRequestToEntityCreate.getExpiration()!=null && !licenseRequestToEntityCreate.getExpiration().withZoneSameInstant(ZoneId.systemDefault()).toOffsetDateTime().equals(licenseRequestToEntity.getExpiration())){
            licenseRequestToEntity.setExpiration(licenseRequestToEntityCreate.getExpiration().withZoneSameInstant(ZoneId.systemDefault()).toOffsetDateTime());
            update=true;
        }
        if(licenseRequestToEntityCreate.getPerpetual()!=null && !licenseRequestToEntityCreate.getPerpetual().equals(licenseRequestToEntity.isPerpetual())){
            licenseRequestToEntity.setPerpetual(licenseRequestToEntityCreate.getPerpetual());
            update=true;
        }
        if(licenseRequestToEntityCreate.getLicenseRequest()!=null && (licenseRequestToEntity.getLicenseRequest()==null || !licenseRequestToEntityCreate.getLicenseRequest().getId().equals(licenseRequestToEntity.getLicenseRequest().getId()))){
            licenseRequestToEntity.setLicenseRequest(licenseRequestToEntityCreate.getLicenseRequest());
            update=true;
        }

        return update;
    }
    

    public List<LicenseRequestToEntity> listAllLicenseRequestToEntities(LicenseRequestToEntityFiltering licenseRequestToEntityFiltering, SecurityContext securityContext) {
        return repository.listAllLicenseRequestToEntities(licenseRequestToEntityFiltering, securityContext);
    }

    public void validate(LicenseRequestToEntityCreate licenseRequestToEntityCreate, SecurityContext securityContext) {
        basicService.validate(licenseRequestToEntityCreate, securityContext);
        String licenseRequestId=licenseRequestToEntityCreate.getLicenseRequestId();
        LicenseRequest licenseRequest=licenseRequestId!=null?getByIdOrNull(licenseRequestId,LicenseRequest.class,securityContext):null;
        if(licenseRequest==null && licenseRequestId!=null){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"No License request with id "+licenseRequestId);
        }
        licenseRequestToEntityCreate.setLicenseRequest(licenseRequest);

    }

    public void validateCreate(LicenseRequestToEntityCreate licenseRequestToEntityCreate, SecurityContext securityContext) {
        validate(licenseRequestToEntityCreate,securityContext);
        if((licenseRequestToEntityCreate.getPerpetual()==null || !licenseRequestToEntityCreate.getPerpetual()) && licenseRequestToEntityCreate.getExpiration()==null){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"Perpetual or expiration date should be set");
        }
    }

    public void validate(LicenseRequestToEntityFiltering licenseRequestToEntityFiltering, SecurityContext securityContext) {
        basicService.validate(licenseRequestToEntityFiltering,securityContext);
        Set<String> licenseRequestIds=licenseRequestToEntityFiltering.getLicenseRequestIds();
        Map<String,LicenseRequest> licenseRequestMap=licenseRequestIds.isEmpty()?new HashMap<>():listByIds(LicenseRequest.class,licenseRequestIds, securityContext).parallelStream().collect(Collectors.toMap(f->f.getId(), f->f));
        licenseRequestIds.removeAll(licenseRequestMap.keySet());
        if(!licenseRequestIds.isEmpty()){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"No License Requests with ids "+licenseRequestIds);
        }
        licenseRequestToEntityFiltering.setLicenseRequests(new ArrayList<>(licenseRequestMap.values()));
    }

    public PaginationResponse<LicenseRequestToEntity> getAllLicenseRequestToEntities(LicenseRequestToEntityFiltering licenseRequestToEntityFiltering, SecurityContext securityContext) {
        List<LicenseRequestToEntity> list = listAllLicenseRequestToEntities(licenseRequestToEntityFiltering, securityContext);
        long count = repository.countAllLicenseRequestToEntities(licenseRequestToEntityFiltering, securityContext);
        return new PaginationResponse<>(list, licenseRequestToEntityFiltering, count);
    }


}
