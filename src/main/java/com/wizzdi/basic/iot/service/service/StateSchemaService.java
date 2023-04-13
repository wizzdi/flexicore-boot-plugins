package com.wizzdi.basic.iot.service.service;


import com.flexicore.model.Baseclass;
import com.flexicore.model.Basic;
import com.flexicore.model.SecuredBasic_;
import com.flexicore.security.SecurityContextBase;
import com.wizzdi.basic.iot.model.DeviceType;
import com.wizzdi.basic.iot.model.StateSchema;
import com.wizzdi.basic.iot.service.data.StateSchemaRepository;
import com.wizzdi.basic.iot.service.request.StateSchemaCreate;
import com.wizzdi.basic.iot.service.request.StateSchemaFilter;
import com.wizzdi.basic.iot.service.request.StateSchemaUpdate;
import com.wizzdi.flexicore.boot.base.interfaces.Plugin;
import com.wizzdi.flexicore.security.request.BasicPropertiesFilter;
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

import javax.persistence.metamodel.SingularAttribute;
import java.util.*;
import java.util.stream.Collectors;

@Extension
@Component

public class StateSchemaService implements Plugin {

    private static final Logger logger= LoggerFactory.getLogger(StateSchemaService.class);

    @Autowired
    private StateSchemaRepository repository;

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

    public void validateFiltering(StateSchemaFilter stateSchemaFilter,
                                  SecurityContextBase securityContext) {
        basicService.validate(stateSchemaFilter, securityContext);
        Set<String> deviceTypeIds = stateSchemaFilter.getDeviceTypeIds();
        Map<String, DeviceType> deviceTypeMap=deviceTypeIds.isEmpty()?new HashMap<>():listByIds(DeviceType.class,deviceTypeIds, SecuredBasic_.security,securityContext).stream().collect(Collectors.toMap(f->f.getId(),f->f));
        deviceTypeIds.removeAll(deviceTypeMap.keySet());
        if(!deviceTypeIds.isEmpty()){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"no device ids "+deviceTypeIds);
        }
        stateSchemaFilter.setDeviceTypes(new ArrayList<>(deviceTypeMap.values()));
    }

    public PaginationResponse<StateSchema> getAllStateSchemas(
            SecurityContextBase securityContext, StateSchemaFilter filtering) {
        List<StateSchema> list = listAllStateSchemas(securityContext, filtering);
        long count = repository.countAllStateSchemas(securityContext, filtering);
        return new PaginationResponse<>(list, filtering, count);
    }

    public List<StateSchema> listAllStateSchemas(SecurityContextBase securityContext, StateSchemaFilter stateSchemaFilter) {
        return repository.getAllStateSchemas(securityContext, stateSchemaFilter);
    }

    public StateSchema createStateSchema(StateSchemaCreate creationContainer,
                                 SecurityContextBase securityContext) {
        StateSchema stateSchema = createStateSchemaNoMerge(creationContainer, securityContext);
        repository.merge(stateSchema);
        return stateSchema;
    }

    public StateSchema createStateSchemaNoMerge(StateSchemaCreate creationContainer,
                                        SecurityContextBase securityContext) {
        StateSchema stateSchema = new StateSchema();
        stateSchema.setId(UUID.randomUUID().toString());

        updateStateSchemaNoMerge(stateSchema, creationContainer);
        BaseclassService.createSecurityObjectNoMerge(stateSchema, securityContext);
        return stateSchema;
    }

    public boolean updateStateSchemaNoMerge(StateSchema stateSchema,
                                        StateSchemaCreate stateSchemaCreate) {
        boolean updated = basicService.updateBasicNoMerge(stateSchemaCreate, stateSchema);
        if(stateSchemaCreate.getStateSchemaJson()!=null&&!stateSchemaCreate.getStateSchemaJson().equals(stateSchema.getStateJsonSchema())){
            stateSchema.setStateJsonSchema(stateSchemaCreate.getStateSchemaJson());
            updated=true;
        }
        if(stateSchemaCreate.getVersion()!=null&&!stateSchemaCreate.getVersion().equals(stateSchema.getVersion())){
            stateSchema.setVersion(stateSchemaCreate.getVersion());
            updated=true;
        }
        if(stateSchemaCreate.getUserAddedSchema()!=null&&!stateSchemaCreate.getUserAddedSchema().equals(stateSchema.isUserAddedSchema())){
            stateSchema.setUserAddedSchema(stateSchemaCreate.getUserAddedSchema());
            updated=true;
        }

        if(stateSchemaCreate.getDeviceType()!=null&&(stateSchema.getDeviceType()==null||!stateSchemaCreate.getDeviceType().getId().equals(stateSchema.getDeviceType().getId()))){
            stateSchema.setDeviceType(stateSchemaCreate.getDeviceType());
            updated=true;
        }

        return updated;
    }

    public StateSchema updateStateSchema(StateSchemaUpdate stateSchemaUpdate,
                                 SecurityContextBase securityContext) {
        StateSchema stateSchema = stateSchemaUpdate.getStateSchema();
        if (updateStateSchemaNoMerge(stateSchema, stateSchemaUpdate)) {
            repository.merge(stateSchema);
        }
        return stateSchema;
    }

    public void validate(StateSchemaCreate stateSchemaCreate,
                         SecurityContextBase securityContext) {
        basicService.validate(stateSchemaCreate, securityContext);
        String deviceTypeId = stateSchemaCreate.getDeviceTypeId();
        DeviceType deviceType = deviceTypeId == null ? null : getByIdOrNull(deviceTypeId, DeviceType.class, SecuredBasic_.security, securityContext);
        if (deviceTypeId != null && deviceType == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "No DeviceType with id " + deviceTypeId);
        }
        stateSchemaCreate.setDeviceType(deviceType);
    }

}