package com.wizzdi.basic.iot.service;

import com.wizzdi.basic.iot.service.utils.KeyUtils;
import org.junit.jupiter.api.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testcontainers.shaded.org.bouncycastle.openssl.jcajce.JcaPEMWriter;

import java.io.IOException;
import java.io.StringWriter;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.security.*;
import java.security.spec.InvalidKeySpecException;
import java.util.Base64;

import static com.wizzdi.basic.iot.client.BasicIOTClient.SIGNATURE_ALGORITHM;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@Disabled
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
        PublicKey publicKey = KeyUtils.readPublicKey("C:\\Users\\Asaf\\Desktop\\output_public_key.pem");
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

    @Order(4)
    @Test
    public void generate() throws InvalidKeySpecException, IOException, NoSuchAlgorithmException, InvalidKeyException, SignatureException {

        java.security.Security.addProvider(
                new org.bouncycastle.jce.provider.BouncyCastleProvider()
        );
        KeyPairGenerator keyGen = KeyPairGenerator.getInstance("RSA");
        keyGen.initialize(2048);
        java.security.KeyPair key = keyGen.generateKeyPair();
        PrivateKey privateKey = key.getPrivate();
        PublicKey publicKey = key.getPublic();
        // 3. Convert Public Key to PEM format
        String publicKeyPem = convertToPem(publicKey);
        System.out.println("\n----- PUBLIC KEY (PEM) -----");
        System.out.println(publicKeyPem);
        System.out.println("----- END PUBLIC KEY (PEM) -----");

        // 4. Convert Private Key to PEM format
        String privateKeyPem = convertToPem(privateKey);
        System.out.println("\n----- PRIVATE KEY (PEM) -----");
        System.out.println(privateKeyPem);
        System.out.println("----- END PRIVATE KEY (PEM) -----");

    }

    /**
     * Converts a Key object (PublicKey or PrivateKey) to PEM format string.
     *
     * @param key The Key object to convert.
     * @return The PEM formatted string representation of the key.
     * @throws IOException If an error occurs during writing.
     */
    public static String convertToPem(Object key) throws IOException {
        StringWriter stringWriter = new StringWriter();
        // Use try-with-resources to ensure the writer is closed
        try (JcaPEMWriter pemWriter = new JcaPEMWriter(stringWriter)) {
            pemWriter.writeObject(key);
        } // pemWriter.close() is called automatically here
        return stringWriter.toString();
    }


}
