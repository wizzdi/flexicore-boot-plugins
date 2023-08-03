package com.wizzdi.flexicore.encryption.service;

import com.google.crypto.tink.*;
import com.google.crypto.tink.aead.AeadConfig;
import com.google.crypto.tink.hybrid.EciesAeadHkdfPrivateKeyManager;
import com.google.crypto.tink.hybrid.HybridConfig;
import com.wizzdi.flexicore.boot.base.interfaces.Plugin;
import org.pf4j.Extension;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.*;
import java.security.GeneralSecurityException;


@Component
@Extension
public class CommonEncryptionService implements Plugin, InitializingBean {
	private KeysetHandle keysetHandle;


	private static final Logger logger = LoggerFactory.getLogger(CommonEncryptionService.class);

	@Value("${flexicore.security.encryption.tinkKeySetPath:/home/flexicore/keyset.json}")
	private String tinkKeySetPath;



	public byte[] getEncryptingKey() {
		try {
			ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
			KeysetHandle publicKeysetHandle = keysetHandle.getPublicKeysetHandle();
			CleartextKeysetHandle.write(publicKeysetHandle, JsonKeysetWriter.withOutputStream(outputStream));
			return outputStream.toByteArray();

		} catch (Exception e) {
			logger.debug("failed getting encrypting key , attempting old format", e);
			try {
				ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
				CleartextKeysetHandle.write(keysetHandle, JsonKeysetWriter.withOutputStream(outputStream));
				return outputStream.toByteArray();
			} catch (Exception e1) {
				logger.error("failed getting encrypting key , attempting old format", e1);

			}
		}
		return null;


	}


	public EncryptingKey parseKey(byte[] encryptingKey) throws IOException, GeneralSecurityException {

		KeysetHandle keysetHandle = CleartextKeysetHandle.read(JsonKeysetReader.withBytes(encryptingKey));
		return new HybridEncryptImpl(keysetHandle);
	}


	public byte[] encrypt(final byte[] plaintext, final byte[] associatedData) throws GeneralSecurityException {

		return encryptWithFallback(keysetHandle, plaintext, associatedData);

		// 3. Use the primitive.

	}

	private byte[] encryptWithFallback(KeysetHandle keysetHandle, byte[] plaintext, byte[] associatedData) throws GeneralSecurityException {
		try {
			HybridEncrypt hybridEncrypt =
					keysetHandle.getPublicKeysetHandle().getPrimitive(HybridEncrypt.class);
			return hybridEncrypt.encrypt(plaintext, associatedData);

		} catch (GeneralSecurityException e) {
			logger.debug("failed encrypting , retrying with old key format", e);
			Aead primitive =
					keysetHandle.getPrimitive(Aead.class);
			return primitive.encrypt(plaintext, associatedData);
		}
	}


	public byte[] decrypt(final byte[] ciphertext, final byte[] associatedData) throws GeneralSecurityException {

		return decryptWithFallback(keysetHandle, ciphertext, associatedData);

	}

	private byte[] decryptWithFallback(KeysetHandle keysetHandle, byte[] ciphertext, byte[] associatedData) throws GeneralSecurityException {
		try {
			HybridDecrypt hybridDecrypt =
					keysetHandle.getPrimitive(HybridDecrypt.class);
			return hybridDecrypt.decrypt(ciphertext, associatedData);

		} catch (GeneralSecurityException e) {
			logger.debug("failed Decrypting , retrying with old key format", e);
			Aead aead =
					keysetHandle.getPrimitive(Aead.class);
			return aead.decrypt(ciphertext, associatedData);
		}
	}

	@Override
	public void afterPropertiesSet() throws Exception {
		HybridConfig.register();
		AeadConfig.register();
		File keysetFile = new File(tinkKeySetPath);
		if (!keysetFile.exists()) {
			if(!keysetFile.getParentFile().exists()){
				keysetFile.getParentFile().mkdirs();
			}
			// 1. Generate the private key material.
			keysetHandle = KeysetHandle.generateNew(EciesAeadHkdfPrivateKeyManager.eciesP256HkdfHmacSha256Aes128GcmTemplate());
			try(FileOutputStream fileOutputStream=new FileOutputStream(keysetFile)){
				CleartextKeysetHandle.write(keysetHandle, JsonKeysetWriter.withOutputStream(fileOutputStream));
			}
		} else {
			try(FileInputStream fileInputStream=new FileInputStream(keysetFile)){
				keysetHandle = CleartextKeysetHandle.read(JsonKeysetReader.withInputStream(fileInputStream));
			}
		}


	}

	public class HybridEncryptImpl implements EncryptingKey {
		private KeysetHandle keysetHandle;

		public HybridEncryptImpl(KeysetHandle aead) {
			this.keysetHandle = aead;
		}


		public byte[] encrypt(byte[] plaintext, byte[] associatedData) throws GeneralSecurityException {
			return encryptWithFallback(keysetHandle, plaintext, associatedData);
		}


		public byte[] decrypt(byte[] ciphertext, byte[] associatedData) throws GeneralSecurityException {
			throw new UnsupportedOperationException("does not support decrypting");
		}
	}


	public interface EncryptingKey {

		byte[] encrypt(byte[] plaintext, byte[] associatedData) throws GeneralSecurityException;

		byte[] decrypt(byte[] ciphertext, byte[] associatedData) throws GeneralSecurityException;
	}

}
