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

@Component
@Extension
public class BasicIOTEventsToTriggerListeners implements Plugin {

    @Autowired
    private ApplicationEventPublisher eventPublisher;

    @EventListener
    public void remoteStatusChangedToTrigger(RemoteStatusChanged remoteStatusChanged){
        eventPublisher.publishEvent(new RemoteStatusChangedTrigger(remoteStatusChanged.remote(),remoteStatusChanged.newStatus(),remoteStatusChanged.currentStatus()));
    }

    @EventListener
    public void remoteUpdatedToTrigger(RemoteUpdatedEvent remoteUpdatedEvent){
        eventPublisher.publishEvent(new RemoteUpdatedTrigger(remoteUpdatedEvent.getBaseclass(),remoteUpdatedEvent.getPreviousState()));
    }

    @EventListener
    public <T extends Remote> void remoteCreatedToTrigger(BasicCreated<T> remoteUpdatedEvent){
        eventPublisher.publishEvent(new RemoteCreatedTrigger(remoteUpdatedEvent.getBaseclass()));
    }
}
