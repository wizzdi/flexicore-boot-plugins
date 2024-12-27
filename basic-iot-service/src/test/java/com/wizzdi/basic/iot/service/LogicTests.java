package com.wizzdi.basic.iot.service;

import com.wizzdi.flexicore.security.configuration.SecurityContext;
import com.wizzdi.basic.iot.client.*;
import com.wizzdi.basic.iot.model.Connectivity;
import com.wizzdi.basic.iot.model.Device;
import com.wizzdi.basic.iot.model.DeviceType;
import com.wizzdi.basic.iot.model.Gateway;
import com.wizzdi.basic.iot.service.app.App;
import com.wizzdi.basic.iot.service.request.*;
import com.wizzdi.basic.iot.service.service.*;
import com.wizzdi.flexicore.security.request.BasicPropertiesFilter;
import com.wizzdi.flexicore.security.response.PaginationResponse;
import com.wizzdi.maps.model.MapIcon;
import com.wizzdi.maps.model.MappedPOI;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.context.annotation.Lazy;
import org.springframework.http.ResponseEntity;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.hivemq.HiveMQContainer;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.testcontainers.utility.DockerImageName;

import java.io.File;
import java.time.OffsetDateTime;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT, classes = App.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@Testcontainers
@ActiveProfiles("test")
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE) // deactivate the default behaviour
@DirtiesContext
public class LogicTests {

    private final static PostgreSQLContainer postgresqlContainer = new PostgreSQLContainer("postgres:15")

            .withDatabaseName("flexicore-test")
            .withUsername("flexicore")
            .withPassword("flexicore");
    private final static HiveMQContainer hivemqCe = new HiveMQContainer(DockerImageName.parse("hivemq/hivemq-ce").withTag("2023.5"));



    static {
        postgresqlContainer.start();
        hivemqCe.start();

    }

