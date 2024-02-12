package com.wizzdi.basic.iot.service.events;

import com.flexicore.security.SecurityContextBase;
import com.wizzdi.basic.iot.model.Remote;
import com.wizzdi.basic.iot.service.request.RemoteCreate;
import com.wizzdi.flexicore.security.events.BasicUpdated;
import org.springframework.core.ResolvableType;

public class RemoteUpdatedEvent extends BasicUpdated<Remote> {

    private final RemoteCreate previousState;


    public RemoteUpdatedEvent(Remote baseclass, RemoteCreate previousState) {
        super(baseclass);
        this.previousState = previousState;
    }


    public RemoteCreate getPreviousState() {
        return previousState;
    }

    @Override
    public ResolvableType getResolvableType() {
        return  ResolvableType.forClass(RemoteUpdatedEvent.class);
    }

}
