package com.wizzdi.basic.iot.client;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.nats.client.Connection;
import io.nats.client.Dispatcher;
import io.nats.client.Message;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;

public class BasicIOTClient {
    public static final String IOT_MESSAGES_SUBJECT = "IOT_MESSAGES_SUBJECT";
    private static final Logger logger = LoggerFactory.getLogger(BasicIOTClient.class);


    private final Connection connection;
    private final ObjectMapper objectMapper;
    private final List<IOTMessageSubscriber> subscribers;
    private final Dispatcher dispatcher;


    public BasicIOTClient(Connection connection, ObjectMapper objectMapper, List<IOTMessageSubscriber> subscribers) {
        this.connection = connection;
        this.objectMapper = objectMapper;
        this.subscribers = subscribers;
        this.dispatcher = connection.createDispatcher(this::parseAndCallSubscribers);
        open();

    }

    private void parseAndCallSubscribers(Message message) {
        IOTMessage iotMessage = parseMessage(message,IOTMessage.class);
        for (IOTMessageSubscriber subscriber : subscribers) {
            try {
                subscriber.onIOTMessage(iotMessage);
            } catch (Throwable e) {
                logger.error("IOTMessageSubscriber failed", e);
            }
        }


    }

    private <T extends IOTMessage> T parseMessage(Message message,Class<T> type) {
        try {
            return objectMapper.readValue(message.getData(), type).setMessage(message);
        } catch (Exception e) {
            throw new CompletionException(e);
        }
    }

    private void open() {
        dispatcher.subscribe(IOT_MESSAGES_SUBJECT);
    }

    public void close() {
        dispatcher.unsubscribe(IOT_MESSAGES_SUBJECT);

    }


    public <T extends IOTMessage> CompletableFuture<T> request(IOTMessage iotMessage,Class<T> type) throws JsonProcessingException {
        prepareMessage(iotMessage);
        String s = objectMapper.writeValueAsString(iotMessage);
        CompletableFuture<Message> request = connection.request(IOT_MESSAGES_SUBJECT, s.getBytes(StandardCharsets.UTF_8));
        return request.thenApply(f->parseMessage(f,type));
    }

    private void prepareMessage(IOTMessage iotMessage) {
        if(iotMessage.getId()==null){
            iotMessage.setId(UUID.randomUUID().toString());
        }
        if(iotMessage.getSentAt()==null){
            iotMessage.setSentAt(OffsetDateTime.now());
        }
    }

    public void sendMessage(IOTMessage iotMessage) throws JsonProcessingException {
        prepareMessage(iotMessage);
        String s = objectMapper.writeValueAsString(iotMessage);
        connection.publish(IOT_MESSAGES_SUBJECT, s.getBytes(StandardCharsets.UTF_8));
    }

    public void sendMessage(IOTMessage iotMessage, String replyTo) throws JsonProcessingException {
        prepareMessage(iotMessage);
        String s = objectMapper.writeValueAsString(iotMessage);
        connection.publish(replyTo, s.getBytes(StandardCharsets.UTF_8));
    }

    public void sendMessage(IOTMessage iotMessage, IOTMessage replyTo) throws JsonProcessingException {
        String replyToTopic = replyTo.getMessage().getReplyTo();
        if(replyToTopic==null){
            replyToTopic=replyTo.getId();
        }
        sendMessage(iotMessage, replyToTopic);
    }

}
