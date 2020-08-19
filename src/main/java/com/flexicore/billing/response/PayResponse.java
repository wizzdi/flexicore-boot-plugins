package com.flexicore.billing.response;

import java.time.OffsetDateTime;

public class PayResponse {
    private PayStatus payStatus;
    private OffsetDateTime dateCharged;
    private String transactionId;

    public PayStatus getPayStatus() {
        return payStatus;
    }

    public <T extends PayResponse> T setPayStatus(PayStatus payStatus) {
        this.payStatus = payStatus;
        return (T) this;
    }

    public OffsetDateTime getDateCharged() {
        return dateCharged;
    }

    public <T extends PayResponse> T setDateCharged(OffsetDateTime dateCharged) {
        this.dateCharged = dateCharged;
        return (T) this;
    }

    public String getTransactionId() {
        return transactionId;
    }

    public <T extends PayResponse> T setTransactionId(String transactionId) {
        this.transactionId = transactionId;
        return (T) this;
    }
}
