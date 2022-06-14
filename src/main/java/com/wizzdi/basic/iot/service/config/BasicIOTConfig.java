package com.wizzdi.basic.iot.service.config;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
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
import org.springframework.integration.config.EnableIntegration;
import org.springframework.integration.dsl.IntegrationFlow;
import org.springframework.integration.dsl.IntegrationFlows;
import org.springframework.integration.mqtt.core.DefaultMqttPahoClientFactory;
import org.springframework.integration.mqtt.core.MqttPahoClientFactory;
import org.springframework.integration.mqtt.inbound.MqttPahoMessageDrivenChannelAdapter;
import org.springframework.integration.mqtt.outbound.MqttPahoMessageHandler;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.spec.InvalidKeySpecException;
import java.util.Collections;
import java.util.stream.Collectors;

@Extension
@Configuration
@EnableScheduling
@EnableIntegration
public class BasicIOTConfig implements Plugin {

    private static final Logger logger = LoggerFactory.getLogger(BasicIOTClient.class);

    @Value("${basic.iot.id:iot-server}")
    private String iotId;
    @Value("${basic.iot.keyPath}")
    private String keyPath;
    @Value("${basic.iot.mqtt.username:#{null}}")
    private String username;
    @Value("${basic.iot.mqtt.password:#{null}}")
    private char[] password;
    @Value("${basic.iot.mqtt.keyStore:#{null}}")
    private String keystore;
    @Value("${basic.iot.mqtt.keyStorePassword:#{null}}")
    private String keystorePassword;
    @Value("${basic.iot.mqtt.keyStoreType:#{null}}")
    private String keyStoreType;

    @Value("${basic.iot.mqtt.url:ssl://localhost:8883}")
    private String[] mqttURLs;
    @Autowired
    private GatewayService gatewayService;

    @Bean
    public MqttPahoClientFactory mqttServerFactory() {
        if (mqttURLs == null || mqttURLs.length == 0) {
            logger.warn("mqtt server will not start as basic.iot.mqtt.url is empty");
            return null;
        }
        if (keystore != null) {
            System.setProperty("javax.net.ssl.keyStore", keystore);

        }
        if (keystorePassword != null) {
            System.setProperty("javax.net.ssl.keyStorePassword", keystorePassword);

        }
        if (keyStoreType != null) {
            System.setProperty("javax.net.ssl.keyStoreType", keyStoreType);

        }
        DefaultMqttPahoClientFactory factory = new DefaultMqttPahoClientFactory();
        MqttConnectOptions options = new MqttConnectOptions();
        if (username != null) {
            options.setUserName(username);
        }
        if (password != null) {
            options.setPassword(password);
        }
        options.setServerURIs(mqttURLs);
        factory.setConnectionOptions(options);
        return factory;
    }

    @Bean
    public ThreadPoolTaskScheduler taskScheduler() {
        ThreadPoolTaskScheduler threadPoolTaskScheduler
                = new ThreadPoolTaskScheduler();
        threadPoolTaskScheduler.setPoolSize(5);
        threadPoolTaskScheduler.setThreadNamePrefix(
                "mqtt-task-scheduler");
        return threadPoolTaskScheduler;
    }

    @Bean
    public MqttPahoMessageHandler mqttPahoMessageHandlerServer(MqttPahoClientFactory mqttServerFactory) {
        if (mqttURLs == null || mqttURLs.length == 0) {
            logger.warn("mqtt server will not start as basic.iot.mqtt.url is empty");
            return null;
        }
        MqttPahoMessageHandler someMqttClient = new MqttPahoMessageHandler("iot-server-out", mqttServerFactory);
        someMqttClient.setDefaultQos(1);
        return someMqttClient;
    }

    @Bean
    public MqttPahoMessageDrivenChannelAdapter mqttPahoMessageDrivenChannelAdapterServer(MqttPahoClientFactory mqttServerFactory) {
        if (mqttURLs == null || mqttURLs.length == 0) {
            logger.warn("mqtt server will not start as basic.iot.mqtt.url is empty");
            return null;
        }
        MqttPahoMessageDrivenChannelAdapter messageProducer = new MqttPahoMessageDrivenChannelAdapter("iot-server-in", mqttServerFactory, BasicIOTClient.MAIN_TOPIC_PATH_OUT);
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
    public BasicIOTClient basicIOTClient(PrivateKey privateKey, PublicKeyProvider publicKeyProvider, ObjectProvider<IOTMessageSubscriber> iotMessageSubscribers) throws IOException, InterruptedException {
        if (mqttURLs == null || mqttURLs.length == 0) {
            logger.warn("mqtt server will not start as basic.iot.mqtt.url is empty");
            return null;
        }
        ObjectMapper objectMapper = new ObjectMapper()
                .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
                .registerModule(new JavaTimeModule())
                .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

        return new BasicIOTClient(iotId, privateKey, objectMapper, iotMessageSubscribers.stream().collect(Collectors.toList()))
                .setPublicKeyProvider(publicKeyProvider);
    }

    @Bean
    public IntegrationFlow serverInputIntegrationFlow(BasicIOTClient basicIOTClient, MqttPahoMessageDrivenChannelAdapter mqttPahoMessageDrivenChannelAdapterServer) {
        if (mqttURLs == null || mqttURLs.length == 0) {
            logger.warn("mqtt server will not start as basic.iot.mqtt.url is empty");
            return null;
        }
        return IntegrationFlows.from(
                        mqttPahoMessageDrivenChannelAdapterServer)
                .handle(basicIOTClient)
                .get();
    }


    @Bean
    public IntegrationFlow serverOutputIntegrationFlow(MqttPahoMessageHandler mqttPahoMessageHandlerServer) {
        if (mqttURLs == null || mqttURLs.length == 0) {
            logger.warn("mqtt server will not start as basic.iot.mqtt.url is empty");
            return null;
        }
        return f -> f.handle(mqttPahoMessageHandlerServer);
    }

    @Bean
    public BasicIOTConnection basicIOTConnection(BasicIOTClient basicIOTClient, IntegrationFlow serverOutputIntegrationFlow, IntegrationFlow serverInputIntegrationFlow, MqttPahoMessageDrivenChannelAdapter mqttPahoMessageDrivenChannelAdapterServer) {
        if (mqttURLs == null || mqttURLs.length == 0) {
            logger.warn("mqtt server will not start as basic.iot.mqtt.url is empty");
            return null;
        }
        return basicIOTClient.open(serverInputIntegrationFlow, serverOutputIntegrationFlow, mqttPahoMessageDrivenChannelAdapterServer);
    }


}
