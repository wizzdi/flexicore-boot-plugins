package com.flexicore.billing.model;

import com.flexicore.model.SecuredBasic;


import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import java.time.OffsetDateTime;

@Entity
public class ContractItem extends SecuredBasic {

    @ManyToOne(targetEntity = PriceListToService.class)
    private PriceListToService priceListToService;
    @ManyToOne(targetEntity = BusinessService.class)
    private BusinessService businessService;
    @ManyToOne(targetEntity = Currency.class)
    private Currency currency;
    @ManyToOne(targetEntity = Contract.class)
    private Contract contract;
    private long price;
    @Column(columnDefinition = "timestamp with time zone")
    private OffsetDateTime validFrom;
    @Column(columnDefinition = "timestamp with time zone")
    private OffsetDateTime validTo;
    private ContractItemStatus contractItemStatus;
    private String externalId;

    public ContractItem() {
    }


    @ManyToOne(targetEntity = PriceListToService.class)
    public PriceListToService getPriceListToService() {
        return priceListToService;
    }

    public <T extends ContractItem> T setPriceListToService(PriceListToService priceListToService) {
        this.priceListToService = priceListToService;
        return (T) this;
    }

    @ManyToOne(targetEntity = BusinessService.class)
    public BusinessService getBusinessService() {
        return businessService;
    }

    public <T extends ContractItem> T setBusinessService(BusinessService businessService) {
        this.businessService = businessService;
        return (T) this;
    }

    @ManyToOne(targetEntity = Currency.class)
    public Currency getCurrency() {
        return currency;
    }

    public <T extends ContractItem> T setCurrency(Currency currency) {
        this.currency = currency;
        return (T) this;
    }

    public long getPrice() {
        return price;
    }

    public <T extends ContractItem> T setPrice(long price) {
        this.price = price;
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

    public ContractItemStatus getContractItemStatus() {
        return contractItemStatus;
    }

    public <T extends ContractItem> T setContractItemStatus(ContractItemStatus contractItemStatus) {
        this.contractItemStatus = contractItemStatus;
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
