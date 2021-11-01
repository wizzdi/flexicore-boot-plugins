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
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.UUID;
import java.util.concurrent.CompletionException;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.atomic.AtomicReference;

public class BasicIOTClient implements MessageHandler {
    public static final String IOT_MESSAGES_SUBJECT = "IOT_MESSAGES_SUBJECT";
    private static final Logger logger = LoggerFactory.getLogger(BasicIOTClient.class);
    public static final String MQTT_TOPIC = "mqtt_topic";
    public static final String SIGNATURE_ALGORITHM = "SHA1WithRSA";
    private final Signature signature;


    private IntegrationFlow inbound;
    private final ObjectMapper objectMapper;
    private final List<IOTMessageSubscriber> subscribers;
    private IntegrationFlow outbound;
    private MqttPahoMessageDrivenChannelAdapter mqttPahoMessageDrivenChannelAdapter;
    private final Queue<IOTMessageSubscriber> requestResponseMessageHandlers = new LinkedBlockingQueue<>();
    private final String id;
    private PublicKeyProvider publicKeyProvider;


    public BasicIOTClient(String id, PrivateKey key, ObjectMapper objectMapper, List<IOTMessageSubscriber> subscribers) {
        this.objectMapper = objectMapper;
        this.subscribers = subscribers;
        this.id = id;
        try {
            signature = Signature.getInstance(SIGNATURE_ALGORITHM);
            signature.initSign(key);

        } catch (Exception e) {
            throw new RuntimeException("failed to init signature",e);
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
        if (verifyMessage(iotMessage)) {
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


    }

    private boolean verifyMessage(IOTMessage iotMessage) {
        if (iotMessage.isRequireAuthentication() && publicKeyProvider != null) {
            String gatewayId = iotMessage.getGatewayId();
            PublicKey publicKey = publicKeyProvider.getPublicKey(gatewayId);
            if (publicKey == null) {
                throw new RuntimeException("could not find public key for gateway " + gatewayId);
            }
            try {
                Signature sig = Signature.getInstance(SIGNATURE_ALGORITHM);
                sig.initVerify(publicKey);
                sig.update(iotMessage.getId().getBytes(StandardCharsets.UTF_8));
                boolean verify = sig.verify(iotMessage.getSignature());
                if(!verify){
                    logger.warn("message "+iotMessage +" verify failed");
                }
                return verify;


            } catch (Exception e) {
                throw new RuntimeException(e);
            }

        } else {
            return true;
        }

    }

    private <T extends IOTMessage> T parseMessage(Message message, Class<T> type) {
        try {
            return objectMapper.readValue((String) message.getPayload(), type).setMessage(message);
        } catch (Exception e) {
            throw new CompletionException(e);
        }
    }

    public <T extends IOTMessage> T request(IOTMessage iotMessage) throws InterruptedException {
        return request(iotMessage, iotMessage.getId(), 5000);
    }

    private void prepareMessage(IOTMessage iotMessage) {
        if (iotMessage.getId() == null) {
            iotMessage.setId(UUID.randomUUID().toString());
        }
        if (iotMessage.getSentAt() == null) {
            iotMessage.setSentAt(OffsetDateTime.now());
        }
        iotMessage.setGatewayId(id);
        try {
            signature.update(iotMessage.getId().getBytes(StandardCharsets.UTF_8));
            byte[] sign = signature.sign();
            iotMessage.setSignature(sign);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public void sendMessage(IOTMessage iotMessage) throws JsonProcessingException {
        reply(iotMessage, IOT_MESSAGES_SUBJECT);

    }

    public <T extends IOTMessage> T request(IOTMessage request, String replyToTemp, long timeout) throws InterruptedException {
        prepareMessage(request);
        if(replyToTemp==null){
            replyToTemp=request.getId();
        }
        String replyTo=replyToTemp;
        mqttPahoMessageDrivenChannelAdapter.addTopic(replyTo, 1);
        AtomicReference<T> response = new AtomicReference<>(null);
        IOTMessageSubscriber<T> messageHandler = f -> {
            String mqtt_receivedTopic = f.getMessage().getHeaders().get("mqtt_receivedTopic", String.class);
            if (!replyTo.equals(mqtt_receivedTopic)) {
                logger.info("mqtt_receivedTopic was "+mqtt_receivedTopic +" when expected"+replyTo );
                return;
            }
            logger.info("received response for "+request +":"+f);
            synchronized (request) {
                try {
                    response.set(f);
                } catch (Exception e) {
                    logger.error("failed to parse message", e);
                }
                request.notifyAll();
            }
        };
        requestResponseMessageHandlers.add(messageHandler);
        synchronized (request) {
            try {
                GenericMessage<String> message = new GenericMessage<>(objectMapper.writeValueAsString(request), Map.of(MQTT_TOPIC, IOT_MESSAGES_SUBJECT, MessageHeaders.REPLY_CHANNEL, replyTo));
                outbound.getInputChannel().send(message);
            } catch (Exception e) {
                logger.error("error", e);
            }
            if (timeout > 0) {
                request.wait(timeout);
            } else {
                request.wait();
            }
            requestResponseMessageHandlers.remove(messageHandler);
        }
        return response.get();
    }


    public void reply(IOTMessage iotMessage, String replyTo) throws JsonProcessingException {
        prepareMessage(iotMessage);
        GenericMessage<String> message = new GenericMessage<>(objectMapper.writeValueAsString(iotMessage), Map.of(MQTT_TOPIC, replyTo));
        outbound.getInputChannel().send(message);
    }

    public void reply(IOTMessage iotMessage, IOTMessage replyTo) throws JsonProcessingException {
        String replyToTopic = (String) replyTo.getMessage().getHeaders().get(MessageHeaders.REPLY_CHANNEL);
        if (replyToTopic == null) {
            replyToTopic = replyTo.getId();
        }
        reply(iotMessage, replyToTopic);
    }

    @Override
    public void handleMessage(Message<?> message) throws MessagingException {
        parseAndCallSubscribers(message);
    }
}
