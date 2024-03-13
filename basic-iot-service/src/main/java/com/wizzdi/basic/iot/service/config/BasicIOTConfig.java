package com.wizzdi.basic.iot.service.config;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.wizzdi.basic.iot.client.*;
import com.wizzdi.basic.iot.service.response.ServerIntegrationFlowHolder;
import com.wizzdi.basic.iot.service.service.PublicKeyService;
import com.wizzdi.basic.iot.service.utils.KeyUtils;
import com.wizzdi.flexicore.boot.base.interfaces.Plugin;
import io.micrometer.core.instrument.Counter;
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
import org.springframework.context.annotation.Primary;
import org.springframework.core.task.TaskExecutor;
import org.springframework.core.task.support.TaskExecutorAdapter;
import org.springframework.integration.channel.PublishSubscribeChannel;
import org.springframework.integration.channel.QueueChannel;
import org.springframework.integration.config.EnableIntegration;
import org.springframework.integration.dsl.IntegrationFlow;
import org.springframework.integration.dsl.MessageChannels;
import org.springframework.integration.dsl.StandardIntegrationFlow;
import org.springframework.integration.mqtt.core.DefaultMqttPahoClientFactory;
import org.springframework.integration.mqtt.core.MqttPahoClientFactory;
import org.springframework.integration.mqtt.inbound.MqttPahoMessageDrivenChannelAdapter;
import org.springframework.integration.mqtt.outbound.MqttPahoMessageHandler;
import org.springframework.messaging.Message;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.spec.InvalidKeySpecException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;

@Extension
@Configuration
@EnableScheduling
@EnableIntegration
@EnableTransactionManagement(proxyTargetClass = true)
@EnableAsync(proxyTargetClass = true)
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
    @Value("${basic.iot.mqtt.maxInflight:300}")
    private int maxInFlight;
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
    private PublicKeyService publicKeyService;
    @Value("${basic.iot.mqtt.jdbcRatio:0.6666}")
    private float mqttJdbcRatio;
    @Autowired
    private MeterRegistry meterRegistry;



    @Value("${spring.datasource.hikari.maximum-pool-size}")
    private int maximumPoolSize;

    @Bean
    @Qualifier("virtualThreadsLogicSemaphore")
    public Semaphore virtualThreadsLogicSemaphore(){
        return new Semaphore((int) (maximumPoolSize*mqttJdbcRatio));
    }



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
        options.setMaxInflight(maxInFlight);
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
    public QueueChannel bufferChannel() {
        return new QueueChannel();
    }

    @Bean
    @Primary
    public TaskExecutor taskExecutor() {

        return new TaskExecutorAdapter(Executors.newCachedThreadPool());

    }





    @Bean
    public PrivateKey privateKey() throws NoSuchAlgorithmException, InvalidKeySpecException, IOException {

        return KeyUtils.readPrivateKey(keyPath);
    }




    @Bean
    public PublicKeyProvider publicKeyProvider() {
        logger.info("publicKeyProvider");

        return f ->  publicKeyService.getPublicKeyForGateway(f);
    }





    @Bean
    public BasicIOTClient basicIOTClient(PrivateKey privateKey, PublicKeyProvider publicKeyProvider, ObjectProvider<IOTMessageSubscriber> iotMessageSubscribers) throws IOException, InterruptedException {
        logger.info("basicIOTClient");

        if (mqttURLs == null || mqttURLs.length == 0) {
            logger.warn("mqtt server will not start as basic.iot.mqtt.url is empty");
            return null;
        }
        ObjectMapper objectMapper = new ObjectMapper()
                .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
                .registerModule(new JavaTimeModule())
                .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

        return new BasicIOTClient(iotId, privateKey, objectMapper, iotMessageSubscribers,false,disableVerification,f->incOutgoingMessage(f.getClass().getSimpleName()))
                .setPublicKeyProvider(publicKeyProvider);
    }

    private static final Map<String, Timer> timerMap = new ConcurrentHashMap<>();

    private static final Map<String,Counter> outgoingCounterMap=new ConcurrentHashMap<>();


    private void incOutgoingMessage( String type) {
        Counter counter = outgoingCounterMap.computeIfAbsent(type, e -> Counter.builder("outgoing.message.count")
                .tag("type", type)
                .register(meterRegistry));
        counter.increment();
    }


    private void timeMessage(TimerType timerType, String type, long time) {
        String timerName="message.%s.time".formatted(timerType.getTimerLogicalPart());
        String timerNameWithType="%s.%s".formatted(timerName,type);
        Timer timer = timerMap.computeIfAbsent(timerNameWithType, e -> Timer.builder(timerName)
                .tag("type", e)
                .register(meterRegistry));
        timer.record(time, TimeUnit.NANOSECONDS);
    }

    @Bean
    public Timer checkConnectivityTimer(){
        return Timer.builder("iot.connectivity.timer")
                .register(meterRegistry);
    }


    @Bean
    public ServerIntegrationFlowHolder serverInputIntegrationFlowHolder(BasicIOTClient basicIOTClient, MqttPahoClientFactory mqttServerFactory, @Qualifier("mqttOutboundFlow") IntegrationFlow mqttOutboundFlow, Semaphore virtualThreadsLogicSemaphore, MeterRegistry meterRegistry) {
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
                .channel(MessageChannels.executor("mqtt-in-executor",new TaskExecutorAdapter(Executors.newVirtualThreadPerTaskExecutor())))
                .handle(message->{
                    long start = System.nanoTime();
                   // logger.info("handling mqtt id "+message.getHeaders().getId() +" with id "+message.getPayload());
                    String type="unknown";
                    IOTMessage iotMessage =null;
                  try {
                      iotMessage = basicIOTClient.parseMessage(message, IOTMessage.class);
                      type = iotMessage != null ? iotMessage.getClass().getSimpleName() : type;
                      timeMessage(TimerType.PARSING, type, System.nanoTime() - start);
                  }
                  catch (Throwable e){
                      logger.error("failed parsing message",e);
                      return;
                  }
                    try {
                        long waitingStart=System.nanoTime();

                        virtualThreadsLogicSemaphore.acquire();
                        timeMessage(TimerType.WAITING,type, System.nanoTime() - waitingStart);

                        long verifyingStart=System.nanoTime();

                        boolean verified = basicIOTClient.verifyMessage(iotMessage);
                        if (!verified) {
                            iotMessage = basicIOTClient.getBadMessage(message, (String) message.getPayload(), "signature verification failed for message " + iotMessage.getId() + " with signature " + iotMessage.getSignature());
                        }
                        timeMessage(TimerType.VERIFYING,type, System.nanoTime() - verifyingStart);


                        long processingStart=System.nanoTime();
                        basicIOTClient.callSubscribersAndHandlers(iotMessage);
                        timeMessage(TimerType.PROCESSING,type, System.nanoTime() - processingStart);


                    }
                    catch (Throwable e){
                        logger.error("error handling message",e);
                    }
                    finally {
                        virtualThreadsLogicSemaphore.release();
                        timeMessage(TimerType.TOTAL,type, System.nanoTime() - start);

                    }

                })
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
