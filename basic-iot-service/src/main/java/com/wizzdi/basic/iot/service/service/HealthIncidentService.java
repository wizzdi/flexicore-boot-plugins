package com.wizzdi.basic.iot.service.service;

import com.flexicore.model.Baseclass;
import com.flexicore.model.SecurityUser;
import com.wizzdi.basic.iot.model.FleetHealthPolicy;
import com.wizzdi.basic.iot.model.HealthIncident;
import com.wizzdi.basic.iot.model.HealthIncidentAction;
import com.wizzdi.basic.iot.model.HealthIncidentActionType;
import com.wizzdi.basic.iot.model.HealthIncidentStatus;
import com.wizzdi.basic.iot.model.HealthNotificationEventType;
import com.wizzdi.basic.iot.model.Remote;
import com.wizzdi.basic.iot.model.RemoteGroup;
import com.wizzdi.basic.iot.model.RemoteHealthProfile;
import com.wizzdi.basic.iot.service.data.HealthIncidentRepository;
import com.wizzdi.basic.iot.service.request.HealthIncidentActionCreate;
import com.wizzdi.basic.iot.service.request.HealthIncidentActionFilter;
import com.wizzdi.basic.iot.service.request.HealthIncidentFilter;
import com.wizzdi.flexicore.boot.base.interfaces.Plugin;
import com.wizzdi.flexicore.security.configuration.SecurityContext;
import com.wizzdi.flexicore.security.response.PaginationResponse;
import com.wizzdi.flexicore.security.service.BaseclassService;
import com.wizzdi.flexicore.security.service.BasicService;
import org.pf4j.Extension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

@Extension
@Component
public class HealthIncidentService implements Plugin {
    @Autowired
    private HealthIncidentRepository repository;
    @Autowired
    private HealthNotificationService notificationService;
    @Autowired
    private BasicService basicService;
    @Autowired
    private DerivedEntitySecurityService derivedEntitySecurityService;
    @Value("${basic.iot.health.actionRequiredFromSeverityValue:60}")
    private int defaultActionRequiredFromSeverityValue;

    public void validateFiltering(HealthIncidentFilter filter, SecurityContext securityContext) {
        basicService.validate(filter, securityContext);
    }

    public void validateFiltering(HealthIncidentActionFilter filter, SecurityContext securityContext) {
        basicService.validate(filter, securityContext);
    }

    public PaginationResponse<HealthIncident> getAll(SecurityContext securityContext,
                                                       HealthIncidentFilter filter) {
        List<HealthIncident> incidents = repository.list(securityContext, filter);
        return new PaginationResponse<>(incidents, filter, repository.count(securityContext, filter));
    }

    public PaginationResponse<HealthIncidentAction> getAllActions(SecurityContext securityContext,
                                                                   HealthIncidentActionFilter filter) {
        List<HealthIncidentAction> actions = repository.listActions(securityContext, filter);
        return new PaginationResponse<>(actions, filter, repository.countActions(securityContext, filter));
    }

    @Transactional
    public HealthIncident handleRemoteTransition(Remote remote,
                                                  RemoteHealthProfile profile,
                                                  String previousSeverityName,
                                                  Integer previousSeverityValue,
                                                  String severityName,
                                                  Integer severityValue,
                                                  String matchedRuleId,
                                                  String summary,
                                                  String mitigationInstructions,
                                                  OffsetDateTime occurredAt) {
        int threshold = profile != null && profile.getActionRequiredFromSeverityValue() != null
                ? profile.getActionRequiredFromSeverityValue()
                : defaultActionRequiredFromSeverityValue;
        return handleTransition(remote, null, threshold,
                previousSeverityName, previousSeverityValue,
                severityName, severityValue, matchedRuleId,
                summary, mitigationInstructions, occurredAt);
    }

    @Transactional
    public HealthIncident handleGroupTransition(RemoteGroup group,
                                                 FleetHealthPolicy policy,
                                                 String previousSeverityName,
                                                 Integer previousSeverityValue,
                                                 String severityName,
                                                 Integer severityValue,
                                                 String matchedRuleId,
                                                 String summary,
                                                 OffsetDateTime occurredAt) {
        int threshold = policy != null && policy.getActionRequiredFromSeverityValue() != null
                ? policy.getActionRequiredFromSeverityValue()
                : defaultActionRequiredFromSeverityValue;
        return handleTransition(null, group, threshold,
                previousSeverityName, previousSeverityValue,
                severityName, severityValue, matchedRuleId,
                summary, null, occurredAt);
    }

