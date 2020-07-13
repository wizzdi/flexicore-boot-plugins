package com.flexicore.billing.request;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.flexicore.billing.model.PaymentMethod;
import com.flexicore.request.BaseclassCreate;

public class InvoiceCreate extends BaseclassCreate {

    private String usedPaymentMethodId;
    @JsonIgnore
    private PaymentMethod usedPaymentMethod;

    public String getUsedPaymentMethodId() {
        return usedPaymentMethodId;
    }

    public <T extends InvoiceCreate> T setUsedPaymentMethodId(String usedPaymentMethodId) {
        this.usedPaymentMethodId = usedPaymentMethodId;
        return (T) this;
    }

    @JsonIgnore
    public PaymentMethod getUsedPaymentMethod() {
        return usedPaymentMethod;
    }

    public <T extends InvoiceCreate> T setUsedPaymentMethod(PaymentMethod usedPaymentMethod) {
        this.usedPaymentMethod = usedPaymentMethod;
        return (T) this;
    }
}
