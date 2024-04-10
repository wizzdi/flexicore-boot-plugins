package com.wizzdi.basic.iot.service.events;

import com.wizzdi.basic.iot.model.Remote;
import com.wizzdi.basic.iot.service.request.RemoteCreate;
import com.wizzdi.flexicore.security.events.BasicUpdated;
import org.springframework.core.ResolvableType;

public class RemoteUpdatedEvent extends BasicUpdated<Remote> {

    private final RemoteCreate previousState;
    private final boolean stateUpdated;


    public RemoteUpdatedEvent(Remote baseclass, RemoteCreate previousState, boolean stateUpdated) {
        super(baseclass);
        this.previousState = previousState;
        this.stateUpdated = stateUpdated;
    }


    public RemoteCreate getPreviousState() {
        return previousState;
    }

    public boolean isStateUpdated() {
        return stateUpdated;
    }

    @Override
    public ResolvableType getResolvableType() {
        return  ResolvableType.forClass(RemoteUpdatedEvent.class);
    }

}
