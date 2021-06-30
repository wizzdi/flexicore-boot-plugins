package com.flexicore.billing.request;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.flexicore.billing.model.*;
import com.wizzdi.flexicore.security.request.BasicCreate;


public class PriceListToServiceCreate extends BasicCreate {

    private String priceListId;
    @JsonIgnore
    private PriceList priceList;
    private String businessServiceId;
    @JsonIgnore
    private BusinessService businessService;
    private Long price;
    private String currencyId;
    @JsonIgnore
    private Currency currency;
    private PaymentType paymentType;
    private BillingCycleGranularity billingCycleGranularity;
    private Integer billingCycleInterval;
    private Integer totalCycles;

    public String getPriceListId() {
        return priceListId;
    }

    public <T extends PriceListToServiceCreate> T setPriceListId(String priceListId) {
        this.priceListId = priceListId;
        return (T) this;
    }

    @JsonIgnore
    public PriceList getPriceList() {
        return priceList;
    }

    public <T extends PriceListToServiceCreate> T setPriceList(PriceList priceList) {
        this.priceList = priceList;
        return (T) this;
    }

    public String getBusinessServiceId() {
        return businessServiceId;
    }

    public <T extends PriceListToServiceCreate> T setBusinessServiceId(String businessServiceId) {
        this.businessServiceId = businessServiceId;
        return (T) this;
    }

    @JsonIgnore
    public BusinessService getBusinessService() {
        return businessService;
    }

    public <T extends PriceListToServiceCreate> T setBusinessService(BusinessService businessService) {
        this.businessService = businessService;
        return (T) this;
    }

    public Long getPrice() {
        return price;
    }

    public <T extends PriceListToServiceCreate> T setPrice(Long price) {
        this.price = price;
        return (T) this;
    }

    public String getCurrencyId() {
        return currencyId;
    }

    public <T extends PriceListToServiceCreate> T setCurrencyId(String currencyId) {
        this.currencyId = currencyId;
        return (T) this;
    }

    @JsonIgnore
    public Currency getCurrency() {
        return currency;
    }

    public <T extends PriceListToServiceCreate> T setCurrency(Currency currency) {
        this.currency = currency;
        return (T) this;
    }

    public PaymentType getPaymentType() {
        return paymentType;
    }

    public <T extends PriceListToServiceCreate> T setPaymentType(PaymentType paymentType) {
        this.paymentType = paymentType;
        return (T) this;
    }

    public BillingCycleGranularity getBillingCycleGranularity() {
        return billingCycleGranularity;
    }

    public <T extends PriceListToServiceCreate> T setBillingCycleGranularity(BillingCycleGranularity billingCycleGranularity) {
        this.billingCycleGranularity = billingCycleGranularity;
        return (T) this;
    }

    public Integer getBillingCycleInterval() {
        return billingCycleInterval;
    }

    public <T extends PriceListToServiceCreate> T setBillingCycleInterval(Integer billingCycleInterval) {
        this.billingCycleInterval = billingCycleInterval;
        return (T) this;
    }

    public Integer getTotalCycles() {
        return totalCycles;
    }

    public <T extends PriceListToServiceCreate> T setTotalCycles(Integer totalCycles) {
        this.totalCycles = totalCycles;
        return (T) this;
    }
}
