package com.flexicore.billing.request;

public class ActivateContractItemsRequest {

    private ContractItemFiltering contractItemFiltering;

    public ContractItemFiltering getContractItemFiltering() {
        return contractItemFiltering;
    }

    public <T extends ActivateContractItemsRequest> T setContractItemFiltering(ContractItemFiltering contractItemFiltering) {
        this.contractItemFiltering = contractItemFiltering;
        return (T) this;
    }
}
