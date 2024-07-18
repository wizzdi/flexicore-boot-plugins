package com.wizzdi.basic.iot.service.service;

import com.flexicore.model.*;
import com.flexicore.security.SecurityContextBase;
import com.wizzdi.basic.iot.model.Device;
import com.wizzdi.basic.iot.model.DeviceType;
import com.wizzdi.basic.iot.model.Gateway;
import com.wizzdi.basic.iot.service.request.DeviceFilter;
import com.wizzdi.basic.iot.service.request.DeviceTypeCreate;
import com.wizzdi.basic.iot.service.request.DeviceTypeFilter;
import com.wizzdi.basic.iot.service.request.MoveGatewaysRequest;
import com.wizzdi.basic.iot.service.response.MoveGatewaysResponse;
import com.wizzdi.flexicore.boot.base.interfaces.Plugin;
import com.wizzdi.flexicore.security.request.TenantToUserFilter;
import com.wizzdi.flexicore.security.service.TenantToUserService;
import com.wizzdi.maps.model.MapIcon;
import com.wizzdi.maps.model.MappedPOI;
import com.wizzdi.maps.service.request.MapIconCreate;
import com.wizzdi.maps.service.request.MapIconFilter;
import com.wizzdi.maps.service.service.MapIconService;
import org.pf4j.Extension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

import static com.wizzdi.basic.iot.service.service.DeviceTypeService.UNKNOWN_STATUS_SUFFIX;
import static com.wizzdi.basic.iot.service.service.DeviceTypeService.getMapIconCreate;
import static org.springframework.http.HttpStatus.BAD_REQUEST;

@Component
@Extension
public class GatewayMoveService implements Plugin {

    @Autowired
    private GatewayService gatewayService;
    @Autowired
    private DeviceService deviceService;
    @Autowired
    private TenantToUserService tenantToUserService;
    @Autowired
    private DeviceTypeService deviceTypeService;
    @Autowired
    private MapIconService mapIconService;


    public void validate(MoveGatewaysRequest moveGatewaysRequest, SecurityContextBase securityContext) {
        if (moveGatewaysRequest.getGatewayFilter() == null) {
            throw new ResponseStatusException(BAD_REQUEST, "gatewayFilter must be provided");
        }
        gatewayService.validateFiltering(moveGatewaysRequest.getGatewayFilter(), securityContext);
        if (moveGatewaysRequest.getTargetTenantId() == null) {
            throw new ResponseStatusException(BAD_REQUEST, "targetTenantId must be provided");
        }
        SecurityTenant targetTenant = gatewayService.getByIdOrNull(moveGatewaysRequest.getTargetTenantId(), SecurityTenant.class, SecuredBasic_.security, securityContext);
        if (targetTenant == null) {
            throw new ResponseStatusException(BAD_REQUEST, "no tenant with id %s".formatted(moveGatewaysRequest.getTargetTenantId()));
        }
        moveGatewaysRequest.setTargetTenant(targetTenant);
        if (moveGatewaysRequest.getTargetTenantAdminId() == null) {
            throw new ResponseStatusException(BAD_REQUEST, "targetTenantAdminId must be provided");
        }
        SecurityUser targetTenantAdmin = gatewayService.getByIdOrNull(moveGatewaysRequest.getTargetTenantAdminId(), SecurityUser.class, SecuredBasic_.security, securityContext);
        if (targetTenantAdmin == null) {
            throw new ResponseStatusException(BAD_REQUEST, "no user with id %s".formatted(moveGatewaysRequest.getTargetTenantAdminId()));
        }
        moveGatewaysRequest.setTargetTenantAdmin(targetTenantAdmin);
    }

