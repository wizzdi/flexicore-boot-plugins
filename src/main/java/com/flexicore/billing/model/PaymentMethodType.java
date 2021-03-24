package com.flexicore.billing.model;

import com.flexicore.model.SecuredBasic;


import javax.persistence.Entity;

@Entity
public class PaymentMethodType extends SecuredBasic {

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
