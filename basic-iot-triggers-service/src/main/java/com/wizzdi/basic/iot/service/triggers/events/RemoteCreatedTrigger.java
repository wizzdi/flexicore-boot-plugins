package com.wizzdi.basic.iot.service.triggers.events;

import com.flexicore.model.SecurityTenant;
import com.flexicore.rules.events.ScenarioEventBase;
import com.wizzdi.flexicore.security.configuration.SecurityContext;
import com.wizzdi.basic.iot.model.Remote;
import com.wizzdi.basic.iot.service.request.RemoteCreate;

import java.util.List;

public class RemoteCreatedTrigger extends ScenarioEventBase {

    private final Remote remote;

    public RemoteCreatedTrigger(Remote remote, List<SecurityTenant> tenants) {
        super(tenants);
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
