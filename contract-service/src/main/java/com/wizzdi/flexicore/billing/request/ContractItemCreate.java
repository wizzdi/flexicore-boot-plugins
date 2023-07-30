package com.wizzdi.flexicore.billing.request;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.wizzdi.flexicore.contract.model.Contract;
import com.wizzdi.flexicore.pricing.model.price.PriceListItem;
import com.wizzdi.flexicore.pricing.model.price.RecurringPrice;
import com.wizzdi.flexicore.security.request.BasicCreate;

import java.time.OffsetDateTime;


public class ContractItemCreate extends BasicCreate {

    private String contractId;
    @JsonIgnore
    private Contract contract;
    private String priceListItemId;

    @JsonIgnore
    private PriceListItem priceListItem;
    private String recurringPriceId;

    @JsonIgnore
    private RecurringPrice recurringPrice;
    private OffsetDateTime validFrom;
    private OffsetDateTime validTo;
    private String externalId;

    public String getContractId() {
        return contractId;
    }

    public <T extends ContractItemCreate> T setContractId(String contractId) {
        this.contractId = contractId;
        return (T) this;
    }

    @JsonIgnore
    public Contract getContract() {
        return contract;
    }

    public <T extends ContractItemCreate> T setContract(Contract contract) {
        this.contract = contract;
        return (T) this;
    }

    public String getPriceListItemId() {
        return priceListItemId;
    }

    public <T extends ContractItemCreate> T setPriceListItemId(String priceListItemId) {
        this.priceListItemId = priceListItemId;
        return (T) this;
    }

    @JsonIgnore
    public PriceListItem getPriceListItem() {
        return priceListItem;
    }

    public <T extends ContractItemCreate> T setPriceListItem(PriceListItem priceListItem) {
        this.priceListItem = priceListItem;
        return (T) this;
    }

    public String getRecurringPriceId() {
        return recurringPriceId;
    }

    public <T extends ContractItemCreate> T setRecurringPriceId(String recurringPriceId) {
        this.recurringPriceId = recurringPriceId;
        return (T) this;
    }

    @JsonIgnore
    public RecurringPrice getRecurringPrice() {
        return recurringPrice;
    }

    public <T extends ContractItemCreate> T setRecurringPrice(RecurringPrice recurringPrice) {
        this.recurringPrice = recurringPrice;
        return (T) this;
    }

    public OffsetDateTime getValidFrom() {
        return validFrom;
    }

    public <T extends ContractItemCreate> T setValidFrom(OffsetDateTime validFrom) {
        this.validFrom = validFrom;
        return (T) this;
    }

    public OffsetDateTime getValidTo() {
        return validTo;
    }

    public <T extends ContractItemCreate> T setValidTo(OffsetDateTime validTo) {
        this.validTo = validTo;
        return (T) this;
    }

    public String getExternalId() {
        return externalId;
    }

    public <T extends ContractItemCreate> T setExternalId(String externalId) {
        this.externalId = externalId;
        return (T) this;
    }
}
