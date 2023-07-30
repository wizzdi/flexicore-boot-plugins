package com.flexicore.organization.request;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.flexicore.annotations.TypeRetention;
import com.wizzdi.flexicore.security.request.BasicPropertiesFilter;
import com.wizzdi.flexicore.security.request.PaginationFilter;
import com.flexicore.organization.model.Customer;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class IndustryFiltering extends PaginationFilter {

	private BasicPropertiesFilter basicPropertiesFilter;

    private Set<String> customerIds=new HashSet<>();
    @JsonIgnore
    @TypeRetention(Customer.class)
    private List<Customer> customers;

    public BasicPropertiesFilter getBasicPropertiesFilter() {
        return basicPropertiesFilter;
    }

    public <T extends IndustryFiltering> T setBasicPropertiesFilter(BasicPropertiesFilter basicPropertiesFilter) {
        this.basicPropertiesFilter = basicPropertiesFilter;
        return (T) this;
    }

    public Set<String> getCustomerIds() {
        return customerIds;
    }

    public <T extends IndustryFiltering> T setCustomerIds(Set<String> customerIds) {
        this.customerIds = customerIds;
        return (T) this;
    }

    @JsonIgnore
    public List<Customer> getCustomers() {
        return customers;
    }

    public <T extends IndustryFiltering> T setCustomers(List<Customer> customers) {
        this.customers = customers;
        return (T) this;
    }
}
