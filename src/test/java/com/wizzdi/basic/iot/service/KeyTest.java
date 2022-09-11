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
import com.wizzdi.basic.iot.service.utils.KeyUtils;
import com.wizzdi.flexicore.security.request.BasicPropertiesFilter;
import com.wizzdi.flexicore.security.response.PaginationResponse;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.security.*;
import java.security.spec.InvalidKeySpecException;
import java.util.*;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.atomic.AtomicBoolean;

import static com.wizzdi.basic.iot.client.BasicIOTClient.SIGNATURE_ALGORITHM;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)

public class KeyTest {

    public static final String toSign = "61fb419a-b66d-11ec-b909-0242ac120002";
    private String signed;

    @Order(1)
    @Test
    public void testSign() throws InvalidKeySpecException, IOException, NoSuchAlgorithmException, InvalidKeyException, SignatureException {
        PrivateKey key=KeyUtils.readPrivateKey("C:\\Users\\Asaf\\Desktop\\private.pem");
        Signature signature = Signature.getInstance(SIGNATURE_ALGORITHM);
        signature.initSign(key);
        signature.update(toSign.getBytes(StandardCharsets.UTF_8));
        byte[] sign = signature.sign();
        signed = Base64.getEncoder().encodeToString(sign);
        System.out.println(signed);
    }

    @Order(2)
    @Test
    public void testVerify() throws InvalidKeySpecException, IOException, NoSuchAlgorithmException, InvalidKeyException, SignatureException {
        PublicKey publicKey = KeyUtils.readPublicKey("C:\\Users\\Asaf\\Desktop\\public.pem");
        Signature signature = Signature.getInstance(SIGNATURE_ALGORITHM);
        signature.initVerify(publicKey);
        signature.update(toSign.getBytes(StandardCharsets.UTF_8));
        byte[] bytes = Base64.getDecoder().decode(signed);
        boolean verify = signature.verify(bytes);
        Assertions.assertTrue(verify);

    }

    @Order(3)
    @Test
    public void test() throws InvalidKeySpecException, IOException, NoSuchAlgorithmException, InvalidKeyException, SignatureException {
        PublicKey publicKey = KeyUtils.readPublicKey("C:\\Users\\Asaf\\Desktop\\test-public.pem");
        Signature signature = Signature.getInstance(SIGNATURE_ALGORITHM);
        signature.initVerify(publicKey);
        signature.update("6318740d-0000-1000-8000-1fb1500efdec".getBytes(StandardCharsets.UTF_8));
        byte[] bytes = Base64.getDecoder().decode("gDflHf9qzFwd9PRzhWW2hhkFn4N9ii+gPCTo28yfnBwO9HPvNio2Paisc1tb9TTsIO07grDWItRVzMvfFH/OGWECBLrztJZR4vMnzJRlBn/iyTKPGmC6w/U9tvu9yQsW8/e4q110GHNHXZs1dZC+FMO7RYjk+pYEDdG2pxUlReH0tIFlBTeUoYGm2E4RBpYv3UjpK0mwlC1nNbf0XydZKmT0VgKYwxg4eogtqOo5I6BqciEinFILLh7VZPkAP176/Xx+E2PTQuw9QxFkdY7zMmL+Ta0obt5WXe2Y8bf0JeeaTAYOhzQndzu0vqBlW3ljp1mN7Wa1c1SVcQ5gdiFdCw==");
        boolean verify = signature.verify(bytes);
        Assertions.assertTrue(verify);

    }


}
