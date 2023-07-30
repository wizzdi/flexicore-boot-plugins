package com.wizzdi.basic.iot.service;

import com.wizzdi.basic.iot.model.Device;
import com.wizzdi.basic.iot.service.app.App;
import com.wizzdi.basic.iot.service.request.DeviceCreate;
import com.wizzdi.basic.iot.service.request.DeviceFilter;
import com.wizzdi.basic.iot.service.request.DeviceUpdate;
import com.wizzdi.flexicore.security.response.PaginationResponse;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.Collections;
import java.util.List;
import java.util.UUID;

@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT, classes = App.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@ActiveProfiles("test")

public class DeviceControllerTest {

    private Device device;
    @Autowired
    private TestRestTemplate restTemplate;

    @BeforeAll
    private void init() {
        restTemplate.getRestTemplate().setInterceptors(
                Collections.singletonList((request, body, execution) -> {
                    request.getHeaders()
                            .add("authenticationKey", "fake");
                    return execution.execute(request, body);
                }));

    }

    @Test
    @Order(1)
    public void testDeviceCreate() {
        String name = UUID.randomUUID().toString();
        DeviceCreate request = new DeviceCreate()
                .setName(name);
        ResponseEntity<Device> deviceResponse = this.restTemplate.postForEntity("/plugins/Device/createDevice", request, Device.class);
        Assertions.assertEquals(200, deviceResponse.getStatusCodeValue());
        device = deviceResponse.getBody();
        assertDevice(request, device);

    }

    @Test
    @Order(2)
    public void testGetAllDevices() {
        DeviceFilter request=new DeviceFilter();
        ParameterizedTypeReference<PaginationResponse<Device>> t= new ParameterizedTypeReference<>() {
        };

        ResponseEntity<PaginationResponse<Device>> deviceResponse = this.restTemplate.exchange("/plugins/Device/getAllDevices", HttpMethod.POST, new HttpEntity<>(request), t);
        Assertions.assertEquals(200, deviceResponse.getStatusCodeValue());
        PaginationResponse<Device> body = deviceResponse.getBody();
        Assertions.assertNotNull(body);
        List<Device> devices = body.getList();
        Assertions.assertNotEquals(0,devices.size());
        Assertions.assertTrue(devices.stream().anyMatch(f->f.getId().equals(device.getId())));


    }

    public void assertDevice(DeviceCreate request, Device device) {
        Assertions.assertNotNull(device);
        Assertions.assertEquals(request.getName(), device.getName());
    }

    @Test
    @Order(3)
    public void testDeviceUpdate(){
        String name = UUID.randomUUID().toString();
        DeviceUpdate request = new DeviceUpdate()
                .setId(device.getId())
                .setName(name);
        ResponseEntity<Device> deviceResponse = this.restTemplate.exchange("/plugins/Device/updateDevice",HttpMethod.PUT, new HttpEntity<>(request), Device.class);
        Assertions.assertEquals(200, deviceResponse.getStatusCodeValue());
        device = deviceResponse.getBody();
        assertDevice(request, device);

    }

}
