package com.wizzdi.basic.iot.service.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.flexicore.model.Basic;
import com.flexicore.security.SecurityContextBase;
import com.wizzdi.basic.iot.client.*;
import com.wizzdi.basic.iot.client.SchemaAction;
import com.wizzdi.basic.iot.model.*;
import com.wizzdi.basic.iot.service.events.RemoteStatusChanged;
import com.wizzdi.basic.iot.service.events.RemoteUpdatedEvent;
import com.wizzdi.basic.iot.service.request.*;
import com.wizzdi.basic.iot.service.response.RemoteUpdateResponse;
import com.wizzdi.basic.iot.service.utils.DistanceUtils;
import com.wizzdi.flexicore.boot.base.interfaces.Plugin;
import com.wizzdi.flexicore.security.events.BasicCreated;
import com.wizzdi.flexicore.security.events.BasicUpdated;
import com.wizzdi.flexicore.security.request.BasicPropertiesFilter;
import com.wizzdi.flexicore.security.request.DateFilter;
import com.wizzdi.maps.model.MapIcon;
import com.wizzdi.maps.model.MappedPOI;
import com.wizzdi.maps.service.request.MappedPOICreate;
import com.wizzdi.maps.service.request.MappedPOIUpdate;
import com.wizzdi.maps.service.service.MapIconService;
import com.wizzdi.maps.service.service.MappedPOIService;
import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.Timer;
import org.pf4j.Extension;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.OffsetDateTime;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.concurrent.TimeUnit;
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
    @Value("${basic.iot.firmware.update.reminderInterval:#{60*60*1000}}")
    private long reminderInterval;
    @Value("${basic.iot.mqtt.badMessageResponse:false}")
    private boolean badMessageResponse;
    @Value("${basic.iot.mqtt.keepAlive.bounce:#{3*60*1000}}")
    private long keepAliveBounceThreshold;

    @Value("${basic.iot.map.locationDistanceThresholdMeters:#{200}}")
    private int locationDistanceThreshold;
    @Value("${basic.iot.mqtt.dropMessageMs:#{60*60*1000}}")
    private long dropMessageMs;
    @Autowired
    @Qualifier("gatewayMapIcon")
    private MapIcon gatewayMapIcon;

    @Autowired
    private KeepAliveBounceService keepAliveBounceService;
    @Autowired
    private ApplicationEventPublisher eventPublisher;
    @Autowired
    private Timer checkConnectivityTimer;
    @Autowired
    private Counter droppedMessagesCounter;


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
        if(iotMessage.getSentAt().isBefore(OffsetDateTime.now().minus(dropMessageMs,ChronoUnit.MILLIS))){
            logger.debug("dropping message , sent at {} ",iotMessage.getSentAt());
            droppedMessagesCounter.increment();
            return null;
        }
        if(iotMessage instanceof KeepAlive keepAlive ){
            if(shouldBounce(iotMessage)){
                logger.debug("bouncing keep alive {}",keepAlive.getId());
                return null;
            }
            keepAliveBounceService.setLastBounce(keepAlive.getGatewayId(),System.currentTimeMillis());

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
        MessageHandleContext messageHandleContext= switch (iotMessage) {
            case KeepAlive keepAlive ->   onKeepAlive(keepAlive, gateway, gatewaySecurityContext);
            case StateChanged stateChanged -> stateChanged(stateChanged, gateway, gatewaySecurityContext);
            case SetStateSchema setStateSchema -> setStateSchema(setStateSchema, gateway, gatewaySecurityContext);
            case UpdateStateSchema updateStateSchema -> updateStateSchema(updateStateSchema, gateway, gatewaySecurityContext);
            default -> MessageHandleContext.EMPTY;
        };
        return postHandle(iotMessage,messageHandleContext);

    }

    private IOTMessage postHandle(IOTMessage iotMessage, MessageHandleContext messageHandleContext) {
        List<Basic> toMerge = new ArrayList<>(messageHandleContext.toMerge.stream().filter(f -> f instanceof Basic).map(f->(Basic)f).collect(Collectors.toMap(f->f.getId(),f->f,(a,b)->a)).values());
        LinkedHashMap<String, Object> eventMap = messageHandleContext.events.stream().filter(f -> f != null).collect(Collectors.toMap(f -> getEventId(f), f -> f, (a, b) -> mergeEvents(a, b), LinkedHashMap::new));
        logger.debug("iot message {} handled, merging {} and publishing {} events",iotMessage.getId(),toMerge.size(),eventMap.size());
        remoteService.massMerge(toMerge,true,false);

         for (Object event : eventMap.values()) {
              eventPublisher.publishEvent(event);
         }
         return messageHandleContext.response();

    }

    private Object mergeEvents(Object a, Object b) {
        if(a instanceof RemoteUpdatedEvent ){
            return a;
        }
        return b;
    }

    private String getEventId(Object f) {
        if(f instanceof BasicCreated<?> basicCreated){
            return basicCreated.getBaseclass().getId();
        }
        if(f instanceof BasicUpdated<?> basicUpdated){
            return basicUpdated.getBaseclass().getId();
        }
        if( f instanceof RemoteStatusChanged remoteStatusChanged){
            return remoteStatusChanged.remote().getId();
        }
        return UUID.randomUUID().toString();
    }

    private boolean shouldBounce(IOTMessage iotMessage) {
        Long lastKeepAliveReceived = keepAliveBounceService.getLastBounce(iotMessage.getGatewayId());
        return (System.currentTimeMillis() - lastKeepAliveReceived) < keepAliveBounceThreshold;
    }

    private MessageHandleContext onKeepAlive(KeepAlive keepAlive, Gateway gateway, SecurityContextBase gatewaySecurityContext) {
        MessageHandleContext messageHandleContext = new MessageHandleContext(new ArrayList<>(), new ArrayList<>(), null);
        List<Remote> remotesWithKeepAlive = new ArrayList<>(List.of(gateway));
        List<Device> devices = keepAlive.getDeviceIds().isEmpty() ? new ArrayList<>() : deviceService.listAllDevices(gatewaySecurityContext, new DeviceFilter().setRemoteIds(keepAlive.getDeviceIds()));
        remotesWithKeepAlive.addAll(devices);
        UpdateKeepAliveResponse updateKeepAliveResponse = updateKeepAlive(keepAlive.getSentAt(), gatewaySecurityContext, remotesWithKeepAlive);
        List<Remote> remotesThatChangedState=updateKeepAliveResponse.statusChanged();
        messageHandleContext = MessageHandleContext.merged(messageHandleContext, updateKeepAliveResponse.messageHandleContext());
        for (Remote remote : remotesThatChangedState) {
            MapIcon newStatus = remote.getPreConnectivityLossIcon();
            if(remote.getMappedPOI()!=null&& newStatus !=null){
                MapIcon previous=remote.getMappedPOI().getMapIcon();
                remote.getMappedPOI().setMapIcon(newStatus);
                messageHandleContext.toMerge.add(remote.getMappedPOI());
                messageHandleContext.events.add(new BasicUpdated<>(remote.getMappedPOI()));
                messageHandleContext.events.add(new RemoteStatusChanged(remote,newStatus,previous));
                logger.debug("remote {}({}) connected , changing status to {}({})",remote.getRemoteId(),remote.getName(), newStatus.getName(), newStatus.getId());
            }
        }
        return messageHandleContext;


    }


    record MessageHandleContext(List<Object> toMerge,List<Object> events,IOTMessage response){

        public static MessageHandleContext EMPTY=new MessageHandleContext(Collections.emptyList(),Collections.emptyList(),null);



        public static MessageHandleContext merged(MessageHandleContext one , MessageHandleContext two) {
            MessageHandleContext context=new MessageHandleContext(new ArrayList<>(one.toMerge()),new ArrayList<>(one.events()),one.response()!=null?one.response():two.response());
            context.toMerge.addAll(two.toMerge());
            context.events.addAll(two.events());
            return context;
        }

        public MessageHandleContext withResponse(IOTMessage iotMessage) {
            return new MessageHandleContext(toMerge,events,iotMessage);
        }
    };

    record UpdateKeepAliveResponse(List<Remote> statusChanged,MessageHandleContext messageHandleContext) {

    };

    private UpdateKeepAliveResponse updateKeepAlive(OffsetDateTime lastSeen,SecurityContextBase gatewaySecurityContext, List<Remote> remotesWithKeepAlive) {
        MessageHandleContext messageHandleContext=new MessageHandleContext(new ArrayList<>(),new ArrayList<>(),null);
        List<Remote> statusChanged = new ArrayList<>();
        if(lastSeen==null || lastSeen.isAfter(OffsetDateTime.now())){
            lastSeen=OffsetDateTime.now();
        }
        for (Remote remote : remotesWithKeepAlive) {
            if(remote.getLastSeen()==null||lastSeen.isAfter(remote.getLastSeen())){
                RemoteUpdateResponse remoteUpdateResponse = remoteService.updateRemoteNoMerge(remote, new RemoteCreate().setLastSeen(lastSeen));
                if(remoteUpdateResponse.updated()){
                    messageHandleContext.toMerge.add(remote);
                }
            }
            if(remote.getLastSeen().plus(lastSeenThreshold,ChronoUnit.MILLIS).isAfter(OffsetDateTime.now())){
                if (remote.getLastConnectivityChange() == null || remote.getLastConnectivityChange().getConnectivity().equals(Connectivity.OFF)) {
                    ConnectivityChangeCreate connectivityChangeCreate = new ConnectivityChangeCreate().setConnectivity(Connectivity.ON).setRemote(remote).setDate(OffsetDateTime.now());
                    boolean createConnectivityChange = remote.isKeepConnectivityHistory() || remote.getLastConnectivityChange() == null;
                    ConnectivityChange connectivityChange = createConnectivityChange ?connectivityChangeService.createConnectivityChangeNoMerge(connectivityChangeCreate, gatewaySecurityContext): updateConnectivityChangeNoMerge(remote.getLastConnectivityChange(), connectivityChangeCreate,messageHandleContext);
                    if(createConnectivityChange){
                        remote.setLastConnectivityChange(connectivityChange);
                        messageHandleContext.toMerge.add(connectivityChange);
                        messageHandleContext.toMerge.add(remote);
                        messageHandleContext.events().add(new BasicUpdated<>(remote));
                        messageHandleContext.events().add(new BasicUpdated<>(connectivityChange));

                    }

                    logger.info("remote " + remote.getRemoteId() + "(" + remote.getId() + ") is ON");
                    statusChanged.add(remote);

                }
            }

        }
        return new UpdateKeepAliveResponse(statusChanged,messageHandleContext);
    }

    private ConnectivityChange updateConnectivityChangeNoMerge(ConnectivityChange lastConnectivityChange, ConnectivityChangeCreate connectivityChangeCreate, MessageHandleContext messageHandleContext) {
        if(connectivityChangeService.updateConnectivityChangeNoMerge(lastConnectivityChange,connectivityChangeCreate)){
            messageHandleContext.toMerge.add(lastConnectivityChange);
        }
        return lastConnectivityChange;
    }

    /**
     * since connectivity and device mapped POI mapIcon, there can be a situation where the connectivity is off but the status is on.
     * this happens when the device sends stateChanged while checkConnectivity schedule runs, instead of using locks and reducing performance,
     * and since this is not very critical or common situation, we will fix it once a day
     * ${basic.iot.fixInvalidStatusCron:0 0 6 * * *}
     */
    @Scheduled(cron = "${basic.iot.fixInvalidStatusCron:0 0 6 * * *}")
    public void fixInvalidStatus() {
        logger.debug("fixing invalid status");
        long started=System.currentTimeMillis();
        List<Device> remotesAtInvalidStatus = deviceService.listAllDevices(null, new DeviceFilter().setWithoutDefaultIcon(true).setConnectivity(Collections.singleton(Connectivity.OFF)));
        logger.debug("Have found {}  devices at invalid status",remotesAtInvalidStatus.size());
        for (Device atInvalidStatus : remotesAtInvalidStatus) {
            MapIcon defaultMapIcon = atInvalidStatus.getDeviceType().getDefaultMapIcon();
            mappedPOIService.updateMappedPOI(new MappedPOIUpdate().setMappedPOI(atInvalidStatus.getMappedPOI()).setMapIcon(defaultMapIcon),null);
            logger.info("fixed invalid status for device "+atInvalidStatus.getRemoteId()+"("+atInvalidStatus.getId()+")");
        }
        logger.debug("done fixing invalid status {} ",System.currentTimeMillis()-started);
    }
    public long fixIcons(SecurityContextBase securityContext) {
        long started=System.currentTimeMillis();
        fixInvalidStatus();
        return System.currentTimeMillis()-started;
    }

    @Scheduled(fixedDelayString = "${basic.iot.connectivityCheckInterval:60000}",initialDelayString = "${basic.iot.connectivityDelayInterval:60000}")
    public void checkConnectivity() {
        long started=System.nanoTime();
        try {
            logger.debug("checking connectivity");
            OffsetDateTime threshold = OffsetDateTime.now().minus(lastSeenThreshold, ChronoUnit.MILLIS);
            List<Remote> remotesToUpdate = remoteService.listAllRemotes(null, new RemoteFilter().setConnectivity(Collections.singleton(Connectivity.ON)).setLastSeenTo(threshold));
            List<RemoteStatusChanged> events = new ArrayList<>();
            Map<String, Object> toMerge = new HashMap<>();

            for (Remote remote : remotesToUpdate) {
                ConnectivityChangeCreate connectivityChangeCreate = new ConnectivityChangeCreate().setConnectivity(Connectivity.OFF).setRemote(remote).setDate(OffsetDateTime.now());
                connectivityChangeCreate.setName(ConnectivityChangeService.getConnectivityChangeName(connectivityChangeCreate));
                boolean createConnectivityChange = remote.isKeepConnectivityHistory() || remote.getLastConnectivityChange() == null;
                ConnectivityChange connectivityChange = createConnectivityChange ? connectivityChangeService.createConnectivityChangeNoMerge(connectivityChangeCreate, remoteService.getRemoteSecurityContext(remote)) : updateConnectivityChangeNoMerge(remote.getLastConnectivityChange(), connectivityChangeCreate, toMerge);
                if (createConnectivityChange) {
                    remote.setLastConnectivityChange(connectivityChange);
                    toMerge.put(remote.getId(), remote);
                    toMerge.put(connectivityChange.getId(), connectivityChange);
                }

                logger.info("remote " + remote.getRemoteId() + "(" + remote.getId() + ") is OFF");
                if (remote instanceof Device device) {
                    if (device.getMappedPOI() != null && device.getDeviceType() != null && device.getDeviceType().getDefaultMapIcon() != null) {
                        MapIcon previousStatus = device.getMappedPOI().getMapIcon();
                        MapIcon newStatus = device.getDeviceType().getDefaultMapIcon();
                        if (previousStatus != null && !previousStatus.getId().equals(newStatus.getId())) {
                            device.setPreConnectivityLossIcon(previousStatus);
                        }
                        device.getMappedPOI().setMapIcon(newStatus);
                        toMerge.put(device.getId(), device);
                        toMerge.put(device.getMappedPOI().getId(), device.getMappedPOI());
                        events.add(new RemoteStatusChanged(device, newStatus, previousStatus));
                    }
                }



            }
            connectivityChangeService.massMerge(new ArrayList<>(toMerge.values()));
            for (RemoteStatusChanged event : events) {
                eventPublisher.publishEvent(event);

            }
            logger.debug("done checking connectivity");
        }
        finally {
            checkConnectivityTimer.record(System.nanoTime()-started, TimeUnit.NANOSECONDS);

        }
    }

    private ConnectivityChange updateConnectivityChangeNoMerge(ConnectivityChange lastConnectivityChange, ConnectivityChangeCreate connectivityChangeCreate, Map<String, Object> toMerge) {
        if(connectivityChangeService.updateConnectivityChangeNoMerge(lastConnectivityChange,connectivityChangeCreate)){
            toMerge.put(lastConnectivityChange.getId(),lastConnectivityChange);
        }
        return lastConnectivityChange;
    }

    @Scheduled(fixedDelayString = "${basic.iot.firmware.update.checkInterval:60000}")
    public void sendFirmwareUpdates() {
        logger.debug("checking firmwareUpdates");
        List<FirmwareUpdateInstallation> toInstall = firmwareUpdateInstallationService.listAllFirmwareUpdateInstallations(null, new FirmwareUpdateInstallationFilter().setNotExpiredAt(OffsetDateTime.now()).setFirmwareInstallationStates(Collections.singleton(FirmwareInstallationState.PENDING)).setTimeForReminder(OffsetDateTime.now()));


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


    private record GetOrCreateDeviceResponse(Device device, String newVersion,MessageHandleContext messageHandleContext) {

    }
    private MessageHandleContext stateChanged(StateChanged stateChanged, Gateway gateway, SecurityContextBase gatewaySecurityContext) {
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
        MessageHandleContext messageHandleContext=new MessageHandleContext(new ArrayList<>(),new ArrayList<>(),null);
        if(deviceId !=null){
            GetOrCreateDeviceResponse getOrCreateDeviceResponse = getGetOrCreateDevice(gateway, gatewaySecurityContext, values, version, deviceId, deviceTypeId);
            newVersion=getOrCreateDeviceResponse.newVersion();
            remote=getOrCreateDeviceResponse.device();
            messageHandleContext=MessageHandleContext.merged(messageHandleContext,getOrCreateDeviceResponse.messageHandleContext());
            keepAlive.add(remote);
        }
        else{
            newVersion= version !=null&&!version.equals(gateway.getVersion())? version :null;
            RemoteUpdateResponse remoteUpdateResponse = gatewayService.updateGatewayNoMerge(gateway, new GatewayCreate().setDeviceProperties(values).setVersion(version));
            if(remoteUpdateResponse.updated()){
                messageHandleContext.toMerge.add(gateway);
                messageHandleContext.events.add(remoteUpdateResponse.remoteUpdatedEvent()!=null?remoteUpdateResponse.remoteUpdatedEvent():new BasicUpdated<>(gateway));
            }
            remote=gateway;
        }

        String status=getStatus(remote,stateChanged.getStatus());
        MappedPOI mappedPOI = remote.getMappedPOI();

        MapIcon mapIcon=getOrCreateMapIcon(status,remote,gatewaySecurityContext);
        MappedPOICreate mappedPOICreate = new MappedPOICreate()
                .setExternalId(deviceId)
                .setRelatedId(remote.getId())
                .setRelatedType(remote.getClass().getCanonicalName())

                .setMapIcon(mapIcon);

        if(!remote.isLockLocation()
                &&latitude!=null&&longitude!=null
                &&getDistanceFromCurrentLocation(mappedPOI,longitude,latitude)>locationDistanceThreshold){
            mappedPOICreate
                    .setLon(longitude)
                    .setLat(latitude);
        }
        if(!remote.isLockName()){
            mappedPOICreate.setName(remote.getName());
        }

        if(mappedPOI ==null){
            mappedPOI = mappedPOIService.createMappedPOINoMerge(mappedPOICreate, gatewaySecurityContext);
            messageHandleContext.toMerge().add(mappedPOI);
            messageHandleContext.events().add(new BasicCreated<>(mappedPOI));
        }
        else{
            MapIcon previousMapIcon = mappedPOI.getMapIcon();
            if(mappedPOIService.updateMappedPOINoMerge(mappedPOICreate, mappedPOI)){
                messageHandleContext.toMerge().add(mappedPOI);
                messageHandleContext.events().add(new BasicUpdated<>(mappedPOI));
                MapIcon currentMapIcon = mappedPOI.getMapIcon();
                if(currentMapIcon!=null&&(previousMapIcon==null||!currentMapIcon.getId().equals(previousMapIcon.getId()))){
                    messageHandleContext.events().add(new RemoteStatusChanged(remote,currentMapIcon,previousMapIcon));
                }
            }

        }
        RemoteUpdateResponse remoteUpdateResponse = remoteService.updateRemoteNoMerge(remote, new RemoteUpdate().setReportedLat(latitude).setReportedLon(longitude).setMappedPOI(mappedPOI));
        if(remoteUpdateResponse.updated()){
            messageHandleContext.toMerge().add(remote);
            messageHandleContext.events().add(remoteUpdateResponse.remoteUpdatedEvent()!=null?remoteUpdateResponse.remoteUpdatedEvent():new BasicUpdated<>(remote));
        }


        if(newVersion != null){
            MessageHandleContext updateVersionContext=updateVersion(newVersion,remote);
            messageHandleContext=MessageHandleContext.merged(messageHandleContext,updateVersionContext);

        }

        messageHandleContext=MessageHandleContext.merged(messageHandleContext,updateKeepAlive(stateChanged.getSentAt(),gatewaySecurityContext,keepAlive).messageHandleContext());

        return messageHandleContext.withResponse(new StateChangedReceived().setStateChangedId(stateChanged.getId()));
    }

    private double getDistanceFromCurrentLocation(MappedPOI mappedPOI, double longitude, double latitude) {
        if(mappedPOI.getLat()==null || mappedPOI.getLon()==null){
            return Double.POSITIVE_INFINITY;
        }
        return DistanceUtils.haversineDistance(mappedPOI.getLat(),mappedPOI.getLon(),latitude,longitude);
    }



    private boolean isZeroLocation(double longitude, double latitude) {
        return longitude==0||latitude==0;
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
        MessageHandleContext messageHandleContext=new MessageHandleContext(new ArrayList<>(),new ArrayList<>(),null);
        String newVersion;
        DeviceCreate deviceCreate = new DeviceCreate()
                .setGateway(gateway)
                .setDeviceProperties(state)
                .setVersion(version)
                .setName(deviceId);


        Device device = deviceService.listAllDevices(gatewaySecurityContext, new DeviceFilter().setRemoteIds(Collections.singleton(deviceId))).stream().findFirst().orElse(null);
        if (device == null) {
            DeviceType deviceType = deviceTypeService.getOrCreateDeviceType(deviceTypeId, gatewaySecurityContext);
            deviceCreate
                    .setDeviceType(deviceType)
                    .setRemoteId(deviceId);


            device=deviceService.createDeviceNoMerge(deviceCreate, gatewaySecurityContext);
            messageHandleContext.toMerge().add(device);
            messageHandleContext.events().add(new BasicCreated<>(device));
            newVersion= device.getVersion();
        } else {
            newVersion= version !=null&&!version.equals(device.getVersion())? version :null;
            RemoteUpdateResponse remoteUpdateResponse = deviceService.updateDeviceNoMerge(device, deviceCreate);
            if (remoteUpdateResponse.updated()) {
                messageHandleContext.toMerge().add(device);
                messageHandleContext.events().add(remoteUpdateResponse.remoteUpdatedEvent()!=null?remoteUpdateResponse.remoteUpdatedEvent():new BasicUpdated<>(device));
            }
        }
        return new GetOrCreateDeviceResponse(device,newVersion,messageHandleContext);
    }

    private MessageHandleContext updateVersion(String newVersion, Remote remote) {
        MessageHandleContext messageHandleContext=new MessageHandleContext(new ArrayList<>(),new ArrayList<>(),null);
        List<FirmwareUpdateInstallation> firmwareUpdateInstallations = firmwareUpdateInstallationService.listAllFirmwareUpdateInstallations(null, new FirmwareUpdateInstallationFilter().setTargetRemotes(Collections.singletonList(remote)).setFirmwareInstallationStates(Collections.singleton(FirmwareInstallationState.PENDING)).setVersions(Collections.singleton(newVersion)));
        for (FirmwareUpdateInstallation firmwareUpdateInstallation : firmwareUpdateInstallations) {
            if(firmwareUpdateInstallationService.updateFirmwareUpdateInstallationNoMerge(firmwareUpdateInstallation,new FirmwareUpdateInstallationCreate().setFirmwareInstallationState(FirmwareInstallationState.INSTALLED).setDateInstalled(OffsetDateTime.now()))){
                messageHandleContext.toMerge.add(firmwareUpdateInstallation);
                messageHandleContext.events.add(new BasicUpdated<>(firmwareUpdateInstallation));
            }
        }
        if(!firmwareUpdateInstallations.isEmpty()){
            logger.info("marked "+firmwareUpdateInstallations.size() +" as installed due to remote "+remote.getRemoteId()+"("+remote.getId()+") updated to version "+newVersion);
        }
        return messageHandleContext;
    }


    private RegisterGatewayReceived registerGateway(RegisterGateway registerGateway) {
        PendingGateway pendingGateway = pendingGatewayService.createPendingGateway(new PendingGatewayCreate().setGatewayId(registerGateway.getGatewayId()).setPublicKey(registerGateway.getPublicKey()).setNoSignatureCapabilities(registerGateway.getNoSignatureCapabilities()).setName(registerGateway.getGatewayId()), adminSecurityContext);
        return new RegisterGatewayReceived().setRegisterGatewayId(registerGateway.getId());
    }

    private MessageHandleContext updateStateSchema(UpdateStateSchema updateStateSchema, Gateway gateway, SecurityContextBase gatewaySecurityContext) {
        GetOrCreateDeviceResponse getOrCreateDeviceResponse = getGetOrCreateDevice(gateway, gatewaySecurityContext, null, null, updateStateSchema.getDeviceId(), updateStateSchema.getDeviceType());
        Device device=getOrCreateDeviceResponse.device();
        DeviceType deviceType=device.getDeviceType();
        MessageHandleContext messageHandleContext=getOrCreateDeviceResponse.messageHandleContext();
        StateSchema stateSchema=getOrCreateStateSchema(deviceType,updateStateSchema.getVersion(),updateStateSchema.getJsonSchema(),gatewaySecurityContext);
        RemoteUpdateResponse remoteUpdateResponse = deviceService.updateDeviceNoMerge(device, new DeviceCreate().setCurrentSchema(stateSchema));
        if(remoteUpdateResponse.updated()){
            messageHandleContext.toMerge.add(device);
            messageHandleContext.events.add(remoteUpdateResponse.remoteUpdatedEvent()!=null?remoteUpdateResponse.remoteUpdatedEvent():new BasicUpdated<>(device));
        }
        Set<String> externalIds=updateStateSchema.getSchemaActions().stream().map(f->f.getId()).collect(Collectors.toSet());
        Map<String, com.wizzdi.basic.iot.model.SchemaAction> schemaActionMap=externalIds.isEmpty()?Collections.emptyMap():schemaActionService.listAllSchemaActions(null,new SchemaActionFilter().setStateSchemas(Collections.singletonList(stateSchema)).setExternalIds(externalIds)).stream().collect(Collectors.toMap(f->f.getId(), f->f,(a, b)->a));
        for (SchemaAction incomingSchemaAction : updateStateSchema.getSchemaActions()) {
            com.wizzdi.basic.iot.model.SchemaAction schemaAction = schemaActionMap.get(incomingSchemaAction.getId());
            if(schemaAction==null){
                schemaAction=schemaActionService.createSchemaActionNoMerge(new SchemaActionCreate().setActionSchema(incomingSchemaAction.getJsonSchema()).setStateSchema(stateSchema).setExternalId(incomingSchemaAction.getId()).setName(incomingSchemaAction.getName()),gatewaySecurityContext);
                messageHandleContext.toMerge().add(schemaAction);
                messageHandleContext.events().add(new BasicCreated<>(schemaAction));
                schemaActionMap.put(schemaAction.getExternalId(),schemaAction);
            }
        }
        return messageHandleContext.withResponse(new UpdateStateSchemaReceived().setUpdateStateSchemaId(updateStateSchema.getId()));
    }

    private MessageHandleContext setStateSchema(SetStateSchema setStateSchema, Gateway gateway, SecurityContextBase gatewaySecurityContext) {
        GetOrCreateDeviceResponse getOrCreateDeviceResponse = getGetOrCreateDevice(gateway, gatewaySecurityContext, null, null, setStateSchema.getDeviceId(), setStateSchema.getDeviceType());
        Device device=getOrCreateDeviceResponse.device();
        DeviceType deviceType=device.getDeviceType();
        MessageHandleContext messageHandleContext=getOrCreateDeviceResponse.messageHandleContext();
        StateSchema stateSchema=stateSchemaService.listAllStateSchemas(null,new StateSchemaFilter().setUserAddedSchema(false).setVersion(setStateSchema.getVersion()).setDeviceTypes(Collections.singletonList(deviceType))).stream().findFirst().orElse(null);
        boolean found=stateSchema!=null;
        //TODO: consider fallback to previous version
        if(found){
            RemoteUpdateResponse remoteUpdateResponse = deviceService.updateDeviceNoMerge(device, new DeviceCreate().setCurrentSchema(stateSchema));
            if(remoteUpdateResponse.updated()){
                messageHandleContext.toMerge().add(device);
                messageHandleContext.events().add(remoteUpdateResponse.remoteUpdatedEvent()!=null?remoteUpdateResponse.remoteUpdatedEvent():new BasicUpdated<>(device));
            }
        }
        return messageHandleContext.withResponse(new SetStateSchemaReceived().setSetStateSchemaId(setStateSchema.getId()).setFound(found));
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
