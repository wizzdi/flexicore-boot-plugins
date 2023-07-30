package com.wizzdi.flexicore.billing.request;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.wizzdi.flexicore.billing.model.billing.ChargeReference;
import com.wizzdi.flexicore.billing.model.payment.InvoiceItem;
import com.wizzdi.flexicore.security.request.BasicPropertiesFilter;
import com.wizzdi.flexicore.security.request.PaginationFilter;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class ChargeFiltering extends PaginationFilter {
    private BasicPropertiesFilter basicPropertiesFilter;

    private Set<String> chargeReferencesIds =new HashSet<>();
    @JsonIgnore
    private List<ChargeReference> chargeReferences;
    private Set<String> invoiceItemIds=new HashSet<>();
    @JsonIgnore
    private List<InvoiceItem> invoiceItems;

    public BasicPropertiesFilter getBasicPropertiesFilter() {
        return basicPropertiesFilter;
    }

    public <T extends ChargeFiltering> T setBasicPropertiesFilter(BasicPropertiesFilter basicPropertiesFilter) {
        this.basicPropertiesFilter = basicPropertiesFilter;
        return (T) this;
    }

    @JsonIgnore
    public List<ChargeReference> getChargeReferences() {
        return chargeReferences;
    }

    public <T extends ChargeFiltering> T setChargeReferences(List<ChargeReference> chargeReferences) {
        this.chargeReferences = chargeReferences;
        return (T) this;
    }

    public Set<String> getChargeReferencesIds() {
        return chargeReferencesIds;
    }

    public <T extends ChargeFiltering> T setChargeReferencesIds(Set<String> chargeReferencesIds) {
        this.chargeReferencesIds = chargeReferencesIds;
        return (T) this;
    }

    public Set<String> getInvoiceItemIds() {
        return invoiceItemIds;
    }

    public <T extends ChargeFiltering> T setInvoiceItemIds(Set<String> invoiceItemIds) {
        this.invoiceItemIds = invoiceItemIds;
        return (T) this;
    }

    @JsonIgnore
    public List<InvoiceItem> getInvoiceItems() {
        return invoiceItems;
    }

    public <T extends ChargeFiltering> T setInvoiceItems(List<InvoiceItem> invoiceItems) {
        this.invoiceItems = invoiceItems;
        return (T) this;
    }
}
