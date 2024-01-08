package com.wizzdi.basic.iot.service.service;


import com.flexicore.model.Baseclass;
import com.flexicore.model.Basic;
import com.flexicore.model.SecuredBasic_;
import com.flexicore.model.SecurityTenant;
import com.flexicore.security.SecurityContextBase;
import com.wizzdi.basic.iot.model.Device;
import com.wizzdi.basic.iot.model.DeviceType;
import com.wizzdi.basic.iot.service.data.DeviceTypeRepository;
import com.wizzdi.basic.iot.service.request.DeviceTypeCreate;
import com.wizzdi.basic.iot.service.request.DeviceTypeFilter;
import com.wizzdi.basic.iot.service.request.DeviceTypeUpdate;
import com.wizzdi.flexicore.boot.base.interfaces.Plugin;
import com.wizzdi.flexicore.security.request.BasicPropertiesFilter;
import com.wizzdi.flexicore.security.response.PaginationResponse;
import com.wizzdi.flexicore.security.service.BaseclassService;
import com.wizzdi.flexicore.security.service.BasicService;
import com.wizzdi.maps.model.MapIcon;
import com.wizzdi.maps.service.request.MapIconCreate;
import com.wizzdi.maps.service.request.MapIconFilter;
import com.wizzdi.maps.service.service.MapIconService;
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

@Extension
@Component

public class DeviceTypeService implements Plugin {

    private static final Logger logger= LoggerFactory.getLogger(DeviceTypeService.class);
    public static final String UNKNOWN_STATUS_SUFFIX = "UNKNOWN";

    @Autowired
    private DeviceTypeRepository repository;

    @Autowired
    private BasicService basicService;
    @Autowired
    private MapIconService mapIconService;

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
        deviceType.setId(UUID.randomUUID().toString());

        updateDeviceTypeNoMerge(deviceType, creationContainer);
        BaseclassService.createSecurityObjectNoMerge(deviceType, securityContext);
        return deviceType;
    }

    public boolean updateDeviceTypeNoMerge(DeviceType deviceType,
                                        DeviceTypeCreate deviceTypeCreate) {
        boolean updated = basicService.updateBasicNoMerge(deviceTypeCreate, deviceType);
        if(deviceTypeCreate.getDefaultMapIcon()!=null&&(deviceType.getDefaultMapIcon()==null||!deviceTypeCreate.getDefaultMapIcon().getId().equals(deviceType.getDefaultMapIcon().getId()))){
            deviceType.setDefaultMapIcon(deviceTypeCreate.getDefaultMapIcon());
            updated=true;
        }
        if(deviceTypeCreate.getKeepStateHistory()!=null&& deviceTypeCreate.getKeepStateHistory()!=deviceType.isKeepStateHistory()){
            deviceType.setKeepStateHistory(deviceTypeCreate.getKeepStateHistory());
            updated=true;
        }
        return updated;
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
        String defaultMapIconId= deviceTypeCreate.getDefaultMapIconId();
        MapIcon mapIcon=defaultMapIconId!=null?getByIdOrNull(defaultMapIconId,MapIcon.class, SecuredBasic_.security,securityContext):null;
        if(mapIcon==null&&defaultMapIconId!=null){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"no map icon with id "+defaultMapIconId);
        }
        deviceTypeCreate.setDefaultMapIcon(mapIcon);
    }

    public MapIcon getOrCreateMapIcon(String status, String deviceTypeName, Class<? extends Device> deviceClass,SecurityContextBase securityContextBase) {
        MapIconCreate mapIconCreate = getMapIconCreate(status, deviceTypeName,deviceClass,securityContextBase.getTenantToCreateIn());
        return getOrCreateMapIcon(mapIconCreate,securityContextBase);
    }

    private MapIcon getOrCreateMapIcon( MapIconCreate mapIconCreate,SecurityContextBase securityContextBase) {
        String externalId = mapIconCreate.getExternalId();
        String relatedType = mapIconCreate.getRelatedType();
        return mapIconService.listAllMapIcons(new MapIconFilter().setRelatedType(Collections.singleton(relatedType))
                        .setExternalId(Collections.singleton(externalId)), null).stream().filter(f -> f.getSecurity().getTenant().getId().equals(securityContextBase.getTenantToCreateIn().getId()))
                .findFirst().orElseGet(() -> mapIconService.createMapIcon(mapIconCreate, securityContextBase));
    }

    private static MapIconCreate getMapIconCreate(String status, String deviceTypeName, Class<? extends Device> deviceClass, SecurityTenant tenantToCreateIn) {
        String name = deviceTypeName + "_" + status;
        String externalId = name + "_" + tenantToCreateIn.getId();
        String relatedType = deviceClass.getCanonicalName();
        return new MapIconCreate().setExternalId(externalId).setRelatedType(relatedType).setName(name);
    }

    public DeviceType getOrCreateDeviceType(String deviceTypeName,SecurityContextBase securityContext) {
        return getOrCreateDeviceType(deviceTypeName, false, securityContext);
    }


    public DeviceType getOrCreateDeviceType(String deviceTypeName, boolean checkMapIcon, SecurityContextBase securityContext) {
        DeviceType deviceType = listAllDeviceTypes(null, new DeviceTypeFilter().setBasicPropertiesFilter(new BasicPropertiesFilter().setNames(Collections.singleton(deviceTypeName)))).stream().filter(f->f.getSecurity().getTenant().getId().equals(securityContext.getTenantToCreateIn().getId())).findFirst().orElse(null);
        if(deviceType!=null){
            logger.info("created device type "+deviceTypeName);
            return deviceType;
        }
        MapIconCreate mapIconCreate = getMapIconCreate(UNKNOWN_STATUS_SUFFIX, deviceTypeName, Device.class, securityContext.getTenantToCreateIn());
        MapIcon unknown = Optional.of(checkMapIcon).filter(f->f).map(f->getOrCreateMapIcon(mapIconCreate,securityContext)).orElseGet(()->mapIconService.createMapIcon(mapIconCreate, securityContext));
        deviceType = createDeviceType(new DeviceTypeCreate().setDefaultMapIcon(unknown).setName(deviceTypeName), securityContext);
        return deviceType;
    }


}