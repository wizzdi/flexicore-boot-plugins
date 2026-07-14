package com.wizzdi.basic.iot.service.request;

import com.wizzdi.basic.iot.model.HealthNotificationScopeType;
import com.wizzdi.flexicore.security.request.BaseclassCreate;

import java.util.List;

public class HealthNotificationPolicyCreate extends BaseclassCreate {
    private String userId;
    private HealthNotificationScopeType scopeType;
    private String remoteId;
    private String remoteGroupId;
    private String deviceTypeId;
    private Boolean enabled;
    private Integer minimumSeverityValue;
    private Boolean escalationOnly;
    private Boolean notifyOnRecovery;
    private Boolean notifyOnIncidentActions;
    private Boolean incidentOnly;
    private List<HealthNotificationChannelPreferenceCreate> channelPreferences;

    public String getUserId() { return userId; }
    public <T extends HealthNotificationPolicyCreate> T setUserId(String userId) { this.userId = userId; return (T) this; }
    public HealthNotificationScopeType getScopeType() { return scopeType; }
    public <T extends HealthNotificationPolicyCreate> T setScopeType(HealthNotificationScopeType scopeType) { this.scopeType = scopeType; return (T) this; }
    public String getRemoteId() { return remoteId; }
    public <T extends HealthNotificationPolicyCreate> T setRemoteId(String remoteId) { this.remoteId = remoteId; return (T) this; }
    public String getRemoteGroupId() { return remoteGroupId; }
    public <T extends HealthNotificationPolicyCreate> T setRemoteGroupId(String remoteGroupId) { this.remoteGroupId = remoteGroupId; return (T) this; }
    public String getDeviceTypeId() { return deviceTypeId; }
    public <T extends HealthNotificationPolicyCreate> T setDeviceTypeId(String deviceTypeId) { this.deviceTypeId = deviceTypeId; return (T) this; }
    public Boolean getEnabled() { return enabled; }
    public <T extends HealthNotificationPolicyCreate> T setEnabled(Boolean enabled) { this.enabled = enabled; return (T) this; }
    public Integer getMinimumSeverityValue() { return minimumSeverityValue; }
    public <T extends HealthNotificationPolicyCreate> T setMinimumSeverityValue(Integer minimumSeverityValue) { this.minimumSeverityValue = minimumSeverityValue; return (T) this; }
    public Boolean getEscalationOnly() { return escalationOnly; }
    public <T extends HealthNotificationPolicyCreate> T setEscalationOnly(Boolean escalationOnly) { this.escalationOnly = escalationOnly; return (T) this; }
    public Boolean getNotifyOnRecovery() { return notifyOnRecovery; }
    public <T extends HealthNotificationPolicyCreate> T setNotifyOnRecovery(Boolean notifyOnRecovery) { this.notifyOnRecovery = notifyOnRecovery; return (T) this; }
    public Boolean getNotifyOnIncidentActions() { return notifyOnIncidentActions; }
    public <T extends HealthNotificationPolicyCreate> T setNotifyOnIncidentActions(Boolean notifyOnIncidentActions) { this.notifyOnIncidentActions = notifyOnIncidentActions; return (T) this; }
    public Boolean getIncidentOnly() { return incidentOnly; }
    public <T extends HealthNotificationPolicyCreate> T setIncidentOnly(Boolean incidentOnly) { this.incidentOnly = incidentOnly; return (T) this; }
    public List<HealthNotificationChannelPreferenceCreate> getChannelPreferences() { return channelPreferences; }
    public <T extends HealthNotificationPolicyCreate> T setChannelPreferences(List<HealthNotificationChannelPreferenceCreate> channelPreferences) { this.channelPreferences = channelPreferences; return (T) this; }
}
