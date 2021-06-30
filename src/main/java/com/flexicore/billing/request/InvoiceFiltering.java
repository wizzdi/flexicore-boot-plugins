package com.flexicore.billing.request;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.flexicore.billing.model.PaymentMethod;
import com.wizzdi.flexicore.security.request.BasicPropertiesFilter;
import com.wizzdi.flexicore.security.request.PaginationFilter;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class InvoiceFiltering extends PaginationFilter {
    private BasicPropertiesFilter basicPropertiesFilter;

    private boolean unpaid;
    private Boolean automatic;

    public BasicPropertiesFilter getBasicPropertiesFilter() {
        return basicPropertiesFilter;
    }

    public <T extends InvoiceFiltering> T setBasicPropertiesFilter(BasicPropertiesFilter basicPropertiesFilter) {
        this.basicPropertiesFilter = basicPropertiesFilter;
        return (T) this;
    }


    public boolean isAutomatic() {
        return automatic;
    }

    public <T extends InvoiceFiltering> T setAutomatic(boolean automatic) {
        this.automatic = automatic;
        return (T) this;
    }

    public Boolean isUnpaid() {
        return unpaid;
    }

    public <T extends InvoiceFiltering> T setUnpaid(Boolean unpaid) {
        this.unpaid = unpaid;
        return (T) this;
    }
}
