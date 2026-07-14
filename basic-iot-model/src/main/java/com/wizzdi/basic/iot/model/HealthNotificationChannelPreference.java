package com.wizzdi.basic.iot.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.flexicore.model.Baseclass;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Index;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

import java.time.LocalTime;

@Entity
@Table(indexes = @Index(name = "health_notification_channel_policy_idx", columnList = "healthNotificationPolicy_id,channel,softdelete"))
public class HealthNotificationChannelPreference extends Baseclass {
    @ManyToOne(targetEntity = HealthNotificationPolicy.class)
    @JsonIgnore
    private HealthNotificationPolicy healthNotificationPolicy;
    @Enumerated(EnumType.STRING)
    private HealthNotificationChannel channel;
    @Enumerated(EnumType.STRING)
    private HealthNotificationDeliveryMode deliveryMode = HealthNotificationDeliveryMode.IMMEDIATE;
    private boolean enabled = true;
    private String destination;
    private String locale = "en";
    @Column(name = "notificationTimeZone")
    private String timeZone = "UTC";
    private LocalTime summaryLocalTime = LocalTime.of(8, 0);
    private Integer summaryDayOfWeek = 1;

    public HealthNotificationPolicy getHealthNotificationPolicy() { return healthNotificationPolicy; }
    public <T extends HealthNotificationChannelPreference> T setHealthNotificationPolicy(HealthNotificationPolicy healthNotificationPolicy) { this.healthNotificationPolicy = healthNotificationPolicy; return (T) this; }
    public HealthNotificationChannel getChannel() { return channel; }
    public <T extends HealthNotificationChannelPreference> T setChannel(HealthNotificationChannel channel) { this.channel = channel; return (T) this; }
    public HealthNotificationDeliveryMode getDeliveryMode() { return deliveryMode; }
    public <T extends HealthNotificationChannelPreference> T setDeliveryMode(HealthNotificationDeliveryMode deliveryMode) { this.deliveryMode = deliveryMode; return (T) this; }
    public boolean isEnabled() { return enabled; }
    public <T extends HealthNotificationChannelPreference> T setEnabled(boolean enabled) { this.enabled = enabled; return (T) this; }
    public String getDestination() { return destination; }
    public <T extends HealthNotificationChannelPreference> T setDestination(String destination) { this.destination = destination; return (T) this; }
    public String getLocale() { return locale; }
    public <T extends HealthNotificationChannelPreference> T setLocale(String locale) { this.locale = locale; return (T) this; }
    public String getTimeZone() { return timeZone; }
    public <T extends HealthNotificationChannelPreference> T setTimeZone(String timeZone) { this.timeZone = timeZone; return (T) this; }
    public LocalTime getSummaryLocalTime() { return summaryLocalTime; }
    public <T extends HealthNotificationChannelPreference> T setSummaryLocalTime(LocalTime summaryLocalTime) { this.summaryLocalTime = summaryLocalTime; return (T) this; }
    public Integer getSummaryDayOfWeek() { return summaryDayOfWeek; }
    public <T extends HealthNotificationChannelPreference> T setSummaryDayOfWeek(Integer summaryDayOfWeek) { this.summaryDayOfWeek = summaryDayOfWeek; return (T) this; }
}
