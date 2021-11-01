package com.wizzdi.basic.iot.service.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.wizzdi.basic.iot.client.BasicIOTClient;
import com.wizzdi.basic.iot.client.BasicIOTConnection;
import com.wizzdi.basic.iot.client.IOTMessageSubscriber;
import com.wizzdi.basic.iot.client.PublicKeyProvider;
import com.wizzdi.basic.iot.service.request.GatewayFilter;
import com.wizzdi.basic.iot.service.service.GatewayService;
import com.wizzdi.basic.iot.service.utils.KeyUtils;
import com.wizzdi.flexicore.boot.base.interfaces.Plugin;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.pf4j.Extension;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.annotation.Autowired;
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
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.*;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Collections;
import java.util.stream.Collectors;

@Extension
@Configuration
public class BasicIOTConfig implements Plugin {

    private static final Logger logger = LoggerFactory.getLogger(BasicIOTClient.class);

    @Value("${basic.iot.id:iot-server}")
    private String iotId;
    @Value("${basic.iot.keyPath}")
    private String keyPath;
    @Autowired
    private GatewayService gatewayService;

    @Bean
    public MqttPahoClientFactory mqttServerFactory() {
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
    public MqttPahoMessageHandler mqttPahoMessageHandlerServer(MqttPahoClientFactory mqttServerFactory) {
        MqttPahoMessageHandler someMqttClient = new MqttPahoMessageHandler("iot-server-out", mqttServerFactory);
        someMqttClient.setDefaultQos(1);
        return someMqttClient;
    }

    @Bean
    public MqttPahoMessageDrivenChannelAdapter mqttPahoMessageDrivenChannelAdapterServer(MqttPahoClientFactory mqttServerFactory) {
        MqttPahoMessageDrivenChannelAdapter messageProducer = new MqttPahoMessageDrivenChannelAdapter("iot-server-in", mqttServerFactory, BasicIOTClient.IOT_MESSAGES_SUBJECT);
        messageProducer.setQos(1);
        return messageProducer;
    }

    @Bean
    public PrivateKey privateKey() throws NoSuchAlgorithmException, InvalidKeySpecException, IOException {

        return KeyUtils.readPrivateKey(keyPath);
    }

    public static PublicKey readPublicKey(String publicKeyUnwrapped) {

        try {
         return KeyUtils.readPublicKeyUnwrapped(publicKeyUnwrapped);
        } catch (Exception e) {
            logger.error("failed parsing public key", e);
            return null;
        }
    }


    @Bean
    public PublicKeyProvider publicKeyProvider() {
        return f -> gatewayService.listAllGateways(null, new GatewayFilter().setRemoteIds(Collections.singleton(f))).stream().findFirst()
                .map(e -> readPublicKey(e.getPublicKey())).orElse(null);
    }

    @Bean
    public BasicIOTClient basicIOTClient(PrivateKey privateKey, PublicKeyProvider publicKeyProvider, ObjectProvider<IOTMessageSubscriber> iotMessageSubscribers, ObjectMapper objectMapper) throws IOException, InterruptedException {
        return new BasicIOTClient(iotId, privateKey, objectMapper, iotMessageSubscribers.stream().collect(Collectors.toList()))
                .setPublicKeyProvider(publicKeyProvider);
    }

    @Bean
    public IntegrationFlow serverInputIntegrationFlow(BasicIOTClient basicIOTClient, MqttPahoMessageDrivenChannelAdapter mqttPahoMessageDrivenChannelAdapterServer) {
        return IntegrationFlows.from(
                        mqttPahoMessageDrivenChannelAdapterServer)
                .handle(basicIOTClient)
                .get();
    }


    @Bean
    public IntegrationFlow serverOutputIntegrationFlow(MqttPahoMessageHandler mqttPahoMessageHandlerServer) {
        return f -> f.handle(mqttPahoMessageHandlerServer);
    }

    @Bean
    public BasicIOTConnection basicIOTConnection(BasicIOTClient basicIOTClient, IntegrationFlow serverOutputIntegrationFlow, IntegrationFlow serverInputIntegrationFlow, MqttPahoMessageDrivenChannelAdapter mqttPahoMessageDrivenChannelAdapterServer) {
        return basicIOTClient.open(serverInputIntegrationFlow, serverOutputIntegrationFlow, mqttPahoMessageDrivenChannelAdapterServer);
    }


}
