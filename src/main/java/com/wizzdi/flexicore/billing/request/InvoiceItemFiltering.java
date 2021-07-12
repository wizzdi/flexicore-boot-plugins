package com.wizzdi.flexicore.billing.request;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.wizzdi.flexicore.billing.model.billing.Charge;
import com.wizzdi.flexicore.billing.model.payment.Invoice;
import com.wizzdi.flexicore.security.request.BasicPropertiesFilter;
import com.wizzdi.flexicore.security.request.PaginationFilter;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class InvoiceItemFiltering extends PaginationFilter {
    private BasicPropertiesFilter basicPropertiesFilter;
    private Boolean unpaid;

    private Set<String> invoiceIds = new HashSet<>();
    @JsonIgnore
    private List<Invoice> invoices;

    private Set<String> chargeIds = new HashSet<>();
    @JsonIgnore
    private List<Charge> charges;

    public BasicPropertiesFilter getBasicPropertiesFilter() {
        return basicPropertiesFilter;
    }

    public <T extends InvoiceItemFiltering> T setBasicPropertiesFilter(BasicPropertiesFilter basicPropertiesFilter) {
        this.basicPropertiesFilter = basicPropertiesFilter;
        return (T) this;
    }

    public Set<String> getInvoiceIds() {
        return invoiceIds;
    }

    public <T extends InvoiceItemFiltering> T setInvoiceIds(Set<String> invoiceIds) {
        this.invoiceIds = invoiceIds;
        return (T) this;
    }

    @JsonIgnore
    public List<Invoice> getInvoices() {
        return invoices;
    }

    public <T extends InvoiceItemFiltering> T setInvoices(List<Invoice> invoices) {
        this.invoices = invoices;
        return (T) this;
    }

    public Set<String> getChargeIds() {
        return chargeIds;
    }

    public <T extends InvoiceItemFiltering> T setChargeIds(Set<String> chargeIds) {
        this.chargeIds = chargeIds;
        return (T) this;
    }

    @JsonIgnore
    public List<Charge> getCharges() {
        return charges;
    }

    public <T extends InvoiceItemFiltering> T setCharges(List<Charge> charges) {
        this.charges = charges;
        return (T) this;
    }

    public Boolean getUnpaid() {
        return unpaid;
    }

    public <T extends InvoiceItemFiltering> T setUnpaid(Boolean unpaid) {
        this.unpaid = unpaid;
        return (T) this;
    }
}
