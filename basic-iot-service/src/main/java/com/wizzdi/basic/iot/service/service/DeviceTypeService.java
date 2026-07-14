package com.wizzdi.basic.iot.service.service;


import com.flexicore.model.Baseclass;
import com.flexicore.model.Basic;

import com.flexicore.model.SecurityTenant;
import com.wizzdi.flexicore.security.configuration.SecurityContext;
import com.wizzdi.basic.iot.model.Device;
import com.wizzdi.basic.iot.model.DeviceType;
import com.wizzdi.basic.iot.model.RemoteHealthProfile;
import com.wizzdi.basic.iot.service.data.DeviceTypeRepository;
import com.wizzdi.basic.iot.service.request.DeviceTypeCreate;
import com.wizzdi.basic.iot.service.request.DeviceTypeFilter;
import com.wizzdi.basic.iot.service.request.DeviceTypeUpdate;
import com.wizzdi.basic.iot.service.events.DeviceTypeHealthProfileChangedEvent;
import com.wizzdi.basic.iot.service.events.DeviceTypeGroupDefinitionChangedEvent;
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
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import jakarta.persistence.metamodel.SingularAttribute;

import java.time.OffsetDateTime;
import java.util.*;

@Extension
@Component

public class DeviceTypeService implements Plugin {

    private static final Logger logger= LoggerFactory.getLogger("basic-iot");
    public static final String UNKNOWN_STATUS_SUFFIX = "UNKNOWN";

    @Autowired
    private DeviceTypeRepository repository;

    @Autowired
    private BasicService basicService;
    @Autowired
    private ApplicationEventPublisher eventPublisher;
    @Autowired
    private MapIconService mapIconService;

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

    public void validateFiltering(DeviceTypeFilter deviceTypeFilter,
                                  SecurityContext securityContext) {
        basicService.validate(deviceTypeFilter, securityContext);
        Set<String> productTypesIds = deviceTypeFilter.getDeviceTypeIds();
    }

    public PaginationResponse<DeviceType> getAllDeviceTypes(
            SecurityContext securityContext, DeviceTypeFilter filtering) {
        List<DeviceType> list = listAllDeviceTypes(securityContext, filtering);
        long count = repository.countAllDeviceTypes(securityContext, filtering);
        return new PaginationResponse<>(list, filtering, count);
    }

    public List<DeviceType> listAllDeviceTypes(SecurityContext securityContext, DeviceTypeFilter deviceTypeFilter) {
        return repository.getAllDeviceTypes(securityContext, deviceTypeFilter);
    }

    public DeviceType createDeviceType(DeviceTypeCreate creationContainer,
                                 SecurityContext securityContext) {
        DeviceType deviceType = createDeviceTypeNoMerge(creationContainer, securityContext);
        repository.merge(deviceType);
        eventPublisher.publishEvent(new DeviceTypeGroupDefinitionChangedEvent(deviceType.getId(), OffsetDateTime.now()));
        if (deviceType.getDefaultHealthProfile() != null) {
            eventPublisher.publishEvent(new DeviceTypeHealthProfileChangedEvent(
                    deviceType.getId(),
                    null,
                    deviceType.getDefaultHealthProfile().getId(),
                    OffsetDateTime.now()));
        }
        return deviceType;
    }

    public DeviceType createDeviceTypeNoMerge(DeviceTypeCreate creationContainer,
                                        SecurityContext securityContext) {
        DeviceType deviceType = new DeviceType();
        deviceType.setId(UUID.randomUUID().toString());

        updateDeviceTypeNoMerge(deviceType, creationContainer);
        BaseclassService.createSecurityObjectNoMerge(deviceType, securityContext);
        return deviceType;
    }

    public boolean updateDeviceTypeNoMerge(DeviceType deviceType,
                                        DeviceTypeCreate deviceTypeCreate) {
        boolean updated = basicService.updateBasicNoMerge(deviceTypeCreate, deviceType);
        if (deviceTypeCreate.getExternalId() != null && !Objects.equals(deviceTypeCreate.getExternalId(), deviceType.getExternalId())) {
            deviceType.setExternalId(deviceTypeCreate.getExternalId());
            updated = true;
        }
        if(deviceTypeCreate.getDefaultMapIcon()!=null&&(deviceType.getDefaultMapIcon()==null||!deviceTypeCreate.getDefaultMapIcon().getId().equals(deviceType.getDefaultMapIcon().getId()))){
            deviceType.setDefaultMapIcon(deviceTypeCreate.getDefaultMapIcon());
            updated=true;
        }
        if(deviceTypeCreate.getKeepStateHistory()!=null&& deviceTypeCreate.getKeepStateHistory()!=deviceType.isKeepStateHistory()){
            deviceType.setKeepStateHistory(deviceTypeCreate.getKeepStateHistory());
            updated=true;
        }
        if (deviceTypeCreate.getDefaultHealthProfileId() != null) {
            String currentId = deviceType.getDefaultHealthProfile() == null ? null : deviceType.getDefaultHealthProfile().getId();
            String requestedId = deviceTypeCreate.getDefaultHealthProfile() == null ? null : deviceTypeCreate.getDefaultHealthProfile().getId();
            if (!Objects.equals(currentId, requestedId)) {
                deviceType.setDefaultHealthProfile(deviceTypeCreate.getDefaultHealthProfile());
                updated = true;
            }
        }
        return updated;
    }

