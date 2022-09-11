package com.wizzdi.basic.iot.service.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.flexicore.security.SecurityContextBase;
import com.wizzdi.basic.iot.client.*;
import com.wizzdi.basic.iot.model.*;
import com.wizzdi.basic.iot.service.request.*;
import com.wizzdi.flexicore.boot.base.interfaces.Plugin;
import com.wizzdi.flexicore.security.events.BasicCreated;
import com.wizzdi.flexicore.security.request.BasicPropertiesFilter;
import com.wizzdi.flexicore.security.request.DateFilter;
import org.pf4j.Extension;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.time.OffsetDateTime;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

@Component
@Extension
public class BasicIOTLogic implements Plugin, IOTMessageSubscriber {
    private static final Logger logger = LoggerFactory.getLogger(BasicIOTLogic.class);

    @Autowired
    private SecurityContextBase adminSecurityContext;
    @Autowired(required = false)
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
    @Autowired
    private FirmwareUpdateInstallationService firmwareUpdateInstallationService;
    @Value("${basic.iot.fota.baseUrl:http://localhost:8080/downloadFirmware/}")
    private String fotaBaseUrl;


    @Value("${basic.iot.lastSeenThreshold:#{60*60*1000}}")
    private long lastSeenThreshold;
    @Value("${basic.iot.firmware.update.reminderInterval:#{60*60*60*1000}}")
    private long reminderInterval;
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
        SecurityContextBase gatewaySecurityContext = gatewayOptional.map(f -> remoteService.getRemoteSecurityContext(f)).orElse(null);

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
        if (iotMessage instanceof BadMessage) {
            BadMessage badMessage = (BadMessage) iotMessage;
            return new BadMessageReceived().setOriginalMessage(badMessage.getOriginalMessage()).setError(badMessage.getError());
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
                logger.info("remote " + remote.getRemoteId() + "(" + remote.getId() + ") is ON");

            }
        }


    }

    @Scheduled(fixedDelayString = "${basic.iot.connectivityCheckInterval:60000}")
    public void checkConnectivity() {
        logger.debug("checking connectivity");
        Set<String> actuallyConnectedRemotes = gatewayToLastSeen.entrySet().stream().filter(f -> System.currentTimeMillis() - f.getValue() < lastSeenThreshold).map(f -> f.getKey()).collect(Collectors.toSet());
        List<Remote> remotesToUpdate = remoteService.listAllRemotes(null, new RemoteFilter().setConnectivity(Collections.singleton(Connectivity.ON)).setNotIds(actuallyConnectedRemotes));
        for (Remote remote : remotesToUpdate) {
            SecurityContextBase remoteSecurityContext = remoteService.getRemoteSecurityContext(remote);
            ConnectivityChange connectivityChange = connectivityChangeService.createConnectivityChange(new ConnectivityChangeCreate().setConnectivity(Connectivity.OFF).setRemote(remote).setDate(OffsetDateTime.now()), remoteSecurityContext);
            remote.setLastConnectivityChange(connectivityChange);
            gatewayService.merge(remote);
            logger.info("remote " + remote.getRemoteId() + "(" + remote.getId() + ") is OFF");
        }
        logger.debug("done checking connectivity");
    }

    @Scheduled(fixedDelayString = "${basic.iot.firmware.update.checkInterval:60000}")
    public void sendFirmwareUpdates() {
        logger.debug("checking firmwareUpdates");
        List<FirmwareUpdateInstallation> toInstall = firmwareUpdateInstallationService.listAllFirmwareUpdateInstallations(null, new FirmwareUpdateInstallationFilter().setFirmwareInstallationStates(Collections.singleton(FirmwareInstallationState.PENDING)).setTimeForReminder(OffsetDateTime.now()));


        for (FirmwareUpdateInstallation firmwareUpdateInstallation : toInstall) {
            Gateway gateway = remoteService.getGateway(firmwareUpdateInstallation.getTargetRemote());
            OTAAvailable otaAvailable = new OTAAvailable()
                    .setBaseUrl(fotaBaseUrl)
                    .setVersion(firmwareUpdateInstallation.getFirmwareUpdate().getVersion())
                    .setTargetInstallationDate(firmwareUpdateInstallation.getTargetInstallationDate())
                    .setFirmwareInstallationId(firmwareUpdateInstallation.getId())
                    .setMd5(firmwareUpdateInstallation.getFirmwareUpdate().getFileResource().getMd5())
                    .setCrc(firmwareUpdateInstallation.getFirmwareUpdate().getCrc());
            try {
                logger.info(String.format("sending OTA Available version %s to remote %s ",otaAvailable.getVersion(),firmwareUpdateInstallation.getTargetRemote().getRemoteId()));
                basicIOTClient.sendMessage(otaAvailable, gateway.getRemoteId());
            } catch (JsonProcessingException e) {
                logger.error("failed sending reminder for firmware update installation " + firmwareUpdateInstallation.getId());
            }
            firmwareUpdateInstallationService.updateFirmwareUpdateInstallation(new FirmwareUpdateInstallationUpdate().setFirmwareUpdateInstallation(firmwareUpdateInstallation).setNextTimeForReminder(OffsetDateTime.now().plus(reminderInterval, ChronoUnit.MILLIS)), null);

        }
        logger.debug("done checking firmwareUpdates");
    }

    @EventListener
    public void onFirmwareUpdateInstallationCreated(BasicCreated<FirmwareUpdateInstallation> basicCreated) {
        FirmwareUpdateInstallation firmwareUpdateInstallation = basicCreated.getBaseclass();
        FirmwareUpdateInstallationFilter firmwareUpdateInstallationFilter = new FirmwareUpdateInstallationFilter()
                .setNotInstallations(Collections.singletonList(firmwareUpdateInstallation))
                .setTargetRemotes(Collections.singletonList(firmwareUpdateInstallation.getTargetRemote()))
                .setFirmwareInstallationStates(Collections.singleton(FirmwareInstallationState.PENDING))
                .setBasicPropertiesFilter(new BasicPropertiesFilter().setCreationDateFilter(new DateFilter().setEnd(firmwareUpdateInstallation.getCreationDate())));
        List<FirmwareUpdateInstallation> oldUnfinishedInstallations = firmwareUpdateInstallationService.listAllFirmwareUpdateInstallations(null, firmwareUpdateInstallationFilter);
        for (FirmwareUpdateInstallation oldUnfinishedInstallation : oldUnfinishedInstallations) {
            oldUnfinishedInstallation.setFirmwareInstallationState(FirmwareInstallationState.CANCELLED);
        }
        firmwareUpdateInstallationService.massMerge(oldUnfinishedInstallations);
        logger.info("auto cancelled "+oldUnfinishedInstallations.size() +" installations ");
    }


    private IOTMessage stateChanged(StateChanged stateChanged, Gateway gateway, SecurityContextBase gatewaySecurityContext) {
        String newVersion=null;
        Remote remote=null;
        if(stateChanged.getDeviceId()!=null){
            DeviceCreate deviceCreate = new DeviceCreate()
                    .setGateway(gateway)
                    .setOther(stateChanged.getValues());

            Device device = deviceService.listAllDevices(gatewaySecurityContext, new DeviceFilter().setRemoteIds(Collections.singleton(stateChanged.getDeviceId()))).stream().findFirst().orElse(null);
            if (device == null) {
                DeviceType deviceType = deviceTypeService.getOrCreateDeviceType(stateChanged.getDeviceType(), gatewaySecurityContext);
                deviceCreate
                        .setDeviceType(deviceType)
                        .setRemoteId(stateChanged.getDeviceId())
                        .setVersion(stateChanged.getVersion());

                device=deviceService.createDevice(deviceCreate, gatewaySecurityContext);
                newVersion= device.getVersion();
            } else {
                newVersion=stateChanged.getVersion()!=null&&!stateChanged.getVersion().equals(device.getVersion())?stateChanged.getVersion():null;
                if (deviceService.updateDeviceNoMerge(device, deviceCreate)) {
                    deviceService.merge(device);
                }
            }
            remote=device;
        }
        else{
            newVersion=stateChanged.getVersion()!=null&&!stateChanged.getVersion().equals(gateway.getVersion())?stateChanged.getVersion():null;
            gatewayService.updateGateway(new GatewayUpdate().setGateway(gateway).setOther(stateChanged.getValues()).setVersion(stateChanged.getVersion()),null);
            remote=gateway;
        }
        if(newVersion != null){
            updateVersion(newVersion,remote);
        }

        return new StateChangedReceived().setStateChangedId(stateChanged.getId());
    }

    private void updateVersion(String newVersion, Remote remote) {
        List<FirmwareUpdateInstallation> firmwareUpdateInstallations = firmwareUpdateInstallationService.listAllFirmwareUpdateInstallations(null, new FirmwareUpdateInstallationFilter().setTargetRemotes(Collections.singletonList(remote)).setFirmwareInstallationStates(Collections.singleton(FirmwareInstallationState.PENDING)).setVersions(Collections.singleton(newVersion)));
        for (FirmwareUpdateInstallation firmwareUpdateInstallation : firmwareUpdateInstallations) {
            firmwareUpdateInstallationService.updateFirmwareUpdateInstallation(new FirmwareUpdateInstallationUpdate().setFirmwareUpdateInstallation(firmwareUpdateInstallation).setFirmwareInstallationState(FirmwareInstallationState.INSTALLED).setDateInstalled(OffsetDateTime.now()),null);
        }
        if(!firmwareUpdateInstallations.isEmpty()){
            logger.info("marked "+firmwareUpdateInstallations.size() +" as installed due to remote "+remote.getRemoteId()+"("+remote.getId()+") updated to version "+newVersion);
        }
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
