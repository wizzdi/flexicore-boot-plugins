package com.flexicore.organization.request;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.flexicore.organization.model.Customer;
import com.flexicore.organization.model.Industry;
import com.wizzdi.flexicore.security.request.BasicCreate;

public class IndustryToCustomerCreate extends BasicCreate {

    private String industryId;
    @JsonIgnore
    private Industry industry;
    private String customerId;
    @JsonIgnore
    private Customer customer;

    public String getIndustryId() {
        return industryId;
    }

    public <T extends IndustryToCustomerCreate> T setIndustryId(String industryId) {
        this.industryId = industryId;
        return (T) this;
    }

    @JsonIgnore
    public Industry getIndustry() {
        return industry;
    }

    public <T extends IndustryToCustomerCreate> T setIndustry(Industry industry) {
        this.industry = industry;
        return (T) this;
    }

    public String getCustomerId() {
        return customerId;
    }

    public <T extends IndustryToCustomerCreate> T setCustomerId(String customerId) {
        this.customerId = customerId;
        return (T) this;
    }

    @JsonIgnore
    public Customer getCustomer() {
        return customer;
    }

    public <T extends IndustryToCustomerCreate> T setCustomer(Customer customer) {
        this.customer = customer;
        return (T) this;
    }
}


