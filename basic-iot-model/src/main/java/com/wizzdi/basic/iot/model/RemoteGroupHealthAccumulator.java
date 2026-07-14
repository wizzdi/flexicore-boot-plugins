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
        @Index(name = "remote_group_health_accumulator_group_idx", columnList = "remoteGroup_id,softDelete"),
        @Index(name = "remote_group_health_accumulator_role_idx", columnList = "remoteGroup_id,role_id,requiredMembersOnly,softDelete")
})
public class RemoteGroupHealthAccumulator extends Baseclass {

    @ManyToOne(targetEntity = RemoteGroup.class)
    @JsonIgnore
    private RemoteGroup remoteGroup;

    @ManyToOne(targetEntity = RemoteRoleDefinition.class)
    private RemoteRoleDefinition role;

    private boolean requiredMembersOnly;
    private long totalMembers;
    private long knownSeverityMembers;
    private long offlineMembers;
    private long humanInterventionMembers;
    private double totalWeight;
    private double weightedSeveritySum;
    private long accumulatorVersion;

    @Column(columnDefinition = "timestamp with time zone")
    private OffsetDateTime reconciledAt;

    public RemoteGroup getRemoteGroup() { return remoteGroup; }
    public <T extends RemoteGroupHealthAccumulator> T setRemoteGroup(RemoteGroup remoteGroup) { this.remoteGroup = remoteGroup; return (T) this; }
    public RemoteRoleDefinition getRole() { return role; }
    public <T extends RemoteGroupHealthAccumulator> T setRole(RemoteRoleDefinition role) { this.role = role; return (T) this; }
    public boolean isRequiredMembersOnly() { return requiredMembersOnly; }
    public <T extends RemoteGroupHealthAccumulator> T setRequiredMembersOnly(boolean requiredMembersOnly) { this.requiredMembersOnly = requiredMembersOnly; return (T) this; }
    public long getTotalMembers() { return totalMembers; }
    public <T extends RemoteGroupHealthAccumulator> T setTotalMembers(long totalMembers) { this.totalMembers = totalMembers; return (T) this; }
    public long getKnownSeverityMembers() { return knownSeverityMembers; }
    public <T extends RemoteGroupHealthAccumulator> T setKnownSeverityMembers(long knownSeverityMembers) { this.knownSeverityMembers = knownSeverityMembers; return (T) this; }
    public long getOfflineMembers() { return offlineMembers; }
    public <T extends RemoteGroupHealthAccumulator> T setOfflineMembers(long offlineMembers) { this.offlineMembers = offlineMembers; return (T) this; }
    public long getHumanInterventionMembers() { return humanInterventionMembers; }
    public <T extends RemoteGroupHealthAccumulator> T setHumanInterventionMembers(long humanInterventionMembers) { this.humanInterventionMembers = humanInterventionMembers; return (T) this; }
    public double getTotalWeight() { return totalWeight; }
    public <T extends RemoteGroupHealthAccumulator> T setTotalWeight(double totalWeight) { this.totalWeight = totalWeight; return (T) this; }
    public double getWeightedSeveritySum() { return weightedSeveritySum; }
    public <T extends RemoteGroupHealthAccumulator> T setWeightedSeveritySum(double weightedSeveritySum) { this.weightedSeveritySum = weightedSeveritySum; return (T) this; }
    public long getAccumulatorVersion() { return accumulatorVersion; }
    public <T extends RemoteGroupHealthAccumulator> T setAccumulatorVersion(long accumulatorVersion) { this.accumulatorVersion = accumulatorVersion; return (T) this; }
    public OffsetDateTime getReconciledAt() { return reconciledAt; }
    public <T extends RemoteGroupHealthAccumulator> T setReconciledAt(OffsetDateTime reconciledAt) { this.reconciledAt = reconciledAt; return (T) this; }
}
