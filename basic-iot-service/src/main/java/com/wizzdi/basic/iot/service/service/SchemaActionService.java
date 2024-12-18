package com.wizzdi.basic.iot.service.service;


import com.flexicore.model.Baseclass;
import com.flexicore.model.Basic;

import com.wizzdi.flexicore.security.configuration.SecurityContext;
import com.wizzdi.basic.iot.model.DeviceType;
import com.wizzdi.basic.iot.model.SchemaAction;
import com.wizzdi.basic.iot.model.StateSchema;
import com.wizzdi.basic.iot.service.data.SchemaActionRepository;
import com.wizzdi.basic.iot.service.request.SchemaActionCreate;
import com.wizzdi.basic.iot.service.request.SchemaActionFilter;
import com.wizzdi.basic.iot.service.request.SchemaActionUpdate;
import com.wizzdi.flexicore.boot.base.interfaces.Plugin;
import com.wizzdi.flexicore.security.response.PaginationResponse;
import com.wizzdi.flexicore.security.service.BaseclassService;
import com.wizzdi.flexicore.security.service.BasicService;
import org.pf4j.Extension;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import jakarta.persistence.metamodel.SingularAttribute;
import java.util.*;
import java.util.stream.Collectors;

@Extension
@Component

public class SchemaActionService implements Plugin {

    private static final Logger logger= LoggerFactory.getLogger(SchemaActionService.class);

    @Autowired
    private SchemaActionRepository repository;

    @Autowired
    private BasicService basicService;

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

    public void validateFiltering(SchemaActionFilter schemaActionFilter,
                                  SecurityContext securityContext) {
        basicService.validate(schemaActionFilter, securityContext);
        Set<String> deviceTypeIds = schemaActionFilter.getDeviceTypeIds();
        Map<String, DeviceType> deviceTypeMap=deviceTypeIds.isEmpty()?new HashMap<>():listByIds(DeviceType.class,deviceTypeIds, securityContext).stream().collect(Collectors.toMap(f->f.getId(),f->f));
        deviceTypeIds.removeAll(deviceTypeMap.keySet());
        if(!deviceTypeIds.isEmpty()){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"no device ids "+deviceTypeIds);
        }
        schemaActionFilter.setDeviceTypes(new ArrayList<>(deviceTypeMap.values()));


        Set<String> stateSchemaIds = schemaActionFilter.getStateSchemaIds();
        Map<String, StateSchema> stateSchemaMap=stateSchemaIds.isEmpty()?new HashMap<>():listByIds(StateSchema.class,stateSchemaIds, securityContext).stream().collect(Collectors.toMap(f->f.getId(), f->f));
        stateSchemaIds.removeAll(stateSchemaMap.keySet());
        if(!stateSchemaIds.isEmpty()){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"no StateSchema ids "+stateSchemaIds);
        }
        schemaActionFilter.setStateSchemas(new ArrayList<>(stateSchemaMap.values()));
    }

    public PaginationResponse<SchemaAction> getAllSchemaActions(
            SecurityContext securityContext, SchemaActionFilter filtering) {
        List<SchemaAction> list = listAllSchemaActions(securityContext, filtering);
        long count = repository.countAllSchemaActions(securityContext, filtering);
        return new PaginationResponse<>(list, filtering, count);
    }

    public List<SchemaAction> listAllSchemaActions(SecurityContext securityContext, SchemaActionFilter schemaActionFilter) {
        return repository.getAllSchemaActions(securityContext, schemaActionFilter);
    }

    public SchemaAction createSchemaAction(SchemaActionCreate creationContainer,
                                 SecurityContext securityContext) {
        SchemaAction schemaAction = createSchemaActionNoMerge(creationContainer, securityContext);
        repository.merge(schemaAction);
        return schemaAction;
    }

    public SchemaAction createSchemaActionNoMerge(SchemaActionCreate creationContainer,
                                        SecurityContext securityContext) {
        SchemaAction schemaAction = new SchemaAction();
        schemaAction.setId(UUID.randomUUID().toString());

        updateSchemaActionNoMerge(schemaAction, creationContainer);
        BaseclassService.createSecurityObjectNoMerge(schemaAction, securityContext);
        return schemaAction;
    }

    public boolean updateSchemaActionNoMerge(SchemaAction schemaAction,
                                        SchemaActionCreate schemaActionCreate) {
        boolean updated = basicService.updateBasicNoMerge(schemaActionCreate, schemaAction);
        if(schemaActionCreate.getActionSchema()!=null&&!schemaActionCreate.getActionSchema().equals(schemaAction.getActionSchema())){
            schemaAction.setActionSchema(schemaActionCreate.getActionSchema());
            updated=true;
        }


        if(schemaActionCreate.getStateSchema()!=null&&(schemaAction.getStateSchema()==null||!schemaActionCreate.getStateSchema().getId().equals(schemaAction.getStateSchema().getId()))){
            schemaAction.setStateSchema(schemaActionCreate.getStateSchema());
            updated=true;
        }
        if(schemaActionCreate.getExternalId()!=null&&(!schemaActionCreate.getExternalId().equals(schemaAction.getExternalId()))){
            schemaAction.setExternalId(schemaActionCreate.getExternalId());
            updated=true;
        }

        return updated;
    }

    public SchemaAction updateSchemaAction(SchemaActionUpdate schemaActionUpdate,
                                 SecurityContext securityContext) {
        SchemaAction schemaAction = schemaActionUpdate.getSchemaAction();
        if (updateSchemaActionNoMerge(schemaAction, schemaActionUpdate)) {
            repository.merge(schemaAction);
        }
        return schemaAction;
    }

    public void validate(SchemaActionCreate schemaActionCreate,
                         SecurityContext securityContext) {
        basicService.validate(schemaActionCreate, securityContext);
        String stateSchemaId= schemaActionCreate.getStateSchemaId();
        StateSchema stateSchema=stateSchemaId!=null?getByIdOrNull(stateSchemaId,StateSchema.class,securityContext):null;
        if(stateSchema==null){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"no stateSchema with id "+stateSchemaId);
        }
        schemaActionCreate.setStateSchema(stateSchema);
    }

}
