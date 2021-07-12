package com.wizzdi.flexicore.billing.request;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.wizzdi.flexicore.billing.model.billing.Charge;
import com.wizzdi.flexicore.contract.model.Contract;
import com.wizzdi.flexicore.pricing.model.price.PriceListItem;
import com.wizzdi.flexicore.security.request.BasicPropertiesFilter;
import com.wizzdi.flexicore.security.request.PaginationFilter;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class ContractItemFiltering extends PaginationFilter {
    private BasicPropertiesFilter basicPropertiesFilter;

    private Set<String> contractIds = new HashSet<>();
    @JsonIgnore
    private List<Contract> contracts;

    private Set<String> priceListsItemsIds = new HashSet<>();
    @JsonIgnore
    private List<PriceListItem> priceListItems;

    public BasicPropertiesFilter getBasicPropertiesFilter() {
        return basicPropertiesFilter;
    }

    public <T extends ContractItemFiltering> T setBasicPropertiesFilter(BasicPropertiesFilter basicPropertiesFilter) {
        this.basicPropertiesFilter = basicPropertiesFilter;
        return (T) this;
    }

    public Set<String> getContractIds() {
        return contractIds;
    }

    public <T extends ContractItemFiltering> T setContractIds(Set<String> contractIds) {
        this.contractIds = contractIds;
        return (T) this;
    }

    @JsonIgnore
    public List<Contract> getContracts() {
        return contracts;
    }

    public <T extends ContractItemFiltering> T setContracts(List<Contract> contracts) {
        this.contracts = contracts;
        return (T) this;
    }

    public Set<String> getPriceListsItemsIds() {
        return priceListsItemsIds;
    }

    public <T extends ContractItemFiltering> T setPriceListsItemsIds(Set<String> priceListsItemsIds) {
        this.priceListsItemsIds = priceListsItemsIds;
        return (T) this;
    }

    @JsonIgnore
    public List<PriceListItem> getPriceListItems() {
        return priceListItems;
    }

    public <T extends ContractItemFiltering> T setPriceListItems(List<PriceListItem> priceListItems) {
        this.priceListItems = priceListItems;
        return (T) this;
    }

}
