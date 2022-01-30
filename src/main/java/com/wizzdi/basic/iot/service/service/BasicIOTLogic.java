package com.wizzdi.basic.iot.service.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.flexicore.security.SecurityContextBase;
import com.wizzdi.basic.iot.client.*;
import com.wizzdi.basic.iot.model.*;
import com.wizzdi.basic.iot.service.request.*;
import com.wizzdi.flexicore.boot.base.interfaces.Plugin;
import com.wizzdi.flexicore.security.interfaces.SecurityContextProvider;
import org.pf4j.Extension;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Lazy;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.OffsetDateTime;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

@Component
@Extension
public class BasicIOTLogic implements Plugin, IOTMessageSubscriber {
    private static final Logger logger = LoggerFactory.getLogger(BasicIOTLogic.class);

    @Autowired
    private SecurityContextBase adminSecurityContext;
    @Autowired
    private BasicIOTClient basicIOTClient;
    @Autowired
    private GatewayService gatewayService;
    @Autowired
    private DeviceService deviceService;
    @Autowired
    private DeviceTypeService deviceTypeService;
    @Autowired
    private PendingGatewayService pendingGatewayService;
    @Autowired
    private ConnectivityChangeService connectivityChangeService;
    @Autowired
    private RemoteService remoteService;
    @Value("${basic.iot.lastSeenThreshold:#{60*60*1000}}")
    private  long lastSeenThreshold;
    private final Map<String, Long> gatewayToLastSeen = new ConcurrentHashMap<>();

    @Override
    public void onIOTMessage(IOTMessage iotMessage) {
        logger.info("received message " + iotMessage);
        IOTMessage response = onIOTMessageResponse(iotMessage);
        if (response != null) {
            try {
                basicIOTClient.reply(response, iotMessage);
            } catch (JsonProcessingException e) {
                logger.error("failed sending response message");
            }
        }
    }

    private IOTMessage onIOTMessageResponse(IOTMessage iotMessage) {
        if (iotMessage instanceof RegisterGateway) {
            return registerGateway((RegisterGateway) iotMessage);
        }
        Optional<Gateway> gatewayOptional = gatewayService.listAllGateways(null, new GatewayFilter().setRemoteIds(Collections.singleton(iotMessage.getGatewayId()))).stream().findFirst();
        if (gatewayOptional.isEmpty()) {
            logger.warn("could not get gateway " + iotMessage.getGatewayId());
        }
        SecurityContextBase gatewaySecurityContext = gatewayOptional.map(f->remoteService.getRemoteSecurityContext(f)).orElse(null);

        if (gatewaySecurityContext == null) {
            logger.warn("could not find security context for gateway " + iotMessage.getGatewayId());
            return null;
        }

        Gateway gateway = gatewayOptional.get();
        if (iotMessage instanceof StateChanged) {
            return stateChanged((StateChanged) iotMessage, gateway, gatewaySecurityContext);
        }

        if (iotMessage instanceof UpdateStateSchema) {
            return updateStateSchema((UpdateStateSchema) iotMessage, gateway, gatewaySecurityContext);
        }
        if (iotMessage instanceof KeepAlive) {
            onKeepAlive((KeepAlive) iotMessage, gateway, gatewaySecurityContext);
        }
        return null;
    }

    private void onKeepAlive(KeepAlive keepAlive, Gateway gateway, SecurityContextBase gatewaySecurityContext) {
        List<Remote> remotesWithKeepAlive = new ArrayList<>(List.of(gateway));
        List<Device> devices = keepAlive.getDeviceIds().isEmpty() ? new ArrayList<>() : deviceService.listAllDevices(gatewaySecurityContext, new DeviceFilter().setRemoteIds(keepAlive.getDeviceIds()));
        remotesWithKeepAlive.addAll(devices);
        for (Remote remote : remotesWithKeepAlive) {
            gatewayToLastSeen.put(remote.getId(), System.currentTimeMillis());
            if (remote.getLastConnectivityChange() == null || remote.getLastConnectivityChange().getConnectivity().equals(Connectivity.OFF)) {
                ConnectivityChange connectivityChange = connectivityChangeService.createConnectivityChange(new ConnectivityChangeCreate().setConnectivity(Connectivity.ON).setRemote(remote).setDate(OffsetDateTime.now()), gatewaySecurityContext);
                remote.setLastConnectivityChange(connectivityChange);
                gatewayService.merge(remote);
                logger.info("remote "+remote.getRemoteId() +"("+remote.getId()+") is ON");

            }
        }


    }

