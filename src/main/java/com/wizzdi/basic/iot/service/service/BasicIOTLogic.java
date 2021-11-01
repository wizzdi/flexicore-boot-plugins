package com.wizzdi.basic.iot.service.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.flexicore.security.SecurityContextBase;
import com.wizzdi.basic.iot.client.*;
import com.wizzdi.basic.iot.model.Device;
import com.wizzdi.basic.iot.model.DeviceType;
import com.wizzdi.basic.iot.model.PendingGateway;
import com.wizzdi.basic.iot.service.request.*;
import com.wizzdi.flexicore.boot.base.interfaces.Plugin;
import org.pf4j.Extension;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Collections;

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

    @Override
    public void onIOTMessage(IOTMessage iotMessage) {
        logger.info("received message "+iotMessage);
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
        if (iotMessage instanceof ConnectMessage) {
            return connect((ConnectMessage) iotMessage);
        }
        if (iotMessage instanceof StateChanged) {
            return stateChanged((StateChanged) iotMessage);
        }
        if (iotMessage instanceof RegisterGateway) {
            return registerGateway((RegisterGateway) iotMessage);
        }
        if (iotMessage instanceof UpdateStateSchema) {
            return updateStateSchema((UpdateStateSchema) iotMessage);
        }
        return null;
    }

    private IOTMessage stateChanged(StateChanged stateChanged) {
        DeviceCreate deviceCreate = new DeviceCreate()
                .setOther(stateChanged.getValues());


        Device device = deviceService.listAllDevices(adminSecurityContext, new DeviceFilter().setRemoteIds(Collections.singleton(stateChanged.getDeviceId()))).stream().findFirst().orElse(null);
        if(device==null){
            DeviceType deviceType = deviceTypeService.getOrCreateDeviceType(stateChanged.getDeviceType(),adminSecurityContext);
            deviceCreate
                    .setDeviceType(deviceType)
                    .setRemoteId(stateChanged.getDeviceId());
            deviceService.createDevice(deviceCreate,adminSecurityContext);
        }
        else{
            if(deviceService.updateDeviceNoMerge(device,deviceCreate)){
                deviceService.merge(device);
            }
        }
        return new StateChangedReceived().setStateChangedId(stateChanged.getId());
    }

    private ConnectReceived connect(ConnectMessage connectMessage) {
        ConnectionState connectionState = gatewayService.listAllGateways(null, new GatewayFilter().setRemoteIds(Collections.singleton(connectMessage.getGatewayId()))).stream().findFirst().map(f -> ConnectionState.CONNECTED).orElse(ConnectionState.INVALID_GATEWAY_ID);
        return new ConnectReceived().setConnectId(connectMessage.getId()).setConnectionState(connectionState);

    }

    private RegisterGatewayReceived registerGateway(RegisterGateway registerGateway) {
        PendingGateway pendingGateway = pendingGatewayService.createPendingGateway(new PendingGatewayCreate().setGatewayId(registerGateway.getGatewayId()).setPublicKey(registerGateway.getPublicKey()).setName(registerGateway.getGatewayId()), adminSecurityContext);
        return new RegisterGatewayReceived().setRegisterGatewayId(registerGateway.getId());
    }

    private UpdateStateSchemaReceived updateStateSchema(UpdateStateSchema updateStateSchema) {
        DeviceType deviceType = deviceTypeService.getOrCreateDeviceType(updateStateSchema.getDeviceType(), adminSecurityContext);
        DeviceTypeCreate deviceTypeCreate=new DeviceTypeCreate().setStateJsonSchema(updateStateSchema.getJsonSchema()).setVersion(updateStateSchema.getVersion());
        if(updateStateSchema.getVersion()>deviceType.getVersion()){
            if(deviceTypeService.updateDeviceTypeNoMerge(deviceType,deviceTypeCreate)){
                deviceTypeService.merge(deviceType);
            }
        }
        return new UpdateStateSchemaReceived().setUpdateStateSchemaId(updateStateSchema.getId());
    }
}
