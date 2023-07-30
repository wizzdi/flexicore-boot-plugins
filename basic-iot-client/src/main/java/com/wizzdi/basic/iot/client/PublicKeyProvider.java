package com.wizzdi.basic.iot.client;

import java.security.PublicKey;

public interface PublicKeyProvider {

    PublicKeyResponse getPublicKey(String id);
}