    @Scheduled(fixedDelayString = "${basic.iot.connectivityCheckInterval:60000}")
    public void checkConnectivity() {
        logger.debug("checking connectivity");
        Set<String> actuallyConnectedRemotes=gatewayToLastSeen.entrySet().stream().filter(f->System.currentTimeMillis()-f.getValue() <lastSeenThreshold ).map(f->f.getKey()).collect(Collectors.toSet());
        List<Remote> remotesToUpdate=remoteService.listAllRemotes(null,new RemoteFilter().setConnectivity(Collections.singleton(Connectivity.ON)).setNotIds(actuallyConnectedRemotes));
        for (Remote remote : remotesToUpdate) {
            SecurityContextBase remoteSecurityContext=remoteService.getRemoteSecurityContext(remote);
            ConnectivityChange connectivityChange = connectivityChangeService.createConnectivityChange(new ConnectivityChangeCreate().setConnectivity(Connectivity.OFF).setRemote(remote).setDate(OffsetDateTime.now()), remoteSecurityContext);
            remote.setLastConnectivityChange(connectivityChange);
            gatewayService.merge(remote);
            logger.info("remote "+remote.getRemoteId() +"("+remote.getId()+") is OFF");
        }
        logger.debug("done checking connectivity");
    }



    private IOTMessage stateChanged(StateChanged stateChanged, Gateway gateway, SecurityContextBase gatewaySecurityContext) {
        DeviceCreate deviceCreate = new DeviceCreate()
                .setGateway(gateway)
                .setOther(stateChanged.getValues());


        Device device = deviceService.listAllDevices(gatewaySecurityContext, new DeviceFilter().setRemoteIds(Collections.singleton(stateChanged.getDeviceId()))).stream().findFirst().orElse(null);
        if (device == null) {
            DeviceType deviceType = deviceTypeService.getOrCreateDeviceType(stateChanged.getDeviceType(), gatewaySecurityContext);
            deviceCreate
                    .setDeviceType(deviceType)
                    .setRemoteId(stateChanged.getDeviceId());
            deviceService.createDevice(deviceCreate, gatewaySecurityContext);
        } else {
            if (deviceService.updateDeviceNoMerge(device, deviceCreate)) {
                deviceService.merge(device);
            }
        }
        return new StateChangedReceived().setStateChangedId(stateChanged.getId());
    }


    private RegisterGatewayReceived registerGateway(RegisterGateway registerGateway) {
        PendingGateway pendingGateway = pendingGatewayService.createPendingGateway(new PendingGatewayCreate().setGatewayId(registerGateway.getGatewayId()).setPublicKey(registerGateway.getPublicKey()).setName(registerGateway.getGatewayId()), adminSecurityContext);
        return new RegisterGatewayReceived().setRegisterGatewayId(registerGateway.getId());
    }

    private UpdateStateSchemaReceived updateStateSchema(UpdateStateSchema updateStateSchema, Gateway gateway, SecurityContextBase gatewaySecurityContext) {
        DeviceType deviceType = deviceTypeService.getOrCreateDeviceType(updateStateSchema.getDeviceType(), gatewaySecurityContext);
        DeviceTypeCreate deviceTypeCreate = new DeviceTypeCreate().setStateJsonSchema(updateStateSchema.getJsonSchema()).setVersion(updateStateSchema.getVersion());
        if (updateStateSchema.getVersion() > deviceType.getVersion()) {
            if (deviceTypeService.updateDeviceTypeNoMerge(deviceType, deviceTypeCreate)) {
                deviceTypeService.merge(deviceType);
            }
        }
        return new UpdateStateSchemaReceived().setUpdateStateSchemaId(updateStateSchema.getId());
    }
}
