package com.wizzdi.basic.iot.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.flexicore.security.SecurityContextBase;
import com.wizzdi.basic.iot.client.*;
import com.wizzdi.basic.iot.model.Connectivity;
import com.wizzdi.basic.iot.model.Device;
import com.wizzdi.basic.iot.model.DeviceType;
import com.wizzdi.basic.iot.model.Gateway;
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
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.security.PublicKey;
import java.util.*;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;

@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT, classes = App.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@ActiveProfiles("test")

public class BadMessageFlowTest {

    private static final Logger logger = LoggerFactory.getLogger(BadMessageFlowTest.class);

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

    @BeforeAll
    private void start() {
        TestEntities.clearMessageSubscribers();

    }



    @Test
    @Order(1)
    public void testRegister() throws JsonProcessingException, ExecutionException, InterruptedException, TimeoutException {
        RegisterGateway registerGateway = new RegisterGateway()
                .setPublicKey("fake")
                .setGatewayId(clientId)

                .setId(UUID.randomUUID().toString());
        RegisterGatewayReceived registerGatewayReceived = testBasicIOTClient.request(registerGateway);
        Assertions.assertNotNull(registerGatewayReceived);


    }

    @Test
    @Order(2)
    public void testApproveGateway() {
        PaginationResponse<Gateway> approveResponse = gatewayService.approveGateways(adminSecurityContext, new ApproveGatewaysRequest().setPendingGatewayFilter(new PendingGatewayFilter().setGatewayIds(Collections.singleton(clientId))));
        Gateway gateway = approveResponse.getList().stream().filter(f -> f.getRemoteId().equals(clientId)).findFirst().orElse(null);
        Assertions.assertNotNull(gateway);
        Assertions.assertNotNull(gateway.getLastConnectivityChange());
        Assertions.assertEquals(Connectivity.OFF,gateway.getLastConnectivityChange().getConnectivity());

    }




    @Test
    @Order(3)
    public void testDeviceStateChanged() throws JsonProcessingException, ExecutionException, InterruptedException, TimeoutException {
        String id = UUID.randomUUID().toString();
        AtomicReference<BadMessageReceived> ref=new AtomicReference<>(null);
        IOTMessageSubscriber iotMessageSubscriber = f -> {
            if (f instanceof BadMessageReceived) {
                BadMessageReceived badMessageReceived = (BadMessageReceived) f;
                if (badMessageReceived.getOriginalMessage().contains(id)) {
                    ref.set(badMessageReceived);
                }
                logger.info("Bad Message Received: " + badMessageReceived.getError());
            }
        };
        TestEntities.addMessageSubscriber(iotMessageSubscriber);
        StateChangedReceived stateChangedReceived = testBasicIOTClient.request(new StateChanged().setDeviceId(DEVICE_ID).setDeviceType(DEVICE_TYPE).setValue("dim", 30).setId(id));
        TestEntities.removeMessageSubscriber(iotMessageSubscriber);
        BadMessageReceived badMessageReceived = ref.get();
        Assertions.assertNotNull(badMessageReceived);
        System.out.println(badMessageReceived+"");



    }


}
