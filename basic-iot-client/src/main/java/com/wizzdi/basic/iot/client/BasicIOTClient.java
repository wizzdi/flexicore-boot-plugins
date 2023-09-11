package com.wizzdi.basic.iot.client;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.integration.dsl.IntegrationFlow;
import org.springframework.integration.mqtt.inbound.MqttPahoMessageDrivenChannelAdapter;
import org.springframework.integration.mqtt.support.MqttHeaders;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageHandler;
import org.springframework.messaging.MessageHeaders;
import org.springframework.messaging.MessagingException;
import org.springframework.messaging.support.GenericMessage;

import java.nio.charset.StandardCharsets;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.Signature;
import java.time.OffsetDateTime;
import java.util.*;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.atomic.AtomicReference;

public class BasicIOTClient {
    public static final String MAIN_TOPIC_PATH = "GATEWAY";
    public static final String IN_SUFFIX = "IN";
    public static final String OUT_SUFFIX = "OUT";
    public static final String MAIN_TOPIC_PATH_OUT = MAIN_TOPIC_PATH + "/+/" + OUT_SUFFIX;


    public static String getInTopic(String gatewayId) {
        return MAIN_TOPIC_PATH + "/" + gatewayId + "/" + IN_SUFFIX;
    }

    public static String getOutTopic(String gatewayId) {
        return MAIN_TOPIC_PATH + "/" + gatewayId + "/" + OUT_SUFFIX;
    }

    private static final Logger logger = LoggerFactory.getLogger(BasicIOTClient.class);
    public static final String MQTT_TOPIC = "mqtt_topic";
    public static final String SIGNATURE_ALGORITHM = "SHA256withRSA";
    public static final String MQTT_RECEIVED_TOPIC = "mqtt_receivedTopic";
    private static final String MQTT_BY = "MQTT_BY";
    private final Signature signature;


    private IntegrationFlow inbound;
    private final ObjectMapper objectMapper;
    private final Iterable<IOTMessageSubscriber> subscribers;
    private IntegrationFlow outbound;
    private MqttPahoMessageDrivenChannelAdapter mqttPahoMessageDrivenChannelAdapter;
    private final Queue<IOTMessageSubscriber> requestResponseMessageHandlers = new LinkedBlockingQueue<>();


    private final String id;
    private PublicKeyProvider publicKeyProvider;
    private final boolean client;
    private final boolean disableVerification;

    public BasicIOTClient(String id, PrivateKey key, ObjectMapper objectMapper, Iterable<IOTMessageSubscriber> subscribers) {
        this(id, key, objectMapper, subscribers, false, false);
    }


    public BasicIOTClient(String id, PrivateKey key, ObjectMapper objectMapper, Iterable<IOTMessageSubscriber> subscribers, boolean client,boolean disableVerification) {
        this.objectMapper = objectMapper;
        this.subscribers = subscribers;
        this.id = id;
        this.client = client;
        this.disableVerification=disableVerification;
        try {
            signature = Signature.getInstance(SIGNATURE_ALGORITHM);
            signature.initSign(key);

        } catch (Exception e) {
            throw new RuntimeException("failed to init signature", e);
        }


    }

    public PublicKeyProvider getPublicKeyProvider() {
        return publicKeyProvider;
    }

    public <T extends BasicIOTClient> T setPublicKeyProvider(PublicKeyProvider publicKeyProvider) {
        this.publicKeyProvider = publicKeyProvider;
        return (T) this;
    }

    public BasicIOTConnection open(IntegrationFlow inbound, IntegrationFlow outbound, MqttPahoMessageDrivenChannelAdapter mqttPahoMessageDrivenChannelAdapter) {
        this.inbound = inbound;
        this.outbound = outbound;
        this.mqttPahoMessageDrivenChannelAdapter = mqttPahoMessageDrivenChannelAdapter;
        return new BasicIOTConnection(inbound, outbound, mqttPahoMessageDrivenChannelAdapter);
    }


    private IOTMessage parseAndCallSubscribers(Message<?> message) {
        try {
            IOTMessage iotMessage = parseMessage(message, IOTMessage.class);
            boolean verified = verifyMessage(iotMessage);
            if (!verified) {
                iotMessage = getBadMessage(message, (String) message.getPayload(), "signature verification failed for message " + iotMessage.getId() + " with signature " + iotMessage.getSignature());
            }


            callSubscribersAndHandlers(iotMessage);
            return iotMessage;
        } catch (Throwable e) {
            logger.error("failed handling message", e);
        }


        return null;
    }

    public void callSubscribersAndHandlers(IOTMessage iotMessage) {
        for (IOTMessageSubscriber requestResponseMessageHandler : requestResponseMessageHandlers) {
            requestResponseMessageHandler.onIOTMessage(iotMessage);
        }
        for (IOTMessageSubscriber subscriber : subscribers) {
            try {
                subscriber.onIOTMessage(iotMessage);
            } catch (Throwable e) {
                logger.error("IOTMessageSubscriber failed", e);
            }
        }
    }

    public boolean verifyMessage(IOTMessage iotMessage) {
        if(disableVerification){
            return true;
        }
        if(!iotMessage.isRequireAuthentication()){
            return true;
        }
        if(publicKeyProvider==null){
            return true;
        }
        String gatewayId = iotMessage.getGatewayId();
        PublicKeyResponse publicKeyResponse = publicKeyProvider.getPublicKey(gatewayId);
        if (publicKeyResponse == null) {
            return false;
        }
        PublicKey publicKey = publicKeyResponse.publicKey();
        boolean signatureMandatory = publicKeyResponse.signatureMandatory();
        if (!signatureMandatory) {
            return true;
        }
        if (publicKey == null) {
            return false;
        }
        try {
            Signature sig = Signature.getInstance(SIGNATURE_ALGORITHM);
            sig.initVerify(publicKey);
            sig.update(iotMessage.getId().getBytes(StandardCharsets.UTF_8));
            boolean verify = sig.verify(Base64.getDecoder().decode(iotMessage.getSignature()));
            if (!verify) {
                logger.warn("message " + iotMessage + " verify failed");
            }
            return verify;


        } catch (Exception e) {
            logger.warn("failed to verify message " + iotMessage, e);
            return false;
        }

    }

