package com.wizzdi.basic.iot.model;

import com.flexicore.model.Baseclass;
import com.flexicore.model.SecurityUser;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Index;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(indexes = {
        @Index(name = "health_notification_policy_user_idx", columnList = "user_id,enabled,softdelete"),
        @Index(name = "health_notification_policy_scope_idx", columnList = "scopeType,remote_id,remoteGroup_id,deviceType_id")
})
public class HealthNotificationPolicy extends Baseclass {
    @ManyToOne(targetEntity = SecurityUser.class)
    private SecurityUser user;
    @Enumerated(EnumType.STRING)
    private HealthNotificationScopeType scopeType = HealthNotificationScopeType.TENANT;
    @ManyToOne(targetEntity = Remote.class)
    private Remote remote;
    @ManyToOne(targetEntity = RemoteGroup.class)
    private RemoteGroup remoteGroup;
    @ManyToOne(targetEntity = DeviceType.class)
    private DeviceType deviceType;
    private boolean enabled = true;
    private Integer minimumSeverityValue = 60;
    private boolean escalationOnly = true;
    private boolean notifyOnRecovery = true;
    private boolean notifyOnIncidentActions;
    private boolean incidentOnly = true;
    @Transient
    private List<HealthNotificationChannelPreference> channelPreferences = new ArrayList<>();

    public SecurityUser getUser() { return user; }
    public <T extends HealthNotificationPolicy> T setUser(SecurityUser user) { this.user = user; return (T) this; }
    public HealthNotificationScopeType getScopeType() { return scopeType; }
    public <T extends HealthNotificationPolicy> T setScopeType(HealthNotificationScopeType scopeType) { this.scopeType = scopeType; return (T) this; }
    public Remote getRemote() { return remote; }
    public <T extends HealthNotificationPolicy> T setRemote(Remote remote) { this.remote = remote; return (T) this; }
    public RemoteGroup getRemoteGroup() { return remoteGroup; }
    public <T extends HealthNotificationPolicy> T setRemoteGroup(RemoteGroup remoteGroup) { this.remoteGroup = remoteGroup; return (T) this; }
    public DeviceType getDeviceType() { return deviceType; }
    public <T extends HealthNotificationPolicy> T setDeviceType(DeviceType deviceType) { this.deviceType = deviceType; return (T) this; }
    public boolean isEnabled() { return enabled; }
    public <T extends HealthNotificationPolicy> T setEnabled(boolean enabled) { this.enabled = enabled; return (T) this; }
    public Integer getMinimumSeverityValue() { return minimumSeverityValue; }
    public <T extends HealthNotificationPolicy> T setMinimumSeverityValue(Integer minimumSeverityValue) { this.minimumSeverityValue = minimumSeverityValue; return (T) this; }
    public boolean isEscalationOnly() { return escalationOnly; }
    public <T extends HealthNotificationPolicy> T setEscalationOnly(boolean escalationOnly) { this.escalationOnly = escalationOnly; return (T) this; }
    public boolean isNotifyOnRecovery() { return notifyOnRecovery; }
    public <T extends HealthNotificationPolicy> T setNotifyOnRecovery(boolean notifyOnRecovery) { this.notifyOnRecovery = notifyOnRecovery; return (T) this; }
    public boolean isNotifyOnIncidentActions() { return notifyOnIncidentActions; }
    public <T extends HealthNotificationPolicy> T setNotifyOnIncidentActions(boolean notifyOnIncidentActions) { this.notifyOnIncidentActions = notifyOnIncidentActions; return (T) this; }
    public boolean isIncidentOnly() { return incidentOnly; }
    public <T extends HealthNotificationPolicy> T setIncidentOnly(boolean incidentOnly) { this.incidentOnly = incidentOnly; return (T) this; }
    public List<HealthNotificationChannelPreference> getChannelPreferences() { return channelPreferences; }
    public <T extends HealthNotificationPolicy> T setChannelPreferences(List<HealthNotificationChannelPreference> channelPreferences) { this.channelPreferences = channelPreferences; return (T) this; }
}
