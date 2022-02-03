package com.wizzdi.basic.iot.client;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.integration.dsl.IntegrationFlow;
import org.springframework.integration.mqtt.inbound.MqttPahoMessageDrivenChannelAdapter;
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
import java.util.concurrent.CompletionException;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.atomic.AtomicReference;

public class BasicIOTClient implements MessageHandler {
    public static final String MAIN_TOPIC_PATH = "GATEWAY";
    public static final String IOT_MESSAGES_SUBJECT = MAIN_TOPIC_PATH + "/#";
    private static final Logger logger = LoggerFactory.getLogger(BasicIOTClient.class);
    public static final String MQTT_TOPIC = "mqtt_topic";
    public static final String SIGNATURE_ALGORITHM = "SHA1WithRSA";
    public static final String MQTT_RECEIVED_TOPIC = "mqtt_receivedTopic";
    private static final String MQTT_BY = "MQTT_BY";
    private final Signature signature;


    private IntegrationFlow inbound;
    private final ObjectMapper objectMapper;
    private final Collection<IOTMessageSubscriber> subscribers;
    private IntegrationFlow outbound;
    private MqttPahoMessageDrivenChannelAdapter mqttPahoMessageDrivenChannelAdapter;
    private final Queue<IOTMessageSubscriber> requestResponseMessageHandlers = new LinkedBlockingQueue<>();

    private final String id;
    private PublicKeyProvider publicKeyProvider;


    public BasicIOTClient(String id, PrivateKey key, ObjectMapper objectMapper, Collection<IOTMessageSubscriber> subscribers) {
        this.objectMapper = objectMapper;
        this.subscribers = subscribers;
        this.id = id;
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


    private void parseAndCallSubscribers(Message<?> message) {
        IOTMessage iotMessage = parseMessage(message, IOTMessage.class);
        if(iotMessage.getGatewayId().equals(id)){
            logger.debug("ignoring self message");
            return;
        }
        try {
            if (!verifyMessage(iotMessage)) {
                iotMessage = getBadMessage(message, (String) message.getPayload(), "signature verification failed for message " + iotMessage.getId() + " with signature " + iotMessage.getSignature());
            }
        } catch (VerificationException e) {
            iotMessage = getBadMessage(message, (String) message.getPayload(), "signature verification failed for message " + iotMessage.getId() + " with exception " + e.getMessage());
        }


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

    private boolean verifyMessage(IOTMessage iotMessage) throws VerificationException {
        if (iotMessage.isRequireAuthentication() && publicKeyProvider != null) {
            String gatewayId = iotMessage.getGatewayId();
            PublicKey publicKey = publicKeyProvider.getPublicKey(gatewayId);
            if (publicKey == null) {
                throw new VerificationException("could not find public key for gateway " + gatewayId);
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
                throw new VerificationException(e);
            }

        } else {
            return true;
        }

    }

    private <T extends IOTMessage> T parseMessage(Message message, Class<T> type) {
        String payload = (String) message.getPayload();

        try {
            String mqtt_receivedTopic = message.getHeaders().get(MQTT_RECEIVED_TOPIC, String.class);
            logger.debug("in (" + mqtt_receivedTopic + "): " + payload);
            T t = objectMapper.readValue(payload, type);
            return t.setMessage(message);
        } catch (Exception e) {
            throw new RuntimeException("failed parsing message", e);
        }
    }

    private BadMessage getBadMessage(Message message, String payload, String error) {
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
        reply(iotMessage, MAIN_TOPIC_PATH + "/" + targetGatewayId);

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
                    GenericMessage<String> message = new GenericMessage<>(jsonString, Map.of(MQTT_TOPIC, MAIN_TOPIC_PATH + "/" + targetGatewayId, MessageHeaders.REPLY_CHANNEL, replyTo, MQTT_BY, id));
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


    public void reply(IOTMessage iotMessage, String replyTo) throws JsonProcessingException {
        prepareMessage(iotMessage);
        GenericMessage<String> message = new GenericMessage<>(objectMapper.writeValueAsString(iotMessage), Map.of(MQTT_TOPIC, replyTo, MQTT_BY, id));
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

    @Override
    public void handleMessage(Message<?> message) throws MessagingException {

        parseAndCallSubscribers(message);

    }
}
