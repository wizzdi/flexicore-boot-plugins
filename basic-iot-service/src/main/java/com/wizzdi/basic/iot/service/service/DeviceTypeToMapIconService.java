package com.wizzdi.basic.iot.service.service;

import com.flexicore.model.Baseclass;
import com.flexicore.model.Basic;
import com.wizzdi.basic.iot.model.DeviceType;
import com.wizzdi.basic.iot.model.DeviceTypeToMapIcon;
import com.wizzdi.basic.iot.service.data.DeviceTypeToMapIconRepository;
import com.wizzdi.basic.iot.service.request.DeviceTypeToMapIconCreate;
import com.wizzdi.basic.iot.service.request.DeviceTypeToMapIconFilter;
import com.wizzdi.basic.iot.service.request.DeviceTypeToMapIconUpdate;
import com.wizzdi.flexicore.boot.base.interfaces.Plugin;
import com.wizzdi.flexicore.security.configuration.SecurityContext;
import com.wizzdi.flexicore.security.response.PaginationResponse;
import com.wizzdi.flexicore.security.service.BaseclassService;
import com.wizzdi.flexicore.security.service.BasicService;
import com.wizzdi.maps.model.MapIcon;
import jakarta.persistence.metamodel.SingularAttribute;
import org.pf4j.Extension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.*;
import java.util.stream.Collectors;

@Extension
@Component
public class DeviceTypeToMapIconService implements Plugin {

    @Autowired
    private DeviceTypeToMapIconRepository repository;
    @Autowired
    private BasicService basicService;

    public DeviceTypeToMapIcon createDeviceTypeToMapIcon(DeviceTypeToMapIconCreate create, SecurityContext securityContext) {
        DeviceTypeToMapIcon link = createDeviceTypeToMapIconNoMerge(create, securityContext);
        repository.merge(link);
        return link;
    }

    public DeviceTypeToMapIcon createDeviceTypeToMapIconNoMerge(DeviceTypeToMapIconCreate create, SecurityContext securityContext) {
        DeviceTypeToMapIcon link = new DeviceTypeToMapIcon();
        link.setId(UUID.randomUUID().toString());
        updateDeviceTypeToMapIconNoMerge(link, create);
        BaseclassService.createSecurityObjectNoMerge(link, securityContext);
        return link;
    }

    public boolean updateDeviceTypeToMapIconNoMerge(DeviceTypeToMapIcon link, DeviceTypeToMapIconCreate create) {
        boolean updated = basicService.updateBasicNoMerge(create, link);
        if (create.getDeviceType() != null && (link.getDeviceType() == null || !create.getDeviceType().getId().equals(link.getDeviceType().getId()))) {
            link.setDeviceType(create.getDeviceType());
            updated = true;
        }
        if (create.getMapIcon() != null && (link.getMapIcon() == null || !create.getMapIcon().getId().equals(link.getMapIcon().getId()))) {
            link.setMapIcon(create.getMapIcon());
            updated = true;
        }
        if (create.getDeviceTypeStates() != null && !create.getDeviceTypeStates().equals(link.getDeviceTypeStates())) {
            link.setDeviceTypeStates(create.getDeviceTypeStates());
            updated = true;
        }
        if (create.getDefaultIcon() != null && create.getDefaultIcon() != link.isDefaultIcon()) {
            link.setDefaultIcon(create.getDefaultIcon());
            updated = true;
        }
        return updated;
    }

    public DeviceTypeToMapIcon updateDeviceTypeToMapIcon(DeviceTypeToMapIconUpdate update, SecurityContext securityContext) {
        DeviceTypeToMapIcon link = update.getDeviceTypeToMapIcon();
        if (updateDeviceTypeToMapIconNoMerge(link, update)) {
            repository.merge(link);
        }
        return link;
    }

    public PaginationResponse<DeviceTypeToMapIcon> getAllDeviceTypeToMapIcons(SecurityContext securityContext, DeviceTypeToMapIconFilter filter) {
        List<DeviceTypeToMapIcon> list = listAllDeviceTypeToMapIcons(securityContext, filter);
        long count = repository.countAllDeviceTypeToMapIcons(securityContext, filter);
        return new PaginationResponse<>(list, filter, count);
    }

    public List<DeviceTypeToMapIcon> listAllDeviceTypeToMapIcons(SecurityContext securityContext, DeviceTypeToMapIconFilter filter) {
        return repository.listAllDeviceTypeToMapIcons(securityContext, filter);
    }

