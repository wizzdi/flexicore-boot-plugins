package com.wizzdi.flexicore.billing.request;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.flexicore.organization.model.Customer;
import com.wizzdi.flexicore.contract.model.ContractItem;
import com.wizzdi.flexicore.security.request.BasicPropertiesFilter;
import com.wizzdi.flexicore.security.request.PaginationFilter;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class ContractItemChargeReferenceFiltering extends ChargeReferenceFiltering {
    private Set<String> contractItemsIds =new HashSet<>();
    @JsonIgnore
    private List<ContractItem> contractItems;


    public Set<String> getContractItemsIds() {
        return contractItemsIds;
    }

    public <T extends ContractItemChargeReferenceFiltering> T setContractItemsIds(Set<String> contractItemsIds) {
        this.contractItemsIds = contractItemsIds;
        return (T) this;
    }

    @JsonIgnore
    public List<ContractItem> getContractItems() {
        return contractItems;
    }

    public <T extends ContractItemChargeReferenceFiltering> T setContractItems(List<ContractItem> contractItems) {
        this.contractItems = contractItems;
        return (T) this;
    }
}
