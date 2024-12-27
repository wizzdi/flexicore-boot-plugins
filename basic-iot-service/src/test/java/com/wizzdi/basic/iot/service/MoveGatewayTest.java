package com.wizzdi.basic.iot.service;

import com.flexicore.annotations.IOperation;
import com.flexicore.model.*;
import com.wizzdi.flexicore.security.configuration.SecurityContext;
import com.wizzdi.basic.iot.model.Device;
import com.wizzdi.basic.iot.model.DeviceType;
import com.wizzdi.basic.iot.model.Gateway;
import com.wizzdi.basic.iot.service.app.App;
import com.wizzdi.basic.iot.service.request.*;
import com.wizzdi.basic.iot.service.response.ImportGatewaysResponse;
import com.wizzdi.basic.iot.service.response.MoveGatewaysResponse;
import com.wizzdi.basic.iot.service.service.DeviceService;
import com.wizzdi.basic.iot.service.service.DeviceTypeService;
import com.wizzdi.basic.iot.service.service.GatewayMoveService;
import com.wizzdi.basic.iot.service.service.GatewayService;
import com.wizzdi.flexicore.file.model.FileResource;
import com.wizzdi.flexicore.file.request.FileResourceCreate;
import com.wizzdi.flexicore.file.service.FileResourceService;
import com.wizzdi.flexicore.security.interfaces.SecurityContextProvider;
import com.wizzdi.flexicore.security.request.*;
import com.wizzdi.flexicore.security.response.PaginationResponse;
import com.wizzdi.flexicore.security.service.SecurityTenantService;
import com.wizzdi.flexicore.security.service.SecurityUserService;
import com.wizzdi.flexicore.security.service.TenantToBaseclassService;
import com.wizzdi.flexicore.security.service.TenantToUserService;
import com.wizzdi.maps.model.MapIcon;
import com.wizzdi.maps.model.MappedPOI;
import com.wizzdi.maps.service.request.MappedPOICreate;
import com.wizzdi.maps.service.service.MapIconService;
import com.wizzdi.maps.service.service.MappedPOIService;
import com.wizzdi.segmantix.model.Access;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.assertj.core.util.Files;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.context.annotation.Lazy;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.hivemq.HiveMQContainer;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.UUID;

@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT, classes = App.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@Testcontainers
@ActiveProfiles("test")
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE) // deactivate the default behaviour
@DirtiesContext
public class MoveGatewayTest {
    private final static PostgreSQLContainer postgresqlContainer = new PostgreSQLContainer("postgres:15")

            .withDatabaseName("flexicore-test")
            .withUsername("flexicore")
            .withPassword("flexicore");
    private final static HiveMQContainer hivemqCe = new HiveMQContainer(DockerImageName.parse("hivemq/hivemq-ce").withTag("2023.5"));



    static {
        postgresqlContainer.start();
        hivemqCe.start();

    }

    @Autowired
    private GatewayMoveService gatewayMoveService;
    @Autowired
    private GatewayService gatewayService;
    @Autowired
    private TenantToUserService tenantToUserService;
    @Autowired
    private SecurityOperation allOps;
    @Autowired
    private Clazz securityWildcard;
    private RealUser realUser;

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

    private Gateway gateway;
    @Autowired
    private TestRestTemplate restTemplate;
    private FileResource csv;
    @Autowired
    private FileResourceService fileResourceService;
    @Autowired
    @Lazy
    private SecurityContext adminSecurityContext;
    @Autowired
    @Lazy
    private SecurityUserService securityUserService;
    @Autowired
    @Lazy
    private SecurityTenantService securityTenantService;
    @Autowired
    @Lazy
    private SecurityContextProvider securityContextProvider;
    @Autowired
    private DeviceService deviceService;
    @Autowired
    private MappedPOIService mappedPOIService;
    @Autowired
    private MapIconService mapIconService;
    @Autowired
    private DeviceTypeService deviceTypeService;
    private NewUserResult user1;
    private SecurityContext user1SecurityContext;
    private NewUserResult user2;
    @Autowired
    private TenantToBaseclassService tenantToBaseclassService;



    @BeforeAll
    public void init() throws IOException {
        restTemplate.getRestTemplate().setInterceptors(
                Collections.singletonList((request, body, execution) -> {
                    request.getHeaders()
                            .add("authenticationKey", "fake");
                    return execution.execute(request, body);
                }));
        user1= createUserInTenant("tenant1");
        user2= createUserInTenant("tenant2");
        SecurityUser securityUser = user1.securityUser();
        user1SecurityContext= securityContextProvider.getSecurityContext(securityUser);
        File file=new File(Files.temporaryFolderPath(),"test"+UUID.randomUUID().toString()+".csv");
        String csvS = IOUtils.resourceToString("gatewayImportForMove.csv", StandardCharsets.UTF_8, MoveGatewayTest.class.getClassLoader());
        FileUtils.writeStringToFile(file,csvS,StandardCharsets.UTF_8);
        csv=fileResourceService.createFileResource(new FileResourceCreate().setFullPath(file.getAbsolutePath()).setName("csv"),user1SecurityContext);

    }

