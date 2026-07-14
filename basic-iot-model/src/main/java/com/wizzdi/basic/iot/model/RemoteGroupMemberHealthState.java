package com.wizzdi.basic.iot.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.flexicore.model.Baseclass;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Index;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

import java.time.OffsetDateTime;

@Entity
@Table(indexes = {
        @Index(name = "remote_group_member_health_membership_idx", columnList = "remoteGroupToRemote_id,softDelete"),
        @Index(name = "remote_group_member_health_group_idx", columnList = "remoteGroup_id,active,softDelete"),
        @Index(name = "remote_group_member_health_remote_idx", columnList = "remote_id,active,softDelete"),
        @Index(name = "remote_group_member_health_stale_idx", columnList = "remoteGroup_id,lastSeen,active,softDelete")
})
public class RemoteGroupMemberHealthState extends Baseclass {

    @ManyToOne(targetEntity = RemoteGroup.class)
    @JsonIgnore
    private RemoteGroup remoteGroup;

    @ManyToOne(targetEntity = RemoteGroupToRemote.class)
    @JsonIgnore
    private RemoteGroupToRemote remoteGroupToRemote;

    @ManyToOne(targetEntity = Remote.class)
    private Remote remote;

    @ManyToOne(targetEntity = RemoteRoleDefinition.class)
    private RemoteRoleDefinition role;

    private boolean requiredMember;
    private boolean active;
    private double weight = 1D;
    private boolean offline;
    private Integer severityValue;
    private boolean humanInterventionRequired;

    @Column(columnDefinition = "timestamp with time zone")
    private OffsetDateTime lastSeen;

    @Column(columnDefinition = "timestamp with time zone")
    private OffsetDateTime capturedAt;

    public RemoteGroup getRemoteGroup() { return remoteGroup; }
    public <T extends RemoteGroupMemberHealthState> T setRemoteGroup(RemoteGroup remoteGroup) { this.remoteGroup = remoteGroup; return (T) this; }
    public RemoteGroupToRemote getRemoteGroupToRemote() { return remoteGroupToRemote; }
    public <T extends RemoteGroupMemberHealthState> T setRemoteGroupToRemote(RemoteGroupToRemote remoteGroupToRemote) { this.remoteGroupToRemote = remoteGroupToRemote; return (T) this; }
    public Remote getRemote() { return remote; }
    public <T extends RemoteGroupMemberHealthState> T setRemote(Remote remote) { this.remote = remote; return (T) this; }
    public RemoteRoleDefinition getRole() { return role; }
    public <T extends RemoteGroupMemberHealthState> T setRole(RemoteRoleDefinition role) { this.role = role; return (T) this; }
    public boolean isRequiredMember() { return requiredMember; }
    public <T extends RemoteGroupMemberHealthState> T setRequiredMember(boolean requiredMember) { this.requiredMember = requiredMember; return (T) this; }
    public boolean isActive() { return active; }
    public <T extends RemoteGroupMemberHealthState> T setActive(boolean active) { this.active = active; return (T) this; }
    public double getWeight() { return weight; }
    public <T extends RemoteGroupMemberHealthState> T setWeight(double weight) { this.weight = weight; return (T) this; }
    public boolean isOffline() { return offline; }
    public <T extends RemoteGroupMemberHealthState> T setOffline(boolean offline) { this.offline = offline; return (T) this; }
    public Integer getSeverityValue() { return severityValue; }
    public <T extends RemoteGroupMemberHealthState> T setSeverityValue(Integer severityValue) { this.severityValue = severityValue; return (T) this; }
    public boolean isHumanInterventionRequired() { return humanInterventionRequired; }
    public <T extends RemoteGroupMemberHealthState> T setHumanInterventionRequired(boolean humanInterventionRequired) { this.humanInterventionRequired = humanInterventionRequired; return (T) this; }
    public OffsetDateTime getLastSeen() { return lastSeen; }
    public <T extends RemoteGroupMemberHealthState> T setLastSeen(OffsetDateTime lastSeen) { this.lastSeen = lastSeen; return (T) this; }
    public OffsetDateTime getCapturedAt() { return capturedAt; }
    public <T extends RemoteGroupMemberHealthState> T setCapturedAt(OffsetDateTime capturedAt) { this.capturedAt = capturedAt; return (T) this; }
}
