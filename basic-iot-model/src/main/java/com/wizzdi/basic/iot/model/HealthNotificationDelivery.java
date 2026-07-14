package com.wizzdi.basic.iot.model;

import com.flexicore.model.Baseclass;
import com.flexicore.model.SecurityUser;
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
        @Index(name = "health_notification_delivery_due_idx", columnList = "status,scheduledAt,nextAttemptAt"),
        @Index(name = "health_notification_delivery_user_idx", columnList = "user_id,channel,readAt,softdelete"),
        @Index(name = "health_notification_delivery_outbox_idx", columnList = "healthNotificationOutbox_id")
})
public class HealthNotificationDelivery extends Baseclass {
    @ManyToOne(targetEntity = HealthNotificationOutbox.class)
    private HealthNotificationOutbox healthNotificationOutbox;
    @ManyToOne(targetEntity = HealthNotificationPolicy.class)
    private HealthNotificationPolicy healthNotificationPolicy;
    @ManyToOne(targetEntity = HealthNotificationChannelPreference.class)
    private HealthNotificationChannelPreference channelPreference;
    @ManyToOne(targetEntity = SecurityUser.class)
    private SecurityUser user;
    @Enumerated(EnumType.STRING)
    private HealthNotificationChannel channel;
    @Enumerated(EnumType.STRING)
    private HealthNotificationDeliveryMode deliveryMode;
    @Enumerated(EnumType.STRING)
    private HealthNotificationDeliveryStatus status = HealthNotificationDeliveryStatus.PENDING;
    private String destination;
    @Column(columnDefinition = "timestamp with time zone")
    private OffsetDateTime scheduledAt;
    @Column(columnDefinition = "timestamp with time zone")
    private OffsetDateTime nextAttemptAt;
    @Column(columnDefinition = "timestamp with time zone")
    private OffsetDateTime processingStartedAt;
    @Column(columnDefinition = "timestamp with time zone")
    private OffsetDateTime deliveredAt;
    @Column(columnDefinition = "timestamp with time zone")
    private OffsetDateTime readAt;
    @Column(columnDefinition = "timestamp with time zone")
    private OffsetDateTime dismissedAt;
    private int attemptCount;
    private String providerMessageId;
    private Integer providerResponseCode;
    @Column(length = 2000)
    private String providerResponse;
    @Column(length = 4000)
    private String lastError;

    public HealthNotificationOutbox getHealthNotificationOutbox() { return healthNotificationOutbox; }
    public <T extends HealthNotificationDelivery> T setHealthNotificationOutbox(HealthNotificationOutbox healthNotificationOutbox) { this.healthNotificationOutbox = healthNotificationOutbox; return (T) this; }
    public HealthNotificationPolicy getHealthNotificationPolicy() { return healthNotificationPolicy; }
    public <T extends HealthNotificationDelivery> T setHealthNotificationPolicy(HealthNotificationPolicy healthNotificationPolicy) { this.healthNotificationPolicy = healthNotificationPolicy; return (T) this; }
    public HealthNotificationChannelPreference getChannelPreference() { return channelPreference; }
    public <T extends HealthNotificationDelivery> T setChannelPreference(HealthNotificationChannelPreference channelPreference) { this.channelPreference = channelPreference; return (T) this; }
    public SecurityUser getUser() { return user; }
    public <T extends HealthNotificationDelivery> T setUser(SecurityUser user) { this.user = user; return (T) this; }
    public HealthNotificationChannel getChannel() { return channel; }
    public <T extends HealthNotificationDelivery> T setChannel(HealthNotificationChannel channel) { this.channel = channel; return (T) this; }
    public HealthNotificationDeliveryMode getDeliveryMode() { return deliveryMode; }
    public <T extends HealthNotificationDelivery> T setDeliveryMode(HealthNotificationDeliveryMode deliveryMode) { this.deliveryMode = deliveryMode; return (T) this; }
    public HealthNotificationDeliveryStatus getStatus() { return status; }
    public <T extends HealthNotificationDelivery> T setStatus(HealthNotificationDeliveryStatus status) { this.status = status; return (T) this; }
    public String getDestination() { return destination; }
    public <T extends HealthNotificationDelivery> T setDestination(String destination) { this.destination = destination; return (T) this; }
    public OffsetDateTime getScheduledAt() { return scheduledAt; }
    public <T extends HealthNotificationDelivery> T setScheduledAt(OffsetDateTime scheduledAt) { this.scheduledAt = scheduledAt; return (T) this; }
    public OffsetDateTime getNextAttemptAt() { return nextAttemptAt; }
    public <T extends HealthNotificationDelivery> T setNextAttemptAt(OffsetDateTime nextAttemptAt) { this.nextAttemptAt = nextAttemptAt; return (T) this; }
    public OffsetDateTime getProcessingStartedAt() { return processingStartedAt; }
    public <T extends HealthNotificationDelivery> T setProcessingStartedAt(OffsetDateTime processingStartedAt) { this.processingStartedAt = processingStartedAt; return (T) this; }
    public OffsetDateTime getDeliveredAt() { return deliveredAt; }
    public <T extends HealthNotificationDelivery> T setDeliveredAt(OffsetDateTime deliveredAt) { this.deliveredAt = deliveredAt; return (T) this; }
    public OffsetDateTime getReadAt() { return readAt; }
    public <T extends HealthNotificationDelivery> T setReadAt(OffsetDateTime readAt) { this.readAt = readAt; return (T) this; }
    public OffsetDateTime getDismissedAt() { return dismissedAt; }
    public <T extends HealthNotificationDelivery> T setDismissedAt(OffsetDateTime dismissedAt) { this.dismissedAt = dismissedAt; return (T) this; }
    public int getAttemptCount() { return attemptCount; }
    public <T extends HealthNotificationDelivery> T setAttemptCount(int attemptCount) { this.attemptCount = attemptCount; return (T) this; }
    public String getProviderMessageId() { return providerMessageId; }
    public <T extends HealthNotificationDelivery> T setProviderMessageId(String providerMessageId) { this.providerMessageId = providerMessageId; return (T) this; }
    public Integer getProviderResponseCode() { return providerResponseCode; }
    public <T extends HealthNotificationDelivery> T setProviderResponseCode(Integer providerResponseCode) { this.providerResponseCode = providerResponseCode; return (T) this; }
    public String getProviderResponse() { return providerResponse; }
    public <T extends HealthNotificationDelivery> T setProviderResponse(String providerResponse) { this.providerResponse = providerResponse; return (T) this; }
    public String getLastError() { return lastError; }
    public <T extends HealthNotificationDelivery> T setLastError(String lastError) { this.lastError = lastError; return (T) this; }
}
