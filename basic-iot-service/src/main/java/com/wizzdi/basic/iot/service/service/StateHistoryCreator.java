package com.wizzdi.basic.iot.service.service;

import com.wizzdi.flexicore.security.configuration.SecurityContext;
import com.wizzdi.basic.iot.model.Device;
import com.wizzdi.basic.iot.model.DeviceType;
import com.wizzdi.basic.iot.model.Remote;
import com.wizzdi.basic.iot.service.events.RemoteUpdatedEvent;
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

    private static final Logger logger= LoggerFactory.getLogger(StateHistoryCreator.class);

    @Autowired
    private SecurityContextProvider securityContextProvider;

    @Autowired
    private StateHistoryService stateHistoryService;

    @EventListener
    public <T extends Remote> void onRemoteCreated(BasicCreated<T> basicCreated) {
        createStateHistory(basicCreated.getBaseclass());
    }

    @EventListener
    public <T extends Remote> void onRemoteUpdated(RemoteUpdatedEvent basicUpdated) {
        if(!basicUpdated.isStateUpdated()){
            logger.debug("remote was updated but there was no change in device state for device " + basicUpdated.getBaseclass().getName() + " with id " + basicUpdated.getBaseclass().getId());
            return;
        }
        createStateHistory(basicUpdated.getBaseclass());
    }

    private void createStateHistory(Remote remote) {

        if ( !remote.isKeepStateHistory()) {
            logger.debug("not keeping state history for device " + remote.getName() + " with id " + remote.getId());
            return;
        }

        SecurityContext securityContext = securityContextProvider.getSecurityContext(remote.getCreator());
        securityContext.setTenantToCreateIn(remote.getTenant());
        StateHistoryCreate stateHistoryCreate = new StateHistoryCreate()
                .setRemote(remote)
                .setTimeAtState(OffsetDateTime.now())
                .setDeviceProperties(remote.getDeviceProperties())
                .setUserAddedProperties(remote.getUserAddedProperties());
        stateHistoryService.createStateHistory(stateHistoryCreate, securityContext);
        logger.debug("created state history for device " + remote.getName() + " with id " + remote.getId());
    }
}
