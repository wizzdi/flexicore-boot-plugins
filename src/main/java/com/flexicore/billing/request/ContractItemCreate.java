package com.flexicore.billing.request;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.flexicore.billing.model.BusinessService;
import com.flexicore.billing.model.Contract;
import com.flexicore.billing.model.Currency;
import com.flexicore.billing.model.PriceListToService;
import com.flexicore.request.BaseclassCreate;


import java.time.OffsetDateTime;

public class ContractItemCreate extends BaseclassCreate {


    private String priceListToServiceId;
    @JsonIgnore
    private PriceListToService priceListToService;

    private String businessServiceId;
    @JsonIgnore
    private BusinessService businessService;

    private String currencyId;
    @JsonIgnore
    private Currency currency;

    private String contractId;
    @JsonIgnore
    private Contract contract;
    private Long price;
    private OffsetDateTime validFrom;
    private OffsetDateTime validTo;

    public String getPriceListToServiceId() {
        return priceListToServiceId;
    }

    public <T extends ContractItemCreate> T setPriceListToServiceId(String priceListToServiceId) {
        this.priceListToServiceId = priceListToServiceId;
        return (T) this;
    }

    @JsonIgnore
    public PriceListToService getPriceListToService() {
        return priceListToService;
    }

    public <T extends ContractItemCreate> T setPriceListToService(PriceListToService priceListToService) {
        this.priceListToService = priceListToService;
        return (T) this;
    }

    public String getBusinessServiceId() {
        return businessServiceId;
    }

    public <T extends ContractItemCreate> T setBusinessServiceId(String businessServiceId) {
        this.businessServiceId = businessServiceId;
        return (T) this;
    }

    @JsonIgnore
    public BusinessService getBusinessService() {
        return businessService;
    }

    public <T extends ContractItemCreate> T setBusinessService(BusinessService businessService) {
        this.businessService = businessService;
        return (T) this;
    }

    public String getCurrencyId() {
        return currencyId;
    }

    public <T extends ContractItemCreate> T setCurrencyId(String currencyId) {
        this.currencyId = currencyId;
        return (T) this;
    }

    @JsonIgnore
    public Currency getCurrency() {
        return currency;
    }

    public <T extends ContractItemCreate> T setCurrency(Currency currency) {
        this.currency = currency;
        return (T) this;
    }

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

    public Long getPrice() {
        return price;
    }

    public <T extends ContractItemCreate> T setPrice(Long price) {
        this.price = price;
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
}