    public DeviceType updateDeviceType(DeviceTypeUpdate deviceTypeUpdate,
                                 SecurityContext securityContext) {
        DeviceType deviceType = deviceTypeUpdate.getDeviceType();
        String previousProfileId = deviceType.getDefaultHealthProfile() == null ? null : deviceType.getDefaultHealthProfile().getId();
        boolean updated = updateDeviceTypeNoMerge(deviceType, deviceTypeUpdate);
        if (updated) {
            repository.merge(deviceType);
            eventPublisher.publishEvent(new DeviceTypeGroupDefinitionChangedEvent(deviceType.getId(), OffsetDateTime.now()));
        }
        String currentProfileId = deviceType.getDefaultHealthProfile() == null ? null : deviceType.getDefaultHealthProfile().getId();
        if (!Objects.equals(previousProfileId, currentProfileId)) {
            eventPublisher.publishEvent(new DeviceTypeHealthProfileChangedEvent(
                    deviceType.getId(),
                    previousProfileId,
                    currentProfileId,
                    OffsetDateTime.now()));
        }
        return deviceType;
    }

    public void validate(DeviceTypeCreate deviceTypeCreate,
                         SecurityContext securityContext) {
        basicService.validate(deviceTypeCreate, securityContext);
        String defaultMapIconId= deviceTypeCreate.getDefaultMapIconId();
        MapIcon mapIcon=defaultMapIconId!=null?getByIdOrNull(defaultMapIconId,MapIcon.class, securityContext):null;
        if(mapIcon==null&&defaultMapIconId!=null){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"no map icon with id "+defaultMapIconId);
        }
        if (mapIcon == null && defaultMapIconId == null && deviceTypeCreate.getExternalId() != null) {
            mapIcon = mapIconService.listAllMapIcons(new MapIconFilter().setExternalId(Collections.singleton(deviceTypeCreate.getExternalId())), securityContext).stream().findFirst().orElse(null);
        }
        deviceTypeCreate.setDefaultMapIcon(mapIcon);
        if (deviceTypeCreate.getDefaultHealthProfileId() != null) {
            String profileId = deviceTypeCreate.getDefaultHealthProfileId().trim();
            RemoteHealthProfile profile = profileId.isEmpty()
                    ? null
                    : getByIdOrNull(profileId, RemoteHealthProfile.class, securityContext);
            if (!profileId.isEmpty() && profile == null) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "No accessible RemoteHealthProfile with id " + profileId);
            }
            deviceTypeCreate.setDefaultHealthProfile(profile);
        }
    }

    public MapIcon getOrCreateMapIcon(String status, String deviceTypeName, Class<? extends Device> deviceClass,SecurityContext SecurityContext) {
        MapIconCreate mapIconCreate = getMapIconCreate(status, deviceTypeName,deviceClass,SecurityContext.getTenantToCreateIn());
        return mapIconService.getOrCreateMapIcon(mapIconCreate,SecurityContext);
    }



    public static MapIconCreate getMapIconCreate(String status, String deviceTypeName, Class<? extends Device> deviceClass, SecurityTenant tenantToCreateIn) {
        String name = deviceTypeName + "_" + status;
        String externalId = name + "_" + tenantToCreateIn.getId();
        String relatedType = deviceClass.getCanonicalName();
        return new MapIconCreate().setExternalId(externalId).setRelatedType(relatedType).setName(name);
    }

    public DeviceType getOrCreateDeviceType(String deviceTypeName, SecurityContext securityContext) {
        return getOrCreateDeviceType(deviceTypeName, normalizeExternalId(deviceTypeName), false, securityContext);
    }

    public DeviceType getOrCreateDeviceType(String deviceTypeName, String externalId, SecurityContext securityContext) {
        return getOrCreateDeviceType(deviceTypeName, externalId, false, securityContext);
    }

    public DeviceType getOrCreateDeviceType(String deviceTypeName, boolean checkMapIcon, SecurityContext securityContext) {
        return getOrCreateDeviceType(deviceTypeName, normalizeExternalId(deviceTypeName), checkMapIcon, securityContext);
    }

    public DeviceType getOrCreateDeviceType(String deviceTypeName, String externalId, boolean checkMapIcon, SecurityContext securityContext) {
        String normalizedExternalId = externalId == null || externalId.isBlank() ? normalizeExternalId(deviceTypeName) : externalId;
        DeviceTypeFilter filter = new DeviceTypeFilter().setExternalIds(Collections.singleton(normalizedExternalId));
        DeviceType deviceType = listAllDeviceTypes(null, filter).stream()
                .filter(f -> f.getTenant().getId().equals(securityContext.getTenantToCreateIn().getId()))
                .findFirst().orElse(null);
        if (deviceType == null) {
            deviceType = listAllDeviceTypes(null, new DeviceTypeFilter().setBasicPropertiesFilter(new BasicPropertiesFilter().setNames(Collections.singleton(deviceTypeName)))).stream()
                    .filter(f -> f.getTenant().getId().equals(securityContext.getTenantToCreateIn().getId()))
                    .findFirst().orElse(null);
        }
        if (deviceType != null) {
            return deviceType;
        }
        MapIcon matching = mapIconService.listAllMapIcons(new MapIconFilter().setExternalId(Collections.singleton(normalizedExternalId)), securityContext).stream().findFirst().orElse(null);
        if (matching == null) {
            MapIconCreate mapIconCreate = getMapIconCreate(UNKNOWN_STATUS_SUFFIX, deviceTypeName, Device.class, securityContext.getTenantToCreateIn());
            matching = Optional.of(checkMapIcon).filter(f -> f).map(f -> mapIconService.getOrCreateMapIcon(mapIconCreate, securityContext)).orElseGet(() -> mapIconService.createMapIcon(mapIconCreate, securityContext));
        }
        return createDeviceType(new DeviceTypeCreate().setExternalId(normalizedExternalId).setDefaultMapIcon(matching).setName(deviceTypeName), securityContext);
    }

    public static String normalizeExternalId(String name) {
        return name == null ? null : name.trim().replaceAll("\\s+", "_");
    }


}
