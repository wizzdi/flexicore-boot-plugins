package com.wizzdi.flexicore.billing.model.billing;

import com.flexicore.model.Baseclass;

import jakarta.persistence.Entity;

@Entity
public class ChargeReference extends Baseclass {

    private String chargeReference;

    public String getChargeReference() {
        return chargeReference;
    }

    public <T extends ChargeReference> T setChargeReference(String referenceId) {
        this.chargeReference = referenceId;
        return (T) this;
    }
}
