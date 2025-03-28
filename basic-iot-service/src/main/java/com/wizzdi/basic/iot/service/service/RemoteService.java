package com.wizzdi.basic.iot.service.service;


import com.flexicore.model.Baseclass;
import com.flexicore.model.Basic;
import com.wizzdi.flexicore.security.configuration.SecurityContext;
import com.wizzdi.basic.iot.model.*;
import com.wizzdi.basic.iot.service.data.RemoteRepository;
import com.wizzdi.basic.iot.service.events.RemoteUpdatedEvent;
import com.wizzdi.basic.iot.service.request.ConnectivityChangeCreate;
import com.wizzdi.basic.iot.service.request.RemoteCreate;
import com.wizzdi.basic.iot.service.request.RemoteFilter;
import com.wizzdi.basic.iot.service.request.RemoteUpdate;
import com.wizzdi.basic.iot.service.response.FixRemotesResponse;
import com.wizzdi.basic.iot.service.response.RemoteUpdateResponse;
import com.wizzdi.dynamic.properties.converter.DynamicPropertiesUtils;
import com.wizzdi.flexicore.boot.base.interfaces.Plugin;
import com.wizzdi.flexicore.security.events.BasicCreated;
import com.wizzdi.flexicore.security.interfaces.SecurityContextProvider;
import com.wizzdi.flexicore.security.response.PaginationResponse;
import com.wizzdi.flexicore.security.service.BaseclassService;
import com.wizzdi.flexicore.security.service.BasicService;
import com.wizzdi.maps.model.MapIcon;
import com.wizzdi.maps.model.MappedPOI;
import com.wizzdi.maps.service.service.MappedPOIService;
import org.pf4j.Extension;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import jakarta.persistence.metamodel.SingularAttribute;
import java.time.OffsetDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Extension
@Component

public class RemoteService implements Plugin {

    private static final Logger logger= LoggerFactory.getLogger(RemoteService.class);

    @Autowired
    private RemoteRepository repository;

    @Autowired
    private BasicService basicService;
    @Autowired
    private ConnectivityChangeService connectivityChangeService;
    @Autowired
    @Lazy
    private SecurityContextProvider securityContextProvider;
    @Autowired
    private MappedPOIService mappedPOIService;
    @Autowired
    private DeviceTypeService deviceTypeService;

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


    public void validateFiltering(RemoteFilter remoteFilter,
                                  SecurityContext securityContext) {
        basicService.validate(remoteFilter, securityContext);
        if(remoteFilter.getMappedPOIFilter()!=null){
            mappedPOIService.validate(remoteFilter.getMappedPOIFilter(),securityContext);
        }
    }

    public PaginationResponse<Remote> getAllRemotes(
            SecurityContext securityContext, RemoteFilter filtering) {
        List<Remote> list = listAllRemotes(securityContext, filtering);
        long count = repository.countAllRemotes(securityContext, filtering);
        return new PaginationResponse<>(list, filtering, count);
    }

    public List<Remote> listAllRemotes(SecurityContext securityContext, RemoteFilter remoteFilter) {
        return repository.getAllRemotes(securityContext, remoteFilter);
    }

    public Remote createRemote(RemoteCreate creationContainer,
                                 SecurityContext securityContext) {
        Remote remote = createRemoteNoMerge(creationContainer, securityContext);
        repository.merge(remote,null);
        return remote;
    }

    public Remote createRemoteNoMerge(RemoteCreate creationContainer,
                                        SecurityContext securityContext) {
        Remote remote = new Remote();
        remote.setId(UUID.randomUUID().toString());

        updateRemoteNoMerge(remote, creationContainer);
        BaseclassService.createSecurityObjectNoMerge(remote, securityContext);
        return remote;
    }

