package com.flexicore.billing.request;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.flexicore.billing.model.ContractItem;
import com.flexicore.billing.model.InvoiceItem;
import com.wizzdi.flexicore.security.request.BasicPropertiesFilter;
import com.wizzdi.flexicore.security.request.PaginationFilter;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class PaymentFiltering extends PaginationFilter {
    private BasicPropertiesFilter basicPropertiesFilter;

    private Set<String> invoiceItemIds = new HashSet<>();
    @JsonIgnore
    private List<InvoiceItem> invoiceItems;

    private Set<String> contractItemIds = new HashSet<>();
    @JsonIgnore
    private List<ContractItem> contractItems;
    private String paymentReferenceLike;

    public BasicPropertiesFilter getBasicPropertiesFilter() {
        return basicPropertiesFilter;
    }

    public <T extends PaymentFiltering> T setBasicPropertiesFilter(BasicPropertiesFilter basicPropertiesFilter) {
        this.basicPropertiesFilter = basicPropertiesFilter;
        return (T) this;
    }

    public Set<String> getInvoiceItemIds() {
        return invoiceItemIds;
    }

    public <T extends PaymentFiltering> T setInvoiceItemIds(Set<String> invoiceItemIds) {
        this.invoiceItemIds = invoiceItemIds;
        return (T) this;
    }

    @JsonIgnore
    public List<InvoiceItem> getInvoiceItems() {
        return invoiceItems;
    }

    public <T extends PaymentFiltering> T setInvoiceItems(List<InvoiceItem> invoiceItems) {
        this.invoiceItems = invoiceItems;
        return (T) this;
    }

    public Set<String> getContractItemIds() {
        return contractItemIds;
    }

    public <T extends PaymentFiltering> T setContractItemIds(Set<String> contractItemIds) {
        this.contractItemIds = contractItemIds;
        return (T) this;
    }

    @JsonIgnore
    public List<ContractItem> getContractItems() {
        return contractItems;
    }

    public <T extends PaymentFiltering> T setContractItems(List<ContractItem> contractItems) {
        this.contractItems = contractItems;
        return (T) this;
    }

    public String getPaymentReferenceLike() {
        return paymentReferenceLike;
    }

    public <T extends PaymentFiltering> T setPaymentReferenceLike(String paymentReferenceLike) {
        this.paymentReferenceLike = paymentReferenceLike;
        return (T) this;
    }
}
