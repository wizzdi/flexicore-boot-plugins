package com.wizzdi.basic.iot.service.request;

import com.wizzdi.basic.iot.model.HealthNotificationChannel;
import com.wizzdi.basic.iot.model.HealthNotificationDeliveryMode;
import com.wizzdi.flexicore.security.request.BasicCreate;

import java.time.LocalTime;

public class HealthNotificationChannelPreferenceCreate extends BasicCreate {
    private String id;
    private HealthNotificationChannel channel;
    private HealthNotificationDeliveryMode deliveryMode;
    private Boolean enabled;
    private String destination;
    private String locale;
    private String timeZone;
    private LocalTime summaryLocalTime;
    private Integer summaryDayOfWeek;

    public String getId() { return id; }
    public <T extends HealthNotificationChannelPreferenceCreate> T setId(String id) { this.id = id; return (T) this; }
    public HealthNotificationChannel getChannel() { return channel; }
    public <T extends HealthNotificationChannelPreferenceCreate> T setChannel(HealthNotificationChannel channel) { this.channel = channel; return (T) this; }
    public HealthNotificationDeliveryMode getDeliveryMode() { return deliveryMode; }
    public <T extends HealthNotificationChannelPreferenceCreate> T setDeliveryMode(HealthNotificationDeliveryMode deliveryMode) { this.deliveryMode = deliveryMode; return (T) this; }
    public Boolean getEnabled() { return enabled; }
    public <T extends HealthNotificationChannelPreferenceCreate> T setEnabled(Boolean enabled) { this.enabled = enabled; return (T) this; }
    public String getDestination() { return destination; }
    public <T extends HealthNotificationChannelPreferenceCreate> T setDestination(String destination) { this.destination = destination; return (T) this; }
    public String getLocale() { return locale; }
    public <T extends HealthNotificationChannelPreferenceCreate> T setLocale(String locale) { this.locale = locale; return (T) this; }
    public String getTimeZone() { return timeZone; }
    public <T extends HealthNotificationChannelPreferenceCreate> T setTimeZone(String timeZone) { this.timeZone = timeZone; return (T) this; }
    public LocalTime getSummaryLocalTime() { return summaryLocalTime; }
    public <T extends HealthNotificationChannelPreferenceCreate> T setSummaryLocalTime(LocalTime summaryLocalTime) { this.summaryLocalTime = summaryLocalTime; return (T) this; }
    public Integer getSummaryDayOfWeek() { return summaryDayOfWeek; }
    public <T extends HealthNotificationChannelPreferenceCreate> T setSummaryDayOfWeek(Integer summaryDayOfWeek) { this.summaryDayOfWeek = summaryDayOfWeek; return (T) this; }
}
