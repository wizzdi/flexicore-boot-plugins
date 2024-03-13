package com.flexicore.rules.events;


import com.flexicore.model.SecurityTenant;

import java.util.List;
import java.util.UUID;

public class ScenarioEventBase implements ScenarioEvent{

    private String id;
    private List<SecurityTenant> tenants;

    public ScenarioEventBase(String id, List<SecurityTenant> tenants) {
        this.id = id;
        this.tenants = tenants;
    }

    public ScenarioEventBase(List<SecurityTenant> tenants) {
        this(UUID.randomUUID().toString(), tenants);
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
    public List<SecurityTenant> getTenants() {
        return tenants;
    }

    public <T extends ScenarioEventBase> T setTenants(List<SecurityTenant> tenants) {
        this.tenants = tenants;
        return (T) this;
    }
}
