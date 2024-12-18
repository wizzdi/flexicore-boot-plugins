package com.wizzdi.flexicore.contract.model;

import com.wizzdi.flexicore.pricing.model.price.PriceListItem;
import com.wizzdi.flexicore.pricing.model.price.RecurringPrice;
import com.flexicore.model.Baseclass;


import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.ManyToOne;
import java.time.OffsetDateTime;

@Entity
public class ContractItem extends Baseclass {

    @ManyToOne(targetEntity = PriceListItem.class)
    private PriceListItem priceListItem;
    @ManyToOne(targetEntity = RecurringPrice.class)
    private RecurringPrice recurringPrice;
    @ManyToOne(targetEntity = Contract.class)
    private Contract contract;
    @Column(columnDefinition = "timestamp with time zone")
    private OffsetDateTime validFrom;
    @Column(columnDefinition = "timestamp with time zone")
    private OffsetDateTime validTo;
    private String externalId;

    public ContractItem() {
    }


    @ManyToOne(targetEntity = PriceListItem.class)
    public PriceListItem getPriceListItem() {
        return priceListItem;
    }

    public <T extends ContractItem> T setPriceListItem(PriceListItem priceListItem) {
        this.priceListItem = priceListItem;
        return (T) this;
    }

    @ManyToOne(targetEntity = RecurringPrice.class)
    public RecurringPrice getRecurringPrice() {
        return recurringPrice;
    }

    public <T extends ContractItem> T setRecurringPrice(RecurringPrice recurringPrice) {
        this.recurringPrice = recurringPrice;
        return (T) this;
    }

    @ManyToOne(targetEntity = Contract.class)
    public Contract getContract() {
        return contract;
    }

    public <T extends ContractItem> T setContract(Contract contract) {
        this.contract = contract;
        return (T) this;
    }

    public OffsetDateTime getValidFrom() {
        return validFrom;
    }

    public <T extends ContractItem> T setValidFrom(OffsetDateTime validFrom) {
        this.validFrom = validFrom;
        return (T) this;
    }

    public OffsetDateTime getValidTo() {
        return validTo;
    }

    public <T extends ContractItem> T setValidTo(OffsetDateTime validTo) {
        this.validTo = validTo;
        return (T) this;
    }

    public String getExternalId() {
        return externalId;
    }

    public <T extends ContractItem> T setExternalId(String externalId) {
        this.externalId = externalId;
        return (T) this;
    }
}
