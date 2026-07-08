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
import org.springframework.context.annotation.Lazy;
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
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import java.lang.management.ManagementFactory;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.KeyStore;
import java.security.KeyFactory;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.interfaces.RSAPrivateCrtKey;
import java.security.cert.Certificate;
import java.security.cert.CertificateFactory;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.RSAPublicKeySpec;
import java.util.Base64;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;
import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.TrustManagerFactory;
import javax.net.ssl.SSLContext;
import java.security.cert.X509Certificate;
import java.security.SecureRandom;

@Extension
@Configuration
@EnableScheduling
@EnableIntegration
@EnableTransactionManagement(proxyTargetClass = true)
@EnableAsync(proxyTargetClass = true)
public class BasicIOTConfig implements Plugin {

    private static final Logger logger = LoggerFactory.getLogger("basic-iot");
    private static final com.sun.management.OperatingSystemMXBean operatingSystemMXBean = ManagementFactory.getPlatformMXBean(com.sun.management.OperatingSystemMXBean.class);

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
    @Value("${basic.iot.mqtt.trustStore:#{null}}")
    private String trustStore;
    @Value("${basic.iot.mqtt.trustStorePassword:#{null}}")
    private String trustStorePassword;
    @Value("${basic.iot.mqtt.trustStoreType:#{null}}")
    private String trustStoreType;
    @Value("${basic.iot.mqtt.clientCertificateBase64:#{null}}")
    private String clientCertificateBase64;
    @Value("${basic.iot.mqtt.clientPrivateKeyBase64:#{null}}")
    private String clientPrivateKeyBase64;
    @Value("${basic.iot.mqtt.caCertificateBase64:#{null}}")
    private String caCertificateBase64;
    @Value("${basic.iot.mqtt.privateKeyAlgorithm:RSA}")
    private String mqttPrivateKeyAlgorithm;
    @Value("${basic.iot.mqtt.certsBaseDir:#{null}}")
    private String mqttCertsBaseDir;
    @Value("${basic.iot.mqtt.caCertificatePath:#{null}}")
    private String mqttCaCertificatePath;
    @Value("${basic.iot.mqtt.clientCertificatePath:#{null}}")
    private String mqttClientCertificatePath;
    @Value("${basic.iot.mqtt.clientKeyPath:#{null}}")
    private String mqttClientKeyPath;
    @Value("${basic.iot.mqtt.defaultRetained:false}")
    private boolean defaultRetained;
    @Value("${basic.iot.message.processing.logInterval:1000}")
    private long messageProcessingLogInterval;

    @Value("${basic.iot.mqtt.url:#{null}}")
    private String[] mqttURLs;
    @Value("${basic.iot.start.delay:60}")
    private int startDelay;
    @Autowired
    @Lazy
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

