package com.wizzdi.flexicore.billing.request;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.wizzdi.flexicore.contract.model.ContractItem;

public class ContractItemChargeReferenceCreate extends ChargeReferenceCreate {


    @JsonIgnore
    private ContractItem contractItem;
    private String contractItemId;


    @JsonIgnore
    public ContractItem getContractItem() {
        return contractItem;
    }

    public <T extends ContractItemChargeReferenceCreate> T setContractItem(ContractItem contractItem) {
        this.contractItem = contractItem;
        return (T) this;
    }

    public String getContractItemId() {
        return contractItemId;
    }

    public <T extends ContractItemChargeReferenceCreate> T setContractItemId(String contractItemId) {
        this.contractItemId = contractItemId;
        return (T) this;
    }
}
