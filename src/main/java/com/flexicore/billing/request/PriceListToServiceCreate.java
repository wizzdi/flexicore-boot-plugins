package com.flexicore.billing.request;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.flexicore.billing.model.BusinessService;
import com.flexicore.billing.model.Currency;
import com.flexicore.billing.model.PriceList;
import com.flexicore.request.BaseclassCreate;


public class PriceListToServiceCreate extends BaseclassCreate {

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
}
