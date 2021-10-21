package com.wizzdi.basic.iot.client;

import com.fasterxml.jackson.annotation.JsonTypeInfo;

import java.time.OffsetDateTime;

@JsonTypeInfo(use = JsonTypeInfo.Id.CLASS, property = "messageType", visible = true)
public class IOTMessage {

    private String id;
    private OffsetDateTime sentAt;

    public String getId() {
        return id;
    }

    public <T extends IOTMessage> T setId(String id) {
        this.id = id;
        return (T) this;
    }

    public OffsetDateTime getSentAt() {
        return sentAt;
    }

    public <T extends IOTMessage> T setSentAt(OffsetDateTime sentAt) {
        this.sentAt = sentAt;
        return (T) this;
    }
}
