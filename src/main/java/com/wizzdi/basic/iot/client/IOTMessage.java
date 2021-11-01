package com.wizzdi.basic.iot.client;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import org.springframework.messaging.Message;

import java.time.OffsetDateTime;

@JsonTypeInfo(use = JsonTypeInfo.Id.CLASS, property = "messageType", visible = true)
public class IOTMessage {

    private String id;
    private OffsetDateTime sentAt;
    @JsonIgnore
    private Message message;
    private byte[] signature;
    private String gatewayId;

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

    @JsonIgnore
    public Message getMessage() {
        return message;
    }

    public <T extends IOTMessage> T setMessage(Message message) {
        this.message = message;
        return (T) this;
    }

    public byte[] getSignature() {
        return signature;
    }

    public <T extends IOTMessage> T setSignature(byte[] signature) {
        this.signature = signature;
        return (T) this;
    }

    public String getGatewayId() {
        return gatewayId;
    }

    public <T extends IOTMessage> T setGatewayId(String gatewayId) {
        this.gatewayId = gatewayId;
        return (T) this;
    }

    @JsonIgnore
    public boolean isRequireAuthentication(){
        return true;
    }
}
