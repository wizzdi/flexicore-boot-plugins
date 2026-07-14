package com.wizzdi.basic.iot.service.request;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.wizzdi.basic.iot.model.HealthNotificationPolicy;

public class HealthNotificationPolicyUpdate extends HealthNotificationPolicyCreate {
    private String id;
    @JsonIgnore
    private HealthNotificationPolicy healthNotificationPolicy;

    public String getId() { return id; }
    public <T extends HealthNotificationPolicyUpdate> T setId(String id) { this.id = id; return (T) this; }
    public HealthNotificationPolicy getHealthNotificationPolicy() { return healthNotificationPolicy; }
    public <T extends HealthNotificationPolicyUpdate> T setHealthNotificationPolicy(HealthNotificationPolicy healthNotificationPolicy) { this.healthNotificationPolicy = healthNotificationPolicy; return (T) this; }
}
