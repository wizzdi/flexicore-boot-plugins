package com.wizzdi.basic.iot.service.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.flexicore.security.SecurityContextBase;
import com.wizzdi.basic.iot.client.*;
import com.wizzdi.basic.iot.client.SchemaAction;
import com.wizzdi.basic.iot.model.*;
import com.wizzdi.basic.iot.service.request.*;
import com.wizzdi.flexicore.boot.base.interfaces.Plugin;
import com.wizzdi.flexicore.security.events.BasicCreated;
import com.wizzdi.flexicore.security.request.BasicPropertiesFilter;
import com.wizzdi.flexicore.security.request.DateFilter;
import com.wizzdi.maps.model.MapIcon;
import com.wizzdi.maps.model.MappedPOI;
import com.wizzdi.maps.service.request.MappedPOICreate;
import com.wizzdi.maps.service.service.MapIconService;
import com.wizzdi.maps.service.service.MappedPOIService;
import org.pf4j.Extension;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

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
    private StateSchemaService stateSchemaService;
    @Autowired
    private PendingGatewayService pendingGatewayService;
    @Autowired
    private ConnectivityChangeService connectivityChangeService;
    @Autowired
    private RemoteService remoteService;
    @Autowired
    private MappedPOIService mappedPOIService;
    @Autowired
    private MapIconService mapIconService;
    @Autowired
    private FirmwareUpdateInstallationService firmwareUpdateInstallationService;
    @Autowired
    private SchemaActionService schemaActionService;
    @Value("${basic.iot.fota.baseUrl:http://localhost:8080/downloadFirmware/}")
    private String fotaBaseUrl;


    @Value("${basic.iot.lastSeenThreshold:#{60*60*1000}}")
    private long lastSeenThreshold;
    @Value("${basic.iot.firmware.update.reminderInterval:#{60*60*60*1000}}")
    private long reminderInterval;
    @Value("${basic.iot.mqtt.badMessageResponse:false}")
    private boolean badMessageResponse;
    @Value("${basic.iot.mqtt.keepAlive.bounce:#{3*60*1000}}")
    private long keepAliveBounceThreshold;
    @Autowired
    @Qualifier("gatewayMapIcon")
    private MapIcon gatewayMapIcon;

    private static final Map<String,Long> keepAliveBounceCache =new ConcurrentHashMap<>();


    @Override
    public void onIOTMessage(IOTMessage iotMessage) {
        if(iotMessage instanceof KeepAlive){
            logger.debug("received message " + iotMessage);

        }
        else{
            logger.info("received message " + iotMessage);

        }
        IOTMessage response = executeLogic(iotMessage);
        if (response != null) {
            try {
                basicIOTClient.reply(response, iotMessage);
            } catch (JsonProcessingException e) {
                logger.error("failed sending response message");
            }
        }
    }

    public IOTMessage executeLogic(IOTMessage iotMessage) {
        if (iotMessage instanceof RegisterGateway registerGateway) {
            return registerGateway(registerGateway);
        }
        if(iotMessage instanceof BadMessage badMessage){
            if(logger.isDebugEnabled()){
                logger.warn("bad message: "+badMessage.getError() , " original message: "+badMessage.getOriginalMessage());

            }
            else{
                logger.warn("bad message: "+badMessage.getError());

            }
            return badMessageResponse?badMessage:null;
        }
        if(iotMessage instanceof KeepAlive keepAlive ){
            if(shouldBounce(iotMessage)){
                logger.debug("bouncing keep alive {}",keepAlive.getId());
                return null;
            }
            keepAliveBounceCache.put(keepAlive.getGatewayId(),System.currentTimeMillis());

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

        if (iotMessage instanceof SetStateSchema) {
            return setStateSchema((SetStateSchema) iotMessage, gateway, gatewaySecurityContext);
        }

        if (iotMessage instanceof UpdateStateSchema) {
            return updateStateSchema((UpdateStateSchema) iotMessage, gateway, gatewaySecurityContext);
        }
        if (iotMessage instanceof KeepAlive) {
            onKeepAlive((KeepAlive) iotMessage, gateway, gatewaySecurityContext);
        }
        return null;
    }

    private boolean shouldBounce(IOTMessage iotMessage) {
        Long lastKeepAliveReceived = keepAliveBounceCache.computeIfAbsent(iotMessage.getGatewayId(), f -> 0L);
        return (System.currentTimeMillis() - lastKeepAliveReceived) < keepAliveBounceThreshold;
    }

    private void onKeepAlive(KeepAlive keepAlive, Gateway gateway, SecurityContextBase gatewaySecurityContext) {
        List<Remote> remotesWithKeepAlive = new ArrayList<>(List.of(gateway));
        List<Device> devices = keepAlive.getDeviceIds().isEmpty() ? new ArrayList<>() : deviceService.listAllDevices(gatewaySecurityContext, new DeviceFilter().setRemoteIds(keepAlive.getDeviceIds()));
        remotesWithKeepAlive.addAll(devices);
        List<Remote> remotesThatChangedState = updateKeepAlive(keepAlive.getSentAt(), gatewaySecurityContext, remotesWithKeepAlive);
        for (Remote remote : remotesThatChangedState) {
            if(remote.getMappedPOI()!=null&&remote.getPreConnectivityLossIcon()!=null){
                remote.getMappedPOI().setMapIcon(remote.getPreConnectivityLossIcon());
                gatewayService.merge(remote.getMappedPOI());
                logger.debug("remote {}({}) connected , changing status to {}({})",remote.getRemoteId(),remote.getName(),remote.getPreConnectivityLossIcon().getName(),remote.getPreConnectivityLossIcon().getId());
            }
        }


    }

    private List<Remote> updateKeepAlive(OffsetDateTime lastSeen,SecurityContextBase gatewaySecurityContext, List<Remote> remotesWithKeepAlive) {
        List<Remote> statusChanged = new ArrayList<>();
        if(lastSeen==null || lastSeen.isAfter(OffsetDateTime.now())){
            lastSeen=OffsetDateTime.now();
        }
        for (Remote remote : remotesWithKeepAlive) {
            if(remote.getLastSeen()==null||lastSeen.isAfter(remote.getLastSeen())){
                remoteService.updateRemote(new RemoteUpdate().setRemote(remote).setLastSeen(lastSeen),gatewaySecurityContext);
            }
            if (remote.getLastConnectivityChange() == null || remote.getLastConnectivityChange().getConnectivity().equals(Connectivity.OFF)) {
                ConnectivityChange connectivityChange = connectivityChangeService.createConnectivityChange(new ConnectivityChangeCreate().setConnectivity(Connectivity.ON).setRemote(remote).setDate(OffsetDateTime.now()), gatewaySecurityContext);
                remote.setLastConnectivityChange(connectivityChange);
                gatewayService.merge(remote);
                logger.info("remote " + remote.getRemoteId() + "(" + remote.getId() + ") is ON");
                statusChanged.add(remote);

            }
        }
        return statusChanged;
    }

    @Scheduled(fixedDelayString = "${basic.iot.connectivityCheckInterval:60000}",initialDelayString = "${basic.iot.connectivityDelayInterval:60000}")
    public void checkConnectivity() {
        logger.debug("checking connectivity");
        OffsetDateTime threshold = OffsetDateTime.now().minus(lastSeenThreshold, ChronoUnit.MILLIS);
        List<Remote> remotesToUpdate = remoteService.listAllRemotes(null, new RemoteFilter().setConnectivity(Collections.singleton(Connectivity.ON)).setLastSeenTo(threshold));
        for (Remote remote : remotesToUpdate) {
            SecurityContextBase remoteSecurityContext = remoteService.getRemoteSecurityContext(remote);
            ConnectivityChange connectivityChange = connectivityChangeService.createConnectivityChange(new ConnectivityChangeCreate().setConnectivity(Connectivity.OFF).setRemote(remote).setDate(OffsetDateTime.now()), remoteSecurityContext);
            remote.setLastConnectivityChange(connectivityChange);

            gatewayService.merge(remote);
            logger.info("remote " + remote.getRemoteId() + "(" + remote.getId() + ") is OFF");
            if(remote instanceof Device device){
                if(device.getMappedPOI()!=null&&device.getDeviceType()!=null && device.getDeviceType().getDefaultMapIcon()!=null){
                    if(device.getMappedPOI().getMapIcon()!=null&&!device.getMappedPOI().getMapIcon().getId().equals(device.getDeviceType().getDefaultMapIcon().getId())){
                        device.setPreConnectivityLossIcon(device.getMappedPOI().getMapIcon());
                    }
                    device.getMappedPOI().setMapIcon(device.getDeviceType().getDefaultMapIcon());
                    gatewayService.massMerge(List.of(device,device.getMappedPOI()));
                }
            }
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

private static final class GetOrCreateDeviceResponse{
        private final Device device;
        private final String newVersion;

    public GetOrCreateDeviceResponse(Device device, String newVersion) {
        this.device = device;
        this.newVersion = newVersion;
    }

    public Device getDevice() {
        return device;
    }

    public String getNewVersion() {
        return newVersion;
    }
}
    private IOTMessage stateChanged(StateChanged stateChanged, Gateway gateway, SecurityContextBase gatewaySecurityContext) {
        String newVersion=null;
        Remote remote=null;
        Map<String, Object> values = stateChanged.getValues();
        String version = stateChanged.getVersion();
        String deviceId = stateChanged.getDeviceId();
        String deviceTypeId = stateChanged.getDeviceType();
        Double latitude = stateChanged.getLatitude();
        Double longitude = stateChanged.getLongitude();
        List<Remote> keepAlive=new ArrayList<>();
        keepAlive.add(gateway);
        if(deviceId !=null){
            GetOrCreateDeviceResponse getOrCreateDeviceResponse = getGetOrCreateDevice(gateway, gatewaySecurityContext, values, version, deviceId, deviceTypeId);
            newVersion=getOrCreateDeviceResponse.getNewVersion();
            remote=getOrCreateDeviceResponse.getDevice();
            keepAlive.add(remote);
        }
        else{
            newVersion= version !=null&&!version.equals(gateway.getVersion())? version :null;
            gatewayService.updateGateway(new GatewayUpdate().setGateway(gateway).setDeviceProperties(values).setVersion(version),null);
            remote=gateway;
        }

        String status=getStatus(remote,stateChanged.getStatus());
        MapIcon mapIcon=getOrCreateMapIcon(status,remote,gatewaySecurityContext);
        MappedPOICreate mappedPOICreate = new MappedPOICreate()
                .setExternalId(deviceId)
                .setRelatedId(remote.getId())
                .setRelatedType(remote.getClass().getCanonicalName())

                .setMapIcon(mapIcon);

        if(!remote.isLockLocation()){
            mappedPOICreate
                    .setLon(longitude)
                    .setLat(latitude);
        }
        if(!remote.isLockName()){
            mappedPOICreate.setName(remote.getName());
        }

        MappedPOI mappedPOI = remote.getMappedPOI();
        if(mappedPOI ==null){
            mappedPOI = mappedPOIService.createMappedPOI(mappedPOICreate, gatewaySecurityContext);
            remoteService.updateRemote(new RemoteUpdate().setRemote(remote).setMappedPOI(mappedPOI),gatewaySecurityContext);
        }
        else{
            if(mappedPOIService.updateMappedPOINoMerge(mappedPOICreate, mappedPOI)){
                mappedPOIService.merge(mappedPOI);
            }

        }

        if(newVersion != null){
            updateVersion(newVersion,remote);
        }

        updateKeepAlive(stateChanged.getSentAt(),gatewaySecurityContext,keepAlive);

        return new StateChangedReceived().setStateChangedId(stateChanged.getId());
    }

    private String getStatus(Remote remote, String status) {
        if(status==null){
            //TODO: remove once implemented on HW side
            if(remote.getDeviceProperties().get("DimLevel")!=null&&remote.getDeviceProperties().get("DimOnOff")!=null){
                int dimLevel= (int) remote.getDeviceProperties().get("DimLevel");
                boolean dimOnOff= (boolean) remote.getDeviceProperties().get("DimOnOff");
                return dimOnOff&&dimLevel>0?(dimLevel<100?"dim":"on"):"off";

            }
        }

        return status;
    }

    private MapIcon getOrCreateMapIcon(String status, Remote remote, SecurityContextBase gatewaySecurityContext) {
        if(remote instanceof Gateway){
            return gatewayMapIcon;
        }
        if(status==null){
            status=DeviceTypeService.UNKNOWN_STATUS_SUFFIX;
        }
        if(remote instanceof Device device){
            DeviceType deviceType = device.getDeviceType();
            Class<? extends Device> deviceClass = device.getClass();

            return deviceTypeService.getOrCreateMapIcon(status, deviceType.getName(), deviceClass,gatewaySecurityContext);

        }
        return null;
    }



    private GetOrCreateDeviceResponse getGetOrCreateDevice(Gateway gateway, SecurityContextBase gatewaySecurityContext, Map<String, Object> state, String version, String deviceId, String deviceTypeId) {
        String newVersion;
        DeviceCreate deviceCreate = new DeviceCreate()
                .setGateway(gateway)
                .setDeviceProperties(state)
                .setName(deviceId);


        Device device = deviceService.listAllDevices(gatewaySecurityContext, new DeviceFilter().setRemoteIds(Collections.singleton(deviceId))).stream().findFirst().orElse(null);
        if (device == null) {
            DeviceType deviceType = deviceTypeService.getOrCreateDeviceType(deviceTypeId, gatewaySecurityContext);
            deviceCreate
                    .setDeviceType(deviceType)
                    .setRemoteId(deviceId)
                    .setVersion(version);

            device=deviceService.createDevice(deviceCreate, gatewaySecurityContext);
            newVersion= device.getVersion();
        } else {
            newVersion= version !=null&&!version.equals(device.getVersion())? version :null;
            if (deviceService.updateDeviceNoMerge(device, deviceCreate)) {
                deviceService.merge(device);
            }
        }
        return new GetOrCreateDeviceResponse(device,newVersion);
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
        PendingGateway pendingGateway = pendingGatewayService.createPendingGateway(new PendingGatewayCreate().setGatewayId(registerGateway.getGatewayId()).setPublicKey(registerGateway.getPublicKey()).setNoSignatureCapabilities(registerGateway.getNoSignatureCapabilities()).setName(registerGateway.getGatewayId()), adminSecurityContext);
        return new RegisterGatewayReceived().setRegisterGatewayId(registerGateway.getId());
    }

    private UpdateStateSchemaReceived updateStateSchema(UpdateStateSchema updateStateSchema, Gateway gateway, SecurityContextBase gatewaySecurityContext) {
        GetOrCreateDeviceResponse getOrCreateDeviceResponse = getGetOrCreateDevice(gateway, gatewaySecurityContext, null, null, updateStateSchema.getDeviceId(), updateStateSchema.getDeviceType());
        Device device=getOrCreateDeviceResponse.getDevice();
        DeviceType deviceType=device.getDeviceType();
        StateSchema stateSchema=getOrCreateStateSchema(deviceType,updateStateSchema.getVersion(),updateStateSchema.getJsonSchema(),gatewaySecurityContext);
        deviceService.updateDevice(new DeviceUpdate().setDevice(device).setCurrentSchema(stateSchema),null);
        Set<String> externalIds=updateStateSchema.getSchemaActions().stream().map(f->f.getId()).collect(Collectors.toSet());
        Map<String, com.wizzdi.basic.iot.model.SchemaAction> schemaActionMap=externalIds.isEmpty()?Collections.emptyMap():schemaActionService.listAllSchemaActions(null,new SchemaActionFilter().setStateSchemas(Collections.singletonList(stateSchema)).setExternalIds(externalIds)).stream().collect(Collectors.toMap(f->f.getId(), f->f,(a, b)->a));
        for (SchemaAction incomingSchemaAction : updateStateSchema.getSchemaActions()) {
            com.wizzdi.basic.iot.model.SchemaAction schemaAction = schemaActionMap.get(incomingSchemaAction.getId());
            if(schemaAction==null){
                schemaAction=schemaActionService.createSchemaAction(new SchemaActionCreate().setActionSchema(incomingSchemaAction.getJsonSchema()).setStateSchema(stateSchema).setExternalId(incomingSchemaAction.getId()).setName(incomingSchemaAction.getName()),gatewaySecurityContext);
                schemaActionMap.put(schemaAction.getExternalId(),schemaAction);
            }
        }
        return new UpdateStateSchemaReceived().setUpdateStateSchemaId(updateStateSchema.getId());
    }

    private SetStateSchemaReceived setStateSchema(SetStateSchema setStateSchema, Gateway gateway, SecurityContextBase gatewaySecurityContext) {
        GetOrCreateDeviceResponse getOrCreateDeviceResponse = getGetOrCreateDevice(gateway, gatewaySecurityContext, null, null, setStateSchema.getDeviceId(), setStateSchema.getDeviceType());
        Device device=getOrCreateDeviceResponse.getDevice();
        DeviceType deviceType=device.getDeviceType();
        StateSchema stateSchema=stateSchemaService.listAllStateSchemas(null,new StateSchemaFilter().setUserAddedSchema(false).setVersion(setStateSchema.getVersion()).setDeviceTypes(Collections.singletonList(deviceType))).stream().findFirst().orElse(null);
        boolean found=stateSchema!=null;
        //TODO: consider fallback to previous version
        if(found){
            deviceService.updateDevice(new DeviceUpdate().setDevice(device).setCurrentSchema(stateSchema),null);
        }
        return new SetStateSchemaReceived().setSetStateSchemaId(setStateSchema.getId()).setFound(found);
    }

    private StateSchema getOrCreateStateSchema(DeviceType deviceType, int version, String jsonSchema, SecurityContextBase gatewaySecurityContext) {
        StateSchema stateSchema=stateSchemaService.listAllStateSchemas(null,new StateSchemaFilter().setUserAddedSchema(false).setVersion(version).setDeviceTypes(Collections.singletonList(deviceType))).stream().findFirst().orElse(null);
        StateSchemaCreate stateSchemaCreate=new StateSchemaCreate().setDeviceType(deviceType).setVersion(version).setStateSchemaJson(jsonSchema).setName(deviceType.getName()+" Schema V"+version);
        if(stateSchema==null){
            stateSchema=stateSchemaService.createStateSchema(stateSchemaCreate,gatewaySecurityContext);
        }
        return stateSchema;
    }
}
