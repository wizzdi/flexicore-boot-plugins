package com.wizzdi.basic.iot.service.triggers.events;

import com.flexicore.rules.events.ScenarioEventBase;
import com.flexicore.security.SecurityContextBase;
import com.wizzdi.basic.iot.model.Remote;
import com.wizzdi.basic.iot.service.request.RemoteCreate;

public class RemoteCreatedTrigger extends ScenarioEventBase {

    private final Remote remote;

    public RemoteCreatedTrigger(Remote remote, SecurityContextBase securityContextBase) {
        super(securityContextBase);
        this.remote = remote;
    }

    public Remote getRemote() {
        return remote;
    }

    @Override
    public String toString() {
        return "RemoteCreatedTrigger{" +
                "remote=" + remote +
                '}';
    }
}
