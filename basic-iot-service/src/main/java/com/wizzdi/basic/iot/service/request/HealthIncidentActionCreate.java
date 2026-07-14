package com.wizzdi.basic.iot.service.request;

import com.wizzdi.basic.iot.model.HealthIncidentActionType;
import com.wizzdi.flexicore.security.request.BasicCreate;

public class HealthIncidentActionCreate extends BasicCreate {
    private String healthIncidentId;
    private HealthIncidentActionType actionType;
    private String actionDescription;
    private String assignedUserId;

    public String getHealthIncidentId() { return healthIncidentId; }
    public <T extends HealthIncidentActionCreate> T setHealthIncidentId(String healthIncidentId) { this.healthIncidentId = healthIncidentId; return (T) this; }
    public HealthIncidentActionType getActionType() { return actionType; }
    public <T extends HealthIncidentActionCreate> T setActionType(HealthIncidentActionType actionType) { this.actionType = actionType; return (T) this; }
    public String getActionDescription() { return actionDescription; }
    public <T extends HealthIncidentActionCreate> T setActionDescription(String actionDescription) { this.actionDescription = actionDescription; return (T) this; }
    public String getAssignedUserId() { return assignedUserId; }
    public <T extends HealthIncidentActionCreate> T setAssignedUserId(String assignedUserId) { this.assignedUserId = assignedUserId; return (T) this; }
}
