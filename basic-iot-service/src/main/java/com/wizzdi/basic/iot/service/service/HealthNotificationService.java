package com.wizzdi.basic.iot.service.service;

import com.flexicore.model.Baseclass;
import com.wizzdi.basic.iot.model.Device;
import com.wizzdi.basic.iot.model.HealthIncident;
import com.wizzdi.basic.iot.model.HealthNotificationChannelPreference;
import com.wizzdi.basic.iot.model.HealthNotificationDelivery;
import com.wizzdi.basic.iot.model.HealthNotificationDeliveryMode;
import com.wizzdi.basic.iot.model.HealthNotificationDeliveryStatus;
import com.wizzdi.basic.iot.model.HealthNotificationEventType;
import com.wizzdi.basic.iot.model.HealthNotificationOutbox;
import com.wizzdi.basic.iot.model.HealthNotificationPolicy;
import com.wizzdi.basic.iot.model.Remote;
import com.wizzdi.basic.iot.model.RemoteGroup;
import com.wizzdi.basic.iot.service.data.HealthNotificationRepository;
import com.wizzdi.basic.iot.service.request.HealthNotificationDeliveryFilter;
import com.wizzdi.basic.iot.service.request.MarkHealthNotificationsRequest;
import com.wizzdi.flexicore.boot.base.interfaces.Plugin;
import com.wizzdi.flexicore.security.configuration.SecurityContext;
import com.wizzdi.flexicore.security.interfaces.SecurityContextProvider;
import com.wizzdi.flexicore.security.response.PaginationResponse;
import com.wizzdi.flexicore.security.service.BasicService;
import org.pf4j.Extension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.DayOfWeek;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalAdjusters;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Extension
@Component
public class HealthNotificationService implements Plugin {
    @Autowired
    private HealthNotificationRepository repository;
    @Autowired
    private HealthNotificationPolicyService policyService;
    @Autowired
    private BasicService basicService;
    @Autowired
    private DerivedEntitySecurityService derivedEntitySecurityService;
    @Autowired
    private SecurityContextProvider securityContextProvider;

    public void validateFiltering(HealthNotificationDeliveryFilter filter, SecurityContext securityContext) {
        basicService.validate(filter, securityContext);
    }

    public PaginationResponse<HealthNotificationDelivery> getAll(SecurityContext securityContext,
                                                                  HealthNotificationDeliveryFilter filter) {
        List<HealthNotificationDelivery> deliveries = repository.listDeliveries(securityContext, filter);
        return new PaginationResponse<>(deliveries, filter, repository.countDeliveries(securityContext, filter));
    }

