package com.wizzdi.flexicore.pricing.request;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.wizzdi.flexicore.pricing.model.price.*;
import com.wizzdi.flexicore.pricing.model.product.PricedProduct;
import com.wizzdi.flexicore.security.request.BasicCreate;


public class PriceListItemCreate extends BasicCreate {


    private String pricedProductId;
    @JsonIgnore
    private PricedProduct pricedProduct;
    private String priceListId;
    @JsonIgnore
    private PriceList priceList;
    private String priceId;
    @JsonIgnore
    private Price price;


    public String getPriceListId() {
        return priceListId;
    }

    public <T extends PriceListItemCreate> T setPriceListId(String priceListId) {
        this.priceListId = priceListId;
        return (T) this;
    }

    @JsonIgnore
    public PriceList getPriceList() {
        return priceList;
    }

    public <T extends PriceListItemCreate> T setPriceList(PriceList priceList) {
        this.priceList = priceList;
        return (T) this;
    }

    public String getPricedProductId() {
        return pricedProductId;
    }

    public <T extends PriceListItemCreate> T setPricedProductId(String pricedProductId) {
        this.pricedProductId = pricedProductId;
        return (T) this;
    }

    @JsonIgnore
    public PricedProduct getPricedProduct() {
        return pricedProduct;
    }

    public <T extends PriceListItemCreate> T setPricedProduct(PricedProduct pricedProduct) {
        this.pricedProduct = pricedProduct;
        return (T) this;
    }

    public String getPriceId() {
        return priceId;
    }

    public <T extends PriceListItemCreate> T setPriceId(String priceId) {
        this.priceId = priceId;
        return (T) this;
    }

    @JsonIgnore
    public Price getPrice() {
        return price;
    }

    public <T extends PriceListItemCreate> T setPrice(Price price) {
        this.price = price;
        return (T) this;
    }
}
