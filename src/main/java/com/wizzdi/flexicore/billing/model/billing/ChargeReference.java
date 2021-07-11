package com.wizzdi.flexicore.billing.model.billing;

import com.flexicore.model.SecuredBasic;

import javax.persistence.Entity;

@Entity
public class ChargeReference extends SecuredBasic {

    private String chargeReference;

    public String getChargeReference() {
        return chargeReference;
    }

    public <T extends ChargeReference> T setChargeReference(String referenceId) {
        this.chargeReference = referenceId;
        return (T) this;
    }
}
