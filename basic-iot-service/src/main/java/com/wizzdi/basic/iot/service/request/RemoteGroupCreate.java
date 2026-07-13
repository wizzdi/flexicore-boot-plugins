package com.wizzdi.basic.iot.service.request;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.wizzdi.basic.iot.model.FleetHealthPolicy;
import com.wizzdi.flexicore.security.request.BasicCreate;

public class RemoteGroupCreate extends BasicCreate {
    private String externalId;
    private String fleetHealthPolicyId;
    private Boolean healthEnabled;
    @JsonIgnore
    private FleetHealthPolicy fleetHealthPolicy;

    public String getExternalId() { return externalId; }
    public <T extends RemoteGroupCreate> T setExternalId(String externalId) { this.externalId = externalId; return (T) this; }
    public String getFleetHealthPolicyId() { return fleetHealthPolicyId; }
    public <T extends RemoteGroupCreate> T setFleetHealthPolicyId(String fleetHealthPolicyId) { this.fleetHealthPolicyId = fleetHealthPolicyId; return (T) this; }
    public Boolean getHealthEnabled() { return healthEnabled; }
    public <T extends RemoteGroupCreate> T setHealthEnabled(Boolean healthEnabled) { this.healthEnabled = healthEnabled; return (T) this; }
    public FleetHealthPolicy getFleetHealthPolicy() { return fleetHealthPolicy; }
    public <T extends RemoteGroupCreate> T setFleetHealthPolicy(FleetHealthPolicy fleetHealthPolicy) { this.fleetHealthPolicy = fleetHealthPolicy; return (T) this; }
}
