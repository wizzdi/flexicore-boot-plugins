package com.wizzdi.basic.iot.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.wizzdi.basic.iot.client.BasicIOTClient;
import com.wizzdi.basic.iot.client.KeepAlive;
import org.apache.commons.io.IOUtils;
import org.eclipse.paho.client.mqttv3.*;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.time.OffsetDateTime;
import java.util.Random;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

public class StressTest {

    private static final int TEST_FACTOR = 2;
    private static final ObjectMapper objectMapper = new ObjectMapper().registerModule(new JavaTimeModule());
    public static final int TIME_BETWEEN_MESSAGES = 5*60*1000;
    public static final int WARMUP_PERIOD = 5*60*1000;



    private static final Random random = new Random();

    public static void main(String[] args) throws InterruptedException, MqttException, IOException {
        String broker=args[0];
        String username = args[1];
        String password = args[2];
        String[] allDevices = IOUtils.resourceToString("devices.csv", StandardCharsets.UTF_8,StressTest.class.getClassLoader()).split(",");
        System.out.printf("starting test of %d devices each will send a message every %d seconds%n", allDevices.length*TEST_FACTOR,(TIME_BETWEEN_MESSAGES/1000));
        MqttClient client = new MqttClient(broker, "stress_test" , new MemoryPersistence());
        MqttConnectOptions options = new MqttConnectOptions();


        options.setUserName(username);
        options.setPassword(password.toCharArray());
        options.setCleanSession(true);
        options.setKeepAliveInterval(30);
        options.setAutomaticReconnect(true);
        options.setMaxReconnectDelay(20);
        options.setMaxInflight(allDevices.length*TEST_FACTOR);
        client.connect(options);
        ExecutorService executor = Executors.newVirtualThreadPerTaskExecutor();
        AtomicLong messageCounter = new AtomicLong();
        long startTime = System.currentTimeMillis();
        for (String deviceId : allDevices) {
            for (int i = 0; i < TEST_FACTOR; i++) {
                executor.submit(new Device(client,deviceId, messageCounter, startTime));
            }
        }
        executor.awaitTermination(Long.MAX_VALUE, java.util.concurrent.TimeUnit.DAYS);

    }

    public static class Device implements Runnable {
        private final String deviceId;
        private final AtomicLong globalMessageCounter;
        private final long timeStarted;
        private final MqttClient client;
        private boolean stop;

        public Device(MqttClient client, String deviceId, AtomicLong globalMessageCounter, long timeStarted) {
            this.client = client;
            this.deviceId = deviceId;
            this.globalMessageCounter = globalMessageCounter;
            this.timeStarted = timeStarted;

        }

        @Override
        public void run() {

            String gatewayId = "GIO003_" + deviceId;
            System.out.println("device " + deviceId + " warming up");

            Set<String> deviceIds = Set.of("device_" + deviceId);
            try {
                Thread.sleep(random.nextInt(WARMUP_PERIOD));
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            System.out.println("device " + deviceId + " started");

            while (!stop) {

                MqttMessage message = new MqttMessage();
                KeepAlive keepAlive = new KeepAlive()
                        .setDeviceIds(deviceIds)
                        .setSentAt(OffsetDateTime.now())
                        .setGatewayId(gatewayId)
                        .setId(UUID.randomUUID().toString());
                try {
                    message.setPayload(objectMapper.writeValueAsBytes(keepAlive));

                    client.publish(BasicIOTClient.getOutTopic(gatewayId), message);
                    long sent = globalMessageCounter.incrementAndGet();
                    if (sent % 100 == 0) {
                        System.out.println("sent " + sent + " messages in " + (System.currentTimeMillis() - timeStarted) + " ms");
                    }

                } catch (MqttException e) {
                    e.printStackTrace();
                } catch (JsonProcessingException e) {
                    throw new RuntimeException(e);
                } finally {
                    try {
                        Thread.sleep(TIME_BETWEEN_MESSAGES); // Sleep for 5 minutes
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                }
            }


            System.out.println("stopping device " + deviceId);


        }
    }
}
