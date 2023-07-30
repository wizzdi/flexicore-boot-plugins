package com.wizzdi.basic.iot.client;

import org.springframework.integration.dsl.IntegrationFlow;
import org.springframework.integration.mqtt.inbound.MqttPahoMessageDrivenChannelAdapter;

public class BasicIOTConnection {
    private final IntegrationFlow inbound;
    private final IntegrationFlow outbound;
    private final MqttPahoMessageDrivenChannelAdapter mqttPahoMessageDrivenChannelAdapter;

    public BasicIOTConnection(IntegrationFlow inbound, IntegrationFlow outbound, MqttPahoMessageDrivenChannelAdapter mqttPahoMessageDrivenChannelAdapter) {
        this.inbound = inbound;
        this.outbound = outbound;
        this.mqttPahoMessageDrivenChannelAdapter = mqttPahoMessageDrivenChannelAdapter;
    }

    public IntegrationFlow getInbound() {
        return inbound;
    }

    public IntegrationFlow getOutbound() {
        return outbound;
    }

    public MqttPahoMessageDrivenChannelAdapter getMqttPahoMessageDrivenChannelAdapter() {
        return mqttPahoMessageDrivenChannelAdapter;
    }
}
