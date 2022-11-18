package com.wizzdi.basic.iot.service;

import com.wizzdi.basic.iot.service.utils.KeyUtils;
import org.junit.jupiter.api.*;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.security.*;
import java.security.spec.InvalidKeySpecException;
import java.util.Base64;

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
