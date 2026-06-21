package com.wizzdi.basic.iot.service.service;

import com.wizzdi.basic.iot.client.BasicIOTClient;
import com.wizzdi.basic.iot.client.IOTMessage;
import com.wizzdi.basic.iot.client.IOTMessageSubscriber;
import com.wizzdi.flexicore.boot.base.interfaces.Plugin;
import org.pf4j.Extension;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import java.time.OffsetDateTime;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

@Component
@Extension
public class MqttTestCycleService implements Plugin, IOTMessageSubscriber<IOTMessage> {

    private static final Logger logger = LoggerFactory.getLogger("basic-iot");

    @Autowired(required = false)
    private BasicIOTClient basicIOTClient;

    @Value("${basic.iot.start.delay:60}")
    private int startDelay;

    private final String testMessageId = UUID.randomUUID().toString();
    private final CompletableFuture<Boolean> testResult = new CompletableFuture<>();

    @EventListener(ContextRefreshedEvent.class)
    public void onApplicationEvent(ContextRefreshedEvent event) {
        CompletableFuture.runAsync(() -> {
            try {
                logger.info("Starting MQTT test cycle in {} seconds", startDelay);
                Thread.sleep(TimeUnit.SECONDS.toMillis(startDelay));
                runTest();
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                logger.error("MQTT test cycle interrupted", e);
            }
        });
    }

    private void runTest() {
        if (basicIOTClient == null) {
            logger.warn("MQTT test cycle skipped because MQTT client is not available");
            return;
        }
        logger.info("Running MQTT test cycle, sending message with ID: {}", testMessageId);
        IOTMessage testMessage = new IOTMessage()
                .setId(testMessageId)
                .setSentAt(OffsetDateTime.now());

        try {
            basicIOTClient.reply(testMessage, "mqtt-test");

            try {
                Boolean success = testResult.get(10, TimeUnit.SECONDS);
                if (Boolean.TRUE.equals(success)) {
                    logger.info("MQTT test cycle passed successfully");
                } else {
                    logger.error("MQTT test cycle failed: message received but check failed");
                    mqttSupportFailed();
                }
            } catch (Exception e) {
                logger.error("MQTT test cycle failed: timeout waiting for message. if failed, there is no MQTT support, period");
                mqttSupportFailed();
            }
        } catch (Exception e) {
            logger.error("MQTT test cycle failed: error sending message", e);
            mqttSupportFailed();
        }
    }

    private void mqttSupportFailed() {
        logger.error("No MQTT support, period");
    }

    @Override
    public void onIOTMessage(IOTMessage iotMessage) {
        if (testMessageId.equals(iotMessage.getId())) {
            logger.info("MQTT test cycle message received back");
            testResult.complete(true);
        }
    }
}
