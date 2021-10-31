package com.wizzdi.basic.iot.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.flexicore.security.SecurityContextBase;
import com.wizzdi.basic.iot.client.*;
import com.wizzdi.basic.iot.model.Device;
import com.wizzdi.basic.iot.model.DeviceType;
import com.wizzdi.basic.iot.model.Gateway;
import com.wizzdi.basic.iot.service.app.App;
import com.wizzdi.basic.iot.service.app.TestEntities;
import com.wizzdi.basic.iot.service.request.ApproveGatewaysRequest;
import com.wizzdi.basic.iot.service.request.DeviceFilter;
import com.wizzdi.basic.iot.service.request.DeviceTypeFilter;
import com.wizzdi.basic.iot.service.request.PendingGatewayFilter;
import com.wizzdi.basic.iot.service.service.DeviceService;
import com.wizzdi.basic.iot.service.service.DeviceTypeService;
import com.wizzdi.basic.iot.service.service.GatewayService;
import com.wizzdi.flexicore.security.request.BasicPropertiesFilter;
import com.wizzdi.flexicore.security.response.PaginationResponse;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Bean;
import org.springframework.integration.dsl.IntegrationFlow;
import org.springframework.integration.dsl.IntegrationFlows;
import org.springframework.integration.mqtt.inbound.MqttPahoMessageDrivenChannelAdapter;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageHandler;
import org.springframework.messaging.support.GenericMessage;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.Collections;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.atomic.AtomicReference;

@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT, classes = App.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@ActiveProfiles("test")

public class MqttFlowTest {
    private static final Logger logger = LoggerFactory.getLogger(MqttFlowTest.class);

    public static final String GATEWAY_ID = "Test";
    public static final String DEVICE_ID = "test";
    public static final String DEVICE_TYPE = "light";
    @Autowired
    private SecurityContextBase adminSecurityContext;
    @Autowired
    private BasicIOTClient testBasicIOTClient;
    @Autowired
    private GatewayService gatewayService;
    @Autowired
    private DeviceTypeService deviceTypeService;
    @Autowired
    private DeviceService deviceService;
    @Autowired
    private String jsonSchema;

    @Autowired
    private IntegrationFlow mqttOutboundFlow;
    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private MqttPahoMessageDrivenChannelAdapter mqttPahoMessageDrivenChannelAdapter;

    @BeforeAll
    private void init() {

    }

    @Test
    @Order(1)
    public void testRegister() throws ExecutionException, InterruptedException, TimeoutException {
        RegisterGateway registerGateway = new RegisterGateway()
                .setGatewayId(GATEWAY_ID)
                .setPublicKey("test")
                .setId(UUID.randomUUID().toString());
        RegisterGatewayReceived registerGatewayReceived = send(registerGateway);
        Assertions.assertNotNull(registerGatewayReceived);


    }

    public <T extends IOTMessage> T send(IOTMessage request) throws InterruptedException {
        String channelId = request.getId();
        mqttPahoMessageDrivenChannelAdapter.addTopic(channelId,1);
        AtomicReference<T> response = new AtomicReference<>(null);
        MessageHandler messageHandler = f -> {
            if (!channelId.equals(f.getHeaders().get("mqtt_receivedTopic", String.class))) {
                return;
            }
            synchronized (request) {
                try {
                    IOTMessage iotMessage = objectMapper.readValue((String) f.getPayload(), IOTMessage.class);
                    response.set((T) iotMessage);
                } catch (Exception e) {
                    logger.error("failed to parse message", e);
                }
                request.notifyAll();
            }
        };
        TestEntities.addHandler(messageHandler);
        synchronized (request) {
            try {
                GenericMessage<String> message = new GenericMessage<>(objectMapper.writeValueAsString(request), Map.of("mqtt_topic", "IOT_MESSAGES_SUBJECT", "reply-to", request.getId()));
                mqttOutboundFlow.getInputChannel().send(message);
            } catch (Exception e) {
                logger.error("error", e);
            }
            request.wait(5000);
            TestEntities.removeHandler(messageHandler);
        }
        return response.get();
    }


    @Test
    @Order(2)
    public void testApproveGateway() {
        PaginationResponse<Gateway> approveResponse = gatewayService.approveGateways(adminSecurityContext, new ApproveGatewaysRequest().setPendingGatewayFilter(new PendingGatewayFilter().setGatewayIds(Collections.singleton(GATEWAY_ID))));
        Assertions.assertTrue(approveResponse.getList().stream().anyMatch(f->f.getRemoteId().equals(GATEWAY_ID)));

    }



    @Test
    @Order(3)
    public void testDeviceStateChanged() throws JsonProcessingException, ExecutionException, InterruptedException, TimeoutException {
        StateChangedReceived stateChangedReceived = send(new StateChanged().setDeviceId(DEVICE_ID).setDeviceType(DEVICE_TYPE).setValue("dim", 30).setId(UUID.randomUUID().toString()));
        Assertions.assertNotNull(stateChangedReceived);
        DeviceType deviceType = deviceTypeService.listAllDeviceTypes(adminSecurityContext, new DeviceTypeFilter().setBasicPropertiesFilter(new BasicPropertiesFilter().setNames(Collections.singleton(DEVICE_TYPE)))).stream().findFirst().orElse(null);
        Assertions.assertNotNull(deviceType);
        Device device = deviceService.listAllDevices(adminSecurityContext, new DeviceFilter().setRemoteIds(Collections.singleton(DEVICE_ID))).stream().findFirst().orElse(null);
        Assertions.assertNotNull(device);
        Assertions.assertEquals(deviceType.getId(),device.getDeviceType().getId());
        Assertions.assertEquals(device.getRemoteId(),DEVICE_ID);
        Assertions.assertEquals(30,device.getOther().get("dim"));


    }

    @Test
    @Order(4)
    public void testUpdateSchema() throws JsonProcessingException, ExecutionException, InterruptedException, TimeoutException {
        UpdateStateSchemaReceived updateStateSchemaReceived = send(new UpdateStateSchema().setDeviceType(DEVICE_TYPE).setVersion(1).setJsonSchema(jsonSchema).setId(UUID.randomUUID().toString()));
        Assertions.assertNotNull(updateStateSchemaReceived);
        DeviceType deviceType = deviceTypeService.listAllDeviceTypes(adminSecurityContext, new DeviceTypeFilter().setBasicPropertiesFilter(new BasicPropertiesFilter().setNames(Collections.singleton(DEVICE_TYPE)))).stream().findFirst().orElse(null);
        Assertions.assertNotNull(deviceType);
        Assertions.assertEquals(jsonSchema,deviceType.getStateJsonSchema());



    }

}