    public MapIcon getMapIconForState(DeviceType deviceType, String state, SecurityContext securityContext) {
        if (deviceType == null) {
            return null;
        }
        DeviceTypeToMapIconFilter filter = new DeviceTypeToMapIconFilter().setDeviceTypeIds(new HashSet<>(Collections.singleton(deviceType.getId())));
        validateFiltering(filter, securityContext);
        List<DeviceTypeToMapIcon> links = listAllDeviceTypeToMapIcons(securityContext, filter);
        if (state != null) {
            Optional<MapIcon> exact = links.stream()
                    .filter(link -> !link.isDefaultIcon())
                    .filter(link -> stateMatches(link.getDeviceTypeStates(), state))
                    .map(DeviceTypeToMapIcon::getMapIcon)
                    .filter(Objects::nonNull)
                    .findFirst();
            if (exact.isPresent()) {
                return exact.get();
            }
        }
        return links.stream()
                .filter(DeviceTypeToMapIcon::isDefaultIcon)
                .map(DeviceTypeToMapIcon::getMapIcon)
                .filter(Objects::nonNull)
                .findFirst()
                .orElse(null);
    }

    public MapIcon getDefaultMapIcon(DeviceType deviceType, SecurityContext securityContext) {
        MapIcon configuredDefault = getMapIconForState(deviceType, null, securityContext);
        return configuredDefault != null ? configuredDefault : Optional.ofNullable(deviceType).map(DeviceType::getDefaultMapIcon).orElse(null);
    }

    private boolean stateMatches(String commaSeparatedStates, String state) {
        if (commaSeparatedStates == null || state == null) {
            return false;
        }
        String normalizedState = state.trim();
        if (normalizedState.isEmpty()) {
            return false;
        }
        return Arrays.stream(commaSeparatedStates.split(","))
                .map(String::trim)
                .filter(s -> !s.isEmpty())
                .anyMatch(s -> s.equalsIgnoreCase(normalizedState));
    }

    public void validate(DeviceTypeToMapIconCreate create, SecurityContext securityContext) {
        basicService.validate(create, securityContext);

        String deviceTypeId = create.getDeviceTypeId();
        DeviceType deviceType = deviceTypeId == null ? null : repository.getByIdOrNull(deviceTypeId, DeviceType.class, securityContext);
        if (deviceTypeId != null && deviceType == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "No DeviceType with id " + deviceTypeId);
        }
        create.setDeviceType(deviceType);

        String mapIconId = create.getMapIconId();
        MapIcon mapIcon = mapIconId == null ? null : repository.getByIdOrNull(mapIconId, MapIcon.class, securityContext);
        if (mapIconId != null && mapIcon == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "No MapIcon with id " + mapIconId);
        }
        create.setMapIcon(mapIcon);
    }

    public void validate(DeviceTypeToMapIconUpdate update, SecurityContext securityContext) {
        validate((DeviceTypeToMapIconCreate) update, securityContext);
        String id = update.getId();
        DeviceTypeToMapIcon link = id == null ? null : repository.getByIdOrNull(id, DeviceTypeToMapIcon.class, securityContext);
        if (id != null && link == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "No DeviceTypeToMapIcon with id " + id);
        }
        update.setDeviceTypeToMapIcon(link);
    }

    public void validateFiltering(DeviceTypeToMapIconFilter filter, SecurityContext securityContext) {
        basicService.validate(filter, securityContext);

        Set<String> deviceTypeIds = filter.getDeviceTypeIds();
        Map<String, DeviceType> deviceTypeMap = deviceTypeIds == null || deviceTypeIds.isEmpty() ? new HashMap<>() : repository.listByIds(DeviceType.class, deviceTypeIds, securityContext).stream().collect(Collectors.toMap(DeviceType::getId, f -> f));
        if (deviceTypeIds != null) {
            deviceTypeIds.removeAll(deviceTypeMap.keySet());
            if (!deviceTypeIds.isEmpty()) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "No DeviceType with ids " + deviceTypeIds);
            }
        }
        filter.setDeviceTypes(new ArrayList<>(deviceTypeMap.values()));

        Set<String> mapIconIds = filter.getMapIconIds();
        Map<String, MapIcon> mapIconMap = mapIconIds == null || mapIconIds.isEmpty() ? new HashMap<>() : repository.listByIds(MapIcon.class, mapIconIds, securityContext).stream().collect(Collectors.toMap(MapIcon::getId, f -> f));
        if (mapIconIds != null) {
            mapIconIds.removeAll(mapIconMap.keySet());
            if (!mapIconIds.isEmpty()) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "No MapIcon with ids " + mapIconIds);
            }
        }
        filter.setMapIcons(new ArrayList<>(mapIconMap.values()));
    }

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
}
