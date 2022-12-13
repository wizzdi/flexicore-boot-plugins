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

    public static final String toSign = "cwVEXt9GSdOMVuvoEWQ4QQ";
    private String signed;

    @Order(1)
    @Test
    public void testSign() throws InvalidKeySpecException, IOException, NoSuchAlgorithmException, InvalidKeyException, SignatureException {
        java.security.Security.addProvider(
                new org.bouncycastle.jce.provider.BouncyCastleProvider()
        );
        PrivateKey key=KeyUtils.readPrivateKey("C:\\Users\\Asaf\\Desktop\\test-private.pem");
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
        java.security.Security.addProvider(
                new org.bouncycastle.jce.provider.BouncyCastleProvider()
        );
        PublicKey publicKey = KeyUtils.readPublicKey("C:\\Users\\Asaf\\Desktop\\test-public.pem");
        Signature signature = Signature.getInstance(SIGNATURE_ALGORITHM);
        signature.initVerify(publicKey);
        signature.update("cwVEXt9GSdOMVuvoEWQ4QQ".getBytes(StandardCharsets.UTF_8));
        byte[] bytes = Base64.getDecoder().decode("FQZOdDycAAfp2OC22oW75QWZdPi3kTT4SD1HywtbS4tHwnXoALanewwd9T+ykVzf9reKbZkXKYkBbKezVipWDnOaADyHbA2L4WQC7kavckG6Zxme+Ib6H+Yp2ngdoOJFOcz4kiyo+rcUjciZY1vRPRwV7HnRwLAWBRiQTg++rcTlfAWpYdyOwg7JpdG+qETRRNE9lCNxTaWgUaclxteYRjSqlNaqAHlob5YrduLRjeDV+GCHJs9mce7cC7m/gErRuWdT6JPDV0LPguo+YPTup+OfYfGJ1G6dckH2jgJ8QLJMwrzv1cVLh6bTHKPa21f7T6kR78ax9EH6ZcT9n+zlAQ==");
        boolean verify = signature.verify(bytes);
        Assertions.assertTrue(verify);

    }


}