        DefaultMqttPahoClientFactory factory = new DefaultMqttPahoClientFactory();
        if (!isMqttConfigured()) {
            logger.warn("mqtt server will not start as basic.iot.mqtt.url is empty");
            return factory;
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
        if (trustStore != null) {
            System.setProperty("javax.net.ssl.trustStore", trustStore);

        }
        if (trustStorePassword != null) {
            System.setProperty("javax.net.ssl.trustStorePassword", trustStorePassword);

        }
        if (trustStoreType != null) {
            System.setProperty("javax.net.ssl.trustStoreType", trustStoreType);

        }
        MqttConnectOptions options = new MqttConnectOptions();
        if (isBase64MutualTlsConfigured()) {
            try {
                options.setSocketFactory(buildMutualTlsContext(clientCertificateBase64, clientPrivateKeyBase64, caCertificateBase64, mqttPrivateKeyAlgorithm).getSocketFactory());
            } catch (Exception e) {
                logger.error("failed to initialize mqtt mutual tls from base64 certificates", e);
            }
        } else if (isFileMutualTlsConfigured()) {
            try {
                options.setSocketFactory(buildMutualTlsContextFromFiles(getMqttCaCertificatePath(), getMqttClientCertificatePath(), getMqttClientKeyPath(), mqttPrivateKeyAlgorithm).getSocketFactory());
            } catch (Exception e) {
                logger.error("failed to initialize mqtt mutual tls from certificate files", e);
            }
        } else if (trustStore != null) {
            try {
                SSLContext sslContext = SSLContext.getInstance("TLS");
                TrustManagerFactory tmf = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
                KeyStore ks = KeyStore.getInstance(trustStoreType != null ? trustStoreType : KeyStore.getDefaultType());
                try (InputStream is = new java.io.FileInputStream(trustStore)) {
                    ks.load(is, trustStorePassword != null ? trustStorePassword.toCharArray() : null);
                }
                tmf.init(ks);
                sslContext.init(null, tmf.getTrustManagers(), new SecureRandom());
                options.setSocketFactory(sslContext.getSocketFactory());
            } catch (Exception e) {
                logger.error("failed to initialize mqtt ssl with truststore", e);
            }
        }
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

    private boolean isBase64MutualTlsConfigured() {
        return hasText(clientCertificateBase64) && hasText(clientPrivateKeyBase64) && hasText(caCertificateBase64);
    }

    private boolean isFileMutualTlsConfigured() {
        return hasText(mqttCertsBaseDir) || hasText(mqttCaCertificatePath) || hasText(mqttClientCertificatePath) || hasText(mqttClientKeyPath);
    }

    private SSLContext buildMutualTlsContextFromFiles(String caCertificatePath,
                                                      String clientCertificatePath,
                                                      String clientKeyPath,
                                                      String privateKeyAlgorithm) throws Exception {
        CertificateFactory certificateFactory = CertificateFactory.getInstance("X.509");
        X509Certificate caCert;
        try (InputStream is = Files.newInputStream(Paths.get(caCertificatePath))) {
            caCert = (X509Certificate) certificateFactory.generateCertificate(is);
        }
        X509Certificate clientCert;
        try (InputStream is = Files.newInputStream(Paths.get(clientCertificatePath))) {
            clientCert = (X509Certificate) certificateFactory.generateCertificate(is);
        }

        byte[] keyBytes = Files.readAllBytes(Paths.get(clientKeyPath));
        String keyString = normalizeCertificateValue(new String(keyBytes));
        PrivateKey privateKey = KeyFactory.getInstance(hasText(privateKeyAlgorithm) ? privateKeyAlgorithm : "RSA").generatePrivate(new PKCS8EncodedKeySpec(Base64.getDecoder().decode(keyString)));

        return buildMutualTlsContext(clientCert, privateKey, caCert);
    }

    private SSLContext buildMutualTlsContext(String base64ClientCertificate,
                                            String base64ClientPrivateKey,
                                            String base64CaCertificate,
                                            String privateKeyAlgorithm) throws Exception {
        byte[] clientCertDer = Base64.getDecoder().decode(normalizeCertificateValue(base64ClientCertificate));
        byte[] clientKeyDer = Base64.getDecoder().decode(normalizeCertificateValue(base64ClientPrivateKey));
        byte[] caCertDer = Base64.getDecoder().decode(normalizeCertificateValue(base64CaCertificate));

        CertificateFactory certificateFactory = CertificateFactory.getInstance("X.509");
        X509Certificate clientCert = (X509Certificate) certificateFactory.generateCertificate(new ByteArrayInputStream(clientCertDer));
        X509Certificate caCert = (X509Certificate) certificateFactory.generateCertificate(new ByteArrayInputStream(caCertDer));

        PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(clientKeyDer);
        PrivateKey privateKey = KeyFactory.getInstance(hasText(privateKeyAlgorithm) ? privateKeyAlgorithm : "RSA").generatePrivate(keySpec);

        return buildMutualTlsContext(clientCert, privateKey, caCert);
    }

    private SSLContext buildMutualTlsContext(X509Certificate clientCert, PrivateKey privateKey, X509Certificate caCert) throws Exception {
        KeyStore keyStore = KeyStore.getInstance(KeyStore.getDefaultType());
        keyStore.load(null, null);
        keyStore.setKeyEntry("client", privateKey, new char[0], new Certificate[]{clientCert});

        KeyStore trustStore = KeyStore.getInstance(KeyStore.getDefaultType());
        trustStore.load(null, null);
        trustStore.setCertificateEntry("ca", caCert);

        KeyManagerFactory keyManagerFactory = KeyManagerFactory.getInstance(KeyManagerFactory.getDefaultAlgorithm());
        keyManagerFactory.init(keyStore, new char[0]);
        TrustManagerFactory trustManagerFactory = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
        trustManagerFactory.init(trustStore);

        SSLContext sslContext = SSLContext.getInstance("TLS");
        sslContext.init(keyManagerFactory.getKeyManagers(), trustManagerFactory.getTrustManagers(), new SecureRandom());
        return sslContext;
    }

    private String getMqttCaCertificatePath() {
        return hasText(mqttCaCertificatePath) ? mqttCaCertificatePath : getMqttCertPath("cacert.pem");
    }

    private String getMqttClientCertificatePath() {
        return hasText(mqttClientCertificatePath) ? mqttClientCertificatePath : getMqttCertPath(iotId + ".crt");
    }

    private String getMqttClientKeyPath() {
        return hasText(mqttClientKeyPath) ? mqttClientKeyPath : getMqttCertPath(iotId + ".key");
    }

    private String getMqttCertPath(String fileName) {
        return Path.of(mqttCertsBaseDir, fileName).toString();
    }

    private String normalizeCertificateValue(String value) {
        return value == null ? "" : value.replace("-----BEGIN CERTIFICATE-----", "")
                .replace("-----END CERTIFICATE-----", "")
                .replace("-----BEGIN PRIVATE KEY-----", "")
                .replace("-----END PRIVATE KEY-----", "")
                .replaceAll("\\s+", "");
    }

    private boolean hasText(String value) {
        return value != null && !value.trim().isEmpty();
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
    public PrivateKey privateKey() {

        try {
            return KeyUtils.readPrivateKey(keyPath);
        }
        catch (Exception e) {
            logger.error("failed to read private key from {}, generating temporary key so server can start", keyPath, e);
            return generateTemporaryPrivateKey();
        }
    }

    private PrivateKey generateTemporaryPrivateKey() {
        try {
            KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance("RSA");
            keyPairGenerator.initialize(2048);
            return keyPairGenerator.generateKeyPair().getPrivate();
        }
        catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException("failed to generate temporary private key", e);
        }
    }




    @Bean
    public PublicKeyProvider publicKeyProvider(PrivateKey privateKey) {
        logger.info("publicKeyProvider");
        PublicKey serverPublicKey = getServerPublicKey(privateKey);

        return f -> {
            if (iotId.equals(f) && serverPublicKey != null) {
                return new PublicKeyResponse(serverPublicKey, true);
            }
            return publicKeyService.getPublicKeyForGateway(f);
        };
    }

    private PublicKey getServerPublicKey(PrivateKey privateKey) {
        if (privateKey instanceof RSAPrivateCrtKey priv) {
            try {
                RSAPublicKeySpec publicKeySpec = new RSAPublicKeySpec(priv.getModulus(), priv.getPublicExponent());
                KeyFactory keyFactory = KeyFactory.getInstance("RSA");
                return keyFactory.generatePublic(publicKeySpec);
            } catch (Exception e) {
                logger.error("failed to derive public key from private key", e);
            }
        }
        return null;
    }





    @Bean
    public BasicIOTClient basicIOTClient(PrivateKey privateKey, PublicKeyProvider publicKeyProvider, ObjectProvider<IOTMessageSubscriber> iotMessageSubscribers) throws IOException, InterruptedException {
        logger.info("basicIOTClient");

        ObjectMapper objectMapper = new ObjectMapper()
                .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
                .registerModule(new JavaTimeModule())
                .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

        return new BasicIOTClient(iotId, privateKey, objectMapper, iotMessageSubscribers.stream().toList(), false, f -> incOutgoingMessage(f.getClass().getSimpleName()))
                .setPublicKeyProvider(publicKeyProvider);
    }

    private static final Map<String, Timer> timerMap = new ConcurrentHashMap<>();

    private static final Map<String,Counter> outgoingCounterMap=new ConcurrentHashMap<>();

    private static final AtomicLong processedMessageCount = new AtomicLong();
    private static final AtomicLong processedMessageTotalTimeNanos = new AtomicLong();
    private static final AtomicLong processedMessageMinTimeNanos = new AtomicLong(Long.MAX_VALUE);
    private static final AtomicLong processedMessageMaxTimeNanos = new AtomicLong();


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

    private void recordProcessedMessageTime(String type, long time) {
        long count = processedMessageCount.incrementAndGet();
        long total = processedMessageTotalTimeNanos.addAndGet(time);
        processedMessageMinTimeNanos.accumulateAndGet(time, Math::min);
        processedMessageMaxTimeNanos.accumulateAndGet(time, Math::max);
        if (messageProcessingLogInterval > 0 && count % messageProcessingLogInterval == 0) {
            logger.info("processed {} mqtt messages, latest type {}, latest {} ms, min {} ms, max {} ms, average {} ms, system cpu load {}%",
                    count,
                    type,
                    TimeUnit.NANOSECONDS.toMillis(time),
                    TimeUnit.NANOSECONDS.toMillis(processedMessageMinTimeNanos.get()),
                    TimeUnit.NANOSECONDS.toMillis(processedMessageMaxTimeNanos.get()),
                    TimeUnit.NANOSECONDS.toMillis(total / count),
                    getSystemCpuLoadPercentage());
        }
    }

    private String getSystemCpuLoadPercentage() {
        double cpuLoad = operatingSystemMXBean != null ? operatingSystemMXBean.getCpuLoad() : -1;
        return cpuLoad >= 0 ? String.format("%.2f", cpuLoad * 100) : "unknown";
    }

    @Bean
    public Timer checkConnectivityTimer(){
        return Timer.builder("iot.connectivity.timer")
                .register(meterRegistry);
    }

    @Bean
    public Counter droppedMessagesCounter(){
        return Counter.builder("iot.droppedMessages.counter")
                .register(meterRegistry);
    }

    @Bean
    public ServerIntegrationFlowHolder serverInputIntegrationFlowHolder(BasicIOTClient basicIOTClient, MqttPahoClientFactory mqttServerFactory, @Qualifier("mqttOutboundFlow") IntegrationFlow mqttOutboundFlow, Semaphore virtualThreadsLogicSemaphore, MeterRegistry meterRegistry) {
        logger.info("serverInputIntegrationFlow");

        if (!isMqttConfigured() || mqttServerFactory == null) {
            logger.warn("mqtt server will not start as basic.iot.mqtt.url is empty");
            return new ServerIntegrationFlowHolder(null, new BasicIOTConnection(null, mqttOutboundFlow, null));
        }
        logger.info("mqttPahoMessageDrivenChannelAdapterServer");
        MqttPahoMessageDrivenChannelAdapter mqttPahoMessageDrivenChannelAdapter = new MqttPahoMessageDrivenChannelAdapter(iotId+"-in", mqttServerFactory, BasicIOTClient.MAIN_TOPIC_PATH_OUT, "mqtt-test");
        mqttPahoMessageDrivenChannelAdapter.setQos(1);

        StandardIntegrationFlow standardIntegrationFlow = IntegrationFlow.from(mqttPahoMessageDrivenChannelAdapter)
                .channel(MessageChannels.executor("mqtt-in-executor",new TaskExecutorAdapter(Executors.newVirtualThreadPerTaskExecutor())))
                .handle(message->{
                    long start = System.nanoTime();
                   // logger.info("handling mqtt id "+message.getHeaders().getId() +" with id "+message.getPayload());
                    String type="unknown";
                    IOTMessage iotMessage =null;
                    boolean acquired = false;
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
                        acquired = true;
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
                        if (acquired) {
                            virtualThreadsLogicSemaphore.release();
                        }
                        long totalTime = System.nanoTime() - start;
                        timeMessage(TimerType.TOTAL,type, totalTime);
                        recordProcessedMessageTime(type, totalTime);

                    }

                })
                .get();
        BasicIOTConnection basicIOTConnection;
        try {
            basicIOTConnection = basicIOTClient.open(standardIntegrationFlow, mqttOutboundFlow, mqttPahoMessageDrivenChannelAdapter);
        }
        catch (Exception e) {
            logger.error("mqtt server will not start because opening the MQTT connection failed", e);
            basicIOTConnection = new BasicIOTConnection(standardIntegrationFlow, mqttOutboundFlow, mqttPahoMessageDrivenChannelAdapter);
        }

        return new ServerIntegrationFlowHolder(standardIntegrationFlow,basicIOTConnection);
    }


    @Bean
    @Qualifier("mqttInbound")
    public IntegrationFlow mqttInbound(ServerIntegrationFlowHolder serverIntegrationFlowHolder){
        IntegrationFlow integrationFlow = serverIntegrationFlowHolder.getIntegrationFlow();
        return integrationFlow != null ? integrationFlow : f -> f.nullChannel();
    }


    @Bean
    @Qualifier("mqttOutboundFlow")
    public IntegrationFlow mqttOutboundFlow(MqttPahoClientFactory mqttServerFactory) {
        logger.info("serverOutputIntegrationFlow");
        if (!isMqttConfigured() || mqttServerFactory == null) {
            logger.warn("mqtt server will not start as basic.iot.mqtt.url is empty");
            return f -> f.nullChannel();
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

    private boolean isMqttConfigured() {
        if (mqttURLs == null || mqttURLs.length == 0) {
            return false;
        }
        for (String mqttURL : mqttURLs) {
            if (mqttURL != null && !mqttURL.isBlank()) {
                return true;
            }
        }
        return false;
    }


}
