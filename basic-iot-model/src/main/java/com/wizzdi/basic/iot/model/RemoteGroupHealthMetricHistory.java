package com.wizzdi.basic.iot.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.flexicore.model.Baseclass;
import jakarta.persistence.Entity;
import jakarta.persistence.Index;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(indexes = @Index(name = "remote_group_health_metric_history_idx", columnList = "remoteGroupHealthHistory_id,metricKey"))
public class RemoteGroupHealthMetricHistory extends Baseclass {

    @ManyToOne(targetEntity = RemoteGroupHealthHistory.class)
    @JsonIgnore
    private RemoteGroupHealthHistory remoteGroupHealthHistory;
    private String metricKey;
    private Double metricValue;
    private Double denominator;

    public RemoteGroupHealthHistory getRemoteGroupHealthHistory() { return remoteGroupHealthHistory; }
    public <T extends RemoteGroupHealthMetricHistory> T setRemoteGroupHealthHistory(RemoteGroupHealthHistory remoteGroupHealthHistory) { this.remoteGroupHealthHistory = remoteGroupHealthHistory; return (T) this; }
    public String getMetricKey() { return metricKey; }
    public <T extends RemoteGroupHealthMetricHistory> T setMetricKey(String metricKey) { this.metricKey = metricKey; return (T) this; }
    public Double getMetricValue() { return metricValue; }
    public <T extends RemoteGroupHealthMetricHistory> T setMetricValue(Double metricValue) { this.metricValue = metricValue; return (T) this; }
    public Double getDenominator() { return denominator; }
    public <T extends RemoteGroupHealthMetricHistory> T setDenominator(Double denominator) { this.denominator = denominator; return (T) this; }
}
