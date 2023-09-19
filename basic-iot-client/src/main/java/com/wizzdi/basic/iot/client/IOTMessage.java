package com.wizzdi.basic.iot.client;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import org.springframework.messaging.Message;

import java.time.OffsetDateTime;

@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, property = "messageType", visible = true)
@JsonSubTypes(
        {
                @JsonSubTypes.Type(value = BadMessage.class,name = "com.wizzdi.basic.iot.client.BadMessage"),
                @JsonSubTypes.Type(value = BadMessageReceived.class,name = "com.wizzdi.basic.iot.client.BadMessageReceived"),
                @JsonSubTypes.Type(value = ChangeState.class,name = "com.wizzdi.basic.iot.client.ChangeState"),
                @JsonSubTypes.Type(value = ChangeStateReceived.class,name = "com.wizzdi.basic.iot.client.ChangeStateReceived"),
                @JsonSubTypes.Type(value = ConnectReceived.class,name = "com.wizzdi.basic.iot.client.ConnectReceived"),
                @JsonSubTypes.Type(value = KeepAlive.class,name = "com.wizzdi.basic.iot.client.KeepAlive"),
                @JsonSubTypes.Type(value = RegisterGateway.class,name = "com.wizzdi.basic.iot.client.RegisterGateway"),
                @JsonSubTypes.Type(value = RegisterGatewayReceived.class,name = "com.wizzdi.basic.iot.client.RegisterGatewayReceived"),
                @JsonSubTypes.Type(value = StateChanged.class,name = "com.wizzdi.basic.iot.client.StateChanged"),
                @JsonSubTypes.Type(value = StateChangedReceived.class,name = "com.wizzdi.basic.iot.client.StateChangedReceived"),
                @JsonSubTypes.Type(value = UpdateStateSchema.class,name = "com.wizzdi.basic.iot.client.UpdateStateSchema"),
                @JsonSubTypes.Type(value = UpdateStateSchemaReceived.class,name = "com.wizzdi.basic.iot.client.UpdateStateSchemaReceived"),
                @JsonSubTypes.Type(value = OTAAvailable.class,name = "com.wizzdi.basic.iot.client.OTAAvailable"),
                @JsonSubTypes.Type(value = OTAAvailableReceived.class,name = "com.wizzdi.basic.iot.client.OTAAvailableReceived"),
                @JsonSubTypes.Type(value = SetStateSchema.class,name = "com.wizzdi.basic.iot.client.SetStateSchema"),
                @JsonSubTypes.Type(value = SetStateSchemaReceived.class,name = "com.wizzdi.basic.iot.client.SetStateSchemaReceived"),
                @JsonSubTypes.Type(value = SetStateSchema.class,name = "com.wizzdi.basic.iot.client.setStateSchema")







        }
)
public class IOTMessage {

    private String id;
    private OffsetDateTime sentAt;
    @JsonIgnore
    private Message message;
    private String signature;
    private String gatewayId;

    public IOTMessage() {
    }

    public IOTMessage(IOTMessage other) {
        this.id = other.id;
        this.sentAt = other.sentAt;
        this.message = other.message;
        this.signature = other.signature;
        this.gatewayId = other.gatewayId;
    }

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

    public String getSignature() {
        return signature;
    }

    public <T extends IOTMessage> T setSignature(String signature) {
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

    @JsonIgnore
    public boolean isRetained(){
        return true;
    }


}
