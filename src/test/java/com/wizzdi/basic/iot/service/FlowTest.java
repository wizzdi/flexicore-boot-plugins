package com.wizzdi.basic.iot.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.flexicore.security.SecurityContextBase;
import com.wizzdi.basic.iot.client.*;
import com.wizzdi.basic.iot.model.*;
import com.wizzdi.basic.iot.service.app.App;
import com.wizzdi.basic.iot.service.app.TestEntities;
import com.wizzdi.basic.iot.service.request.*;
import com.wizzdi.basic.iot.service.response.ChangeStateResponse;
import com.wizzdi.basic.iot.service.service.DeviceService;
import com.wizzdi.basic.iot.service.service.DeviceStateService;
import com.wizzdi.basic.iot.service.service.DeviceTypeService;
import com.wizzdi.basic.iot.service.service.GatewayService;
import com.wizzdi.flexicore.security.request.BasicPropertiesFilter;
import com.wizzdi.flexicore.security.response.PaginationResponse;
import org.apache.commons.codec.binary.Base64;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.nio.charset.StandardCharsets;
import java.security.PublicKey;
import java.util.*;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.atomic.AtomicBoolean;

@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT, classes = App.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@ActiveProfiles("test")

public class FlowTest {

    private static final Logger logger = LoggerFactory.getLogger(FlowTest.class);

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
    private PublicKey clientPublicKey;
    @Value("${basic.iot.test.id:iot-client}")
    private String clientId;
    @Autowired
    private DeviceStateService deviceStateService;
    @Value("${basic.iot.connectivityCheckInterval:60000}")
    private long checkInterval;
    private boolean stopKeepAlive;
    private Set<String> keepAliveDevices = new HashSet<>();
    private Thread keepAliveThread;
    private Gateway gateway;

    @AfterAll
    private void destroy() {
        stopKeepAlive();

    }
    @BeforeAll
    private void start() {
        TestEntities.clearMessageSubscribers();

    }

    private void stopKeepAlive() {
        stopKeepAlive = true;
        keepAliveThread.interrupt();
    }

    @Test
    @Order(1)
    public void testRegister() throws JsonProcessingException, ExecutionException, InterruptedException, TimeoutException {
        IOTMessageSubscriber iotMessageSubscriber = f -> {
            if (f instanceof BadMessageReceived) {
                BadMessageReceived badMessageReceived = (BadMessageReceived) f;
                logger.info("Bad Message Received: " + badMessageReceived.getError());
            }
        };
        TestEntities.addMessageSubscriber(iotMessageSubscriber);
        RegisterGateway registerGateway = new RegisterGateway()
                .setPublicKey(Base64.encodeBase64String(clientPublicKey.getEncoded()))
                .setGatewayId(clientId)

                .setId(UUID.randomUUID().toString());
        RegisterGatewayReceived registerGatewayReceived = testBasicIOTClient.request(registerGateway);
        Assertions.assertNotNull(registerGatewayReceived);


    }

    @Test
    @Order(2)
    public void testApproveGateway() {
        PaginationResponse<Gateway> approveResponse = gatewayService.approveGateways(adminSecurityContext, new ApproveGatewaysRequest().setPendingGatewayFilter(new PendingGatewayFilter().setGatewayIds(Collections.singleton(clientId))));
        gateway = approveResponse.getList().stream().filter(f -> f.getRemoteId().equals(clientId)).findFirst().orElse(null);
        Assertions.assertNotNull(gateway);
        Assertions.assertNotNull(gateway.getLastConnectivityChange());
        Assertions.assertEquals(Connectivity.OFF,gateway.getLastConnectivityChange().getConnectivity());
        startKeepAlive();

    }

    private void startKeepAlive() {
        keepAliveThread= new Thread(() -> {
            logger.info("started keep alive");
            while (!stopKeepAlive) {
                try {
                    testBasicIOTClient.sendMessage(new KeepAlive().setDeviceIds(keepAliveDevices), clientId);
                } catch (JsonProcessingException e) {
                    logger.error("failed sending keep alive ", e);
                }
                try {
                    synchronized (keepAliveDevices){
                        keepAliveDevices.wait(5000);
                    }
                } catch (InterruptedException e) {
                    logger.error("failed sleeping", e);
                    stopKeepAlive = true;
                }
            }
            logger.info("stopped keep alive");
        });
        keepAliveThread.start();
    }


