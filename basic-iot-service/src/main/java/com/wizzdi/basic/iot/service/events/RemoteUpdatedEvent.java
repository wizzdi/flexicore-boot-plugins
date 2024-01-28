package com.wizzdi.basic.iot.service.events;

import com.wizzdi.basic.iot.model.Remote;
import com.wizzdi.basic.iot.service.request.RemoteCreate;
import com.wizzdi.flexicore.security.events.BasicUpdated;

public class RemoteUpdatedEvent extends BasicUpdated<Remote> {

    private final RemoteCreate previousState;

    public RemoteUpdatedEvent(Remote baseclass, RemoteCreate previousState) {
        super(baseclass);
        this.previousState = previousState;
    }

    public RemoteCreate getPreviousState() {
        return previousState;
    }

}