    public RemoteUpdateResponse updateRemoteNoMerge(Remote remote,
                                                    RemoteCreate remoteCreate) {
        RemoteCreate previousState=getPreviousState(remote);
        boolean update = basicService.updateBasicNoMerge(remoteCreate, remote);
        boolean stateUpdated=false;
        if (remoteCreate.getRemoteId() != null && !remoteCreate.getRemoteId().equals(remote.getRemoteId())) {
            remote.setRemoteId(remoteCreate.getRemoteId());
            update = true;
        }
        if (remoteCreate.getReportedLat() != null && !remoteCreate.getReportedLat().equals(remote.getReportedLat())) {
            remote.setReportedLat(remoteCreate.getReportedLat());
            update = true;
        }
        if (remoteCreate.getReportedLon() != null && !remoteCreate.getReportedLon().equals(remote.getReportedLon())) {
            remote.setReportedLon(remoteCreate.getReportedLon());
            update = true;
        }
        if (remoteCreate.getVersion() != null && !remoteCreate.getVersion().equals(remote.getVersion())) {
            remote.setVersion(remoteCreate.getVersion());
            update = true;
        }
        if (remoteCreate.getLastSeen() != null && !remoteCreate.getLastSeen().equals(remote.getLastSeen())) {
            remote.setLastSeen(remoteCreate.getLastSeen());
            update = true;
        }
        if (remoteCreate.getLockLocation() != null && !remoteCreate.getLockLocation().equals(remote.isLockLocation())) {
            remote.setLockLocation(remoteCreate.getLockLocation());
            update = true;
        }
        if (remoteCreate.getKeepStateHistory() != null &&!remoteCreate.getKeepStateHistory().equals(remote.isKeepStateHistory())) {
            remote.setKeepStateHistory(remoteCreate.getKeepStateHistory());
            update = true;
        }
        if (remoteCreate.getLockName() != null && !remoteCreate.getLockName().equals(remote.isLockName())) {
            remote.setLockName(remoteCreate.getLockName());
            update = true;
        }
        if (remoteCreate.getCurrentSchema() != null && (remote.getCurrentSchema()==null||!remoteCreate.getCurrentSchema().getId().equals(remote.getCurrentSchema().getId()))) {
            remote.setCurrentSchema(remoteCreate.getCurrentSchema());
            update = true;
        }
        if (remoteCreate.getMappedPOI() != null && (remote.getMappedPOI()==null||!remoteCreate.getMappedPOI().getId().equals(remote.getMappedPOI().getId()))) {
            remote.setMappedPOI(remoteCreate.getMappedPOI());
            update = true;
        }
        Map<String, Object> mergedDeviceValues = DynamicPropertiesUtils.updateDynamic(remoteCreate.getDeviceProperties(), remote.getDeviceProperties());

        if (mergedDeviceValues != null) {
            remote.setDeviceProperties(mergedDeviceValues);
            update = true;
            stateUpdated=true;
        }

        Map<String, Object> mergedUserValues = DynamicPropertiesUtils.updateDynamic(remoteCreate.getUserAddedProperties(), remote.getUserAddedProperties());

        if (mergedUserValues != null) {
            remote.setUserAddedProperties(mergedUserValues);
            update = true;
        }

        return new RemoteUpdateResponse(update,update?new RemoteUpdatedEvent(remote,previousState,stateUpdated):null);
    }

    private RemoteCreate getPreviousState(Remote remote) {
        return new RemoteCreate(remote);
    }

    public Remote updateRemote(RemoteUpdate remoteUpdate,
                                 SecurityContext securityContext) {
        Remote remote = remoteUpdate.getRemote();
        RemoteUpdateResponse remoteUpdateResponse = updateRemoteNoMerge(remote, remoteUpdate);
        if (remoteUpdateResponse.updated()) {
            repository.merge(remote,remoteUpdateResponse.remoteUpdatedEvent());
        }
        return remote;
    }

    public SecurityContext getRemoteSecurityContext(Remote remote) {
        if(remote instanceof Device){
            return getRemoteSecurityContext(((Device) remote).getGateway());
        }
        if(remote instanceof Gateway){
            return securityContextProvider.getSecurityContext(((Gateway) remote).getGatewayUser());
        }
        return null;
    }
    @EventListener
    public void onGatewayCreated(BasicCreated<Gateway> remoteCreated){
        onRemoteCreated(remoteCreated.getBaseclass());
    }
    @EventListener
    public void onDeviceCreated(BasicCreated<Device> remoteCreated){
        onRemoteCreated(remoteCreated.getBaseclass());
    }
    public void onRemoteCreated(Remote remote){
        ConnectivityChange connectivityChange = connectivityChangeService.createConnectivityChange(new ConnectivityChangeCreate().setDate(OffsetDateTime.now()).setConnectivity(Connectivity.OFF).setRemote(remote), getRemoteSecurityContext(remote));
        remote.setLastConnectivityChange(connectivityChange);
        repository.merge(remote,null);
        logger.info("default connectivity created for remote "+remote.getRemoteId() +"("+remote.getId()+")");
    }

