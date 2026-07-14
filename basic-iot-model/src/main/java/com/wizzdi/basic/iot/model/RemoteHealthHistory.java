package com.wizzdi.basic.iot.model;

import com.flexicore.model.Baseclass;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Index;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;

import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(indexes = {
        @Index(name = "remote_health_history_remote_interval_idx", columnList = "remote_id,validFrom,validUntil"),
        @Index(name = "remote_health_history_severity_idx", columnList = "severityValue,validFrom")
})
public class RemoteHealthHistory extends Baseclass {

    @ManyToOne(targetEntity = Remote.class)
    private Remote remote;
    @ManyToOne(targetEntity = RemoteHealthProfile.class)
    private RemoteHealthProfile remoteHealthProfile;
    @ManyToOne(targetEntity = RemoteHealthRule.class)
    private RemoteHealthRule matchedRule;
    private String severityName;
    private Integer severityValue;
    private boolean humanInterventionRequired;
    @Column(columnDefinition = "text")
    private String summary;
    private String mitigationStatus;
    @Column(columnDefinition = "text")
    private String mitigationInstructions;
    private Integer profileEvaluationVersion;
    @Column(columnDefinition = "timestamp with time zone")
    private OffsetDateTime validFrom;
    @Column(columnDefinition = "timestamp with time zone")
    private OffsetDateTime validUntil;
    @Transient
    private List<RemoteHealthSignalEvidence> signalEvidence = new ArrayList<>();

    public Remote getRemote() { return remote; }
    public <T extends RemoteHealthHistory> T setRemote(Remote remote) { this.remote = remote; return (T) this; }
    public RemoteHealthProfile getRemoteHealthProfile() { return remoteHealthProfile; }
    public <T extends RemoteHealthHistory> T setRemoteHealthProfile(RemoteHealthProfile remoteHealthProfile) { this.remoteHealthProfile = remoteHealthProfile; return (T) this; }
    public RemoteHealthRule getMatchedRule() { return matchedRule; }
    public <T extends RemoteHealthHistory> T setMatchedRule(RemoteHealthRule matchedRule) { this.matchedRule = matchedRule; return (T) this; }
    public String getSeverityName() { return severityName; }
    public <T extends RemoteHealthHistory> T setSeverityName(String severityName) { this.severityName = severityName; return (T) this; }
    public Integer getSeverityValue() { return severityValue; }
    public <T extends RemoteHealthHistory> T setSeverityValue(Integer severityValue) { this.severityValue = severityValue; return (T) this; }
    public boolean isHumanInterventionRequired() { return humanInterventionRequired; }
    public <T extends RemoteHealthHistory> T setHumanInterventionRequired(boolean humanInterventionRequired) { this.humanInterventionRequired = humanInterventionRequired; return (T) this; }
    public String getSummary() { return summary; }
    public <T extends RemoteHealthHistory> T setSummary(String summary) { this.summary = summary; return (T) this; }
    public String getMitigationStatus() { return mitigationStatus; }
    public <T extends RemoteHealthHistory> T setMitigationStatus(String mitigationStatus) { this.mitigationStatus = mitigationStatus; return (T) this; }
    public String getMitigationInstructions() { return mitigationInstructions; }
    public <T extends RemoteHealthHistory> T setMitigationInstructions(String mitigationInstructions) { this.mitigationInstructions = mitigationInstructions; return (T) this; }
    public Integer getProfileEvaluationVersion() { return profileEvaluationVersion; }
    public <T extends RemoteHealthHistory> T setProfileEvaluationVersion(Integer profileEvaluationVersion) { this.profileEvaluationVersion = profileEvaluationVersion; return (T) this; }
    public OffsetDateTime getValidFrom() { return validFrom; }
    public <T extends RemoteHealthHistory> T setValidFrom(OffsetDateTime validFrom) { this.validFrom = validFrom; return (T) this; }
    public OffsetDateTime getValidUntil() { return validUntil; }
    public <T extends RemoteHealthHistory> T setValidUntil(OffsetDateTime validUntil) { this.validUntil = validUntil; return (T) this; }
    public List<RemoteHealthSignalEvidence> getSignalEvidence() { return signalEvidence; }
    public <T extends RemoteHealthHistory> T setSignalEvidence(List<RemoteHealthSignalEvidence> signalEvidence) { this.signalEvidence = signalEvidence; return (T) this; }
}