    @Test
    @Order(3)
    public void testDeviceStateChanged() throws JsonProcessingException, ExecutionException, InterruptedException, TimeoutException {
        StateChangedReceived stateChangedReceived = testBasicIOTClient.request(new StateChanged().setDeviceId(DEVICE_ID).setDeviceType(DEVICE_TYPE).setValue("dim", 30));
        Assertions.assertNotNull(stateChangedReceived);
        DeviceType deviceType = deviceTypeService.listAllDeviceTypes(adminSecurityContext, new DeviceTypeFilter().setBasicPropertiesFilter(new BasicPropertiesFilter().setNames(Collections.singleton(DEVICE_TYPE)))).stream().findFirst().orElse(null);
        Assertions.assertNotNull(deviceType);
        Device device = deviceService.listAllDevices(adminSecurityContext, new DeviceFilter().setRemoteIds(Collections.singleton(DEVICE_ID))).stream().findFirst().orElse(null);
        Assertions.assertNotNull(device);
        Assertions.assertEquals(deviceType.getId(), device.getDeviceType().getId());
        Assertions.assertEquals(device.getRemoteId(), DEVICE_ID);
        Assertions.assertEquals(30, device.getOther().get("dim"));
        Assertions.assertNotNull(device.getLastConnectivityChange());
        Assertions.assertEquals(Connectivity.OFF,device.getLastConnectivityChange().getConnectivity());
        synchronized (keepAliveDevices){
            keepAliveDevices.add(device.getRemoteId());
            keepAliveDevices.notifyAll();
        }


    }

    @Test
    @Order(4)
    public void testUpdateSchema() throws JsonProcessingException, ExecutionException, InterruptedException, TimeoutException {
        UpdateStateSchemaReceived updateStateSchemaReceived = testBasicIOTClient.request(new UpdateStateSchema().setDeviceType(DEVICE_TYPE).setVersion(1).setJsonSchema(jsonSchema));
        Assertions.assertNotNull(updateStateSchemaReceived);
        DeviceType deviceType = deviceTypeService.listAllDeviceTypes(adminSecurityContext, new DeviceTypeFilter().setBasicPropertiesFilter(new BasicPropertiesFilter().setNames(Collections.singleton(DEVICE_TYPE)))).stream().findFirst().orElse(null);
        Assertions.assertNotNull(deviceType);
        Assertions.assertEquals(jsonSchema, deviceType.getStateJsonSchema());


    }

    @Test
    @Order(5)
    public void testDeviceStateChange() throws JsonProcessingException, ExecutionException, InterruptedException, TimeoutException {
        ChangeStateRequest changeStateRequest = new ChangeStateRequest().setDeviceFilter(new DeviceFilter().setGatewayIds(Collections.singleton(gateway.getId()))).setValue("dim", 94);
        deviceStateService.validate(changeStateRequest, adminSecurityContext);
        AtomicBoolean received = new AtomicBoolean(false);
        synchronized (changeStateRequest) {
            IOTMessageSubscriber iotMessageSubscriber = f -> handleMessage(f, testBasicIOTClient, received);
            TestEntities.addMessageSubscriber(iotMessageSubscriber);
            ChangeStateResponse changeStateResponse = deviceStateService.changeState(adminSecurityContext, changeStateRequest);
            Assertions.assertFalse(changeStateResponse.getDevicesExecutedOn().isEmpty());
            changeStateRequest.wait(5000);
            TestEntities.removeMessageSubscriber(iotMessageSubscriber);
        }

        Assertions.assertTrue(received.get());

    }

    @Test
    @Order(6)
    public void testConnectivity() throws InterruptedException {
        checkDevicesAndGateways(Connectivity.ON);
        stopKeepAlive();
        logger.info("waiting "+checkInterval*2 +" ms till we detect devices are off");
        Thread.sleep(checkInterval*2);
        checkDevicesAndGateways(Connectivity.OFF);

    }

    private void checkDevicesAndGateways(Connectivity expected) {
        Gateway gateway = gatewayService.listAllGateways(null, new GatewayFilter().setRemoteIds(Collections.singleton(clientId))).stream().findFirst().orElse(null);
        Assertions.assertNotNull(gateway);
        Assertions.assertNotNull(gateway.getLastConnectivityChange());
        Assertions.assertEquals(expected,gateway.getLastConnectivityChange().getConnectivity());
        List<Device> devices=deviceService.listAllDevices(null,new DeviceFilter().setGateways(Collections.singletonList(gateway)));
        for (Device device : devices) {

            Assertions.assertNotNull(device.getLastConnectivityChange());
            Assertions.assertEquals(expected,device.getLastConnectivityChange().getConnectivity());
        }
    }


    private void handleMessage(IOTMessage iotMessage, BasicIOTClient basicIOTClient, AtomicBoolean lock) {
        if (iotMessage instanceof ChangeState) {
            synchronized (lock) {
                try {
                    logger.info("Change state received");
                    ChangeState changeState = (ChangeState) iotMessage;
                    basicIOTClient.reply(new ChangeStateReceived().setChangeStateId(iotMessage.getId()), iotMessage);
                    logger.info("replied with ChangeStateReceived");

                    basicIOTClient.sendMessage(new StateChanged().setOtherProperties(changeState.getValues()));
                    logger.info("sending StateChanged");
                    lock.set(true);
                } catch (Exception e) {
                    logger.error("failed sending message", e);
                } finally {
                    lock.notifyAll();
                }
            }


        }
    }

}