    public void validate(RemoteCreate remoteCreate,
                         SecurityContext securityContext) {
        basicService.validate(remoteCreate, securityContext);
    }

    public Gateway getGateway(Remote remote){
        if(remote instanceof Gateway ){
            return (Gateway) remote;
        }
        if(remote instanceof Device){
            return ((Device) remote).getGateway();
        }
        return null;
    }

    public FixRemotesResponse fixRemoteMapIcons(SecurityContext securityContext, RemoteFilter remoteFilter) {
        SecurityContext copy = securityContextProvider.getSecurityContext(securityContext.getUser());

        List<Remote> remotes = listAllRemotes(securityContext, remoteFilter);
        List<Basic> toMerge=new ArrayList<>();
        int fixedRemotes=0;
        int fixedMappedPOIs=0;
        int fixedRemoteDeviceTypes=0;
        int fixedDeviceTypes=0;

        for (Remote remote : remotes) {
            if(remote.getPreConnectivityLossIcon()!=null&&(!remote.getTenant().getId().equals(remote.getPreConnectivityLossIcon().getTenant().getId())||remote.getPreConnectivityLossIcon().isSoftDelete())){
                remote.setPreConnectivityLossIcon(null);
                toMerge.add(remote);
                fixedRemotes++;
            }
            MappedPOI mappedPOI = remote.getMappedPOI();
            if(mappedPOI !=null){
                if(mappedPOI.getMapIcon()!=null&&(!mappedPOI.getMapIcon().getTenant().getId().equals(mappedPOI.getTenant().getId())||mappedPOI.getMapIcon().isSoftDelete())){
                    if(remote instanceof Device device&&device.getDeviceType()!=null){
                        DeviceType deviceType = device.getDeviceType();
                        if(deviceType.isSoftDelete()||!deviceType.getTenant().getId().equals(remote.getTenant().getId())){
                            copy.setTenantToCreateIn(remote.getTenant());
                            DeviceType correctedType=deviceTypeService.getOrCreateDeviceType(deviceType.getName(),true, copy);
                            device.setDeviceType(correctedType);
                            toMerge.add(device);
                            fixedRemoteDeviceTypes++;
                        }
                        if(deviceType.getDefaultMapIcon()==null||deviceType.getDefaultMapIcon().isSoftDelete()||!deviceType.getDefaultMapIcon().getTenant().getId().equals(deviceType.getTenant().getId())){
                            copy.setTenantToCreateIn(deviceType.getTenant());

                            MapIcon correctedDefault=deviceTypeService.getOrCreateMapIcon(DeviceTypeService.UNKNOWN_STATUS_SUFFIX,deviceType.getName(),Device.class,copy);
                            deviceType.setDefaultMapIcon(correctedDefault);
                            toMerge.add(deviceType);
                            fixedDeviceTypes++;
                        }
                        MapIcon mapIcon = deviceType.getDefaultMapIcon();
                        mappedPOI.setMapIcon(mapIcon);
                        toMerge.add(mappedPOI);
                        fixedMappedPOIs++;
                    }

                }
            }
        }
        repository.massMergePlain(new ArrayList<>(toMerge.stream().collect(Collectors.toMap(f->f.getId(),f->f,(a,b)->a)).values()));
        return new FixRemotesResponse(fixedRemotes,fixedMappedPOIs,fixedRemoteDeviceTypes,fixedDeviceTypes);
    }

    @Transactional
    public void massMerge(List<?> toMerge, boolean updatedate, boolean propagateEvents) {
        repository.massMerge(toMerge, updatedate, propagateEvents);
    }
}
