package com.wizzdi.basic.iot.service.request;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.wizzdi.basic.iot.model.Remote;
import com.wizzdi.basic.iot.model.RemoteGroup;
import com.wizzdi.basic.iot.model.RemoteGroupMembershipAction;
import com.wizzdi.basic.iot.model.RemoteRoleDefinition;
import com.wizzdi.flexicore.security.request.BasicCreate;

import java.time.OffsetDateTime;

public class RemoteGroupToRemoteCreate extends BasicCreate {
    private String remoteGroupId;
    private String remoteId;
    private String roleId;
    private RemoteGroupMembershipAction membershipAction;
    private Boolean requiredMember;
    private Double weight;
    private OffsetDateTime activeFrom;
    private OffsetDateTime activeUntil;
    @JsonIgnore private RemoteGroup remoteGroup;
    @JsonIgnore private Remote remote;
    @JsonIgnore private RemoteRoleDefinition role;

    public String getRemoteGroupId() { return remoteGroupId; }
    public <T extends RemoteGroupToRemoteCreate> T setRemoteGroupId(String remoteGroupId) { this.remoteGroupId = remoteGroupId; return (T) this; }
    public String getRemoteId() { return remoteId; }
    public <T extends RemoteGroupToRemoteCreate> T setRemoteId(String remoteId) { this.remoteId = remoteId; return (T) this; }
    public String getRoleId() { return roleId; }
    public <T extends RemoteGroupToRemoteCreate> T setRoleId(String roleId) { this.roleId = roleId; return (T) this; }
    public RemoteGroupMembershipAction getMembershipAction() { return membershipAction; }
    public <T extends RemoteGroupToRemoteCreate> T setMembershipAction(RemoteGroupMembershipAction membershipAction) { this.membershipAction = membershipAction; return (T) this; }
    public Boolean getRequiredMember() { return requiredMember; }
    public <T extends RemoteGroupToRemoteCreate> T setRequiredMember(Boolean requiredMember) { this.requiredMember = requiredMember; return (T) this; }
    public Double getWeight() { return weight; }
    public <T extends RemoteGroupToRemoteCreate> T setWeight(Double weight) { this.weight = weight; return (T) this; }
    public OffsetDateTime getActiveFrom() { return activeFrom; }
    public <T extends RemoteGroupToRemoteCreate> T setActiveFrom(OffsetDateTime activeFrom) { this.activeFrom = activeFrom; return (T) this; }
    public OffsetDateTime getActiveUntil() { return activeUntil; }
    public <T extends RemoteGroupToRemoteCreate> T setActiveUntil(OffsetDateTime activeUntil) { this.activeUntil = activeUntil; return (T) this; }
    public RemoteGroup getRemoteGroup() { return remoteGroup; }
    public <T extends RemoteGroupToRemoteCreate> T setRemoteGroup(RemoteGroup remoteGroup) { this.remoteGroup = remoteGroup; return (T) this; }
    public Remote getRemote() { return remote; }
    public <T extends RemoteGroupToRemoteCreate> T setRemote(Remote remote) { this.remote = remote; return (T) this; }
    public RemoteRoleDefinition getRole() { return role; }
    public <T extends RemoteGroupToRemoteCreate> T setRole(RemoteRoleDefinition role) { this.role = role; return (T) this; }
}
