package com.wizzdi.flexicore.billing.request;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.flexicore.organization.model.Customer;
import com.wizzdi.flexicore.security.request.BasicPropertiesFilter;
import com.wizzdi.flexicore.security.request.PaginationFilter;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class ContractFiltering extends PaginationFilter {
    private BasicPropertiesFilter basicPropertiesFilter;
    private Set<String> customersIds=new HashSet<>();
    @JsonIgnore
    private List<Customer> customers;

    public BasicPropertiesFilter getBasicPropertiesFilter() {
        return basicPropertiesFilter;
    }

    public <T extends ContractFiltering> T setBasicPropertiesFilter(BasicPropertiesFilter basicPropertiesFilter) {
        this.basicPropertiesFilter = basicPropertiesFilter;
        return (T) this;
    }

    public Set<String> getCustomersIds() {
        return customersIds;
    }

    public <T extends ContractFiltering> T setCustomersIds(Set<String> customersIds) {
        this.customersIds = customersIds;
        return (T) this;
    }

    @JsonIgnore
    public List<Customer> getCustomers() {
        return customers;
    }

    public <T extends ContractFiltering> T setCustomers(List<Customer> customers) {
        this.customers = customers;
        return (T) this;
    }
}
