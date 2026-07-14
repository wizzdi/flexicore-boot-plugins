package com.wizzdi.basic.iot.service.service;

import com.flexicore.model.Baseclass;
import com.wizzdi.basic.iot.model.FleetHealthPolicy;
import com.wizzdi.basic.iot.model.FleetHealthRule;
import com.wizzdi.basic.iot.model.HealthSignalDefinition;
import com.wizzdi.basic.iot.model.Remote;
import com.wizzdi.basic.iot.model.RemoteGroup;
import com.wizzdi.basic.iot.model.RemoteGroupHealthHistory;
import com.wizzdi.basic.iot.model.RemoteGroupHealthMetricHistory;
import com.wizzdi.basic.iot.model.RemoteHealthHistory;
import com.wizzdi.basic.iot.model.RemoteHealthSignalEvidence;
import com.wizzdi.basic.iot.model.RemoteHealthProfile;
import com.wizzdi.basic.iot.model.RemoteHealthRule;
import com.wizzdi.basic.iot.service.data.HealthHistoryRepository;
import com.wizzdi.basic.iot.service.request.RemoteGroupHealthHistoryFilter;
import com.wizzdi.basic.iot.service.request.RemoteHealthHistoryFilter;
import com.wizzdi.flexicore.boot.base.interfaces.Plugin;
import com.wizzdi.flexicore.security.configuration.SecurityContext;
import com.wizzdi.flexicore.security.response.PaginationResponse;
import com.wizzdi.flexicore.security.service.BasicService;
import org.pf4j.Extension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;

import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;
import java.util.stream.Collectors;

@Extension
@Component
public class HealthHistoryService implements Plugin {

    @Autowired
    private HealthHistoryRepository repository;
    @Autowired
    private BasicService basicService;
    @Autowired
    private DerivedEntitySecurityService derivedEntitySecurityService;
    @PersistenceContext
    private EntityManager em;

    public void validate(RemoteHealthHistoryFilter filter, SecurityContext securityContext) {
        basicService.validate(filter, securityContext);
    }

    public void validate(RemoteGroupHealthHistoryFilter filter, SecurityContext securityContext) {
        basicService.validate(filter, securityContext);
    }

    public PaginationResponse<RemoteHealthHistory> getAllRemoteHealthHistory(
            SecurityContext securityContext,
            RemoteHealthHistoryFilter filter) {
        List<RemoteHealthHistory> list = repository.listRemoteHistory(securityContext, filter);
        Map<String, List<RemoteHealthSignalEvidence>> evidenceByHistory = repository.listSignalEvidence(
                        list.stream().map(Baseclass::getId).toList()).stream()
                .collect(Collectors.groupingBy(evidence -> evidence.getRemoteHealthHistory().getId()));
        list.forEach(history -> history.setSignalEvidence(
                evidenceByHistory.getOrDefault(history.getId(), List.of())));
        return new PaginationResponse<>(list, filter, repository.countRemoteHistory(securityContext, filter));
    }

    public PaginationResponse<RemoteGroupHealthHistory> getAllRemoteGroupHealthHistory(
            SecurityContext securityContext,
            RemoteGroupHealthHistoryFilter filter) {
        List<RemoteGroupHealthHistory> list = repository.listGroupHistory(securityContext, filter);
        Map<String, List<RemoteGroupHealthMetricHistory>> metricsByHistory = repository.listMetrics(
                        list.stream().map(Baseclass::getId).toList()).stream()
                .collect(Collectors.groupingBy(metric -> metric.getRemoteGroupHealthHistory().getId()));
        list.forEach(history -> history.setMetrics(metricsByHistory.getOrDefault(history.getId(), List.of())));
        return new PaginationResponse<>(list, filter, repository.countGroupHistory(securityContext, filter));
    }

    @Transactional
    public void recordRemoteTransition(Remote remote,
                                       RemoteHealthProfile profile,
                                       RemoteHealthRule matchedRule,
                                       Map<String, Object> signalEvidence,
                                       OffsetDateTime now) {
        if (remote == null || now == null) {
            return;
        }
        List<Object> toMerge = new ArrayList<>();
        RemoteHealthHistory open = repository.findOpenRemoteHistory(remote.getId());
        if (open != null) {
            if (sameRemoteOutcome(open, remote, profile, matchedRule)) {
                return;
            }
            open.setValidUntil(now);
            toMerge.add(open);
        }

        RemoteHealthHistory history = new RemoteHealthHistory();
        history.setId(UUID.randomUUID().toString());
        history.setName("remote-health-" + remote.getId() + "-" + now.toInstant().toEpochMilli());
        history.setRemote(remote);
        derivedEntitySecurityService.inheritFromRemote(history, remote);
        history.setRemoteHealthProfile(profile);
        history.setMatchedRule(matchedRule);
        history.setSeverityName(remote.getCurrentSeverityName());
        history.setSeverityValue(remote.getCurrentSeverityValue());
        history.setHumanInterventionRequired(remote.isHumanInterventionRequired());
        history.setSummary(remote.getHealthSummary());
        history.setMitigationStatus(remote.getMitigationStatus());
        history.setMitigationInstructions(remote.getMitigationInstructions());
        history.setProfileEvaluationVersion(profile == null ? null : profile.getEvaluationVersion());
        history.setValidFrom(now);
        toMerge.add(history);
        if (signalEvidence != null && !signalEvidence.isEmpty()) {
            int priority = 0;
            for (Map.Entry<String, Object> entry : signalEvidence.entrySet()) {
                HealthSignalDefinition signal = em.find(HealthSignalDefinition.class, entry.getKey());
                if (signal == null || signal.isSoftDelete()) {
                    continue;
                }
                RemoteHealthSignalEvidence evidence = createSignalEvidence(
                        history, remote, signal, entry.getValue(), priority++);
                if (evidence != null) {
                    toMerge.add(evidence);
                }
            }
        }
        repository.massMerge(toMerge);
    }

