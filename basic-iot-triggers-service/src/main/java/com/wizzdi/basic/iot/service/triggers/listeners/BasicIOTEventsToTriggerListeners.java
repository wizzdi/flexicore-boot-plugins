package com.wizzdi.basic.iot.service.triggers.listeners;

import com.flexicore.security.SecurityContextBase;
import com.wizzdi.basic.iot.model.Remote;
import com.wizzdi.basic.iot.service.events.RemoteStatusChanged;
import com.wizzdi.basic.iot.service.events.RemoteUpdatedEvent;
import com.wizzdi.basic.iot.service.triggers.events.RemoteCreatedTrigger;
import com.wizzdi.basic.iot.service.triggers.events.RemoteStatusChangedTrigger;
import com.wizzdi.basic.iot.service.triggers.events.RemoteUpdatedTrigger;
import com.wizzdi.flexicore.boot.base.interfaces.Plugin;
import com.wizzdi.flexicore.security.events.BasicCreated;
import com.wizzdi.flexicore.security.interfaces.SecurityContextProvider;
import org.pf4j.Extension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

@Component
@Extension
public class BasicIOTEventsToTriggerListeners implements Plugin {

    @Autowired
    private ApplicationEventPublisher eventPublisher;
    @Autowired
    private SecurityContextProvider securityContextProvider;

    @Async
    @EventListener
    public void remoteStatusChangedToTrigger(RemoteStatusChanged remoteStatusChanged){
        Remote remote = remoteStatusChanged.remote();
        SecurityContextBase securityContextBase=getSecurityContext(remote);
        eventPublisher.publishEvent(new RemoteStatusChangedTrigger(remoteStatusChanged.remote(),remoteStatusChanged.newStatus(),remoteStatusChanged.currentStatus(),securityContextBase));
    }

    private SecurityContextBase getSecurityContext(Remote remote) {
        SecurityContextBase securityContext = securityContextProvider.getSecurityContext(remote.getSecurity().getCreator());
        securityContext.setTenantToCreateIn(remote.getSecurity().getTenant());
        return securityContext;
    }

    @Async
    @EventListener
    public void remoteUpdatedToTrigger(RemoteUpdatedEvent remoteUpdatedEvent){
        Remote remote = remoteUpdatedEvent.getBaseclass();
        SecurityContextBase securityContextBase=getSecurityContext(remote);

        eventPublisher.publishEvent(new RemoteUpdatedTrigger(remote,remoteUpdatedEvent.getPreviousState(),securityContextBase));
    }

    @Async
    @EventListener
    public <T extends Remote> void remoteCreatedToTrigger(BasicCreated<T> remoteUpdatedEvent){
        T remote = remoteUpdatedEvent.getBaseclass();
        SecurityContextBase securityContextBase=getSecurityContext(remote);
        eventPublisher.publishEvent(new RemoteCreatedTrigger(remote,securityContextBase));
    }
}
