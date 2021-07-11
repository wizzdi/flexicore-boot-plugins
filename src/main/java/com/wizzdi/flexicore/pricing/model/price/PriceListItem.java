package com.wizzdi.flexicore.pricing.model.price;

import com.wizzdi.flexicore.pricing.model.product.PricedProduct;
import com.flexicore.model.SecuredBasic;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;

@Entity
public class PriceListItem extends SecuredBasic {
    @ManyToOne(targetEntity = PricedProduct.class)
    private PricedProduct pricedProduct;
    @ManyToOne(targetEntity = PriceList.class)
    private PriceList priceList;
    @ManyToOne(targetEntity = Price.class)
    private Price price;


    @ManyToOne(targetEntity = PricedProduct.class)
    public PricedProduct getProduct() {
        return pricedProduct;
    }

    public <T extends PriceListItem> T setProduct(PricedProduct pricedProduct) {
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