    public MoveGatewaysResponse moveGatewaysToTenant(SecurityContextBase securityContext, MoveGatewaysRequest moveGatewaysRequest) {
        SecurityTenant targetTenant = moveGatewaysRequest.getTargetTenant();
        SecurityUser tenantAdmin=moveGatewaysRequest.getTargetTenantAdmin();
        List<Gateway> gateways = gatewayService.listAllGateways(securityContext, moveGatewaysRequest.getGatewayFilter()).stream().filter(f -> !f.getSecurity().getTenant().getId().equals(targetTenant.getId())).toList();
        if (gateways.isEmpty()) {
            return  MoveGatewaysResponse.empty();
        }
        securityContext.setTenantToCreateIn(targetTenant);
        Map<String, DeviceType> deviceTypeMap = deviceTypeService.listAllDeviceTypes(securityContext, new DeviceTypeFilter()).stream().filter(f -> f.getSecurity().getTenant().getId().equals(targetTenant.getId())).collect(Collectors.toMap(f -> f.getName(), f -> f));
        Map<String, MapIcon> mapIcons = mapIconService.listAllMapIcons(new MapIconFilter(), securityContext).stream().filter(f -> f.getSecurity().getTenant().getId().equals(targetTenant.getId())).collect(Collectors.toMap(f -> f.getExternalId(), f -> f));
        List<SecurityUser> gatewayUsers = gateways.stream().map(f -> f.getGatewayUser()).collect(Collectors.toMap(f -> f.getId(), f -> f, (a, b) -> a)).values().stream().toList();
        Map<String, TenantToUser> tenantLinks = tenantToUserService.listAllTenantToUsers(new TenantToUserFilter().setUsers(gatewayUsers), null).stream().filter(f -> f.isDefaultTenant()).collect(Collectors.toMap(f -> f.getUser().getId(), f -> f, (a, b) -> a));
        List<Device> allDevices = deviceService.listAllDevices(securityContext, new DeviceFilter().setGateways(gateways));
        Map<String, List<Device>> devicesPerGateway = allDevices.stream().collect(Collectors.groupingBy(f -> f.getGateway().getId()));
        int movedGateways = 0;
        int movedRemotes = 0;
        int movedMappedPOIs = 0;
        AtomicInteger createdDeviceTypes = new AtomicInteger();
        AtomicInteger createdMapIcons = new AtomicInteger();

        for (Gateway gateway : gateways) {
            List<Device> devices = devicesPerGateway.getOrDefault(gateway.getId(), new ArrayList<>());
            for (Device device : devices) {
                DeviceType matchingDeviceType = deviceTypeMap.computeIfAbsent(device.getDeviceType().getName(), name -> {
                    DeviceType deviceType = createDeviceType(name, mapIcons, targetTenant, securityContext);
                    createdDeviceTypes.getAndIncrement();
                    return deviceType;
                });
                device.setDeviceType(matchingDeviceType);
                MappedPOI pois = device.getMappedPOI();
                MapIcon currentMapIcon = pois.getMapIcon();
                String mapIconExtrenalId = currentMapIcon.getExternalId().replace(currentMapIcon.getSecurity().getTenant().getId(), targetTenant.getId());
                MapIcon matchingMapIcon = mapIcons.computeIfAbsent(mapIconExtrenalId, externalId -> {
                    MapIcon mapIcon = mapIconService.createMapIcon(new MapIconCreate().setExternalId(externalId).setRelatedType(device.getClass().getCanonicalName()).setName(currentMapIcon.getName()), securityContext);
                    createdMapIcons.getAndIncrement();
                    return mapIcon;
                });
                pois.setMapIcon(matchingMapIcon);
            }
            List<Object> toMerge = new ArrayList<>(devices);
            List<MappedPOI> mappedPOIS = devices.stream().map(f -> f.getMappedPOI()).toList();
            toMerge.addAll(mappedPOIS);
            List<Baseclass> toMove = devices.stream().map(f -> f.getSecurity()).collect(Collectors.toList());
            toMove.addAll(mappedPOIS.stream().map(f -> f.getSecurity()).toList());
            toMove.add(gateway.getSecurity());
            SecurityUser gatewayUser = gateway.getGatewayUser();
            toMove.add(gatewayUser.getSecurity());
            toMove.add(gateway.getMappedPOI().getSecurity());
            for (Baseclass baseclass : toMove) {
                baseclass.setTenant(targetTenant);
            }
            gatewayUser.getSecurity().setCreator(tenantAdmin);
            gateway.getSecurity().setCreator(tenantAdmin);
            gateway.getMappedPOI().getSecurity().setCreator(tenantAdmin);
            toMerge.addAll(toMove);
            TenantToUser tenantToUser = tenantLinks.get(gatewayUser.getId());
            if (tenantToUser != null && !tenantToUser.getTenant().getId().equals(targetTenant.getId())) {
                tenantToUser.setTenant(targetTenant);
                tenantToUser.getSecurity().setCreator(tenantAdmin);
                tenantToUser.getSecurity().setTenant(targetTenant);
                toMerge.add(tenantToUser);
                toMerge.add(tenantToUser.getSecurity());
            }

            tenantToUserService.massMerge(toMerge);
            movedGateways++;
            movedRemotes += devices.size();
            movedMappedPOIs += mappedPOIS.size() + 1;

        }

        return new MoveGatewaysResponse(movedGateways, movedRemotes, movedMappedPOIs, createdDeviceTypes.get(), createdMapIcons.get());
    }

    private DeviceType createDeviceType(String name, Map<String, MapIcon> mapIcons, SecurityTenant targetTenant, SecurityContextBase securityContext) {

        MapIconCreate mapIconCreate = getMapIconCreate(UNKNOWN_STATUS_SUFFIX, name, Device.class, targetTenant);
        MapIcon unknown = mapIconService.createMapIcon(mapIconCreate, securityContext);
        mapIcons.put(unknown.getExternalId(), unknown);
        return deviceTypeService.createDeviceType(new DeviceTypeCreate().setDefaultMapIcon(unknown).setName(name), securityContext);

    }

}
