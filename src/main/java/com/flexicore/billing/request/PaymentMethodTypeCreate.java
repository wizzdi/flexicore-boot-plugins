package com.flexicore.billing.request;

import com.flexicore.request.BaseclassCreate;

public class PaymentMethodTypeCreate extends BaseclassCreate {

    private String canonicalClassName;

    public String getCanonicalClassName() {
        return canonicalClassName;
    }

    public <T extends PaymentMethodTypeCreate> T setCanonicalClassName(String canonicalClassName) {
        this.canonicalClassName = canonicalClassName;
        return (T) this;
    }
}
