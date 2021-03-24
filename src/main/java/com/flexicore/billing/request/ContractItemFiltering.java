package com.flexicore.billing.request;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.flexicore.billing.model.BusinessService;
import com.flexicore.billing.model.Contract;
import com.flexicore.billing.model.PriceListToService;
import com.wizzdi.flexicore.security.request.BasicPropertiesFilter;
import com.wizzdi.flexicore.security.request.PaginationFilter;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class ContractItemFiltering extends PaginationFilter {
private BasicPropertiesFilter basicPropertiesFilter;

    private Set<String> businessServiceIds=new HashSet<>();
    @JsonIgnore
    private List<BusinessService> businessServices;

    private Set<String> priceListToServiceIds=new HashSet<>();
    @JsonIgnore
    private List<PriceListToService> priceListToService;


    private Set<String> contractIds=new HashSet<>();
    @JsonIgnore
    private List<Contract> contracts;

    public BasicPropertiesFilter getBasicPropertiesFilter() {
        return basicPropertiesFilter;
    }

    public <T extends ContractItemFiltering> T setBasicPropertiesFilter(BasicPropertiesFilter basicPropertiesFilter) {
        this.basicPropertiesFilter = basicPropertiesFilter;
        return (T) this;
    }

    public Set<String> getBusinessServiceIds() {
        return businessServiceIds;
    }

    public <T extends ContractItemFiltering> T setBusinessServiceIds(Set<String> businessServiceIds) {
        this.businessServiceIds = businessServiceIds;
        return (T) this;
    }

    @JsonIgnore
    public List<BusinessService> getBusinessServices() {
        return businessServices;
    }

    public <T extends ContractItemFiltering> T setBusinessServices(List<BusinessService> businessServices) {
        this.businessServices = businessServices;
        return (T) this;
    }

    public Set<String> getPriceListToServiceIds() {
        return priceListToServiceIds;
    }

    public <T extends ContractItemFiltering> T setPriceListToServiceIds(Set<String> priceListToServiceIds) {
        this.priceListToServiceIds = priceListToServiceIds;
        return (T) this;
    }

    @JsonIgnore
    public List<PriceListToService> getPriceListToService() {
        return priceListToService;
    }

    public <T extends ContractItemFiltering> T setPriceListToService(List<PriceListToService> priceListToService) {
        this.priceListToService = priceListToService;
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
}
