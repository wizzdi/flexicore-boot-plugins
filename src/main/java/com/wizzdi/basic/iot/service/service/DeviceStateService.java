package com.wizzdi.basic.iot.service.service;


import com.flexicore.security.SecurityContextBase;
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

@Extension
@Component

public class DeviceStateService implements Plugin {

    private static final Logger logger= LoggerFactory.getLogger(DeviceStateService.class);

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
        for (Device device : devices) {
            long tries= changeStateRequest.getRetries()+1;
            StateChanged changeStateReceived=null;
            String remoteId = device.getGateway().getRemoteId();
            String deviceId=device.getRemoteId();
            int attempt = 0;
            for (attempt = 0; attempt < tries; attempt++) {
                ChangeState changeState=getChangeStateMessage(changeStateRequest.getValues(),device);
                try {
                    changeStateReceived=basicIOTClient.request(changeState, remoteId);
                    if(changeStateReceived!=null){
                       break;
                    }

                }
                catch (Exception e){
                    logger.error("failed sending message to gateway "+ remoteId,e);
                }

            }
            if(changeStateReceived!=null){
                responseEntryMap.put(deviceId,new ChangeStateResponseEntry().setRemoteId(deviceId).setOnTry(attempt));
            }
            else{
                logger.error("did not receive response for device "+device.getRemoteId()+"("+device.getId()+") , after "+tries +" tries");
            }

        }
        return new ChangeStateResponse(new ArrayList<>(responseEntryMap.values()));
    }

    private ChangeState getChangeStateMessage(Map<String, Object> values, Device device) {
        return new ChangeState()
                .setDeviceId(device.getRemoteId())
                .setValues(new HashMap<>(values));
    }
}