package com.wizzdi.flexicore.billing.model.payment;

import com.flexicore.model.Baseclass;


import jakarta.persistence.Entity;

@Entity
public class PaymentMethodType extends Baseclass {

    private String canonicalClassName;

    public PaymentMethodType() {
    }

    public String getCanonicalClassName() {
        return canonicalClassName;
    }

    public <T extends PaymentMethodType> T setCanonicalClassName(String canonicalClassName) {
        this.canonicalClassName = canonicalClassName;
        return (T) this;
    }
}
