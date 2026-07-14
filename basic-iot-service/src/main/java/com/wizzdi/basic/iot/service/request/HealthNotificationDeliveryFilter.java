package com.wizzdi.basic.iot.service.request;

import com.wizzdi.basic.iot.model.HealthNotificationChannel;
import com.wizzdi.basic.iot.model.HealthNotificationDeliveryMode;
import com.wizzdi.basic.iot.model.HealthNotificationDeliveryStatus;
import com.wizzdi.basic.iot.model.HealthNotificationEventType;
import com.wizzdi.flexicore.security.request.BasicPropertiesFilter;
import com.wizzdi.flexicore.security.request.PaginationFilter;

import java.time.OffsetDateTime;
import java.util.Set;

public class HealthNotificationDeliveryFilter extends PaginationFilter {
    private BasicPropertiesFilter basicPropertiesFilter;
    private Set<String> healthNotificationDeliveryIds;
    private Set<String> userIds;
    private Set<HealthNotificationChannel> channels;
    private Set<HealthNotificationDeliveryMode> deliveryModes;
    private Set<HealthNotificationDeliveryStatus> statuses;
    private Set<HealthNotificationEventType> eventTypes;
    private Boolean unreadOnly;
    private OffsetDateTime occurredAfter;
    private OffsetDateTime occurredBefore;

    public BasicPropertiesFilter getBasicPropertiesFilter() { return basicPropertiesFilter; }
    public <T extends HealthNotificationDeliveryFilter> T setBasicPropertiesFilter(BasicPropertiesFilter basicPropertiesFilter) { this.basicPropertiesFilter = basicPropertiesFilter; return (T) this; }
    public Set<String> getHealthNotificationDeliveryIds() { return healthNotificationDeliveryIds; }
    public <T extends HealthNotificationDeliveryFilter> T setHealthNotificationDeliveryIds(Set<String> healthNotificationDeliveryIds) { this.healthNotificationDeliveryIds = healthNotificationDeliveryIds; return (T) this; }
    public Set<String> getUserIds() { return userIds; }
    public <T extends HealthNotificationDeliveryFilter> T setUserIds(Set<String> userIds) { this.userIds = userIds; return (T) this; }
    public Set<HealthNotificationChannel> getChannels() { return channels; }
    public <T extends HealthNotificationDeliveryFilter> T setChannels(Set<HealthNotificationChannel> channels) { this.channels = channels; return (T) this; }
    public Set<HealthNotificationDeliveryMode> getDeliveryModes() { return deliveryModes; }
    public <T extends HealthNotificationDeliveryFilter> T setDeliveryModes(Set<HealthNotificationDeliveryMode> deliveryModes) { this.deliveryModes = deliveryModes; return (T) this; }
    public Set<HealthNotificationDeliveryStatus> getStatuses() { return statuses; }
    public <T extends HealthNotificationDeliveryFilter> T setStatuses(Set<HealthNotificationDeliveryStatus> statuses) { this.statuses = statuses; return (T) this; }
    public Set<HealthNotificationEventType> getEventTypes() { return eventTypes; }
    public <T extends HealthNotificationDeliveryFilter> T setEventTypes(Set<HealthNotificationEventType> eventTypes) { this.eventTypes = eventTypes; return (T) this; }
    public Boolean getUnreadOnly() { return unreadOnly; }
    public <T extends HealthNotificationDeliveryFilter> T setUnreadOnly(Boolean unreadOnly) { this.unreadOnly = unreadOnly; return (T) this; }
    public OffsetDateTime getOccurredAfter() { return occurredAfter; }
    public <T extends HealthNotificationDeliveryFilter> T setOccurredAfter(OffsetDateTime occurredAfter) { this.occurredAfter = occurredAfter; return (T) this; }
    public OffsetDateTime getOccurredBefore() { return occurredBefore; }
    public <T extends HealthNotificationDeliveryFilter> T setOccurredBefore(OffsetDateTime occurredBefore) { this.occurredBefore = occurredBefore; return (T) this; }
}
