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
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import java.time.OffsetDateTime;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

@Component
@Extension
@ConditionalOnProperty(
        name = {
                "basic.iot.mqtt.enabled",
                "basic.iot.mqtt.testCycle.enabled"
        },
        havingValue = "true",
        matchIfMissing = false
)
public class MqttTestCycleService implements Plugin, IOTMessageSubscriber<IOTMessage> {

    private static final Logger logger = LoggerFactory.getLogger("basic-iot");

    /**
     * Static guard because FlexiCore / PF4J / Spring may emit more than one ContextRefreshedEvent,
     * and in some plugin-loading cases more than one service instance may be created.
     */
    private static final AtomicBoolean TEST_CYCLE_STARTED = new AtomicBoolean(false);

    @Autowired(required = false)
    private BasicIOTClient basicIOTClient;

    @Value("${basic.iot.start.delay:60}")
    private int startDelay;

    @Value("${basic.iot.mqtt.testCycle.timeoutSeconds:10}")
    private int timeoutSeconds;

    private final String testMessageId = UUID.randomUUID().toString();

    private final CompletableFuture<Boolean> testResult = new CompletableFuture<>();

    @EventListener(ContextRefreshedEvent.class)
    public void onApplicationEvent(ContextRefreshedEvent event) {
        if (!TEST_CYCLE_STARTED.compareAndSet(false, true)) {
            logger.info(
                    "MQTT test cycle already started, skipping duplicate ContextRefreshedEvent from context: {}",
                    event.getApplicationContext().getId()
            );
            return;
        }

        CompletableFuture.runAsync(() -> {
            try {
                logger.info("Starting MQTT test cycle in {} seconds", startDelay);
                Thread.sleep(TimeUnit.SECONDS.toMillis(startDelay));
                runTest();
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                logger.error("MQTT test cycle interrupted", e);
            } catch (Exception e) {
                logger.error("MQTT test cycle failed unexpectedly", e);
                mqttSupportFailed();
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
                Boolean success = testResult.get(timeoutSeconds, TimeUnit.SECONDS);
                if (Boolean.TRUE.equals(success)) {
                    logger.info("MQTT test cycle passed successfully");
                } else {
                    logger.error("MQTT test cycle failed: message received but check failed");
                    mqttSupportFailed();
                }
            } catch (Exception e) {
                logger.error(
                        "MQTT test cycle failed: timeout waiting for message after {} seconds. If failed, there is no MQTT support, period",
                        timeoutSeconds
                );
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
        if (iotMessage == null) {
            return;
        }

        if (testMessageId.equals(iotMessage.getId())) {
            logger.info("MQTT test cycle message received back");
            testResult.complete(true);
        }
    }
}
