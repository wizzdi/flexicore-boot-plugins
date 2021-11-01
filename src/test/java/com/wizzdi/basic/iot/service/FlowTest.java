package com.wizzdi.basic.iot.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.flexicore.security.SecurityContextBase;
import com.wizzdi.basic.iot.client.*;
import com.wizzdi.basic.iot.model.ConnectivityChange;
import com.wizzdi.basic.iot.model.Device;
import com.wizzdi.basic.iot.model.DeviceType;
import com.wizzdi.basic.iot.model.Gateway;
import com.wizzdi.basic.iot.service.app.App;
import com.wizzdi.basic.iot.service.request.*;
import com.wizzdi.basic.iot.service.service.DeviceService;
import com.wizzdi.basic.iot.service.service.DeviceTypeService;
import com.wizzdi.basic.iot.service.service.GatewayService;
import com.wizzdi.flexicore.security.request.BasicPropertiesFilter;
import com.wizzdi.flexicore.security.response.PaginationResponse;
import org.apache.commons.codec.binary.Base64;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
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
import java.util.Collections;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT, classes = App.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@ActiveProfiles("test")

public class FlowTest {

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


    @Test
    @Order(1)
    public void testRegister() throws JsonProcessingException, ExecutionException, InterruptedException, TimeoutException {
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
        Assertions.assertTrue(approveResponse.getList().stream().anyMatch(f->f.getRemoteId().equals(clientId)));

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
        Assertions.assertEquals(deviceType.getId(),device.getDeviceType().getId());
        Assertions.assertEquals(device.getRemoteId(),DEVICE_ID);
        Assertions.assertEquals(30,device.getOther().get("dim"));


    }

    @Test
    @Order(4)
    public void testUpdateSchema() throws JsonProcessingException, ExecutionException, InterruptedException, TimeoutException {
        UpdateStateSchemaReceived updateStateSchemaReceived = testBasicIOTClient.request(new UpdateStateSchema().setDeviceType(DEVICE_TYPE).setVersion(1).setJsonSchema(jsonSchema));
        Assertions.assertNotNull(updateStateSchemaReceived);
        DeviceType deviceType = deviceTypeService.listAllDeviceTypes(adminSecurityContext, new DeviceTypeFilter().setBasicPropertiesFilter(new BasicPropertiesFilter().setNames(Collections.singleton(DEVICE_TYPE)))).stream().findFirst().orElse(null);
        Assertions.assertNotNull(deviceType);
        Assertions.assertEquals(jsonSchema,deviceType.getStateJsonSchema());



    }
}
