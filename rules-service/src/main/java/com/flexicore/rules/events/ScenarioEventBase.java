package com.flexicore.rules.events;


import com.flexicore.security.SecurityContextBase;

import java.util.UUID;

public class ScenarioEventBase implements ScenarioEvent{

    private String id;
    private SecurityContextBase securityContext;

    public ScenarioEventBase(String id, SecurityContextBase securityContext) {
        this.id = id;
        this.securityContext = securityContext;
    }

    public ScenarioEventBase(SecurityContextBase securityContext) {
        this(UUID.randomUUID().toString(),securityContext);
    }

    @Override
    public String getId() {
        return id;
    }

    public <T extends ScenarioEventBase> T setId(String id) {
        this.id = id;
        return (T) this;
    }

    @Override
    public SecurityContextBase getSecurityContext() {
        return securityContext;
    }

    public <T extends ScenarioEventBase> T setSecurityContext(SecurityContextBase securityContext) {
        this.securityContext = securityContext;
        return (T) this;
    }
}
