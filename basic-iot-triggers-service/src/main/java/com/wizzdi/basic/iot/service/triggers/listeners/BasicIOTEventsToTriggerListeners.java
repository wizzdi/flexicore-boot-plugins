package com.wizzdi.basic.iot.service.triggers.listeners;

import com.wizzdi.basic.iot.model.Remote;
import com.wizzdi.basic.iot.service.events.RemoteStatusChanged;
import com.wizzdi.basic.iot.service.events.RemoteUpdatedEvent;
import com.wizzdi.basic.iot.service.triggers.events.RemoteCreatedTrigger;
import com.wizzdi.basic.iot.service.triggers.events.RemoteStatusChangedTrigger;
import com.wizzdi.basic.iot.service.triggers.events.RemoteUpdatedTrigger;
import com.wizzdi.flexicore.boot.base.interfaces.Plugin;
import com.wizzdi.flexicore.security.events.BasicCreated;
import org.pf4j.Extension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@Extension
public class BasicIOTEventsToTriggerListeners implements Plugin {

    @Autowired
    private ApplicationEventPublisher eventPublisher;


    @EventListener
    public void remoteStatusChangedToTrigger(RemoteStatusChanged remoteStatusChanged){
        Remote remote = remoteStatusChanged.remote();
        eventPublisher.publishEvent(new RemoteStatusChangedTrigger(remote,remoteStatusChanged.newStatus(),remoteStatusChanged.currentStatus(), List.of(remote.getSecurity().getTenant())));
    }

    @EventListener
    public void remoteUpdatedToTrigger(RemoteUpdatedEvent remoteUpdatedEvent){
        Remote remote = remoteUpdatedEvent.getBaseclass();

        eventPublisher.publishEvent(new RemoteUpdatedTrigger(remote,remoteUpdatedEvent.getPreviousState(),List.of(remote.getSecurity().getTenant())));
    }

    @EventListener
    public <T extends Remote> void remoteCreatedToTrigger(BasicCreated<T> remoteUpdatedEvent){
        T remote = remoteUpdatedEvent.getBaseclass();
        eventPublisher.publishEvent(new RemoteCreatedTrigger(remote,List.of(remote.getSecurity().getTenant())));
    }
}
