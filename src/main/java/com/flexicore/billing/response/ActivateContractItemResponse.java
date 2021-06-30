package com.flexicore.billing.response;

import com.flexicore.billing.model.ContractItem;

public class ActivateContractItemResponse {

    private ContractItem contractItem;

    public ContractItem getContractItem() {
        return contractItem;
    }

    public <T extends ActivateContractItemResponse> T setContractItem(ContractItem contractItem) {
        this.contractItem = contractItem;
        return (T) this;
    }
}
