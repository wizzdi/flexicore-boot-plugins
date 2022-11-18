package com.wizzdi.basic.iot.service;

import com.flexicore.security.SecurityContextBase;
import com.wizzdi.basic.iot.client.*;
import com.wizzdi.basic.iot.model.Connectivity;
import com.wizzdi.basic.iot.model.Device;
import com.wizzdi.basic.iot.model.DeviceType;
import com.wizzdi.basic.iot.model.Gateway;
import com.wizzdi.basic.iot.service.app.App;
import com.wizzdi.basic.iot.service.request.ApproveGatewaysRequest;
import com.wizzdi.basic.iot.service.request.DeviceFilter;
import com.wizzdi.basic.iot.service.request.DeviceTypeFilter;
import com.wizzdi.basic.iot.service.request.PendingGatewayFilter;
import com.wizzdi.basic.iot.service.service.BasicIOTLogic;
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
import org.springframework.context.annotation.Lazy;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.Collections;
import java.util.UUID;

@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT, classes = App.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@ActiveProfiles("test")

public class LogicTests {

    private static final Logger logger = LoggerFactory.getLogger(LogicTests.class);

    private static final String GATEWAY_ID="test_gateway";
    public static final String DEVICE_ID = "test";
    public static final String DEVICE_TYPE = "light";
    public static final String JSON_SCHEMA_V2 = "v2";
    public static final String JSON_SCHEMA_V1 = "v1";
    @Autowired
    @Lazy
    private SecurityContextBase adminSecurityContext;
    @Autowired
    @Lazy
    private BasicIOTLogic basicIOTLogic;
    @Autowired
    private GatewayService gatewayService;
    @Autowired
    private DeviceService deviceService;
    @Autowired
    private DeviceTypeService deviceTypeService;





    @Test
    @Order(1)
    public void testRegister()  {
        RegisterGatewayReceived test = (RegisterGatewayReceived) basicIOTLogic.executeLogic(new RegisterGateway().setPublicKey("test").setId(UUID.randomUUID().toString()).setGatewayId(GATEWAY_ID));
        PaginationResponse<Gateway> approveResponse = gatewayService.approveGateways(adminSecurityContext, new ApproveGatewaysRequest().setPendingGatewayFilter(new PendingGatewayFilter().setGatewayIds(Collections.singleton(GATEWAY_ID))));
        Gateway gateway = approveResponse.getList().stream().filter(f -> f.getRemoteId().equals(GATEWAY_ID)).findFirst().orElse(null);
        Assertions.assertNotNull(gateway);
       Assertions.assertEquals(gateway.getRemoteId(),GATEWAY_ID);


    }

    @Test
    @Order(2)
    public void testStateChanged() {
        StateChangedReceived stateChangedReceived = (StateChangedReceived) basicIOTLogic.executeLogic(new StateChanged().setDeviceId(DEVICE_ID).setDeviceType(DEVICE_TYPE).setValue("dim", 30).setGatewayId(GATEWAY_ID).setId(UUID.randomUUID().toString()));
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

    }

   
    @Test
    @Order(4)
    public void testUpdateSchema() throws InterruptedException {
        UpdateStateSchemaReceived updateStateSchemaReceived = (UpdateStateSchemaReceived) basicIOTLogic.executeLogic(new UpdateStateSchema().setDeviceId(DEVICE_ID).setDeviceType(DEVICE_TYPE).setVersion(1).setJsonSchema(JSON_SCHEMA_V1).setGatewayId(GATEWAY_ID).setId(UUID.randomUUID().toString()));
        Assertions.assertNotNull(updateStateSchemaReceived);
        Device device = deviceService.listAllDevices(adminSecurityContext, new DeviceFilter().setRemoteIds(Collections.singleton(DEVICE_ID))).stream().findFirst().orElse(null);
        Assertions.assertNotNull(device);
        Assertions.assertNotNull(device.getCurrentSchema());
        Assertions.assertEquals(JSON_SCHEMA_V1, device.getCurrentSchema().getStateJsonSchema());


    }
    @Test
    @Order(5)
    public void testUpdateSchemaV2() throws InterruptedException {
        UpdateStateSchemaReceived updateStateSchemaReceived = (UpdateStateSchemaReceived) basicIOTLogic.executeLogic(new UpdateStateSchema().setDeviceId(DEVICE_ID).setDeviceType(DEVICE_TYPE).setVersion(2).setJsonSchema(JSON_SCHEMA_V2).setGatewayId(GATEWAY_ID).setId(UUID.randomUUID().toString()));
        Assertions.assertNotNull(updateStateSchemaReceived);
        Device device = deviceService.listAllDevices(adminSecurityContext, new DeviceFilter().setRemoteIds(Collections.singleton(DEVICE_ID))).stream().findFirst().orElse(null);
        Assertions.assertNotNull(device);
        Assertions.assertNotNull(device.getCurrentSchema());
        Assertions.assertEquals(JSON_SCHEMA_V2, device.getCurrentSchema().getStateJsonSchema());


    }
    @Test
    @Order(6)
    public void testSetJsonSchema() throws InterruptedException {
        SetStateSchemaReceived updateStateSchemaReceived = (SetStateSchemaReceived) basicIOTLogic.executeLogic(new SetStateSchema().setDeviceId(DEVICE_ID).setDeviceType(DEVICE_TYPE).setVersion(1).setGatewayId(GATEWAY_ID).setId(UUID.randomUUID().toString()));
        Assertions.assertNotNull(updateStateSchemaReceived);
        Assertions.assertTrue(updateStateSchemaReceived.isFound());
        Device device = deviceService.listAllDevices(adminSecurityContext, new DeviceFilter().setRemoteIds(Collections.singleton(DEVICE_ID))).stream().findFirst().orElse(null);
        Assertions.assertNotNull(device);
        Assertions.assertNotNull(device.getCurrentSchema());
        Assertions.assertEquals(JSON_SCHEMA_V1, device.getCurrentSchema().getStateJsonSchema());



    }

    @Test
    @Order(7)
    public void testSetJsonSchemaWhenMissing() throws InterruptedException {
        SetStateSchemaReceived updateStateSchemaReceived = (SetStateSchemaReceived) basicIOTLogic.executeLogic(new SetStateSchema().setDeviceId(DEVICE_ID).setDeviceType(DEVICE_TYPE).setVersion(3).setGatewayId(GATEWAY_ID).setId(UUID.randomUUID().toString()));
        Assertions.assertNotNull(updateStateSchemaReceived);
        Assertions.assertFalse(updateStateSchemaReceived.isFound());
        Device device = deviceService.listAllDevices(adminSecurityContext, new DeviceFilter().setRemoteIds(Collections.singleton(DEVICE_ID))).stream().findFirst().orElse(null);
        Assertions.assertNotNull(device);
        Assertions.assertNotNull(device.getCurrentSchema());
        Assertions.assertEquals(JSON_SCHEMA_V1, device.getCurrentSchema().getStateJsonSchema());

    }

}
