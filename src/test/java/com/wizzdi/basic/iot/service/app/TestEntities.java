package com.wizzdi.basic.iot.service.app;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.wizzdi.basic.iot.client.BasicIOTClient;
import com.wizzdi.basic.iot.client.BasicIOTConnection;
import com.wizzdi.basic.iot.service.utils.KeyUtils;
import org.apache.commons.io.IOUtils;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.integration.dsl.IntegrationFlow;
import org.springframework.integration.dsl.IntegrationFlows;
import org.springframework.integration.mqtt.core.DefaultMqttPahoClientFactory;
import org.springframework.integration.mqtt.core.MqttPahoClientFactory;
import org.springframework.integration.mqtt.inbound.MqttPahoMessageDrivenChannelAdapter;
import org.springframework.integration.mqtt.outbound.MqttPahoMessageHandler;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.spec.InvalidKeySpecException;
import java.util.Collections;

@Configuration
public class TestEntities {
    private static final Logger logger = LoggerFactory.getLogger(TestEntities.class);
    public static final String IOT_MESSAGES_SUBJECT = "IOT_MESSAGES_SUBJECT";

    @Value("${basic.iot.test.keyPath}")
    private String keyPath;
    @Value("${basic.iot.test.publicKeyPath}")
    private String publicKeyPath;
    @Value("${basic.iot.test.id:iot-client}")
    private String clientId;



    @Bean
    public MqttPahoClientFactory mqttClientFactory() {
        System.setProperty("javax.net.ssl.keyStore", "C:\\Users\\Asaf\\certs\\keystore.jks");
        System.setProperty("javax.net.ssl.keyStorePassword", "asafasaf");
        System.setProperty("javax.net.ssl.keyStoreType", "JKS");
        DefaultMqttPahoClientFactory factory = new DefaultMqttPahoClientFactory();
        MqttConnectOptions options = new MqttConnectOptions();
        options.setUserName("asaf");
        options.setPassword("asaf".toCharArray());

        options.setServerURIs(new String[]{"ssl://localhost:8883"});
        factory.setConnectionOptions(options);
        return factory;
    }

    @Bean
    public PrivateKey clientPrivateKey() throws NoSuchAlgorithmException, InvalidKeySpecException, IOException {
       return KeyUtils.readPrivateKey(keyPath);

    }

    @Bean
    public PublicKey clientPublicKey() throws NoSuchAlgorithmException, InvalidKeySpecException, IOException {
        return KeyUtils.readPublicKey(publicKeyPath);

    }

    @Bean
    public MqttPahoMessageHandler mqttPahoMessageHandlerClient(MqttPahoClientFactory mqttClientFactory) {
        MqttPahoMessageHandler someMqttClient = new MqttPahoMessageHandler("iot-client-out", mqttClientFactory);
        someMqttClient.setDefaultQos(1);
        return someMqttClient;
    }

    @Bean
    public MqttPahoMessageDrivenChannelAdapter mqttPahoMessageDrivenChannelAdapterClient(MqttPahoClientFactory mqttClientFactory) {
        MqttPahoMessageDrivenChannelAdapter messageProducer = new MqttPahoMessageDrivenChannelAdapter("iot-client-in", mqttClientFactory,BasicIOTClient.IOT_MESSAGES_SUBJECT);
        messageProducer.setQos(1);
        return messageProducer;
    }


    @Bean
    public BasicIOTClient testBasicIOTClient(PrivateKey clientPrivateKey, ObjectMapper objectMapper) {
        return new BasicIOTClient(clientId,clientPrivateKey, objectMapper, Collections.emptyList());
    }
    @Bean
    public IntegrationFlow clientInputIntegrationFlow(BasicIOTClient testBasicIOTClient, MqttPahoMessageDrivenChannelAdapter mqttPahoMessageDrivenChannelAdapterClient) {
        return IntegrationFlows.from(
                        mqttPahoMessageDrivenChannelAdapterClient)
                .handle(testBasicIOTClient)
                .get();
    }


    @Bean
    public IntegrationFlow clientOutputIntegrationFlow(MqttPahoMessageHandler mqttPahoMessageHandlerClient) {
        return f -> f.handle(mqttPahoMessageHandlerClient);
    }
    @Bean
    public BasicIOTConnection testBasicIOTConnection(BasicIOTClient testBasicIOTClient,IntegrationFlow clientOutputIntegrationFlow,IntegrationFlow clientInputIntegrationFlow,MqttPahoMessageDrivenChannelAdapter mqttPahoMessageDrivenChannelAdapterClient) {
        return testBasicIOTClient.open(clientInputIntegrationFlow,clientOutputIntegrationFlow,mqttPahoMessageDrivenChannelAdapterClient);
    }


    @Bean
    public String jsonSchema() throws IOException {
        return IOUtils.resourceToString("light.schema.json", StandardCharsets.UTF_8, TestEntities.class.getClassLoader());
    }


}
