package com.wizzdi.basic.iot.service;

import com.flexicore.security.SecurityContextBase;
import com.wizzdi.basic.iot.service.triggers.app.App;
import com.wizzdi.basic.iot.service.request.GatewayCreate;
import com.wizzdi.basic.iot.service.request.GatewayFilter;
import com.wizzdi.basic.iot.service.request.GatewayUpdate;
import com.wizzdi.basic.iot.model.Gateway;
import com.wizzdi.basic.iot.service.request.ImportGatewaysRequest;
import com.wizzdi.basic.iot.service.response.ImportGatewaysResponse;
import com.wizzdi.flexicore.file.model.FileResource;
import com.wizzdi.flexicore.file.request.FileResourceCreate;
import com.wizzdi.flexicore.file.service.FileResourceService;
import com.wizzdi.flexicore.security.response.PaginationResponse;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.assertj.core.util.Files;
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
import java.io.IOException;
import java.nio.charset.StandardCharsets;
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

public class GatewayControllerTest {
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

    private Gateway gateway;
    @Autowired
    private TestRestTemplate restTemplate;
    private FileResource csv;
    @Autowired
    private FileResourceService fileResourceService;
    @Autowired
    @Lazy
    private SecurityContextBase adminSecurityContext;

    @BeforeAll
    private void init() throws IOException {
        restTemplate.getRestTemplate().setInterceptors(
                Collections.singletonList((request, body, execution) -> {
                    request.getHeaders()
                            .add("authenticationKey", "fake");
                    return execution.execute(request, body);
                }));
        File file=new File(Files.temporaryFolderPath(),"test"+UUID.randomUUID().toString()+".csv");
        String csvS = IOUtils.resourceToString("gatewayImport.csv", StandardCharsets.UTF_8, GatewayControllerTest.class.getClassLoader());
        FileUtils.writeStringToFile(file,csvS,StandardCharsets.UTF_8);
        csv=fileResourceService.createFileResource(new FileResourceCreate().setFullPath(file.getAbsolutePath()).setName("csv"),adminSecurityContext);

    }

    @Test
    @Order(1)
    public void testGatewayCreate() {
        String name = UUID.randomUUID().toString();
        GatewayCreate request = new GatewayCreate()
                .setName(name);
        ResponseEntity<Gateway> gatewayResponse = this.restTemplate.postForEntity("/plugins/Gateway/createGateway", request, Gateway.class);
        Assertions.assertEquals(200, gatewayResponse.getStatusCodeValue());
        gateway = gatewayResponse.getBody();
        assertGateway(request, gateway);

    }

    @Test
    @Order(2)
    public void testGetAllGateways() {
        GatewayFilter request=new GatewayFilter();
        ParameterizedTypeReference<PaginationResponse<Gateway>> t= new ParameterizedTypeReference<>() {
        };

        ResponseEntity<PaginationResponse<Gateway>> gatewayResponse = this.restTemplate.exchange("/plugins/Gateway/getAllGateways", HttpMethod.POST, new HttpEntity<>(request), t);
        Assertions.assertEquals(200, gatewayResponse.getStatusCodeValue());
        PaginationResponse<Gateway> body = gatewayResponse.getBody();
        Assertions.assertNotNull(body);
        List<Gateway> gateways = body.getList();
        Assertions.assertNotEquals(0,gateways.size());
        Assertions.assertTrue(gateways.stream().anyMatch(f->f.getId().equals(gateway.getId())));


    }

    public void assertGateway(GatewayCreate request, Gateway gateway) {
        Assertions.assertNotNull(gateway);
        Assertions.assertEquals(request.getName(), gateway.getName());
    }

    @Test
    @Order(3)
    public void testGatewayUpdate(){
        String name = UUID.randomUUID().toString();
        GatewayUpdate request = new GatewayUpdate()
                .setId(gateway.getId())
                .setName(name);
        ResponseEntity<Gateway> gatewayResponse = this.restTemplate.exchange("/plugins/Gateway/updateGateway",HttpMethod.PUT, new HttpEntity<>(request), Gateway.class);
        Assertions.assertEquals(200, gatewayResponse.getStatusCodeValue());
        gateway = gatewayResponse.getBody();
        assertGateway(request, gateway);

    }

    @Test
    @Order(4)
    public void testGatewayImport() {
        String name = UUID.randomUUID().toString();
        ImportGatewaysRequest request = new ImportGatewaysRequest()
                .setCsvId(csv.getId());
        ResponseEntity<ImportGatewaysResponse> gatewayResponse = this.restTemplate.postForEntity("/plugins/Gateway/importGateways", request, ImportGatewaysResponse.class);
        Assertions.assertEquals(200, gatewayResponse.getStatusCodeValue());
        ImportGatewaysResponse body = gatewayResponse.getBody();
        Assertions.assertNotNull(body);
        Assertions.assertNotNull(body.getImportedGateways());
Assertions.assertEquals(12,body.getImportedGateways().getList().size());


    }

    @Test
    @Order(5)
    public void testGatewayImportAgain() {
        String name = UUID.randomUUID().toString();
        ImportGatewaysRequest request = new ImportGatewaysRequest()
                .setCsvId(csv.getId());
        ResponseEntity<ImportGatewaysResponse> gatewayResponse = this.restTemplate.postForEntity("/plugins/Gateway/importGateways", request, ImportGatewaysResponse.class);
        Assertions.assertEquals(200, gatewayResponse.getStatusCodeValue());
        ImportGatewaysResponse body = gatewayResponse.getBody();
        Assertions.assertNotNull(body);
        Assertions.assertNotNull(body.getImportedGateways());
        Assertions.assertEquals(0,body.getImportedGateways().getList().size());


    }

}