    @DynamicPropertySource
    static void setProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgresqlContainer::getJdbcUrl);
        registry.add("spring.datasource.username", postgresqlContainer::getUsername);
        registry.add("spring.datasource.password", postgresqlContainer::getPassword);

        registry.add("basic.iot.mqtt.url", ()->"tcp://"+hivemqCe.getHost()+":"+hivemqCe.getMqttPort());
        KeyPairForTests.KeyPair keyPair = KeyPairForTests.getKeyPair();
        File privateKey = keyPair.privateKey();
        registry.add("basic.iot.keyPath", privateKey::getAbsolutePath);

    }

    private static final Logger logger = LoggerFactory.getLogger(LogicTests.class);

    private static final String GATEWAY_ID = "test_gateway";
    public static final String DEVICE_ID = "test";
    public static final String DEVICE_TYPE = "light";
    public static final String JSON_SCHEMA_V2 = "v2";
    public static final String JSON_SCHEMA_V1 = "v1";
    @Autowired
    @Lazy
    private SecurityContext adminSecurityContext;
    @Autowired
    @Lazy
    private BasicIOTLogic basicIOTLogic;
    @Autowired
    private GatewayService gatewayService;
    @Autowired
    private DeviceService deviceService;
    @Autowired
    private DeviceTypeService deviceTypeService;
    @Autowired
    @Qualifier("gatewayMapIcon")
    private MapIcon gatewayMapIcon;
    @Autowired
    private SchemaActionService schemaActionService;
    @Autowired
    private TestRestTemplate testRestTemplate;


    @Test
    @Order(1)
    public void testRegister() {
        RegisterGatewayReceived test = basicIOTLogic.executeLogic(new RegisterGateway().setPublicKey("test").setId(UUID.randomUUID().toString()).setGatewayId(GATEWAY_ID)).setSentAt(OffsetDateTime.now());
        PaginationResponse<Gateway> approveResponse = gatewayService.approveGateways(adminSecurityContext, new ApproveGatewaysRequest().setPendingGatewayFilter(new PendingGatewayFilter().setGatewayIds(Collections.singleton(GATEWAY_ID))));
        Gateway gateway = approveResponse.getList().stream().filter(f -> f.getRemoteId().equals(GATEWAY_ID)).findFirst().orElse(null);
        Assertions.assertNotNull(gateway);
        Assertions.assertEquals(GATEWAY_ID, gateway.getRemoteId());
        validateMappedPOI(gateway.getMappedPOI());
        Assertions.assertEquals(gatewayMapIcon.getId(), gateway.getMappedPOI().getMapIcon().getId());


    }

    private void validateMappedPOI(MappedPOI mappedPOI) {
        Assertions.assertNotNull(mappedPOI);
        Assertions.assertNotNull(mappedPOI.getRelatedId());
        Assertions.assertNotNull(mappedPOI.getRelatedType());
        Assertions.assertNotNull(mappedPOI.getExternalId());
        Assertions.assertNotNull(mappedPOI.getName());
        validateMappedPOIIcon(mappedPOI.getMapIcon());

    }

    private void validateMappedPOIIcon(MapIcon mapIcon) {
        Assertions.assertNotNull(mapIcon);
        Assertions.assertNotNull(mapIcon.getExternalId());
        Assertions.assertNotNull(mapIcon.getRelatedType());
        Assertions.assertNotNull(mapIcon.getName());

    }

    @Test
    @Order(2)
    public void testStateChanged() {
        StateChangedReceived stateChangedReceived = (StateChangedReceived) basicIOTLogic.executeLogic(new StateChanged().setStatus("dim").setLatitude(50D).setLatitude(50D).setDeviceId(DEVICE_ID).setDeviceType(DEVICE_TYPE).setRoomId("room").setFloorId("floor").setBuildingId("building").setValue("dim", 30).setGatewayId(GATEWAY_ID).setSentAt(OffsetDateTime.now()).setId(UUID.randomUUID().toString()));
        Assertions.assertNotNull(stateChangedReceived);
        DeviceType deviceType = deviceTypeService.listAllDeviceTypes(adminSecurityContext, new DeviceTypeFilter().setBasicPropertiesFilter(new BasicPropertiesFilter().setNames(Collections.singleton(DEVICE_TYPE)))).stream().findFirst().orElse(null);
        Assertions.assertNotNull(deviceType);
        Device device = deviceService.listAllDevices(adminSecurityContext, new DeviceFilter().setRemoteIds(Collections.singleton(DEVICE_ID))).stream().findFirst().orElse(null);
        Assertions.assertNotNull(device);
        Assertions.assertEquals(deviceType.getId(), device.getDeviceType().getId());
        Assertions.assertEquals(DEVICE_ID, device.getRemoteId());
        Assertions.assertEquals(30, device.getDeviceProperties().get("dim"));
        Assertions.assertNotNull(device.getLastConnectivityChange());
        Assertions.assertEquals(Connectivity.ON, device.getLastConnectivityChange().getConnectivity()); // statechange is counted as keepalive
        validateMappedPOI(device.getMappedPOI());
        Assertions.assertEquals(Device.class.getCanonicalName(), device.getMappedPOI().getMapIcon().getRelatedType());
        Assertions.assertTrue(device.getMappedPOI().getMapIcon().getExternalId().contains("light"));
        Assertions.assertTrue(device.getMappedPOI().getMapIcon().getName().contains("dim"));
        Assertions.assertNotNull(device.getMappedPOI().getRoom());
        Assertions.assertNotNull(device.getMappedPOI().getRoom().getBuildingFloor());
        Assertions.assertNotNull(device.getMappedPOI().getRoom().getBuildingFloor().getBuilding());

    }


    @Test
    @Order(4)
    public void testUpdateSchema() throws InterruptedException {
        UpdateStateSchema updateStateSchema = new UpdateStateSchema()
                .setDeviceId(DEVICE_ID)
                .setDeviceType(DEVICE_TYPE)
                .setVersion(1)
                .setJsonSchema(JSON_SCHEMA_V1)
                .setSchemaActions(List.of(new SchemaAction().setJsonSchema("action").setName("action").setId("action")))
                .setGatewayId(GATEWAY_ID)
                .setSentAt(OffsetDateTime.now())
                .setId(UUID.randomUUID().toString());
        UpdateStateSchemaReceived updateStateSchemaReceived = (UpdateStateSchemaReceived) basicIOTLogic.executeLogic(updateStateSchema);
        Assertions.assertNotNull(updateStateSchemaReceived);
        Device device = deviceService.listAllDevices(adminSecurityContext, new DeviceFilter().setRemoteIds(Collections.singleton(DEVICE_ID))).stream().findFirst().orElse(null);
        Assertions.assertNotNull(device);
        Assertions.assertNotNull(device.getCurrentSchema());
        Assertions.assertEquals(JSON_SCHEMA_V1, device.getCurrentSchema().getStateJsonSchema());
        com.wizzdi.basic.iot.model.SchemaAction schemaAction = schemaActionService.listAllSchemaActions(adminSecurityContext, new SchemaActionFilter().setStateSchemas(Collections.singletonList(device.getCurrentSchema()))).stream().findFirst().orElse(null);
        Assertions.assertNotNull(schemaAction);
        Assertions.assertEquals("action",schemaAction.getName());
        Assertions.assertEquals("action",schemaAction.getExternalId());
        Assertions.assertEquals("action",schemaAction.getActionSchema());


    }

    @Test
    @Order(5)
    public void testUpdateSchemaV2() throws InterruptedException {
        UpdateStateSchemaReceived updateStateSchemaReceived = (UpdateStateSchemaReceived) basicIOTLogic.executeLogic(new UpdateStateSchema().setDeviceId(DEVICE_ID).setDeviceType(DEVICE_TYPE).setVersion(2).setJsonSchema(JSON_SCHEMA_V2).setGatewayId(GATEWAY_ID).setSentAt(OffsetDateTime.now()).setId(UUID.randomUUID().toString()));
        Assertions.assertNotNull(updateStateSchemaReceived);
        Device device = deviceService.listAllDevices(adminSecurityContext, new DeviceFilter().setRemoteIds(Collections.singleton(DEVICE_ID))).stream().findFirst().orElse(null);
        Assertions.assertNotNull(device);
        Assertions.assertNotNull(device.getCurrentSchema());
        Assertions.assertEquals(JSON_SCHEMA_V2, device.getCurrentSchema().getStateJsonSchema());


    }

    @Test
    @Order(6)
    public void testSetJsonSchema() throws InterruptedException {
        SetStateSchemaReceived updateStateSchemaReceived = (SetStateSchemaReceived) basicIOTLogic.executeLogic(new SetStateSchema().setDeviceId(DEVICE_ID).setDeviceType(DEVICE_TYPE).setVersion(1).setGatewayId(GATEWAY_ID).setSentAt(OffsetDateTime.now()).setId(UUID.randomUUID().toString()));
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
        SetStateSchemaReceived updateStateSchemaReceived = (SetStateSchemaReceived) basicIOTLogic.executeLogic(new SetStateSchema().setDeviceId(DEVICE_ID).setDeviceType(DEVICE_TYPE).setVersion(3).setGatewayId(GATEWAY_ID).setSentAt(OffsetDateTime.now()).setId(UUID.randomUUID().toString()));
        Assertions.assertNotNull(updateStateSchemaReceived);
        Assertions.assertFalse(updateStateSchemaReceived.isFound());
        Device device = deviceService.listAllDevices(adminSecurityContext, new DeviceFilter().setRemoteIds(Collections.singleton(DEVICE_ID))).stream().findFirst().orElse(null);
        Assertions.assertNotNull(device);
        Assertions.assertNotNull(device.getCurrentSchema());
        Assertions.assertEquals(JSON_SCHEMA_V1, device.getCurrentSchema().getStateJsonSchema());

    }


}
