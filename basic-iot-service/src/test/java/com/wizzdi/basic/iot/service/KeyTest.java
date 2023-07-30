package com.wizzdi.basic.iot.service;

import com.wizzdi.basic.iot.service.utils.KeyUtils;
import org.junit.jupiter.api.*;

import java.io.IOException;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.security.*;
import java.security.spec.InvalidKeySpecException;
import java.util.Base64;

import static com.wizzdi.basic.iot.client.BasicIOTClient.SIGNATURE_ALGORITHM;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)

public class KeyTest {

    public static final String toSign = "067c2435-32b6-459f-bcb9-bcc7143ba0b6";
    private String signed;

    @Order(1)
    @Test
    public void testSign() throws InvalidKeySpecException, IOException, NoSuchAlgorithmException, InvalidKeyException, SignatureException {
        java.security.Security.addProvider(
                new org.bouncycastle.jce.provider.BouncyCastleProvider()
        );
        PrivateKey key=KeyUtils.readPrivateKey("C:\\Users\\Asaf\\Desktop\\DV001_private.pem");
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
        PublicKey publicKey = KeyUtils.readPublicKey("C:\\Users\\Asaf\\Desktop\\DV001_public.pem");
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
        PublicKey publicKey = KeyUtils.readPublicKey("C:\\Users\\Asaf\\Desktop\\DV001_public.pem");
        Signature signature = Signature.getInstance(SIGNATURE_ALGORITHM);
        signature.initVerify(publicKey);
        signature.update("067c2435-32b6-459f-bcb9-bcc7143ba0b6".getBytes(StandardCharsets.UTF_8));
        String decodedSignedId = URLDecoder.decode("Vxh4c8QBSGuY4hvXVeULlEqi7q5%252BePAQVD%252Fj4UeDEPCHe%252BdM3qZgonaLck6M2TuvV0hO3igEinFYb05xksBgkN1gvryWHr%252BtVnrb8qg7CwuaSygoJBPXgX1wD2FF0Ek9R%252F0alhOZP8kaNsgMNb1amN8milnGmVX2G51W86NrvirU3gyJmdFyrIGqaP60S%252B9aB1%252B4XxsMIff62aP8E10XhwLR%252BqBH6s0Cjrx%252FFvLFBJDIpVZ5qsxjdDpBHhZRqthNpCEl2DW1kpgUg8JGuLGjKeJEY4cIS3MTPus5vBEYrmodHj%252BSWV5Nat%252FgOmBlfqsPw67gw50eaBNvpsOpvwnh9w%253D%253D", StandardCharsets.UTF_8);
        String decodedSignedId2=URLDecoder.decode(decodedSignedId, StandardCharsets.UTF_8);
        System.out.println(decodedSignedId);
        byte[] bytes = Base64.getDecoder().decode(decodedSignedId2);
        boolean verify = signature.verify(bytes);
        Assertions.assertTrue(verify);

    }


}
