package com.wizzdi.flexicore.pricing.request;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.wizzdi.flexicore.pricing.model.product.PricedProduct;
import com.wizzdi.flexicore.pricing.model.price.Currency;
import com.wizzdi.flexicore.pricing.model.price.PriceList;
import com.wizzdi.flexicore.security.request.BasicPropertiesFilter;
import com.wizzdi.flexicore.security.request.PaginationFilter;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class PriceListItemFiltering extends PaginationFilter {
    private BasicPropertiesFilter basicPropertiesFilter;

    private Set<String> priceListIds = new HashSet<>();
    @JsonIgnore
    private List<PriceList> priceLists;
    private Set<String> pricedProductIds = new HashSet<>();
    @JsonIgnore
    private List<PricedProduct> pricedProducts;

    public BasicPropertiesFilter getBasicPropertiesFilter() {
        return basicPropertiesFilter;
    }

    public <T extends PriceListItemFiltering> T setBasicPropertiesFilter(BasicPropertiesFilter basicPropertiesFilter) {
        this.basicPropertiesFilter = basicPropertiesFilter;
        return (T) this;
    }

    public Set<String> getPriceListIds() {
        return priceListIds;
    }

    public <T extends PriceListItemFiltering> T setPriceListIds(Set<String> priceListIds) {
        this.priceListIds = priceListIds;
        return (T) this;
    }

    @JsonIgnore
    public List<PriceList> getPriceLists() {
        return priceLists;
    }

    public <T extends PriceListItemFiltering> T setPriceLists(List<PriceList> priceLists) {
        this.priceLists = priceLists;
        return (T) this;
    }

    public Set<String> getPricedProductIds() {
        return pricedProductIds;
    }

    public <T extends PriceListItemFiltering> T setPricedProductIds(Set<String> pricedProductIds) {
        this.pricedProductIds = pricedProductIds;
        return (T) this;
    }

    @JsonIgnore
    public List<PricedProduct> getPricedProducts() {
        return pricedProducts;
    }

    public <T extends PriceListItemFiltering> T setPricedProducts(List<PricedProduct> pricedProducts) {
        this.pricedProducts = pricedProducts;
        return (T) this;
    }

}
