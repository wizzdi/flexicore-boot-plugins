package com.wizzdi.basic.iot.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.wizzdi.basic.iot.client.*;
import com.wizzdi.basic.iot.model.*;
import com.wizzdi.basic.iot.service.app.App;
import com.wizzdi.basic.iot.service.app.TestEntities;
import com.wizzdi.basic.iot.service.request.*;
import com.wizzdi.basic.iot.service.response.ChangeStateResponse;
import com.wizzdi.basic.iot.service.service.DeviceStateService;
import com.wizzdi.flexicore.file.model.FileResource;
import com.wizzdi.flexicore.file.service.MD5Service;
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
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.util.StreamUtils;
import org.springframework.web.client.ResponseExtractor;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.DefaultUriBuilderFactory;

import java.io.File;
import java.io.FileOutputStream;
import java.security.PublicKey;
import java.time.OffsetDateTime;
import java.util.*;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.atomic.AtomicBoolean;

@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT, classes = App.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@ActiveProfiles("test")

public class FlowTestRemote {

    private static final Logger logger = LoggerFactory.getLogger(FlowTestRemote.class);

    public static final String DEVICE_TYPE = "light";
    private static final int FILE_LENGTH=3500000;
    private static final int CHUNK_SIZE = 1000000;

    @Autowired
    private BasicIOTClient testBasicIOTClient;
    @Autowired
    private String jsonSchema;
    @Autowired
    private PublicKey clientPublicKey;
    @Autowired
    private ClientIdHolder clientIdHolder;
    @Autowired
    private DeviceStateService deviceStateService;
    @Autowired
    private MD5Service md5Service;
    @Value("${basic.iot.connectivityCheckInterval:60000}")
    private long checkInterval;
    @Value("${basic.iot.test.server.username}")
    private String username;
    @Value("${basic.iot.test.server.password}")
    private String password;
    @Value("${basic.iot.test.server.url}")
    private String url;
    private boolean stopKeepAlive;
    private Set<String> keepAliveDevices = new HashSet<>();
    private Thread keepAliveThread;
    private RestTemplate restTemplate;
    private Gateway gateway;
    private String fileResourceId;
    private FirmwareUpdate firmwareUpdate;


    @AfterAll
    private void destroy() {
        stopKeepAlive();

    }


    @BeforeAll
    private void start() throws InterruptedException {
        TestEntities.clearMessageSubscribers();
        restTemplate = new RestTemplate();
        restTemplate.setUriTemplateHandler(new DefaultUriBuilderFactory(url));

        ResponseEntity<AuthenticationResponse> authenticationResponseResponseEntity = restTemplate.postForEntity("FlexiCore/rest/authenticationNew/login", new AuthenticationRequest().setEmail(username).setPassword(password), AuthenticationResponse.class);
        Assertions.assertTrue(authenticationResponseResponseEntity.getStatusCode().is2xxSuccessful());
        AuthenticationResponse body1 = authenticationResponseResponseEntity.getBody();
        Assertions.assertNotNull(body1);
        String key= body1.getAuthenticationKey();
        restTemplate.setInterceptors(
                Collections.singletonList((request, body, execution) -> {
                    request.getHeaders()
                            .add("authenticationKey", key);
                    return execution.execute(request, body);
                }));

        gateway=registerAndApproveGateway();
        System.out.println("gateway");
        startKeepAlive();

    }

    private Gateway registerAndApproveGateway() throws InterruptedException {
        RegisterGateway registerGateway = new RegisterGateway()
                .setPublicKey(Base64.encodeBase64String(clientPublicKey.getEncoded()))
                .setGatewayId(clientIdHolder.getGatewayId())

                .setId(UUID.randomUUID().toString());
        RegisterGatewayReceived registerGatewayReceived = testBasicIOTClient.request(registerGateway);
        Assertions.assertNotNull(registerGatewayReceived);


        ApproveGatewaysRequest approve = new ApproveGatewaysRequest()
                .setPendingGatewayFilter(new PendingGatewayFilter().setGatewayIds(Collections.singleton(clientIdHolder.getGatewayId())));
        ParameterizedTypeReference<PaginationResponse<Gateway>> t= new ParameterizedTypeReference<>() {
        };
        ResponseEntity<PaginationResponse<Gateway>> gatewayResponse = this.restTemplate.exchange("/plugins/Gateway/approveGateways", HttpMethod.POST, new HttpEntity<>(approve), t);
        Assertions.assertEquals(200, gatewayResponse.getStatusCodeValue());
        PaginationResponse<Gateway> gateway = gatewayResponse.getBody();
        Assertions.assertNotNull(gateway);
        Assertions.assertEquals(1,gateway.getList().size());
        return gateway.getList().get(0);
    }

    private void stopKeepAlive() {
        stopKeepAlive = true;
        keepAliveThread.interrupt();
    }



