package com.wizzdi.basic.iot.service;

import org.apache.commons.io.FileUtils;
import org.bouncycastle.util.encoders.Base64;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.security.*;
import java.util.UUID;

public class KeyPairForTests {

    static{
        java.security.Security.addProvider(
                new org.bouncycastle.jce.provider.BouncyCastleProvider()
        );
    }
    public record KeyPair(File publicKey, File privateKey) {
    }


    public static KeyPair getKeyPair() {

        try {
            KeyPairGenerator keyGen = KeyPairGenerator.getInstance("RSA");
            keyGen.initialize(2048);
            java.security.KeyPair key = keyGen.generateKeyPair();
            PrivateKey priv = key.getPrivate();
            PublicKey pub = key.getPublic();
            String privateKey = Base64.toBase64String(priv.getEncoded());
            String publicKey = Base64.toBase64String(pub.getEncoded());
            File privateOut = new File(FileUtils.getTempDirectory(), UUID.randomUUID() + "private.key");
            FileUtils.writeStringToFile(privateOut,privateKey, StandardCharsets.UTF_8);
            File publicOut = new File(FileUtils.getTempDirectory(), UUID.randomUUID() + "public.key");
            FileUtils.writeStringToFile(publicOut,publicKey, StandardCharsets.UTF_8);
            return new KeyPair(publicOut,privateOut);
        } catch (NoSuchAlgorithmException | IOException e) {
            throw new RuntimeException(e);
        }

    }
}