    public <T extends IOTMessage> T parseMessage(Message message, Class<T> type) throws JsonProcessingException {
        String payload = (String) message.getPayload();

        String mqtt_receivedTopic = message.getHeaders().get(MQTT_RECEIVED_TOPIC, String.class);
        logger.debug("in (" + mqtt_receivedTopic + "): " + payload);
        T t = objectMapper.readValue(payload, type);
        return t.setMessage(message);

    }

    public BadMessage getBadMessage(Message message, String payload, String error) {
        return new BadMessage().setOriginalMessage(payload).setError(error).setMessage(message);
    }

    public <T extends IOTMessage> T request(IOTMessage iotMessage) throws InterruptedException {
        return request(iotMessage, id);
    }


    public <T extends IOTMessage> T request(IOTMessage iotMessage, String targetGatewayId) throws InterruptedException {
        return request(iotMessage, targetGatewayId, iotMessage.getId(), 5000);
    }

    private void prepareMessage(IOTMessage iotMessage) {
        if (iotMessage.getId() == null) {
            iotMessage.setId(UUID.randomUUID().toString());
        }
        if (iotMessage.getSentAt() == null) {
            iotMessage.setSentAt(OffsetDateTime.now());
        }
        if (iotMessage.getGatewayId() == null) {
            iotMessage.setGatewayId(id);
        }
        try {
            signature.update(iotMessage.getId().getBytes(StandardCharsets.UTF_8));
            byte[] sign = signature.sign();
            iotMessage.setSignature(Base64.getEncoder().encodeToString(sign));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public void sendMessage(IOTMessage iotMessage) throws JsonProcessingException {
        sendMessage(iotMessage, id);
    }


    public void sendMessage(IOTMessage iotMessage, String targetGatewayId) throws JsonProcessingException {
        String topicToSendTo = client ? getOutTopic(targetGatewayId) : getInTopic(targetGatewayId);
        reply(iotMessage, topicToSendTo);

    }

    public <T extends IOTMessage> T request(IOTMessage request, String targetGatewayId, String replyToTemp, long timeout) throws InterruptedException {
        prepareMessage(request);
        if (replyToTemp == null) {
            replyToTemp = request.getId();
        }
        String replyTo = replyToTemp;
        mqttPahoMessageDrivenChannelAdapter.addTopic(replyTo, 1);
        AtomicReference<T> response = new AtomicReference<>(null);
        IOTMessageSubscriber<T> messageHandler = f -> {
            String mqtt_receivedTopic = f.getMessage().getHeaders().get(MQTT_RECEIVED_TOPIC, String.class);
            if (!replyTo.equals(mqtt_receivedTopic)) {
                logger.info("mqtt_receivedTopic was " + mqtt_receivedTopic + " when expected" + replyTo);
                return;
            }
            logger.info("received response for " + request + ":" + f);
            synchronized (request) {
                response.set(f);
                request.notifyAll();
            }
        };
        try {
            requestResponseMessageHandlers.add(messageHandler);
            synchronized (request) {
                try {
                    String jsonString = objectMapper.writeValueAsString(request);
                    logger.debug("out ( " + targetGatewayId + ") reply to " + replyTo + ":" + jsonString);

                    String topicToSendTo = client ? getOutTopic(targetGatewayId) : getInTopic(targetGatewayId);
                    GenericMessage<String> message = new GenericMessage<>(jsonString, Map.of(MqttHeaders.TOPIC, topicToSendTo, MessageHeaders.REPLY_CHANNEL, replyTo, MQTT_BY, id,MqttHeaders.RETAINED,request.isRetained()));
                    outbound.getInputChannel().send(message);
                } catch (Exception e) {
                    logger.error("error", e);
                }
                if (timeout > 0) {
                    request.wait(timeout);
                } else {
                    request.wait();
                }
            }
            return response.get();
        } finally {
            requestResponseMessageHandlers.remove(messageHandler);
            if (!replyTo.startsWith(MAIN_TOPIC_PATH)) {
                mqttPahoMessageDrivenChannelAdapter.removeTopic(replyTo);
            }

        }
    }


    public void reply(IOTMessage iotMessage, String topicToSendTo) throws JsonProcessingException {
        prepareMessage(iotMessage);
        GenericMessage<String> message = new GenericMessage<>(objectMapper.writeValueAsString(iotMessage), Map.of(MqttHeaders.TOPIC, topicToSendTo, MQTT_BY, id,MqttHeaders.RETAINED,iotMessage.isRetained()));
        outbound.getInputChannel().send(message);
    }

    public void reply(IOTMessage iotMessage, IOTMessage replyTo) throws JsonProcessingException {
        String replyToTopic = (String) replyTo.getMessage().getHeaders().get(MessageHeaders.REPLY_CHANNEL);
        if (replyToTopic == null) {
            replyToTopic = replyTo.getId();
        }
        if (replyToTopic == null && replyTo instanceof BadMessage) {
            replyToTopic = (String) replyTo.getMessage().getHeaders().get(MQTT_RECEIVED_TOPIC);
        }
        reply(iotMessage, replyToTopic);
    }



}