    @Transactional
    public void recordGroupTransition(RemoteGroup group,
                                      FleetHealthPolicy policy,
                                      FleetHealthRule matchedRule,
                                      int population,
                                      int unknown,
                                      int offline,
                                      int intervention,
                                      double weightedSeverityAverage,
                                      Map<String, Double> metrics,
                                      OffsetDateTime now) {
        if (group == null || now == null) {
            return;
        }
        List<Object> toMerge = new ArrayList<>();
        RemoteGroupHealthHistory open = repository.findOpenGroupHistory(group.getId());
        if (open != null) {
            if (sameGroupOutcome(open, group, policy, matchedRule)) {
                return;
            }
            open.setValidUntil(now);
            toMerge.add(open);
        }

        RemoteGroupHealthHistory history = new RemoteGroupHealthHistory();
        history.setId(UUID.randomUUID().toString());
        history.setName("remote-group-health-" + group.getId() + "-" + now.toInstant().toEpochMilli());
        history.setRemoteGroup(group);
        derivedEntitySecurityService.inheritFromGroup(history, group);
        history.setFleetHealthPolicy(policy);
        history.setMatchedRule(matchedRule);
        history.setSeverityName(group.getCurrentSeverityName());
        history.setSeverityValue(group.getCurrentSeverityValue());
        history.setHumanInterventionRequired(group.isHumanInterventionRequired());
        history.setPopulationCount(population);
        history.setUnknownCount(unknown);
        history.setOfflineCount(offline);
        history.setHumanInterventionCount(intervention);
        history.setWeightedSeverityAverage(weightedSeverityAverage);
        history.setPolicyEvaluationVersion(policy == null ? null : policy.getEvaluationVersion());
        history.setHealthInputVersion(group.getHealthInputVersion());
        history.setValidFrom(now);
        toMerge.add(history);

        if (metrics != null) {
            for (Map.Entry<String, Double> entry : metrics.entrySet()) {
                RemoteGroupHealthMetricHistory metric = new RemoteGroupHealthMetricHistory();
                metric.setId(UUID.randomUUID().toString());
                metric.setName(entry.getKey());
                metric.setRemoteGroupHealthHistory(history);
                derivedEntitySecurityService.inheritFromGroup(metric, group);
                metric.setMetricKey(entry.getKey());
                metric.setMetricValue(entry.getValue());
                toMerge.add(metric);
            }
        }
        repository.massMerge(toMerge);
    }

    private RemoteHealthSignalEvidence createSignalEvidence(RemoteHealthHistory history,
                                                            Remote remote,
                                                            HealthSignalDefinition signal,
                                                            Object value,
                                                            int priority) {
        if (value == null || signal.getValueType() == null) {
            return null;
        }
        RemoteHealthSignalEvidence evidence = new RemoteHealthSignalEvidence();
        evidence.setId(UUID.randomUUID().toString());
        evidence.setName(signal.getExternalId() == null ? signal.getName() : signal.getExternalId());
        evidence.setRemoteHealthHistory(history);
        evidence.setHealthSignalDefinition(signal);
        evidence.setValueType(signal.getValueType());
        evidence.setPriority(priority);
        derivedEntitySecurityService.inheritFromRemote(evidence, remote);
        switch (signal.getValueType()) {
            case NUMBER -> {
                if (!(value instanceof Number number)) return null;
                evidence.setNumericValue(number.doubleValue());
            }
            case BOOLEAN -> {
                if (!(value instanceof Boolean booleanValue)) return null;
                evidence.setBooleanValue(booleanValue);
            }
            case TIMESTAMP -> {
                if (value instanceof OffsetDateTime timestamp) {
                    evidence.setTimestampValue(timestamp);
                } else {
                    try {
                        evidence.setTimestampValue(OffsetDateTime.parse(String.valueOf(value)));
                    } catch (RuntimeException ignored) {
                        return null;
                    }
                }
            }
            case STRING, ENUM -> evidence.setStringValue(String.valueOf(value));
        }
        return evidence;
    }

    private boolean sameRemoteOutcome(RemoteHealthHistory history,
                                      Remote remote,
                                      RemoteHealthProfile profile,
                                      RemoteHealthRule matchedRule) {
        return Objects.equals(history.getSeverityName(), remote.getCurrentSeverityName())
                && Objects.equals(history.getSeverityValue(), remote.getCurrentSeverityValue())
                && history.isHumanInterventionRequired() == remote.isHumanInterventionRequired()
                && Objects.equals(id(history.getRemoteHealthProfile()), id(profile))
                && Objects.equals(id(history.getMatchedRule()), id(matchedRule))
                && Objects.equals(history.getSummary(), remote.getHealthSummary())
                && Objects.equals(history.getMitigationStatus(), remote.getMitigationStatus())
                && Objects.equals(history.getMitigationInstructions(), remote.getMitigationInstructions());
    }

    private boolean sameGroupOutcome(RemoteGroupHealthHistory history,
                                     RemoteGroup group,
                                     FleetHealthPolicy policy,
                                     FleetHealthRule matchedRule) {
        return Objects.equals(history.getSeverityName(), group.getCurrentSeverityName())
                && Objects.equals(history.getSeverityValue(), group.getCurrentSeverityValue())
                && history.isHumanInterventionRequired() == group.isHumanInterventionRequired()
                && Objects.equals(id(history.getFleetHealthPolicy()), id(policy))
                && Objects.equals(id(history.getMatchedRule()), id(matchedRule));
    }

    private String id(Baseclass baseclass) {
        return baseclass == null ? null : baseclass.getId();
    }
}
