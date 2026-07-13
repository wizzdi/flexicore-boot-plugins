package com.wizzdi.basic.iot.service.request;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.wizzdi.basic.iot.model.FleetHealthPolicy;

public class FleetHealthPolicyUpdate extends FleetHealthPolicyCreate {
    private String id;
    @JsonIgnore
    private FleetHealthPolicy fleetHealthPolicy;

    public String getId() { return id; }
    public <T extends FleetHealthPolicyUpdate> T setId(String id) { this.id = id; return (T) this; }
    public FleetHealthPolicy getFleetHealthPolicy() { return fleetHealthPolicy; }
    public <T extends FleetHealthPolicyUpdate> T setFleetHealthPolicy(FleetHealthPolicy fleetHealthPolicy) { this.fleetHealthPolicy = fleetHealthPolicy; return (T) this; }
}