    private NewUserResult createUserInTenant(String tenantName) {
        SecurityTenant newTenant = securityTenantService.createTenant(new SecurityTenantCreate().setName(tenantName), adminSecurityContext);
        SecurityUser securityUser = securityUserService.createSecurityUser(new SecurityUserCreate().setTenant(newTenant), adminSecurityContext);
        tenantToUserService.createTenantToUser(new TenantToUserCreate().setTenant(newTenant).setUser(securityUser).setDefaultTenant(true),adminSecurityContext);
        tenantToBaseclassService.createTenantToBaseclass(new TenantToBaseclassCreate().setTenant(newTenant).setClazz(securityWildcard).setOperation(allOps).setAccess(Access.allow),adminSecurityContext);
        return new NewUserResult(securityUser,newTenant);
    }
    record NewUserResult(SecurityUser securityUser,SecurityTenant securityTenant){}

    @Test
    @Order(1)
    public void testGatewayImport() {
        ImportGatewaysResponse importGatewaysResponse = gatewayService.importGateways(user1SecurityContext, new ImportGatewaysRequest().setCsv(csv));
        Assertions.assertNotNull(importGatewaysResponse);
        Assertions.assertEquals(12,importGatewaysResponse.getImportedGateways().getList().size());
        DeviceType testDevice = deviceTypeService.getOrCreateDeviceType("testDevice", true, user1SecurityContext);

        for (Gateway gateway : importGatewaysResponse.getImportedGateways().getList()) {
            MapIcon mapIcon= deviceTypeService.getOrCreateMapIcon("ON","testDevice", Device.class,user1SecurityContext);
            MappedPOI mappedPOI = mappedPOIService.createMappedPOI(new MappedPOICreate().setMapIcon(mapIcon).setExternalId(gateway.getRemoteId() + "mappedpoi"), user1SecurityContext);
            deviceService.createDevice(new DeviceCreate().setDeviceType(testDevice).setGateway(gateway).setMappedPOI(mappedPOI),user1SecurityContext);


        }
        Gateway first=importGatewaysResponse.getImportedGateways().getList().stream().filter(f->f.getRemoteId().equals("move1")).findFirst().orElseThrow(()->new RuntimeException("no gateway move1"));
        realUser=new RealUser()
                .setId(UUID.randomUUID().toString())
                .setName("realUser");
        securityUserService.merge(realUser);
        first.setGatewayUser(realUser);
        securityUserService.merge(first);

    }

    @Test
    @Order(2)
    public void testMoveGateways() {

        GatewayFilter gatewayFilter=new GatewayFilter().setRemoteIds(Set.of("move1","move2","move3","move4","move5"));
        MoveGatewaysResponse moveGatewaysResponse = gatewayMoveService.moveGatewaysToTenant(adminSecurityContext, new MoveGatewaysRequest().setTargetTenant(user2.securityTenant()).setTargetTenantAdmin(user2.securityUser()).setGatewayFilter(gatewayFilter));
        Assertions.assertEquals(5,moveGatewaysResponse.movedGateways());
        Assertions.assertEquals(5,moveGatewaysResponse.movedRemotes());
        Assertions.assertEquals(10,moveGatewaysResponse.movedMappedPOIs());
        Assertions.assertEquals(1,moveGatewaysResponse.createdDeviceTypes());
        Assertions.assertEquals(1,moveGatewaysResponse.createdMapIcons());
        Assertions.assertEquals(1,moveGatewaysResponse.createdUsers());
        SecurityContext user2SecurityContext = securityContextProvider.getSecurityContext(user2.securityUser());
        List<Gateway> gateways = gatewayService.listAllGateways(user2SecurityContext, new GatewayFilter());
        Assertions.assertEquals(5,gateways.size());
        List<Device> allDevices = deviceService.listAllDevices(user2SecurityContext, new DeviceFilter());
        Assertions.assertEquals(5,allDevices.size());
        for (Gateway gateway1 : gateways) {
            Assertions.assertFalse(gateway1.getGatewayUser() instanceof RealUser,"gateway %s has real user as gatewayUser".formatted(gateway1.getRemoteId()));
        }
        for (Device allDevice : allDevices) {

            Assertions.assertFalse(allDevice.getCreator() instanceof RealUser);
            Assertions.assertFalse(allDevice.getMappedPOI().getCreator() instanceof RealUser);
        }
    }
}
