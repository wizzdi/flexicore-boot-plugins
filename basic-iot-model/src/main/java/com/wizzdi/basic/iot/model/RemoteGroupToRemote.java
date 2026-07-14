package com.wizzdi.basic.iot.model;

import com.flexicore.model.Baseclass;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Index;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

import java.time.OffsetDateTime;

@Entity
@Table(indexes = {
        @Index(name = "remote_group_to_remote_group_idx", columnList = "remoteGroup_id,softDelete"),
        @Index(name = "remote_group_to_remote_remote_idx", columnList = "remote_id,softDelete"),
        @Index(name = "remote_group_to_remote_source_idx", columnList = "membershipSource,softDelete"),
        @Index(name = "remote_group_to_remote_active_idx", columnList = "activeFrom,activeUntil")
})
public class RemoteGroupToRemote extends Baseclass {

    @ManyToOne(targetEntity = RemoteGroup.class)
    private RemoteGroup remoteGroup;

    @ManyToOne(targetEntity = Remote.class)
    private Remote remote;

    @ManyToOne(targetEntity = RemoteRoleDefinition.class)
    private RemoteRoleDefinition role;

    @Enumerated(EnumType.STRING)
    private RemoteGroupMembershipAction membershipAction = RemoteGroupMembershipAction.INCLUDE;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private RemoteGroupMembershipSource membershipSource = RemoteGroupMembershipSource.MANUAL;

    private boolean requiredMember;
    private Double weight = 1D;

    @Column(columnDefinition = "timestamp with time zone")
    private OffsetDateTime activeFrom;

    @Column(columnDefinition = "timestamp with time zone")
    private OffsetDateTime activeUntil;

    public RemoteGroup getRemoteGroup() {
        return remoteGroup;
    }

    public <T extends RemoteGroupToRemote> T setRemoteGroup(RemoteGroup remoteGroup) {
        this.remoteGroup = remoteGroup;
        return (T) this;
    }

    public Remote getRemote() {
        return remote;
    }

    public <T extends RemoteGroupToRemote> T setRemote(Remote remote) {
        this.remote = remote;
        return (T) this;
    }

    public RemoteRoleDefinition getRole() {
        return role;
    }

    public <T extends RemoteGroupToRemote> T setRole(RemoteRoleDefinition role) {
        this.role = role;
        return (T) this;
    }

    public RemoteGroupMembershipAction getMembershipAction() {
        return membershipAction;
    }

    public <T extends RemoteGroupToRemote> T setMembershipAction(RemoteGroupMembershipAction membershipAction) {
        this.membershipAction = membershipAction;
        return (T) this;
    }

    public RemoteGroupMembershipSource getMembershipSource() {
        return membershipSource;
    }

    public <T extends RemoteGroupToRemote> T setMembershipSource(RemoteGroupMembershipSource membershipSource) {
        this.membershipSource = membershipSource;
        return (T) this;
    }

    public boolean isRequiredMember() {
        return requiredMember;
    }

    public <T extends RemoteGroupToRemote> T setRequiredMember(boolean requiredMember) {
        this.requiredMember = requiredMember;
        return (T) this;
    }

    public Double getWeight() {
        return weight;
    }

    public <T extends RemoteGroupToRemote> T setWeight(Double weight) {
        this.weight = weight;
        return (T) this;
    }

    public OffsetDateTime getActiveFrom() {
        return activeFrom;
    }

    public <T extends RemoteGroupToRemote> T setActiveFrom(OffsetDateTime activeFrom) {
        this.activeFrom = activeFrom;
        return (T) this;
    }

    public OffsetDateTime getActiveUntil() {
        return activeUntil;
    }

    public <T extends RemoteGroupToRemote> T setActiveUntil(OffsetDateTime activeUntil) {
        this.activeUntil = activeUntil;
        return (T) this;
    }
}