    private HealthIncident handleTransition(Remote remote,
                                             RemoteGroup group,
                                             int threshold,
                                             String previousSeverityName,
                                             Integer previousSeverityValue,
                                             String severityName,
                                             Integer severityValue,
                                             String matchedRuleId,
                                             String summary,
                                             String mitigationInstructions,
                                             OffsetDateTime occurredAt) {
        OffsetDateTime now = occurredAt == null ? OffsetDateTime.now() : occurredAt;
        int currentValue = value(severityValue);
        HealthIncident incident = remote != null
                ? repository.findActiveForRemote(remote.getId())
                : repository.findActiveForGroup(group.getId());
        HealthNotificationEventType eventType;
        boolean changedIncident = false;
        List<Object> toMerge = new ArrayList<>();

        if (currentValue >= threshold) {
            if (incident == null) {
                incident = new HealthIncident();
                incident.setId(UUID.randomUUID().toString());
                incident.setSecurityId(incident.getId());
                incident.setName("health-incident-" + (remote != null ? remote.getId() : group.getId())
                        + "-" + now.toInstant().toEpochMilli());
                incident.setRemote(remote);
                incident.setRemoteGroup(group);
                incident.setStatus(HealthIncidentStatus.OPEN);
                incident.setActionRequired(true);
                incident.setOpenedAt(now);
                if (remote != null) derivedEntitySecurityService.inheritFromRemote(incident, remote);
                else derivedEntitySecurityService.inheritFromGroup(incident, group);
                toMerge.add(createSystemAction(incident, HealthIncidentActionType.OPENED,
                        HealthIncidentStatus.OPEN, "Incident opened automatically", now));
                eventType = HealthNotificationEventType.INCIDENT_OPENED;
            } else {
                eventType = value(severityValue) > value(incident.getSeverityValue())
                        ? HealthNotificationEventType.INCIDENT_ESCALATED
                        : HealthNotificationEventType.INCIDENT_UPDATED;
            }
            incident.setSeverityName(severityName);
            incident.setSeverityValue(severityValue);
            incident.setMatchedRuleId(matchedRuleId);
            incident.setSummary(summary);
            incident.setMitigationInstructions(mitigationInstructions);
            incident.setLastHealthEventAt(now);
            incident.setHealthRecovered(false);
            incident.setHealthRecoveredAt(null);
            changedIncident = true;
        } else if (incident != null) {
            incident.setHealthRecovered(true);
            incident.setHealthRecoveredAt(now);
            incident.setLastHealthEventAt(now);
            incident.setSeverityName(severityName);
            incident.setSeverityValue(severityValue);
            incident.setMatchedRuleId(matchedRuleId);
            incident.setSummary(summary);
            incident.setMitigationInstructions(mitigationInstructions);
            eventType = HealthNotificationEventType.HEALTH_RECOVERED;
            changedIncident = true;
        } else {
            eventType = value(previousSeverityValue) > currentValue
                    ? HealthNotificationEventType.HEALTH_RECOVERED
                    : HealthNotificationEventType.HEALTH_CHANGED;
        }

        if (changedIncident) {
            toMerge.add(incident);
            repository.massMerge(toMerge);
        }
        String subjectName = remote != null ? remote.getName() : group.getName();
        String title = title(eventType, subjectName, severityName);
        String message = message(summary, severityName, severityValue, subjectName);
        notificationService.recordEvent(incident, remote, group, eventType,
                previousSeverityName, previousSeverityValue,
                severityName, severityValue, matchedRuleId,
                title, message, now);
        return incident;
    }

    private HealthIncidentAction createSystemAction(HealthIncident incident,
                                                     HealthIncidentActionType type,
                                                     HealthIncidentStatus status,
                                                     String description,
                                                     OffsetDateTime now) {
        HealthIncidentAction action = new HealthIncidentAction();
        action.setId(UUID.randomUUID().toString());
        action.setSecurityId(action.getId());
        action.setName(type.name());
        action.setHealthIncident(incident);
        action.setActionType(type);
        action.setPreviousStatus(null);
        action.setResultingStatus(status);
        action.setPerformedAt(now);
        action.setActionDescription(description);
        action.setTenant(incident.getTenant());
        action.setCreator(incident.getCreator());
        return action;
    }

