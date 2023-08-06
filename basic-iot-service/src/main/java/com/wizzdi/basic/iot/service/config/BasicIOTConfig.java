package com.wizzdi.basic.iot.service.config;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.wizzdi.basic.iot.client.*;
import com.wizzdi.basic.iot.model.Gateway;
import com.wizzdi.basic.iot.service.request.GatewayFilter;
import com.wizzdi.basic.iot.service.response.ServerIntegrationFlowHolder;
import com.wizzdi.basic.iot.service.service.GatewayService;
import com.wizzdi.basic.iot.service.utils.KeyUtils;
import com.wizzdi.flexicore.boot.base.interfaces.Plugin;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Timer;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.pf4j.Extension;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.task.TaskExecutor;
import org.springframework.core.task.support.TaskExecutorAdapter;
import org.springframework.integration.channel.PublishSubscribeChannel;
import org.springframework.integration.config.EnableIntegration;
import org.springframework.integration.dsl.*;
import org.springframework.integration.mqtt.core.DefaultMqttPahoClientFactory;
import org.springframework.integration.mqtt.core.MqttPahoClientFactory;
import org.springframework.integration.mqtt.inbound.MqttPahoMessageDrivenChannelAdapter;
import org.springframework.integration.mqtt.outbound.MqttPahoMessageHandler;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.spec.InvalidKeySpecException;
import java.util.Collections;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Extension
@Configuration
@EnableScheduling
@EnableIntegration
@EnableTransactionManagement(proxyTargetClass = true)
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
    @Value("${basic.iot.mqtt.defaultRetained:false}")
    private boolean defaultRetained;
    @Value("${basic.iot.mqtt.disableVerification:false}")
    private boolean disableVerification;

    @Value("${basic.iot.mqtt.url:ssl://localhost:8883}")
    private String[] mqttURLs;
    @Autowired
    private GatewayService gatewayService;


    @Bean
    public MqttPahoClientFactory mqttServerFactory() {
        logger.info("mqttServerFactory");

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
        options.setCleanSession(true);
        options.setConnectionTimeout(30);
        options.setKeepAliveInterval(60);
        options.setAutomaticReconnect(true);
        options.setServerURIs(mqttURLs);
        factory.setConnectionOptions(options);
        return factory;
    }

    @Bean
    public PublishSubscribeChannel errorChannel() {
        PublishSubscribeChannel publishSubscribeChannel = new PublishSubscribeChannel(false);
        publishSubscribeChannel.setErrorHandler(e->logger.error("mqtt error handler",e));
        publishSubscribeChannel.setIgnoreFailures(true);
        return publishSubscribeChannel;
    }
    @Bean
    public TaskScheduler taskScheduler() {
        logger.info("taskScheduler");
        ThreadPoolTaskScheduler threadPoolTaskScheduler
                = new ThreadPoolTaskScheduler();
        threadPoolTaskScheduler.setPoolSize(3);
        threadPoolTaskScheduler.setThreadNamePrefix(
                "connectivity-scheduler");
        return threadPoolTaskScheduler;
    }

    @Bean
    @Qualifier("mqttTaskExecutor")
    public TaskExecutor mqttTaskExecutor() {

        return new TaskExecutorAdapter(Executors.newVirtualThreadPerTaskExecutor());

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
        logger.info("publicKeyProvider");

        return f -> gatewayService.listAllGateways(null, new GatewayFilter().setRemoteIds(Collections.singleton(f))).stream().findFirst()
                .map(e -> getPublicKeyResponse(e)).orElse(null);
    }

    private PublicKeyResponse getPublicKeyResponse(Gateway e) {
        if(e.isNoSignatureCapabilities()){
            return new PublicKeyResponse(null,false);
        }
        return new PublicKeyResponse(readPublicKey(e.getPublicKey()),true);
    }

    @Bean
    public BasicIOTClient basicIOTClient(PrivateKey privateKey, PublicKeyProvider publicKeyProvider, ObjectProvider<IOTMessageSubscriber> iotMessageSubscribers,TimingCallback timingCallback) throws IOException, InterruptedException {
        logger.info("basicIOTClient");

        if (mqttURLs == null || mqttURLs.length == 0) {
            logger.warn("mqtt server will not start as basic.iot.mqtt.url is empty");
            return null;
        }
        ObjectMapper objectMapper = new ObjectMapper()
                .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
                .registerModule(new JavaTimeModule())
                .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

        return new BasicIOTClient(iotId, privateKey, objectMapper, iotMessageSubscribers,false,timingCallback,disableVerification)
                .setPublicKeyProvider(publicKeyProvider);
    }
    @Bean
    public TimingCallback timingCallback(MeterRegistry meterRegistry) {
        Map<String, Timer> timerMap=new ConcurrentHashMap<>();
        return (type, time) -> {
            Timer timer = timerMap.computeIfAbsent(type, e -> Timer.builder("message.processing.time")
                    .tag("type", e)
                    .register(meterRegistry));
            timer.record(time, TimeUnit.NANOSECONDS);
        };
        }


    @Bean
    @Qualifier("mqttInputChannel")
    public ExecutorChannelSpec mqttInputChannel(@Qualifier("mqttTaskExecutor") TaskExecutor mqttTaskExecutor) {
        return MessageChannels.executor(mqttTaskExecutor);
    }

    @Bean
    public ServerIntegrationFlowHolder serverInputIntegrationFlowHolder(BasicIOTClient basicIOTClient, MqttPahoClientFactory mqttServerFactory, @Qualifier("mqttOutboundFlow") IntegrationFlow mqttOutboundFlow,@Qualifier("mqttInputChannel") ExecutorChannelSpec mqttInputChannel) {
        logger.info("serverInputIntegrationFlow");

        if (mqttURLs == null || mqttURLs.length == 0) {
            logger.warn("mqtt server will not start as basic.iot.mqtt.url is empty");
            return null;
        }
        logger.info("mqttPahoMessageDrivenChannelAdapterServer");

        if (mqttURLs == null || mqttURLs.length == 0) {
            logger.warn("mqtt server will not start as basic.iot.mqtt.url is empty");
            return null;
        }
        MqttPahoMessageDrivenChannelAdapter mqttPahoMessageDrivenChannelAdapter = new MqttPahoMessageDrivenChannelAdapter(iotId+"-in", mqttServerFactory, BasicIOTClient.MAIN_TOPIC_PATH_OUT);
        mqttPahoMessageDrivenChannelAdapter.setQos(1);
        StandardIntegrationFlow standardIntegrationFlow = IntegrationFlow.from(mqttPahoMessageDrivenChannelAdapter)
                .channel(mqttInputChannel)
                .handle(basicIOTClient)
                .get();
        BasicIOTConnection basicIOTConnection = basicIOTClient.open(standardIntegrationFlow, mqttOutboundFlow, mqttPahoMessageDrivenChannelAdapter);

        return new ServerIntegrationFlowHolder(standardIntegrationFlow,basicIOTConnection);
    }

    @Bean
    @Qualifier("mqttInbound")
    public IntegrationFlow mqttInbound(ServerIntegrationFlowHolder serverIntegrationFlowHolder){
        return serverIntegrationFlowHolder.getIntegrationFlow();
    }


    @Bean
    @Qualifier("mqttOutbounndFlow")
    public IntegrationFlow mqttOutboundFlow(MqttPahoClientFactory mqttServerFactory) {
        logger.info("serverOutputIntegrationFlow");
        if (mqttURLs == null || mqttURLs.length == 0) {
            logger.warn("mqtt server will not start as basic.iot.mqtt.url is empty");
            return null;
        }

        MqttPahoMessageHandler someMqttClient = new MqttPahoMessageHandler(iotId+"-out", mqttServerFactory);
        someMqttClient.setDefaultQos(1);
        someMqttClient.setDefaultRetained(defaultRetained);
        return f -> f.handle(someMqttClient);

    }

    @Bean
    public BasicIOTConnection basicIOTConnection( ServerIntegrationFlowHolder serverIntegrationFlowHolder) {
        logger.info("basicIOTConnection");

        return serverIntegrationFlowHolder.getBasicIOTConnection();

    }


}
