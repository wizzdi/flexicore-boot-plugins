package com.flexicore.rules.events;


import com.flexicore.security.SecurityContextBase;

public class ScenarioEventBase implements ScenarioEvent{

    private String id;
    private SecurityContextBase securityContext;


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
