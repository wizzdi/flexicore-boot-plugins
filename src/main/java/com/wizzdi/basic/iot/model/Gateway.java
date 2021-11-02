package com.wizzdi.basic.iot.model;

import javax.persistence.Entity;
import javax.persistence.Lob;

@Entity
public class Gateway extends Remote {

    @Lob
    private String publicKey;


    @Lob
    public String getPublicKey() {
        return publicKey;
    }

    public <T extends Gateway> T setPublicKey(String publicKey) {
        this.publicKey = publicKey;
        return (T) this;
    }
}
