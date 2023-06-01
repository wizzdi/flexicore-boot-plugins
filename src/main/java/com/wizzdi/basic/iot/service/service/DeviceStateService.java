package com.wizzdi.basic.iot.service.service;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.flexicore.security.SecurityContextBase;
import com.google.common.collect.Lists;
import com.wizzdi.basic.iot.client.BasicIOTClient;
import com.wizzdi.basic.iot.client.ChangeState;
import com.wizzdi.basic.iot.client.ChangeStateReceived;
import com.wizzdi.basic.iot.client.StateChanged;
import com.wizzdi.basic.iot.model.Device;
import com.wizzdi.basic.iot.service.request.ChangeStateRequest;
import com.wizzdi.basic.iot.service.response.ChangeStateResponse;
import com.wizzdi.basic.iot.service.response.ChangeStateResponseEntry;
import com.wizzdi.flexicore.boot.base.interfaces.Plugin;
import org.pf4j.Extension;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;

import java.util.*;
import java.util.concurrent.*;

@Extension
@Component

public class DeviceStateService implements Plugin {

    private static final Logger logger= LoggerFactory.getLogger(DeviceStateService.class);
    private static final ExecutorService executorService= Executors.newVirtualThreadPerTaskExecutor();

    @Autowired
    @Lazy
    private DeviceService deviceService;

    @Autowired
    @Lazy
    private BasicIOTClient basicIOTClient;



    public void validate(ChangeStateRequest changeStateRequest,
                         SecurityContextBase securityContext) {
        if (changeStateRequest.getDeviceFilter() == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "device filter must be provided");
        }
        deviceService.validateFiltering(changeStateRequest.getDeviceFilter(), securityContext);
        if (changeStateRequest.getValues() == null || changeStateRequest.getValues().isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "at least one state value must be provided");
        }

    }



    public ChangeStateResponse changeState(SecurityContextBase securityContext, ChangeStateRequest changeStateRequest) {
        Map<String, ChangeStateResponseEntry> responseEntryMap=new HashMap<>();
        List<Device> devices = deviceService.listAllDevices(securityContext, changeStateRequest.getDeviceFilter());
        List<Future<ChangeStateResponseEntry>> tasks=new ArrayList<>();
        if(changeStateRequest.isAsync()){
            for (Device device : devices) {
                ChangeStateResponseEntry changeStateResponseEntry = changeDeviceStateAsync(changeStateRequest, device);
                responseEntryMap.put(changeStateResponseEntry.getRemoteId(),changeStateResponseEntry);

            }
        }
        else{
            for (Device device : devices) {
                Future<ChangeStateResponseEntry> submit = executorService.submit(() -> changeDeviceState(changeStateRequest, device));
                tasks.add(submit);

            }
            for (Future<ChangeStateResponseEntry> task : tasks) {
                try {
                    ChangeStateResponseEntry changeStateResponseEntry = task.get(1, TimeUnit.MINUTES);
                    responseEntryMap.put(changeStateResponseEntry.getRemoteId(),changeStateResponseEntry);
                } catch (Throwable e) {
                    logger.error("failed executing change state task",e);
                }
            }
        }

        return new ChangeStateResponse(new ArrayList<>(responseEntryMap.values()));
    }

    private ChangeStateResponseEntry changeDeviceStateAsync(ChangeStateRequest changeStateRequest, Device device) {
        String remoteId = device.getGateway().getRemoteId();
        String deviceId= device.getRemoteId();
        boolean success=false;
        ChangeState changeState=getChangeStateMessage(changeStateRequest.getValues(), device);
        try {
            basicIOTClient.sendMessage(changeState, remoteId);
            success=true;
        } catch (JsonProcessingException e) {
            logger.error("failed sending message to gateway "+ remoteId,e);
        }
        return new ChangeStateResponseEntry().setRemoteId(deviceId).setOnTry(1).setSuccess(success);
    }

    private ChangeStateResponseEntry changeDeviceState(ChangeStateRequest changeStateRequest, Device device) {
        long tries= changeStateRequest.getRetries()+1;
        ChangeStateReceived changeStateReceived=null;
        String remoteId = device.getGateway().getRemoteId();
        String deviceId= device.getRemoteId();
        int attempt = 0;
        boolean success=false;
        for (attempt = 0; attempt < tries; attempt++) {
            ChangeState changeState=getChangeStateMessage(changeStateRequest.getValues(), device);
            try {
                changeStateReceived=basicIOTClient.request(changeState, remoteId);
                if(changeStateReceived!=null){
                    break;
                }

            }
            catch (Throwable e){
                logger.error("failed sending message to gateway "+ remoteId,e);
            }

        }
        if(changeStateReceived!=null){
            success=true;
        }
        else{
            logger.error("did not receive response for device "+ device.getRemoteId()+"("+ device.getId()+") , after "+tries +" tries");
        }
        return new ChangeStateResponseEntry().setRemoteId(deviceId).setOnTry(attempt).setSuccess(success);
    }

    private ChangeState getChangeStateMessage(Map<String, Object> values, Device device) {
        return new ChangeState()
                .setDeviceId(device.getRemoteId())
                .setValues(new HashMap<>(values));
    }
}