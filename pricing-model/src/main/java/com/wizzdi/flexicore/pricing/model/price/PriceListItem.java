package com.wizzdi.flexicore.pricing.model.price;

import com.wizzdi.flexicore.pricing.model.product.PricedProduct;
import com.flexicore.model.Baseclass;

import jakarta.persistence.Entity;
import jakarta.persistence.ManyToOne;

@Entity
public class PriceListItem extends Baseclass {
    @ManyToOne(targetEntity = PricedProduct.class)
    private PricedProduct pricedProduct;
    @ManyToOne(targetEntity = PriceList.class)
    private PriceList priceList;
    @ManyToOne(targetEntity = Price.class)
    private Price price;

    @ManyToOne(targetEntity = PricedProduct.class)
    public PricedProduct getPricedProduct() {
        return pricedProduct;
    }

    public <T extends PriceListItem> T setPricedProduct(PricedProduct pricedProduct) {
        this.pricedProduct = pricedProduct;
        return (T) this;
    }

    @ManyToOne(targetEntity = PriceList.class)
    public PriceList getPriceList() {
        return priceList;
    }

    public <T extends PriceListItem> T setPriceList(PriceList priceList) {
        this.priceList = priceList;
        return (T) this;
    }

    @ManyToOne(targetEntity = Price.class)
    public Price getPrice() {
        return price;
    }

    public <T extends PriceListItem> T setPrice(Price price) {
        this.price = price;
        return (T) this;
    }
}
