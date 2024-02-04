package com.wizzdi.basic.iot.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.wizzdi.basic.iot.client.*;
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
import java.util.concurrent.atomic.AtomicLong;

public class StressTest {

    private static final int TEST_FACTOR = 1;
    private static final int CONNECT_SEQUENCE_FACTOR=3;
    private static final ObjectMapper objectMapper = new ObjectMapper()
            .disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES)
            .registerModule(new JavaTimeModule());
    public static final int TIME_BETWEEN_MESSAGES = 5*60*1000;
    public static final int WARMUP_PERIOD = 5*60*1000;

    private static StateChanged stateChangedExample;

    private static SetStateSchema setStateSchema;



    private static final Random random = new Random();

    public static void main(String[] args) throws InterruptedException, MqttException, IOException {
        String broker=args[0];
        String username = args[1];
        String password = args[2];
        String[] allDevices = IOUtils.resourceToString("devices.csv", StandardCharsets.UTF_8,StressTest.class.getClassLoader()).split(",");
        stateChangedExample= (StateChanged) objectMapper.readValue(IOUtils.resourceToString("stateChangedExample.json", StandardCharsets.UTF_8,StressTest.class.getClassLoader()),IOTMessage.class);
        setStateSchema= (SetStateSchema) objectMapper.readValue(IOUtils.resourceToString("setStateSchema.json", StandardCharsets.UTF_8,StressTest.class.getClassLoader()), IOTMessage.class);
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
        AtomicLong keepAliveCounter = new AtomicLong();
        AtomicLong stateChangedCounter = new AtomicLong();
        AtomicLong setStateSchemaCounter = new AtomicLong();

        long startTime = System.currentTimeMillis();
        for (String deviceId : allDevices) {
            for (int i = 0; i < TEST_FACTOR; i++) {
                executor.submit(new Device(client,deviceId, keepAliveCounter,stateChangedCounter,setStateSchemaCounter, startTime));
            }
        }
        executor.awaitTermination(Long.MAX_VALUE, java.util.concurrent.TimeUnit.DAYS);

    }

    public static class Device implements Runnable {
        private final String deviceId;
        private final AtomicLong keepAliveCounter;

        private final AtomicLong stateChangedCounter;
        private final AtomicLong setStateSchemaCounter;
        private final long timeStarted;
        private final MqttClient client;
        private OffsetDateTime lastConnectTime=null;
        private boolean stop;

        public Device(MqttClient client, String deviceId, AtomicLong keepAliveCounter,AtomicLong stateChangedCounter,AtomicLong setStateSchemaCounter, long timeStarted) {
            this.client = client;
            this.deviceId = deviceId;
            this.keepAliveCounter = keepAliveCounter;
            this.stateChangedCounter=stateChangedCounter;
            this.setStateSchemaCounter=setStateSchemaCounter;
            this.timeStarted = timeStarted;

        }

        @Override
        public void run() {

            String gatewayId = "GIO003_" + deviceId;
            System.out.println("device " + deviceId + " warming up");

            String deviceId = "device_" + this.deviceId;
            Set<String> deviceIds = Set.of(deviceId);
            try {
                Thread.sleep(random.nextInt(WARMUP_PERIOD));
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            System.out.println("device " + this.deviceId + " started");
            while (!stop) {
                if(lastConnectTime==null||lastConnectTime.plusHours(1).isBefore(OffsetDateTime.now())){
                    for (int i = 0; i < CONNECT_SEQUENCE_FACTOR; i++) {
                        connectSequence(client, gatewayId, deviceId, stateChangedCounter, setStateSchemaCounter);
                        lastConnectTime=OffsetDateTime.now();
                    }

                }

                MqttMessage message = new MqttMessage();
                KeepAlive keepAlive = new KeepAlive()
                        .setDeviceIds(deviceIds)
                        .setSentAt(OffsetDateTime.now())
                        .setGatewayId(gatewayId)
                        .setId(UUID.randomUUID().toString());
                try {
                    message.setPayload(objectMapper.writeValueAsBytes(keepAlive));

                    client.publish(BasicIOTClient.getOutTopic(gatewayId), message);
                    incPrintCounter(keepAliveCounter,"keep alive");

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


            System.out.println("stopping device " + this.deviceId);


        }

        private void incPrintCounter(AtomicLong counter,String type) {
            long sent = counter.incrementAndGet();
            if (sent % 100 == 0) {
                System.out.println("sent " + sent + " "+type+" messages in " + (System.currentTimeMillis() - timeStarted) + " ms");
            }
        }

        private void connectSequence(MqttClient client, String gatewayId, String deviceId, AtomicLong stateChangedCounter, AtomicLong setStateSchemaCounter) {
            try {
                sendStateChanged(client, gatewayId, deviceId,stateChangedCounter);
                sendSetStateSchema(client, gatewayId, deviceId,setStateSchemaCounter);


            }
            catch (Exception e){
                System.out.println("error sending connect sequence");
                e.printStackTrace();
            }
        }

        private void sendStateChanged(MqttClient client, String gatewayId, String deviceId, AtomicLong stateChangedCounter) throws JsonProcessingException, MqttException {
            StateChanged stateChanged = new StateChanged(stateChangedExample)
                    .setDeviceId(deviceId)
                    .setGatewayId(gatewayId)
                    .setSentAt(OffsetDateTime.now());
            stateChanged.getValues().put("StressTest", OffsetDateTime.now().toString());
            MqttMessage message = new MqttMessage();
            message.setPayload(objectMapper.writeValueAsBytes(stateChanged));
            client.publish(BasicIOTClient.getOutTopic(gatewayId), message);
            incPrintCounter(stateChangedCounter,"state changed");

        }


        private void sendSetStateSchema(MqttClient client, String gatewayId, String deviceId, AtomicLong setStateSchemaCounter) throws JsonProcessingException, MqttException {
            SetStateSchema stateChanged = new SetStateSchema(setStateSchema)
                    .setDeviceId(deviceId)
                    .setGatewayId(gatewayId)
                    .setSentAt(OffsetDateTime.now())
                    .setId(UUID.randomUUID().toString());
            MqttMessage message = new MqttMessage();
            message.setPayload(objectMapper.writeValueAsBytes(stateChanged));
            client.publish(BasicIOTClient.getOutTopic(gatewayId), message);
            incPrintCounter(setStateSchemaCounter,"set state schema");

        }
    }
}
