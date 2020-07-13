package com.flexicore.billing.model;

import com.flexicore.model.Baseclass;

import javax.persistence.Entity;

@Entity
public class PaymentMethodType extends Baseclass {

    private String canonicalClassName;

    public String getCanonicalClassName() {
        return canonicalClassName;
    }

    public <T extends PaymentMethodType> T setCanonicalClassName(String canonicalClassName) {
        this.canonicalClassName = canonicalClassName;
        return (T) this;
    }
}
