package com.flexicore.billing.model;

import com.flexicore.model.SecuredBasic;


import javax.persistence.Entity;
import javax.persistence.ManyToOne;

@Entity
public class PriceListToService extends SecuredBasic {

    @ManyToOne(targetEntity = PriceList.class)
    private PriceList priceList;
    @ManyToOne(targetEntity = BusinessService.class)
    private BusinessService businessService;
    private long price;
    @ManyToOne(targetEntity = Currency.class)
    private Currency currency;
    private PaymentType paymentType;
    private BillingCycleGranularity billingCycleGranularity;
    private int billingCycleInterval;
    private int totalCycles;

    public PriceListToService() {
    }

    @ManyToOne(targetEntity = PriceList.class)
    public PriceList getPriceList() {
        return priceList;
    }

    public <T extends PriceListToService> T setPriceList(PriceList priceList) {
        this.priceList = priceList;
        return (T) this;
    }

    @ManyToOne(targetEntity = BusinessService.class)
    public BusinessService getBusinessService() {
        return businessService;
    }

    public <T extends PriceListToService> T setBusinessService(BusinessService businessService) {
        this.businessService = businessService;
        return (T) this;
    }

    public long getPrice() {
        return price;
    }

    public <T extends PriceListToService> T setPrice(long price) {
        this.price = price;
        return (T) this;
    }

    @ManyToOne(targetEntity = Currency.class)
    public Currency getCurrency() {
        return currency;
    }

    public <T extends PriceListToService> T setCurrency(Currency currency) {
        this.currency = currency;
        return (T) this;
    }

    public PaymentType getPaymentType() {
        return paymentType;
    }

    public <T extends PriceListToService> T setPaymentType(PaymentType paymentType) {
        this.paymentType = paymentType;
        return (T) this;
    }

    public BillingCycleGranularity getBillingCycleGranularity() {
        return billingCycleGranularity;
    }

    public <T extends PriceListToService> T setBillingCycleGranularity(BillingCycleGranularity billingCycleGranularity) {
        this.billingCycleGranularity = billingCycleGranularity;
        return (T) this;
    }

    public int getBillingCycleInterval() {
        return billingCycleInterval;
    }

    public <T extends PriceListToService> T setBillingCycleInterval(int billingCycleInterval) {
        this.billingCycleInterval = billingCycleInterval;
        return (T) this;
    }

    public int getTotalCycles() {
        return totalCycles;
    }

    public <T extends PriceListToService> T setTotalCycles(int totalCycles) {
        this.totalCycles = totalCycles;
        return (T) this;
    }
}
