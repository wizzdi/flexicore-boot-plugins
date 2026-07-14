package com.wizzdi.basic.iot.service.service;

import com.flexicore.model.Baseclass;
import com.flexicore.model.SecurityUser;
import com.wizzdi.basic.iot.model.DeviceType;
import com.wizzdi.basic.iot.model.HealthNotificationChannel;
import com.wizzdi.basic.iot.model.HealthNotificationChannelPreference;
import com.wizzdi.basic.iot.model.HealthNotificationDeliveryMode;
import com.wizzdi.basic.iot.model.HealthNotificationPolicy;
import com.wizzdi.basic.iot.model.HealthNotificationScopeType;
import com.wizzdi.basic.iot.model.Remote;
import com.wizzdi.basic.iot.model.RemoteGroup;
import com.wizzdi.basic.iot.service.data.HealthNotificationRepository;
import com.wizzdi.basic.iot.service.request.HealthNotificationChannelPreferenceCreate;
import com.wizzdi.basic.iot.service.request.HealthNotificationPolicyCreate;
import com.wizzdi.basic.iot.service.request.HealthNotificationPolicyFilter;
import com.wizzdi.basic.iot.service.request.HealthNotificationPolicyUpdate;
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

import java.time.DateTimeException;
import java.time.LocalTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.function.Function;
import java.util.stream.Collectors;

@Extension
@Component
public class HealthNotificationPolicyService implements Plugin {
    @Autowired
    private HealthNotificationRepository repository;
    @Autowired
    private BasicService basicService;
    @Autowired
    private DerivedEntitySecurityService derivedEntitySecurityService;
    @Value("${basic.iot.health.actionRequiredFromSeverityValue:60}")
    private int defaultMinimumSeverityValue;

    public void validateFiltering(HealthNotificationPolicyFilter filter, SecurityContext securityContext) {
        basicService.validate(filter, securityContext);
    }

