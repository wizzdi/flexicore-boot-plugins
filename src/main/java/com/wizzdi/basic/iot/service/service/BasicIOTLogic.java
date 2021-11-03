package com.wizzdi.basic.iot.service.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.flexicore.security.SecurityContextBase;
import com.wizzdi.basic.iot.client.*;
import com.wizzdi.basic.iot.model.Device;
import com.wizzdi.basic.iot.model.DeviceType;
import com.wizzdi.basic.iot.model.Gateway;
import com.wizzdi.basic.iot.model.PendingGateway;
import com.wizzdi.basic.iot.service.request.*;
import com.wizzdi.flexicore.boot.base.interfaces.Plugin;
import com.wizzdi.flexicore.security.interfaces.SecurityContextProvider;
import org.pf4j.Extension;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.Optional;

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
    @Lazy
    private SecurityContextProvider securityContextProvider;

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
        if (iotMessage instanceof RegisterGateway) {
            return registerGateway((RegisterGateway) iotMessage);
        }
        Optional<Gateway> gatewayOptional = gatewayService.listAllGateways(null, new GatewayFilter().setRemoteIds(Collections.singleton(iotMessage.getGatewayId()))).stream().findFirst();
        if(gatewayOptional.isEmpty()){
            logger.warn("could not get gateway "+iotMessage.getGatewayId());
        }
        SecurityContextBase gatewaySecurityContext = gatewayOptional.map(Gateway::getGatewayUser).map(f -> securityContextProvider.getSecurityContext(f)).orElse(null);

        if(gatewaySecurityContext==null){
            logger.warn("could not find security context for gateway "+iotMessage.getGatewayId());
            return null;
        }

        Gateway gateway = gatewayOptional.get();
        if (iotMessage instanceof StateChanged) {
            return stateChanged((StateChanged) iotMessage, gateway,gatewaySecurityContext);
        }

        if (iotMessage instanceof UpdateStateSchema) {
            return updateStateSchema((UpdateStateSchema) iotMessage, gateway,gatewaySecurityContext);
        }
        return null;
    }

    private IOTMessage stateChanged(StateChanged stateChanged, Gateway gateway, SecurityContextBase gatewaySecurityContext) {
        DeviceCreate deviceCreate = new DeviceCreate()
                .setGateway(gateway)
                .setOther(stateChanged.getValues());


        Device device = deviceService.listAllDevices(gatewaySecurityContext, new DeviceFilter().setRemoteIds(Collections.singleton(stateChanged.getDeviceId()))).stream().findFirst().orElse(null);
        if(device==null){
            DeviceType deviceType = deviceTypeService.getOrCreateDeviceType(stateChanged.getDeviceType(),gatewaySecurityContext);
            deviceCreate
                    .setDeviceType(deviceType)
                    .setRemoteId(stateChanged.getDeviceId());
            deviceService.createDevice(deviceCreate,gatewaySecurityContext);
        }
        else{
            if(deviceService.updateDeviceNoMerge(device,deviceCreate)){
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
        DeviceTypeCreate deviceTypeCreate=new DeviceTypeCreate().setStateJsonSchema(updateStateSchema.getJsonSchema()).setVersion(updateStateSchema.getVersion());
        if(updateStateSchema.getVersion()>deviceType.getVersion()){
            if(deviceTypeService.updateDeviceTypeNoMerge(deviceType,deviceTypeCreate)){
                deviceTypeService.merge(deviceType);
            }
        }
        return new UpdateStateSchemaReceived().setUpdateStateSchemaId(updateStateSchema.getId());
    }
}
