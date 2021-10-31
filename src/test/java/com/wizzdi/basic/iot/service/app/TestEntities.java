package com.wizzdi.basic.iot.service.app;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.wizzdi.basic.iot.client.BasicIOTClient;
import com.wizzdi.basic.iot.client.IOTMessageSubscriber;
import com.wizzdi.basic.iot.client.RegisterGateway;
import com.wizzdi.basic.iot.service.service.BasicIOTLogic;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import io.nats.client.*;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.integration.annotation.ServiceActivator;
import org.springframework.integration.channel.DirectChannel;
import org.springframework.integration.core.MessageProducer;
import org.springframework.integration.dsl.IntegrationFlow;
import org.springframework.integration.dsl.IntegrationFlows;
import org.springframework.integration.mqtt.inbound.MqttPahoMessageDrivenChannelAdapter;
import org.springframework.integration.mqtt.outbound.MqttPahoMessageHandler;
import org.springframework.integration.mqtt.support.DefaultPahoMessageConverter;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.MessageHandler;
import org.springframework.messaging.MessagingException;
import org.springframework.messaging.support.GenericMessage;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.security.GeneralSecurityException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.SecureRandom;
import java.util.*;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.stream.Collectors;

import static java.lang.String.format;

@Configuration
public class TestEntities {
    private static final Logger logger= LoggerFactory.getLogger(TestEntities.class);
    public static final String IOT_MESSAGES_SUBJECT = "IOT_MESSAGES_SUBJECT";

    @Value("${basic.iot.nats.url:nats://localhost:5222}")
    private String natsUrl;
    @Value("${basic.iot.nats.jwt:test}")
    private String credentials;

    private static final Queue<MessageHandler> messageHandlers=new LinkedBlockingQueue<>();
    @Bean
    public MqttPahoMessageHandler mqttPahoMessageHandler(){
        MqttPahoMessageHandler someMqttClient = new MqttPahoMessageHandler("tcp://localhost:1883", "someMqttClient");
        someMqttClient.setDefaultQos(1);
        return someMqttClient;
    }

    @Bean
    public IntegrationFlow mqttInbound(MqttPahoMessageDrivenChannelAdapter mqttPahoMessageDrivenChannelAdapter) {
        return IntegrationFlows.from(
                        mqttPahoMessageDrivenChannelAdapter)
                .handle(f->messageHandlers.forEach(e->e.handleMessage(f)))
                .get();
    }

    @Bean
    public MqttPahoMessageDrivenChannelAdapter mqttPahoMessageDrivenChannelAdapter() {
        MqttPahoMessageDrivenChannelAdapter messageProducer = new MqttPahoMessageDrivenChannelAdapter("tcp://localhost:1883",
                "testClient", IOT_MESSAGES_SUBJECT);
        messageProducer.setQos(1);
        return messageProducer;
    }

    @Bean
    public IntegrationFlow mqttOutboundFlow(MqttPahoMessageHandler mqttPahoMessageHandler) {
        return f -> f.handle(mqttPahoMessageHandler);
    }





    @Bean
    public Connection testConnection() throws IOException, InterruptedException {
        return Nats.connect(new Options.Builder().server(natsUrl).noEcho().build());
    }
    @Bean
    public BasicIOTClient testBasicIOTClient(Connection testConnection, ObjectProvider<IOTMessageSubscriber> iotMessageSubscribers, ObjectMapper objectMapper) throws IOException, InterruptedException {
        return new BasicIOTClient(testConnection,objectMapper,Collections.emptyList());
    }

    @Bean
    public String jsonSchema() throws IOException {
        return IOUtils.resourceToString("light.schema.json", StandardCharsets.UTF_8,TestEntities.class.getClassLoader());
    }

    public static void addHandler(MessageHandler handler){
        messageHandlers.add(handler);
    }
    public static void removeHandler(MessageHandler handler){
        messageHandlers.remove(handler);
    }


}