    public void validate(HealthNotificationPolicyCreate create, SecurityContext securityContext) {
        basicService.validate(create, securityContext);
        HealthNotificationPolicy existing = null;
        if (create instanceof HealthNotificationPolicyUpdate update) {
            existing = repository.getByIdOrNull(update.getId(), HealthNotificationPolicy.class, securityContext);
            if (existing == null) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                        "No accessible HealthNotificationPolicy with id " + update.getId());
            }
            update.setHealthNotificationPolicy(existing);
        }
        String userId = create.getUserId();
        if ((userId == null || userId.isBlank()) && existing != null && existing.getUser() != null) {
            userId = existing.getUser().getId();
        }
        SecurityUser user = userId == null || userId.isBlank()
                ? securityContext.getUser()
                : repository.getByIdOrNull(userId, SecurityUser.class, securityContext);
        if (user == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "A valid accessible userId is required");
        }
        HealthNotificationScopeType scopeType = create.getScopeType() == null
                ? existing == null ? HealthNotificationScopeType.TENANT : existing.getScopeType()
                : create.getScopeType();
        Baseclass source = resolveScopeSource(create, existing, scopeType, securityContext);
        validateChannels(create.getChannelPreferences());
        if (create.getMinimumSeverityValue() != null && create.getMinimumSeverityValue() < 0) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "minimumSeverityValue must be non-negative");
        }
        create.setUserId(user.getId());
        create.setScopeType(scopeType);
    }

    private Baseclass resolveScopeSource(HealthNotificationPolicyCreate create,
                                         HealthNotificationPolicy existing,
                                         HealthNotificationScopeType scopeType,
                                         SecurityContext securityContext) {
        return switch (scopeType) {
            case TENANT -> null;
            case REMOTE -> {
                String id = value(create.getRemoteId(), existing == null || existing.getRemote() == null ? null : existing.getRemote().getId());
                Remote remote = repository.getByIdOrNull(id, Remote.class, securityContext);
                if (remote == null) throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "A valid accessible remoteId is required");
                yield remote;
            }
            case REMOTE_GROUP -> {
                String id = value(create.getRemoteGroupId(), existing == null || existing.getRemoteGroup() == null ? null : existing.getRemoteGroup().getId());
                RemoteGroup group = repository.getByIdOrNull(id, RemoteGroup.class, securityContext);
                if (group == null) throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "A valid accessible remoteGroupId is required");
                yield group;
            }
            case DEVICE_TYPE -> {
                String id = value(create.getDeviceTypeId(), existing == null || existing.getDeviceType() == null ? null : existing.getDeviceType().getId());
                DeviceType deviceType = repository.getByIdOrNull(id, DeviceType.class, securityContext);
                if (deviceType == null) throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "A valid accessible deviceTypeId is required");
                yield deviceType;
            }
        };
    }

    private String value(String requested, String existing) {
        return requested == null || requested.isBlank() ? existing : requested;
    }

    private void validateChannels(List<HealthNotificationChannelPreferenceCreate> channels) {
        if (channels == null) return;
        Set<HealthNotificationChannel> unique = new HashSet<>();
        for (HealthNotificationChannelPreferenceCreate channel : channels) {
            if (channel.getChannel() == null) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "channel is required");
            }
            if (!unique.add(channel.getChannel())) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Only one preference per channel is allowed");
            }
            HealthNotificationDeliveryMode mode = channel.getDeliveryMode() == null
                    ? HealthNotificationDeliveryMode.IMMEDIATE : channel.getDeliveryMode();
            if (mode != HealthNotificationDeliveryMode.DISABLED
                    && channel.getChannel() != HealthNotificationChannel.IN_APP
                    && (channel.getDestination() == null || channel.getDestination().isBlank())) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                        "destination is required for " + channel.getChannel());
            }
            if (mode != HealthNotificationDeliveryMode.DISABLED
                    && channel.getChannel() == HealthNotificationChannel.EMAIL
                    && !channel.getDestination().matches("^[^@\\s]+@[^@\\s]+\\.[^@\\s]+$")) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid email destination");
            }
            if (mode != HealthNotificationDeliveryMode.DISABLED
                    && channel.getChannel() == HealthNotificationChannel.WHATSAPP
                    && channel.getDestination().replaceAll("[^0-9]", "").length() < 8) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid WhatsApp destination");
            }
            String locale = channel.getLocale() == null || channel.getLocale().isBlank() ? "en" : channel.getLocale().trim();
            if (!locale.matches("[A-Za-z]{2,3}([_-][A-Za-z]{2,4})?")) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid locale " + locale);
            }
            String zone = channel.getTimeZone() == null || channel.getTimeZone().isBlank() ? "UTC" : channel.getTimeZone();
            try {
                ZoneId.of(zone);
            } catch (DateTimeException e) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid timeZone " + zone);
            }
            if (channel.getSummaryDayOfWeek() != null
                    && (channel.getSummaryDayOfWeek() < 1 || channel.getSummaryDayOfWeek() > 7)) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "summaryDayOfWeek must be between 1 and 7");
            }
        }
    }

    public PaginationResponse<HealthNotificationPolicy> getAll(SecurityContext securityContext,
                                                                 HealthNotificationPolicyFilter filter) {
        List<HealthNotificationPolicy> policies = repository.listPolicies(securityContext, filter);
        populate(policies);
        return new PaginationResponse<>(policies, filter, repository.countPolicies(securityContext, filter));
    }

    public void populate(List<HealthNotificationPolicy> policies) {
        if (policies == null || policies.isEmpty()) return;
        Map<String, List<HealthNotificationChannelPreference>> byPolicy = repository.listPreferences(
                        policies.stream().map(Baseclass::getId).toList()).stream()
                .collect(Collectors.groupingBy(p -> p.getHealthNotificationPolicy().getId()));
        policies.forEach(policy -> policy.setChannelPreferences(byPolicy.getOrDefault(policy.getId(), List.of())));
    }

    @Transactional
    public HealthNotificationPolicy getOrCreateMyDefault(SecurityContext securityContext) {
        if (securityContext.getUser() == null || securityContext.getTenantToCreateIn() == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "A user and tenantToCreateIn are required");
        }
        HealthNotificationPolicy existing = repository.findTenantPolicy(
                securityContext.getUser().getId(), securityContext.getTenantToCreateIn().getId());
        if (existing != null) {
            populate(List.of(existing));
            return existing;
        }
        HealthNotificationChannelPreferenceCreate inApp = new HealthNotificationChannelPreferenceCreate();
        inApp.setName("In-app notifications");
        inApp.setChannel(HealthNotificationChannel.IN_APP);
        inApp.setDeliveryMode(HealthNotificationDeliveryMode.IMMEDIATE);
        inApp.setEnabled(true);
        HealthNotificationPolicyCreate create = new HealthNotificationPolicyCreate();
        create.setName("Default health notifications");
        create.setUserId(securityContext.getUser().getId());
        create.setScopeType(HealthNotificationScopeType.TENANT);
        create.setMinimumSeverityValue(defaultMinimumSeverityValue);
        create.setEscalationOnly(true);
        create.setNotifyOnRecovery(true);
        create.setNotifyOnIncidentActions(true);
        create.setIncidentOnly(true);
        create.setChannelPreferences(List.of(inApp));
        validate(create, securityContext);
        return create(create, securityContext);
    }

    @Transactional
    public HealthNotificationPolicy create(HealthNotificationPolicyCreate create, SecurityContext securityContext) {
        HealthNotificationPolicy policy = new HealthNotificationPolicy();
        policy.setId(UUID.randomUUID().toString());
        Baseclass source = updateNoMerge(policy, create, securityContext);
        SecurityContext creationContext = source == null
                ? securityContext
                : derivedEntitySecurityService.creationContext(securityContext, source);
        BaseclassService.createSecurityObjectNoMerge(policy, creationContext);
        policy.setCreator(policy.getUser());
        if (source != null) derivedEntitySecurityService.setTenantFrom(policy, source);
        List<Object> toMerge = new ArrayList<>();
        toMerge.add(policy);
        syncChannels(policy, create.getChannelPreferences(), securityContext, toMerge);
        repository.massMerge(toMerge);
        populate(List.of(policy));
        return policy;
    }

    @Transactional
    public HealthNotificationPolicy update(HealthNotificationPolicyUpdate update, SecurityContext securityContext) {
        HealthNotificationPolicy policy = update.getHealthNotificationPolicy();
        updateNoMerge(policy, update, securityContext);
        List<Object> toMerge = new ArrayList<>();
        toMerge.add(policy);
        if (update.getChannelPreferences() != null) {
            syncChannels(policy, update.getChannelPreferences(), securityContext, toMerge);
        }
        repository.massMerge(toMerge);
        populate(List.of(policy));
        return policy;
    }

    private Baseclass updateNoMerge(HealthNotificationPolicy policy,
                                    HealthNotificationPolicyCreate create,
                                    SecurityContext securityContext) {
        basicService.updateBasicNoMerge(create, policy);
        SecurityUser user = create.getUserId() == null || create.getUserId().isBlank()
                ? policy.getUser() == null ? securityContext.getUser() : policy.getUser()
                : repository.getByIdOrNull(create.getUserId(), SecurityUser.class, securityContext);
        policy.setUser(user);
        HealthNotificationScopeType scopeType = create.getScopeType() == null
                ? policy.getScopeType() == null ? HealthNotificationScopeType.TENANT : policy.getScopeType()
                : create.getScopeType();
        policy.setScopeType(scopeType);
        Baseclass source = resolveScopeSource(create, policy, scopeType, securityContext);
        policy.setRemote(scopeType == HealthNotificationScopeType.REMOTE ? (Remote) source : null);
        policy.setRemoteGroup(scopeType == HealthNotificationScopeType.REMOTE_GROUP ? (RemoteGroup) source : null);
        policy.setDeviceType(scopeType == HealthNotificationScopeType.DEVICE_TYPE ? (DeviceType) source : null);
        if (create.getEnabled() != null) policy.setEnabled(create.getEnabled());
        if (create.getMinimumSeverityValue() != null) policy.setMinimumSeverityValue(create.getMinimumSeverityValue());
        if (create.getEscalationOnly() != null) policy.setEscalationOnly(create.getEscalationOnly());
        if (create.getNotifyOnRecovery() != null) policy.setNotifyOnRecovery(create.getNotifyOnRecovery());
        if (create.getNotifyOnIncidentActions() != null) policy.setNotifyOnIncidentActions(create.getNotifyOnIncidentActions());
        if (create.getIncidentOnly() != null) policy.setIncidentOnly(create.getIncidentOnly());
        if (source != null) derivedEntitySecurityService.setTenantFrom(policy, source);
        return source;
    }

    private void syncChannels(HealthNotificationPolicy policy,
                              List<HealthNotificationChannelPreferenceCreate> requested,
                              SecurityContext securityContext,
                              List<Object> toMerge) {
        if (requested == null) return;
        List<HealthNotificationChannelPreference> existing = repository.listPreferences(List.of(policy.getId()));
        Map<String, HealthNotificationChannelPreference> byId = existing.stream()
                .collect(Collectors.toMap(Baseclass::getId, Function.identity()));
        Set<String> retained = new HashSet<>();
        for (HealthNotificationChannelPreferenceCreate item : requested) {
            HealthNotificationChannelPreference preference = item.getId() == null ? null : byId.get(item.getId());
            if (item.getId() != null && preference == null) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                        "Channel preference " + item.getId() + " does not belong to policy " + policy.getId());
            }
            if (preference == null) {
                preference = new HealthNotificationChannelPreference();
                preference.setId(UUID.randomUUID().toString());
                preference.setHealthNotificationPolicy(policy);
                BaseclassService.createSecurityObjectNoMerge(preference,
                        derivedEntitySecurityService.creationContext(securityContext, policy));
                preference.setCreator(policy.getUser());
            }
            basicService.updateBasicNoMerge(item, preference);
            derivedEntitySecurityService.setTenantFrom(preference, policy);
            preference.setChannel(item.getChannel());
            preference.setDeliveryMode(item.getDeliveryMode() == null
                    ? HealthNotificationDeliveryMode.IMMEDIATE : item.getDeliveryMode());
            preference.setEnabled(item.getEnabled() == null || item.getEnabled());
            preference.setDestination(item.getDestination());
            preference.setLocale(item.getLocale() == null || item.getLocale().isBlank()
                    ? "en" : item.getLocale().trim().replace('-', '_').toLowerCase(java.util.Locale.ROOT));
            preference.setTimeZone(item.getTimeZone() == null || item.getTimeZone().isBlank() ? "UTC" : item.getTimeZone());
            preference.setSummaryLocalTime(item.getSummaryLocalTime() == null ? LocalTime.of(8, 0) : item.getSummaryLocalTime());
            preference.setSummaryDayOfWeek(item.getSummaryDayOfWeek() == null ? 1 : item.getSummaryDayOfWeek());
            preference.setSoftDelete(false);
            retained.add(preference.getId());
            toMerge.add(preference);
        }
        for (HealthNotificationChannelPreference old : existing) {
            if (!retained.contains(old.getId())) {
                old.setSoftDelete(true);
                toMerge.add(old);
            }
        }
    }
}
