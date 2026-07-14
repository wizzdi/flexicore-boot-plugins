package com.wizzdi.basic.iot.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.flexicore.model.Baseclass;
import jakarta.persistence.Entity;
import jakarta.persistence.Index;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(indexes = @Index(name = "remote_group_severity_bucket_idx", columnList = "accumulator_id,severityValue,softDelete"))
public class RemoteGroupSeverityBucket extends Baseclass {

    @ManyToOne(targetEntity = RemoteGroupHealthAccumulator.class)
    @JsonIgnore
    private RemoteGroupHealthAccumulator accumulator;

    private Integer severityValue;
    private long memberCount;
    private double totalWeight;

    public RemoteGroupHealthAccumulator getAccumulator() { return accumulator; }
    public <T extends RemoteGroupSeverityBucket> T setAccumulator(RemoteGroupHealthAccumulator accumulator) { this.accumulator = accumulator; return (T) this; }
    public Integer getSeverityValue() { return severityValue; }
    public <T extends RemoteGroupSeverityBucket> T setSeverityValue(Integer severityValue) { this.severityValue = severityValue; return (T) this; }
    public long getMemberCount() { return memberCount; }
    public <T extends RemoteGroupSeverityBucket> T setMemberCount(long memberCount) { this.memberCount = memberCount; return (T) this; }
    public double getTotalWeight() { return totalWeight; }
    public <T extends RemoteGroupSeverityBucket> T setTotalWeight(double totalWeight) { this.totalWeight = totalWeight; return (T) this; }
}
