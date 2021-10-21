package com.wizzdi.basic.iot.service.service;


import com.flexicore.model.Baseclass;
import com.flexicore.model.Basic;
import com.flexicore.security.SecurityContextBase;
import com.wizzdi.basic.iot.model.DeviceType;
import com.wizzdi.basic.iot.service.data.DeviceTypeRepository;
import com.wizzdi.basic.iot.service.request.DeviceTypeCreate;
import com.wizzdi.basic.iot.service.request.DeviceTypeFilter;
import com.wizzdi.basic.iot.service.request.DeviceTypeUpdate;
import com.wizzdi.flexicore.boot.base.interfaces.Plugin;
import com.wizzdi.flexicore.security.response.PaginationResponse;
import com.wizzdi.flexicore.security.service.BaseclassService;
import com.wizzdi.flexicore.security.service.BasicService;
import org.pf4j.Extension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.metamodel.SingularAttribute;
import java.util.List;
import java.util.Set;

@Extension
@Component

public class DeviceTypeService implements Plugin {

    @Autowired
    private DeviceTypeRepository repository;

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

    public void validateFiltering(DeviceTypeFilter deviceTypeFilter,
                                  SecurityContextBase securityContext) {
        basicService.validate(deviceTypeFilter, securityContext);
        Set<String> productTypesIds = deviceTypeFilter.getDeviceTypeIds();
    }

    public PaginationResponse<DeviceType> getAllDeviceTypes(
            SecurityContextBase securityContext, DeviceTypeFilter filtering) {
        List<DeviceType> list = listAllDeviceTypes(securityContext, filtering);
        long count = repository.countAllDeviceTypes(securityContext, filtering);
        return new PaginationResponse<>(list, filtering, count);
    }

    public List<DeviceType> listAllDeviceTypes(SecurityContextBase securityContext, DeviceTypeFilter deviceTypeFilter) {
        return repository.getAllDeviceTypes(securityContext, deviceTypeFilter);
    }

    public DeviceType createDeviceType(DeviceTypeCreate creationContainer,
                                 SecurityContextBase securityContext) {
        DeviceType deviceType = createDeviceTypeNoMerge(creationContainer, securityContext);
        repository.merge(deviceType);
        return deviceType;
    }

    public DeviceType createDeviceTypeNoMerge(DeviceTypeCreate creationContainer,
                                        SecurityContextBase securityContext) {
        DeviceType deviceType = new DeviceType();
        deviceType.setId(Baseclass.getBase64ID());

        updateDeviceTypeNoMerge(deviceType, creationContainer);
        BaseclassService.createSecurityObjectNoMerge(deviceType, securityContext);
        return deviceType;
    }

    public boolean updateDeviceTypeNoMerge(DeviceType deviceType,
                                        DeviceTypeCreate deviceTypeCreate) {
        boolean update = basicService.updateBasicNoMerge(deviceTypeCreate, deviceType);
        if (deviceTypeCreate.getVersion() != null && !deviceTypeCreate.getVersion().equals(deviceType.getVersion())) {
            deviceType.setVersion(deviceTypeCreate.getVersion());
            update = true;
        }
        if (deviceTypeCreate.getStateJsonSchema() != null && !deviceTypeCreate.getStateJsonSchema().equals(deviceType.getStateJsonSchema())) {
            deviceType.setStateJsonSchema(deviceTypeCreate.getStateJsonSchema());
            update = true;
        }
        return update;
    }

    public DeviceType updateDeviceType(DeviceTypeUpdate deviceTypeUpdate,
                                 SecurityContextBase securityContext) {
        DeviceType deviceType = deviceTypeUpdate.getDeviceType();
        if (updateDeviceTypeNoMerge(deviceType, deviceTypeUpdate)) {
            repository.merge(deviceType);
        }
        return deviceType;
    }

    public void validate(DeviceTypeCreate deviceTypeCreate,
                         SecurityContextBase securityContext) {
        basicService.validate(deviceTypeCreate, securityContext);
    }
}