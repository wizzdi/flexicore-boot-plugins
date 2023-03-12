package com.wizzdi.basic.iot.client;

import java.security.PublicKey;

public record PublicKeyResponse(PublicKey publicKey,boolean signatureMandatory) {

}
