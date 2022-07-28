package com.wizzdi.basic.iot.service.service;


import com.flexicore.model.Baseclass;
import com.flexicore.model.Basic;
import com.flexicore.security.SecurityContextBase;
import com.wizzdi.basic.iot.client.BasicIOTClient;
import com.wizzdi.basic.iot.client.ChangeState;
import com.wizzdi.basic.iot.model.*;
import com.wizzdi.basic.iot.service.data.DeviceRepository;
import com.wizzdi.basic.iot.service.request.ChangeStateRequest;
import com.wizzdi.basic.iot.service.request.DeviceCreate;
import com.wizzdi.basic.iot.service.request.DeviceFilter;
import com.wizzdi.basic.iot.service.request.DeviceUpdate;
import com.wizzdi.basic.iot.service.response.ChangeStateResponse;
import com.wizzdi.dynamic.properties.converter.DynamicPropertiesUtils;
import com.wizzdi.flexicore.boot.base.interfaces.Plugin;
import com.wizzdi.flexicore.security.response.PaginationResponse;
import com.wizzdi.flexicore.security.service.BaseclassService;
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

public class DeviceService implements Plugin {

    private static final Logger logger= LoggerFactory.getLogger(DeviceService.class);

    @Autowired
    private DeviceRepository repository;

    @Autowired
    private RemoteService remoteService;

    @Autowired
    private DeviceTypeService deviceTypeService;
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

    public void validateFiltering(DeviceFilter deviceFilter,
                                  SecurityContextBase securityContext) {
        remoteService.validateFiltering(deviceFilter, securityContext);
        if(deviceFilter.getDeviceTypeFilter()!=null){
            deviceTypeService.validateFiltering(deviceFilter.getDeviceTypeFilter(),securityContext);
        }
        Set<String> gatewayIds = deviceFilter.getGatewayIds();
        Map<String, Gateway> gatewayMap = gatewayIds.isEmpty() ? new HashMap<>() : listByIds(Gateway.class, gatewayIds, Remote_.security, securityContext).stream().collect(Collectors.toMap(f -> f.getId(), f -> f));
        gatewayIds.removeAll(gatewayMap.keySet());
        if (!gatewayIds.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "no Gateway with ids " + gatewayIds);
        }
        deviceFilter.setGateways(new ArrayList<>(gatewayMap.values()));

        Set<String> deviceTypeIds = deviceFilter.getDeviceTypeIds();
        Map<String, DeviceType> deviceTypeMap = deviceTypeIds.isEmpty() ? new HashMap<>() : listByIds(DeviceType.class, deviceTypeIds, Remote_.security, securityContext).stream().collect(Collectors.toMap(f -> f.getId(), f -> f));
        deviceTypeIds.removeAll(deviceTypeMap.keySet());
        if (!deviceTypeIds.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "no DeviceType with ids " + deviceTypeIds);
        }
        deviceFilter.setDeviceTypes(new ArrayList<>(deviceTypeMap.values()));
    }

    public PaginationResponse<Device> getAllDevices(
            SecurityContextBase securityContext, DeviceFilter filtering) {
        List<Device> list = listAllDevices(securityContext, filtering);
        long count = repository.countAllDevices(securityContext, filtering);
        return new PaginationResponse<>(list, filtering, count);
    }

    public List<Device> listAllDevices(SecurityContextBase securityContext, DeviceFilter deviceFilter) {
        return repository.getAllDevices(securityContext, deviceFilter);
    }

    public Device createDevice(DeviceCreate creationContainer,
                               SecurityContextBase securityContext) {
        Device device = createDeviceNoMerge(creationContainer, securityContext);
        repository.merge(device);
        return device;
    }

    public Device createDeviceNoMerge(DeviceCreate creationContainer,
                                      SecurityContextBase securityContext) {
        Device device = new Device();
        device.setId(Baseclass.getBase64ID());

        updateDeviceNoMerge(device, creationContainer);
        BaseclassService.createSecurityObjectNoMerge(device, securityContext);
        return device;
    }

    public boolean updateDeviceNoMerge(Device device,
                                       DeviceCreate deviceCreate) {
        boolean update = remoteService.updateRemoteNoMerge(device, deviceCreate);
        if (deviceCreate.getDeviceType() != null && (device.getDeviceType() == null || !deviceCreate.getDeviceType().getId().equals(device.getDeviceType().getId()))) {
            device.setDeviceType(deviceCreate.getDeviceType());
            update = true;
        }

        if (deviceCreate.getGateway() != null && (device.getGateway() == null || !deviceCreate.getGateway().getId().equals(device.getGateway().getId()))) {
            device.setGateway(deviceCreate.getGateway());
            update = true;
        }


        return update;
    }

    public Device updateDevice(DeviceUpdate deviceUpdate,
                               SecurityContextBase securityContext) {
        Device device = deviceUpdate.getDevice();
        if (updateDeviceNoMerge(device, deviceUpdate)) {
            repository.merge(device);
        }
        return device;
    }



    public void validate(DeviceCreate deviceCreate,
                         SecurityContextBase securityContext) {
        remoteService.validate(deviceCreate, securityContext);

        String deviceTypeId = deviceCreate.getDeviceTypeId();
        DeviceType deviceType = deviceTypeId == null ? null : getByIdOrNull(deviceTypeId, DeviceType.class, Remote_.security, securityContext);
        if (deviceTypeId != null && deviceType == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "No DeviceType with id " + deviceTypeId);
        }
        deviceCreate.setDeviceType(deviceType);

        String gatewayId = deviceCreate.getGatewayId();
        Gateway gateway = gatewayId == null ? null : getByIdOrNull(gatewayId, Gateway.class, Remote_.security, securityContext);
        if (gatewayId != null && gateway == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "No Gateway with id " + gatewayId);
        }
        deviceCreate.setGateway(gateway);

    }


}