package com.wizzdi.basic.iot.service.service;

import com.flexicore.security.SecurityContextBase;
import com.wizzdi.basic.iot.model.Device;
import com.wizzdi.basic.iot.model.DeviceType;
import com.wizzdi.basic.iot.model.Remote;
import com.wizzdi.basic.iot.service.request.StateHistoryCreate;
import com.wizzdi.flexicore.boot.base.interfaces.Plugin;
import com.wizzdi.flexicore.security.events.BasicCreated;
import com.wizzdi.flexicore.security.events.BasicUpdated;
import com.wizzdi.flexicore.security.interfaces.SecurityContextProvider;
import org.pf4j.Extension;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import java.time.OffsetDateTime;

@Component
@Extension
public class StateHistoryCreator implements Plugin {

    private static final Logger logger = LoggerFactory.getLogger(StateHistoryCreator.class);

    @Autowired
    private SecurityContextProvider securityContextProvider;

    @Autowired
    private StateHistoryService stateHistoryService;

    @EventListener
    public <T extends Remote> void onRemoteCreated(BasicCreated<T> basicCreated) {
        createStateHistory(basicCreated.getBaseclass());
    }

    @EventListener
    public <T extends Remote> void onRemoteUpdated(BasicUpdated<T> basicUpdated) {
        createStateHistory(basicUpdated.getBaseclass());
    }

    private void createStateHistory(Remote remote) {
        Device device = remote instanceof Device ? (Device) remote : null;
        if (device == null || device.getDeviceType() == null) return;
        DeviceType deviceType = device.getDeviceType();
        if (deviceType.getKeepStateHistory() == null || !deviceType.getKeepStateHistory()) {
            if (deviceType == null) {
                logger.debug("device type is null for device " + device.getName() + " with id " + device.getId());
            } else {
                logger.debug("not keeping state history for device type " + deviceType.getName() + " with id " + deviceType.getId());
            }
            return;
        }
        if (device.getKeepStateHistory() != null && !device.getKeepStateHistory()) {
            logger.debug("not keeping state history for device " + device.getName() + " with id " + device.getId());
            return;
        }

        SecurityContextBase securityContext = securityContextProvider.getSecurityContext(remote.getSecurity().getCreator());
        securityContext.setTenantToCreateIn(remote.getSecurity().getTenant());
        StateHistoryCreate stateHistoryCreate = new StateHistoryCreate()
                .setRemote(remote)
                .setTimeAtState(OffsetDateTime.now())
                .setDeviceProperties(remote.getDeviceProperties())
                .setUserAddedProperties(remote.getUserAddedProperties());
        stateHistoryService.createStateHistory(stateHistoryCreate, securityContext);
        logger.debug("created state history for device " + device.getName() + " with id " + device.getId());
    }
}
