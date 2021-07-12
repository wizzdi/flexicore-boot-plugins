package com.wizzdi.flexicore.billing.request;

import com.wizzdi.flexicore.security.request.BasicCreate;

public class PaymentMethodTypeCreate extends BasicCreate {

    private String canonicalClassName;

    public String getCanonicalClassName() {
        return canonicalClassName;
    }

    public <T extends PaymentMethodTypeCreate> T setCanonicalClassName(String canonicalClassName) {
        this.canonicalClassName = canonicalClassName;
        return (T) this;
    }
}
