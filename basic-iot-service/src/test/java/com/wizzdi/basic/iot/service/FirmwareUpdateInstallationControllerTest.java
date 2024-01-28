package com.wizzdi.basic.iot.service;

import com.flexicore.security.SecurityContextBase;
import com.wizzdi.basic.iot.model.*;
import com.wizzdi.basic.iot.service.app.App;
import com.wizzdi.basic.iot.service.request.*;
import com.wizzdi.basic.iot.service.service.DeviceService;
import com.wizzdi.basic.iot.service.service.FirmwareUpdateService;
import com.wizzdi.basic.iot.service.service.GatewayService;
import com.wizzdi.basic.iot.service.service.PendingGatewayService;
import com.wizzdi.flexicore.file.model.FileResource;
import com.wizzdi.flexicore.file.request.FileResourceCreate;
import com.wizzdi.flexicore.file.service.FileResourceService;
import com.wizzdi.flexicore.security.response.PaginationResponse;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.context.annotation.Lazy;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
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

public class FirmwareUpdateInstallationControllerTest {
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

    private FirmwareUpdateInstallation firmwareUpdateInstallation;
    @Autowired
    private TestRestTemplate restTemplate;

    private FirmwareUpdate firmwareUpdate;
    @Autowired
    private GatewayService gatewayService;
    @Autowired
    private PendingGatewayService pendingGatewayService;
    @Autowired
    private FirmwareUpdateService firmwareUpdateService;
    private Gateway gateway;
    @Autowired
    @Lazy
    private ObjectHolder<FileResource> firmwareFile;
    @Autowired
    @Lazy
    private SecurityContextBase adminSecurityContext;

    @BeforeAll
    private void init() {
        restTemplate.getRestTemplate().setInterceptors(
                Collections.singletonList((request, body, execution) -> {
                    request.getHeaders()
                            .add("authenticationKey", "fake");
                    return execution.execute(request, body);
                }));
        PendingGateway remote=pendingGatewayService.createPendingGateway(new PendingGatewayCreate().setGatewayId("fake"),adminSecurityContext);
        gateway=gatewayService.approveGateways(adminSecurityContext,new ApproveGatewaysRequest().setPendingGatewayFilter(new PendingGatewayFilter().setGatewayIds(Collections.singleton(remote.getGatewayId())))).getList().stream().findFirst().orElseThrow(()->new RuntimeException("could not get approved gateway"));
        firmwareUpdate=firmwareUpdateService.createFirmwareUpdate(new FirmwareUpdateCreate().setFileResource(firmwareFile.get()).setVersion("3.0.0"),adminSecurityContext);

    }

    @Test
    @Order(1)
    public void testFirmwareUpdateInstallationCreate() {
        String name = UUID.randomUUID().toString();
        FirmwareUpdateInstallationCreate request = new FirmwareUpdateInstallationCreate()
                .setFirmwareUpdateId(firmwareUpdate.getId())
                .setTargetRemoteId(gateway.getId())
                .setName(name);
        ResponseEntity<FirmwareUpdateInstallation> firmwareUpdateInstallationResponse = this.restTemplate.postForEntity("/plugins/FirmwareUpdateInstallation/createFirmwareUpdateInstallation", request, FirmwareUpdateInstallation.class);
        Assertions.assertEquals(200, firmwareUpdateInstallationResponse.getStatusCodeValue());
        firmwareUpdateInstallation = firmwareUpdateInstallationResponse.getBody();
        assertFirmwareUpdateInstallation(request, firmwareUpdateInstallation);

    }

    @Test
    @Order(2)
    public void testGetAllFirmwareUpdateInstallations() {
        FirmwareUpdateInstallationFilter request=new FirmwareUpdateInstallationFilter();
        ParameterizedTypeReference<PaginationResponse<FirmwareUpdateInstallation>> t= new ParameterizedTypeReference<>() {
        };

        ResponseEntity<PaginationResponse<FirmwareUpdateInstallation>> firmwareUpdateInstallationResponse = this.restTemplate.exchange("/plugins/FirmwareUpdateInstallation/getAllFirmwareUpdateInstallations", HttpMethod.POST, new HttpEntity<>(request), t);
        Assertions.assertEquals(200, firmwareUpdateInstallationResponse.getStatusCodeValue());
        PaginationResponse<FirmwareUpdateInstallation> body = firmwareUpdateInstallationResponse.getBody();
        Assertions.assertNotNull(body);
        List<FirmwareUpdateInstallation> firmwareUpdateInstallations = body.getList();
        Assertions.assertNotEquals(0,firmwareUpdateInstallations.size());
        Assertions.assertTrue(firmwareUpdateInstallations.stream().anyMatch(f->f.getId().equals(firmwareUpdateInstallation.getId())));


    }

    public void assertFirmwareUpdateInstallation(FirmwareUpdateInstallationCreate request, FirmwareUpdateInstallation firmwareUpdateInstallation) {
        Assertions.assertNotNull(firmwareUpdateInstallation);
        Assertions.assertEquals(request.getName(), firmwareUpdateInstallation.getName());
    }

    @Test
    @Order(3)
    public void testFirmwareUpdateInstallationUpdate(){
        String name = UUID.randomUUID().toString();
        FirmwareUpdateInstallationUpdate request = new FirmwareUpdateInstallationUpdate()
                .setId(firmwareUpdateInstallation.getId())
                .setName(name);
        ResponseEntity<FirmwareUpdateInstallation> firmwareUpdateInstallationResponse = this.restTemplate.exchange("/plugins/FirmwareUpdateInstallation/updateFirmwareUpdateInstallation",HttpMethod.PUT, new HttpEntity<>(request), FirmwareUpdateInstallation.class);
        Assertions.assertEquals(200, firmwareUpdateInstallationResponse.getStatusCodeValue());
        firmwareUpdateInstallation = firmwareUpdateInstallationResponse.getBody();
        assertFirmwareUpdateInstallation(request, firmwareUpdateInstallation);

    }

}