    private void startKeepAlive() {
        keepAliveThread= new Thread(() -> {
            logger.info("started keep alive");
            while (!stopKeepAlive) {
                try {
                    testBasicIOTClient.sendMessage(new KeepAlive().setDeviceIds(keepAliveDevices), clientIdHolder.getGatewayId());
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
        StateChangedReceived stateChangedReceived = testBasicIOTClient.request(new StateChanged().setDeviceId(clientIdHolder.getDeviceId()).setDeviceType(DEVICE_TYPE).setValue("dim", 30));
        Assertions.assertNotNull(stateChangedReceived);
        DeviceType deviceType = getDeviceTypes(new DeviceTypeFilter().setBasicPropertiesFilter(new BasicPropertiesFilter().setNames(Collections.singleton(DEVICE_TYPE)))).stream().findFirst().orElse(null);
        Assertions.assertNotNull(deviceType);
        Device device = getAllDevices( new DeviceFilter().setGatewayIds(Collections.singleton(gateway.getId())).setRemoteIds(Collections.singleton(clientIdHolder.getDeviceId()))).stream().findFirst().orElse(null);
        Assertions.assertNotNull(device);
        Assertions.assertEquals(deviceType.getId(), device.getDeviceType().getId());
        Assertions.assertEquals(device.getRemoteId(), clientIdHolder.getDeviceId());
        Assertions.assertEquals(30, device.getOther().get("dim"));
        Assertions.assertNotNull(device.getLastConnectivityChange());
        Assertions.assertEquals(Connectivity.OFF,device.getLastConnectivityChange().getConnectivity());
        synchronized (keepAliveDevices){
            keepAliveDevices.add(device.getRemoteId());
            keepAliveDevices.notifyAll();
        }


    }

    private List<DeviceType> getDeviceTypes(DeviceTypeFilter deviceTypeFilter) {
        ParameterizedTypeReference<PaginationResponse<DeviceType>> t= new ParameterizedTypeReference<>() {
        };

        ResponseEntity<PaginationResponse<DeviceType>> deviceTypeResponse = this.restTemplate.exchange("/plugins/DeviceType/getAllDeviceTypes", HttpMethod.POST, new HttpEntity<>(deviceTypeFilter), t);
        Assertions.assertTrue(deviceTypeResponse.getStatusCode().is2xxSuccessful());
        PaginationResponse<DeviceType> body = deviceTypeResponse.getBody();
        Assertions.assertNotNull(body);

        return body.getList();
    }

    private List<Device> getAllDevices(DeviceFilter deviceFilter) {
        ParameterizedTypeReference<PaginationResponse<Device>> t= new ParameterizedTypeReference<>() {
        };

        ResponseEntity<PaginationResponse<Device>> deviceResponse = this.restTemplate.exchange("/plugins/Device/getAllDevices", HttpMethod.POST, new HttpEntity<>(deviceFilter), t);
        Assertions.assertTrue(deviceResponse.getStatusCode().is2xxSuccessful());
        PaginationResponse<Device> body = deviceResponse.getBody();
        Assertions.assertNotNull(body);

        return body.getList();
    }

    private List<Gateway> getAllGateways(GatewayFilter gatewayFilter) {
        ParameterizedTypeReference<PaginationResponse<Gateway>> t= new ParameterizedTypeReference<>() {
        };

        ResponseEntity<PaginationResponse<Gateway>> gatewayResponse = this.restTemplate.exchange("/plugins/Gateway/getAllGateways", HttpMethod.POST, new HttpEntity<>(gatewayFilter), t);
        Assertions.assertTrue(gatewayResponse.getStatusCode().is2xxSuccessful());
        PaginationResponse<Gateway> body = gatewayResponse.getBody();
        Assertions.assertNotNull(body);

        return body.getList();
    }

    private ChangeStateResponse changeState(ChangeStateRequest changeStateRequest) {


        ResponseEntity<ChangeStateResponse> deviceResponse = this.restTemplate.postForEntity("/plugins/DeviceState/changeState",changeStateRequest,ChangeStateResponse.class);
        Assertions.assertTrue(deviceResponse.getStatusCode().is2xxSuccessful());
        ChangeStateResponse body = deviceResponse.getBody();
        Assertions.assertNotNull(body);

        return body;
    }
    @Test
    @Order(4)
    public void testUpdateSchema() throws JsonProcessingException, ExecutionException, InterruptedException, TimeoutException {
        UpdateStateSchemaReceived updateStateSchemaReceived = testBasicIOTClient.request(new UpdateStateSchema().setDeviceType(DEVICE_TYPE).setVersion(1).setJsonSchema(jsonSchema));
        Assertions.assertNotNull(updateStateSchemaReceived);
        DeviceType deviceType = getDeviceTypes(new DeviceTypeFilter().setBasicPropertiesFilter(new BasicPropertiesFilter().setNames(Collections.singleton(DEVICE_TYPE)))).stream().findFirst().orElse(null);
        Assertions.assertNotNull(deviceType);
        Assertions.assertEquals(jsonSchema, deviceType.getStateJsonSchema());


    }

    @Test
    @Order(5)
    public void testDeviceStateChange() throws JsonProcessingException, ExecutionException, InterruptedException, TimeoutException {
        Gateway gateway = getAllGateways(new GatewayFilter().setRemoteIds(Collections.singleton(clientIdHolder.getGatewayId()))).stream().findFirst().orElse(null);
        ChangeStateRequest changeStateRequest = new ChangeStateRequest().setDeviceFilter(new DeviceFilter().setGatewayIds(Collections.singleton(gateway.getId()))).setValue("dim", 94);
        AtomicBoolean received = new AtomicBoolean(false);
        synchronized (changeStateRequest) {
            IOTMessageSubscriber iotMessageSubscriber = f -> handleChangeState(f, testBasicIOTClient, received);
            TestEntities.addMessageSubscriber(iotMessageSubscriber);
            ChangeStateResponse changeStateResponse = changeState( changeStateRequest);
            Assertions.assertFalse(changeStateResponse.getDevicesExecutedOn().isEmpty());
            changeStateRequest.wait(5000);
            TestEntities.removeMessageSubscriber(iotMessageSubscriber);
        }

        Assertions.assertTrue(received.get());

    }

    @Test
    @Order(10)
    public void testConnectivity() throws InterruptedException {
        checkDevicesAndGateways(Connectivity.ON);
        stopKeepAlive();
        logger.info("waiting "+checkInterval*2 +" ms till we detect devices are off");
        Thread.sleep(checkInterval*2);
        checkDevicesAndGateways(Connectivity.OFF);

    }

    @Test
    @Order(1)
    public void testUploadFileFullMd5() {
        Random rd = new Random();
        byte[] data = new byte[FILE_LENGTH];
        rd.nextBytes(data);
        String md5=md5Service.generateMD5(data);
        String name="test-"+System.currentTimeMillis()+".js";
        String id=null;
        //just for sake of the example , frResponse will always be 204 here since we are generating a random file content
        ResponseEntity<FileResource> frResponse=this.restTemplate.getForEntity("/fileResource/"+md5, FileResource.class);
        FileResource body = frResponse.getBody();
        long offset=body!=null?body.getOffset():0L;

        // chunk size to divide
        for(long i=offset;i<data.length;i+=CHUNK_SIZE){
            byte[] chunk=Arrays.copyOfRange(data, (int)i, Math.min(data.length,(int)(i+CHUNK_SIZE)));
            String chunkMD5= md5Service.generateMD5(chunk);
            boolean lastChunk=i+CHUNK_SIZE >=data.length;

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
            headers.add("md5",md5);
            headers.add("chunkMd5",chunkMD5);
            headers.add("lastChunk",lastChunk+"");
            headers.add("name",name);



            HttpEntity<byte[]> requestEntity = new HttpEntity<>(chunk, headers);

            ResponseEntity<FileResource> response=this.restTemplate.exchange("/upload", HttpMethod.POST, requestEntity, FileResource.class);
            Assertions.assertEquals(200, response.getStatusCodeValue());
            FileResource fileResource=response.getBody();
            Assertions.assertNotNull(fileResource);
            Assertions.assertEquals(fileResource.isDone(),lastChunk);
            id=fileResource.getId();

        }
        this.fileResourceId=id;

        this.restTemplate.execute("/downloadUnsecure/" + id, HttpMethod.GET, null, (ResponseExtractor<Object>) clientHttpResponse -> {
            File ret = File.createTempFile("download", "tmp");
            StreamUtils.copy(clientHttpResponse.getBody(), new FileOutputStream(ret));
            return ret;
        });

    }

    @Test
    @Order(8)
    public void testCreateFirmware() {
        String name = UUID.randomUUID().toString();
        FirmwareUpdateCreate request = new FirmwareUpdateCreate()
                .setVersion("2.0.0")
                .setFileResourceId(fileResourceId)
                .setName(name);
        ResponseEntity<FirmwareUpdate> firmwareUpdateResponse = this.restTemplate.postForEntity("/plugins/FirmwareUpdate/createFirmwareUpdate", request, FirmwareUpdate.class);
        Assertions.assertEquals(200, firmwareUpdateResponse.getStatusCodeValue(),()->"status: "+firmwareUpdateResponse.getStatusCode());
        firmwareUpdate = firmwareUpdateResponse.getBody();
        Assertions.assertNotNull(firmwareUpdate);
    }
    @Test
    @Order(9)
    public void testSetGatewayForUpdate() throws InterruptedException {
        AtomicBoolean received = new AtomicBoolean(false);
        synchronized (received){
            IOTMessageSubscriber iotMessageSubscriber = f -> handleOTAAvailable(f, testBasicIOTClient, received);



            TestEntities.addMessageSubscriber(iotMessageSubscriber);

            String name = UUID.randomUUID().toString();
            FirmwareUpdateInstallationCreate request = new FirmwareUpdateInstallationCreate()
                    .setTargetInstallationDate(OffsetDateTime.now())
                    .setFirmwareUpdateId(firmwareUpdate.getId())
                    .setTargetRemoteId(gateway.getId())
                    .setName(name);
            ResponseEntity<FirmwareUpdateInstallation> firmwareUpdateInstallationResponse = this.restTemplate.postForEntity("/plugins/FirmwareUpdateInstallation/createFirmwareUpdateInstallation", request, FirmwareUpdateInstallation.class);
            Assertions.assertEquals(200, firmwareUpdateInstallationResponse.getStatusCodeValue());
            FirmwareUpdateInstallation body = firmwareUpdateInstallationResponse.getBody();
            Assertions.assertNotNull(body);
            received.wait(1000*120);
            Assertions.assertTrue(received.get());
            FirmwareUpdateInstallation firmwareUpdateInstallation = getAllFirmwareUpdateInstallations(new FirmwareUpdateInstallationFilter().setBasicPropertiesFilter(new BasicPropertiesFilter().setOnlyIds(Collections.singleton(body.getId())))).getList().stream().findFirst().orElse(null);
            Assertions.assertNotNull(firmwareUpdateInstallation);
            Assertions.assertEquals(FirmwareInstallationState.INSTALLED,firmwareUpdateInstallation.getFirmwareInstallationState());
            Assertions.assertNotNull(firmwareUpdateInstallation.getDateInstalled());

        }

    }

    private PaginationResponse<FirmwareUpdateInstallation> getAllFirmwareUpdateInstallations(FirmwareUpdateInstallationFilter request){
            ParameterizedTypeReference<PaginationResponse<FirmwareUpdateInstallation>> t= new ParameterizedTypeReference<>() {};

            ResponseEntity<PaginationResponse<FirmwareUpdateInstallation>> firmwareUpdateInstallationResponse = this.restTemplate.exchange("/plugins/FirmwareUpdateInstallation/getAllFirmwareUpdateInstallations", HttpMethod.POST, new HttpEntity<>(request), t);
            Assertions.assertEquals(200, firmwareUpdateInstallationResponse.getStatusCodeValue());
            PaginationResponse<FirmwareUpdateInstallation> body = firmwareUpdateInstallationResponse.getBody();
            Assertions.assertNotNull(body);

    return body;

    }

    private void checkDevicesAndGateways(Connectivity expected) {
        Gateway gateway = getAllGateways( new GatewayFilter().setRemoteIds(Collections.singleton(clientIdHolder.getGatewayId()))).stream().findFirst().orElse(null);
        Assertions.assertNotNull(gateway);
        Assertions.assertNotNull(gateway.getLastConnectivityChange());
        Assertions.assertEquals(expected,gateway.getLastConnectivityChange().getConnectivity());
        List<Device> devices=getAllDevices(new DeviceFilter().setGatewayIds(Collections.singleton(gateway.getId())));
        Assertions.assertTrue(devices.stream().anyMatch(f->clientIdHolder.getDeviceId().equals(f.getRemoteId())&&f.getLastConnectivityChange()!=null&&expected.equals(f.getLastConnectivityChange().getConnectivity())));
    }
    private void handleOTAAvailable(IOTMessage iotMessage, BasicIOTClient basicIOTClient, AtomicBoolean lock) {
        if (iotMessage instanceof OTAAvailable) {
            OTAAvailable otaAvailable = (OTAAvailable) iotMessage;
            synchronized (lock) {
                try {
                    basicIOTClient.reply(new OTAAvailableReceived().setOTAAvailableId(iotMessage.getId()), iotMessage);
                    StateChangedReceived stateChangedReceived = basicIOTClient.request(new StateChanged().setVersion(otaAvailable.getVersion()));
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

    private void handleChangeState(IOTMessage iotMessage, BasicIOTClient basicIOTClient, AtomicBoolean lock) {
        if (iotMessage instanceof ChangeState) {
            synchronized (lock) {
                try {
                    logger.info("Change state received");
                    ChangeState changeState = (ChangeState) iotMessage;
                    basicIOTClient.reply(new StateChanged().setOtherProperties(changeState.getValues()), iotMessage);
                    logger.info("replied with ChangeStateReceived");

                  //  basicIOTClient.sendMessage(new StateChanged().setOtherProperties(changeState.getValues()));
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
