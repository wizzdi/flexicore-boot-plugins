package com.flexicore.billing.request;

import com.flexicore.billing.model.ContractItem;

public class ActivateContractItemRequest {

    private ContractItem contractItem;

    public ContractItem getContractItem() {
        return contractItem;
    }

    public <T extends ActivateContractItemRequest> T setContractItem(ContractItem contractItem) {
        this.contractItem = contractItem;
        return (T) this;
    }
}
