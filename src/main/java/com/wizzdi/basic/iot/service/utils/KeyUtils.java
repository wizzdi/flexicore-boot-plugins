package com.wizzdi.basic.iot.service.utils;

import org.apache.commons.codec.binary.Base64;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;

public class KeyUtils {

    public static PrivateKey readPrivateKey(String keyPath) throws InvalidKeySpecException, IOException, NoSuchAlgorithmException {
        String key = Files.readString(new File(keyPath).toPath(), Charset.defaultCharset());

        String publicKeyPEM = key
                .replace("-----BEGIN PRIVATE KEY-----", "")
                .replaceAll(System.lineSeparator(), "")
                .replace("-----END PRIVATE KEY-----", "");

        byte[] encoded = Base64.decodeBase64(publicKeyPEM);

        KeyFactory keyFactory = KeyFactory.getInstance("RSA");
        PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(encoded);
        return  keyFactory.generatePrivate(keySpec);
    }

    public static PublicKey readPublicKey(String keyPath) throws InvalidKeySpecException, IOException, NoSuchAlgorithmException {
        String key = Files.readString(new File(keyPath).toPath(), Charset.defaultCharset());

        String publicKeyPEM = key
                .replace("-----BEGIN PUBLIC KEY-----", "")
                .replaceAll(System.lineSeparator(), "")
                .replace("-----END PUBLIC KEY-----", "");


        return readPublicKeyUnwrapped(publicKeyPEM);
    }

    public static PublicKey readPublicKeyUnwrapped(String publicKeyPEM) throws NoSuchAlgorithmException, InvalidKeySpecException {
        byte[] encoded = Base64.decodeBase64(publicKeyPEM);

        return readPublicKey(encoded);
    }

    public static PublicKey readPublicKey(byte[] encoded) throws NoSuchAlgorithmException, InvalidKeySpecException {
        KeyFactory keyFactory = KeyFactory.getInstance("RSA");
        X509EncodedKeySpec keySpec = new X509EncodedKeySpec(encoded);
        return  keyFactory.generatePublic(keySpec);
    }
}