    @Transactional
    public List<HealthNotificationDelivery> mark(SecurityContext securityContext,
                                                  MarkHealthNotificationsRequest request) {
        if (request.getHealthNotificationDeliveryIds() == null
                || request.getHealthNotificationDeliveryIds().isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "healthNotificationDeliveryIds is required");
        }
        OffsetDateTime now = OffsetDateTime.now();
        List<HealthNotificationDelivery> deliveries = new ArrayList<>();
        for (String id : request.getHealthNotificationDeliveryIds()) {
            HealthNotificationDelivery delivery = repository.getByIdOrNull(
                    id, HealthNotificationDelivery.class, securityContext);
            if (delivery == null) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                        "No accessible HealthNotificationDelivery with id " + id);
            }
            delivery.setReadAt(request.isRead() ? now : null);
            if (request.isDismissed()) delivery.setDismissedAt(now);
            deliveries.add(delivery);
        }
        repository.massMerge(deliveries);
        return deliveries;
    }

    @Transactional
    public HealthNotificationOutbox recordEvent(HealthIncident incident,
                                                 Remote remote,
                                                 RemoteGroup group,
                                                 HealthNotificationEventType eventType,
                                                 String previousSeverityName,
                                                 Integer previousSeverityValue,
                                                 String severityName,
                                                 Integer severityValue,
                                                 String matchedRuleId,
                                                 String title,
                                                 String message,
                                                 OffsetDateTime occurredAt) {
        Baseclass source = remote != null ? remote : group;
        if (source == null || source.getTenant() == null) return null;
        OffsetDateTime eventTime = occurredAt == null ? OffsetDateTime.now() : occurredAt;
        HealthNotificationOutbox outbox = new HealthNotificationOutbox();
        outbox.setId(UUID.randomUUID().toString());
        outbox.setEventId(UUID.randomUUID().toString());
        outbox.setName("health-event-" + outbox.getEventId());
        outbox.setHealthIncident(incident);
        outbox.setRemote(remote);
        outbox.setRemoteGroup(group);
        outbox.setEventType(eventType);
        outbox.setPreviousSeverityName(previousSeverityName);
        outbox.setPreviousSeverityValue(previousSeverityValue);
        outbox.setSeverityName(severityName);
        outbox.setSeverityValue(severityValue);
        outbox.setMatchedRuleId(matchedRuleId);
        outbox.setTitle(title);
        outbox.setMessage(message);
        outbox.setOccurredAt(eventTime);
        if (remote != null) derivedEntitySecurityService.inheritFromRemote(outbox, remote);
        else derivedEntitySecurityService.inheritFromGroup(outbox, group);
        outbox.setSecurityId(outbox.getId());

        String deviceTypeId = remote instanceof Device device && device.getDeviceType() != null
                ? device.getDeviceType().getId() : null;
        List<HealthNotificationPolicy> policies = repository.findMatchingPolicies(
                source.getTenant().getId(),
                remote == null ? null : remote.getId(),
                group == null ? null : group.getId(),
                deviceTypeId);
        policyService.populate(policies);

        List<Object> toMerge = new ArrayList<>();
        toMerge.add(outbox);
        for (HealthNotificationPolicy policy : policies) {
            if (!userCanAccessSource(policy, source)) continue;
            if (!matches(policy, incident, eventType, previousSeverityValue, severityValue)) continue;
            for (HealthNotificationChannelPreference preference : policy.getChannelPreferences()) {
                if (!preference.isEnabled()
                        || preference.getDeliveryMode() == HealthNotificationDeliveryMode.DISABLED) continue;
                HealthNotificationDelivery delivery = createDelivery(
                        outbox, policy, preference, source, eventTime);
                toMerge.add(delivery);
            }
        }
        repository.massMerge(toMerge);
        return outbox;
    }

    @SuppressWarnings({"rawtypes", "unchecked"})
    private boolean userCanAccessSource(HealthNotificationPolicy policy, Baseclass source) {
        if (policy == null || policy.getUser() == null || source == null) return false;
        SecurityContext userContext = securityContextProvider.getSecurityContext(policy.getUser());
        if (userContext == null) return false;
        return repository.getByIdOrNull(source.getId(), (Class) source.getClass(), userContext) != null;
    }

    private boolean matches(HealthNotificationPolicy policy,
                            HealthIncident incident,
                            HealthNotificationEventType eventType,
                            Integer previousSeverityValue,
                            Integer severityValue) {
        if (policy == null || !policy.isEnabled()) return false;
        if (policy.isIncidentOnly() && incident == null) return false;
        boolean recovery = eventType == HealthNotificationEventType.HEALTH_RECOVERED;
        boolean incidentAction = eventType == HealthNotificationEventType.INCIDENT_ACKNOWLEDGED
                || eventType == HealthNotificationEventType.INCIDENT_UPDATED
                || eventType == HealthNotificationEventType.INCIDENT_RESOLVED
                || eventType == HealthNotificationEventType.INCIDENT_IGNORED;
        if (recovery && !policy.isNotifyOnRecovery()) return false;
        if (incidentAction && !policy.isNotifyOnIncidentActions()) return false;
        int comparisonSeverity = recovery
                ? value(previousSeverityValue)
                : value(severityValue);
        if (comparisonSeverity < value(policy.getMinimumSeverityValue())) return false;
        if (policy.isEscalationOnly()
                && eventType == HealthNotificationEventType.HEALTH_CHANGED
                && value(severityValue) <= value(previousSeverityValue)) return false;
        return true;
    }

    private int value(Integer value) {
        return value == null ? 0 : value;
    }

    private HealthNotificationDelivery createDelivery(HealthNotificationOutbox outbox,
                                                        HealthNotificationPolicy policy,
                                                        HealthNotificationChannelPreference preference,
                                                        Baseclass source,
                                                        OffsetDateTime now) {
        HealthNotificationDelivery delivery = new HealthNotificationDelivery();
        delivery.setId(UUID.randomUUID().toString());
        delivery.setSecurityId(delivery.getId());
        delivery.setName("health-delivery-" + delivery.getId());
        delivery.setHealthNotificationOutbox(outbox);
        delivery.setHealthNotificationPolicy(policy);
        delivery.setChannelPreference(preference);
        delivery.setUser(policy.getUser());
        delivery.setCreator(policy.getUser());
        delivery.setTenant(source.getTenant());
        delivery.setChannel(preference.getChannel());
        delivery.setDeliveryMode(preference.getDeliveryMode());
        delivery.setStatus(HealthNotificationDeliveryStatus.PENDING);
        delivery.setDestination(preference.getDestination());
        delivery.setScheduledAt(calculateScheduledAt(preference, now));
        delivery.setAttemptCount(0);
        return delivery;
    }

    public OffsetDateTime calculateScheduledAt(HealthNotificationChannelPreference preference,
                                                OffsetDateTime now) {
        HealthNotificationDeliveryMode mode = preference.getDeliveryMode();
        if (mode == null || mode == HealthNotificationDeliveryMode.IMMEDIATE) return now;
        ZoneId zoneId = ZoneId.of(preference.getTimeZone() == null
                || preference.getTimeZone().isBlank() ? "UTC" : preference.getTimeZone());
        ZonedDateTime localNow = now.atZoneSameInstant(zoneId);
        ZonedDateTime scheduled;
        if (mode == HealthNotificationDeliveryMode.HOURLY_SUMMARY) {
            scheduled = localNow.truncatedTo(ChronoUnit.HOURS).plusHours(1);
        } else if (mode == HealthNotificationDeliveryMode.DAILY_SUMMARY) {
            LocalTime time = preference.getSummaryLocalTime() == null
                    ? LocalTime.of(8, 0) : preference.getSummaryLocalTime();
            scheduled = localNow.with(time);
            if (!scheduled.isAfter(localNow)) scheduled = scheduled.plusDays(1);
        } else if (mode == HealthNotificationDeliveryMode.WEEKLY_SUMMARY) {
            LocalTime time = preference.getSummaryLocalTime() == null
                    ? LocalTime.of(8, 0) : preference.getSummaryLocalTime();
            DayOfWeek day = DayOfWeek.of(preference.getSummaryDayOfWeek() == null
                    ? 1 : preference.getSummaryDayOfWeek());
            LocalDateTime localTarget = localNow.toLocalDate()
                    .with(TemporalAdjusters.nextOrSame(day)).atTime(time);
            scheduled = localTarget.atZone(zoneId);
            if (!scheduled.isAfter(localNow)) scheduled = scheduled.plusWeeks(1);
        } else {
            scheduled = localNow;
        }
        return scheduled.toOffsetDateTime();
    }
}