    @Transactional
    public HealthIncidentAction addAction(HealthIncidentActionCreate create,
                                          SecurityContext securityContext) {
        if (create.getHealthIncidentId() == null || create.getHealthIncidentId().isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "healthIncidentId is required");
        }
        if (create.getActionType() == null || create.getActionType() == HealthIncidentActionType.OPENED) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "A user actionType is required");
        }
        HealthIncident incident = repository.getByIdOrNull(
                create.getHealthIncidentId(), HealthIncident.class, securityContext);
        if (incident == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "No accessible HealthIncident with id " + create.getHealthIncidentId());
        }
        SecurityUser assigned = null;
        if (create.getAssignedUserId() != null && !create.getAssignedUserId().isBlank()) {
            assigned = repository.getByIdOrNull(create.getAssignedUserId(), SecurityUser.class, securityContext);
            if (assigned == null) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                        "No accessible user with id " + create.getAssignedUserId());
            }
        }
        OffsetDateTime now = OffsetDateTime.now();
        HealthIncidentStatus previousStatus = incident.getStatus();
        HealthIncidentStatus resultingStatus = applyAction(incident, create.getActionType(), securityContext.getUser(), now);
        if (assigned != null) incident.setAssignedTo(assigned);
        incident.setLatestActionSummary(create.getActionDescription());

        HealthIncidentAction action = new HealthIncidentAction();
        action.setId(UUID.randomUUID().toString());
        action.setName(create.getName() == null ? create.getActionType().name() : create.getName());
        action.setHealthIncident(incident);
        action.setPerformedBy(securityContext.getUser());
        action.setActionType(create.getActionType());
        action.setPreviousStatus(previousStatus);
        action.setResultingStatus(resultingStatus);
        action.setPerformedAt(now);
        action.setActionDescription(create.getActionDescription());
        Baseclass source = incident.getRemote() != null ? incident.getRemote() : incident.getRemoteGroup();
        BaseclassService.createSecurityObjectNoMerge(action,
                derivedEntitySecurityService.creationContext(securityContext, source));
        action.setSecurityId(action.getId());
        derivedEntitySecurityService.setTenantFrom(action, source);
        repository.massMerge(List.of(incident, action));

        HealthNotificationEventType eventType = switch (create.getActionType()) {
            case ACKNOWLEDGED -> HealthNotificationEventType.INCIDENT_ACKNOWLEDGED;
            case RESOLVED -> HealthNotificationEventType.INCIDENT_RESOLVED;
            case IGNORED, FALSE_ALARM -> HealthNotificationEventType.INCIDENT_IGNORED;
            default -> HealthNotificationEventType.INCIDENT_UPDATED;
        };
        String subjectName = incident.getRemote() != null
                ? incident.getRemote().getName() : incident.getRemoteGroup().getName();
        notificationService.recordEvent(incident, incident.getRemote(), incident.getRemoteGroup(), eventType,
                incident.getSeverityName(), incident.getSeverityValue(),
                incident.getSeverityName(), incident.getSeverityValue(), incident.getMatchedRuleId(),
                "Incident " + create.getActionType().name().toLowerCase().replace('_', ' '),
                subjectName + ": " + Objects.toString(create.getActionDescription(), create.getActionType().name()),
                now);
        return action;
    }

    private HealthIncidentStatus applyAction(HealthIncident incident,
                                             HealthIncidentActionType actionType,
                                             SecurityUser user,
                                             OffsetDateTime now) {
        switch (actionType) {
            case ACKNOWLEDGED -> {
                incident.setStatus(HealthIncidentStatus.ACKNOWLEDGED);
                incident.setAcknowledgedAt(now);
                incident.setAcknowledgedBy(user);
            }
            case IN_PROGRESS, RESTARTED, REPAIRED, REPLACED ->
                    incident.setStatus(HealthIncidentStatus.IN_PROGRESS);
            case RESOLVED -> {
                incident.setStatus(HealthIncidentStatus.RESOLVED);
                incident.setResolvedAt(now);
                incident.setResolvedBy(user);
            }
            case IGNORED, FALSE_ALARM -> {
                incident.setStatus(HealthIncidentStatus.IGNORED);
                incident.setResolvedAt(now);
                incident.setResolvedBy(user);
            }
            case COMMENT -> { }
            case OPENED -> throw new IllegalArgumentException("OPENED is a system action");
        }
        return incident.getStatus();
    }

    private int value(Integer value) {
        return value == null ? 0 : value;
    }

    private String title(HealthNotificationEventType eventType,
                         String subjectName,
                         String severityName) {
        return switch (eventType) {
            case INCIDENT_OPENED -> "Action required: " + subjectName;
            case INCIDENT_ESCALATED -> "Health escalation: " + subjectName;
            case HEALTH_RECOVERED -> "Health recovered: " + subjectName;
            default -> "Health update: " + subjectName + (severityName == null ? "" : " - " + severityName);
        };
    }

    private String message(String summary,
                           String severityName,
                           Integer severityValue,
                           String subjectName) {
        if (summary != null && !summary.isBlank()) return summary;
        return subjectName + " severity is " + Objects.toString(severityName, "unknown")
                + " (" + Objects.toString(severityValue, "unknown") + ")";
    }
}
