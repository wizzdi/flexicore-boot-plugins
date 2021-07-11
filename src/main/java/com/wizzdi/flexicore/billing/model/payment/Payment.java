package com.wizzdi.flexicore.billing.model.payment;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.wizzdi.flexicore.pricing.model.price.Money;
import com.flexicore.model.SecuredBasic;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
public class Payment extends SecuredBasic {

    @JsonIgnore
    @OneToMany(targetEntity = Receipt.class,mappedBy = "payment")
    private List<Receipt> receipts =new ArrayList<>();
    @Column(columnDefinition = "timestamp with time zone")
    private OffsetDateTime datePaid;
    private String paymentReference;
    @ManyToOne(targetEntity = Money.class)
    private Money money;
    public Payment() {
    }


    @JsonIgnore
    @OneToMany(targetEntity = Receipt.class,mappedBy = "payment")
    public List<Receipt> getReceipts() {
        return receipts;
    }

    public <T extends Payment> T setReceipts(List<Receipt> receipts) {
        this.receipts = receipts;
        return (T) this;
    }

    @ManyToOne(targetEntity = Money.class)
    public Money getMoney() {
        return money;
    }

    public <T extends Payment> T setMoney(Money money) {
        this.money = money;
        return (T) this;
    }


    public OffsetDateTime getDatePaid() {
        return datePaid;
    }

    public <T extends Payment> T setDatePaid(OffsetDateTime datePayed) {
        this.datePaid = datePayed;
        return (T) this;
    }

    public String getPaymentReference() {
        return paymentReference;
    }

    public <T extends Payment> T setPaymentReference(String paymentReference) {
        this.paymentReference = paymentReference;
        return (T) this;
    }


}
