package com.wizzdi.flexicore.billing.request;

import com.wizzdi.flexicore.security.request.BasicCreate;

public class ChargeReferenceCreate extends BasicCreate {



    private String chargeReference;

    public String getChargeReference() {
        return chargeReference;
    }

    public <T extends ChargeReferenceCreate> T setChargeReference(String chargeReference) {
        this.chargeReference = chargeReference;
        return (T) this;
    }
}
