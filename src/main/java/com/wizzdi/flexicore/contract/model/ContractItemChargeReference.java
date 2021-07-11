package com.wizzdi.flexicore.contract.model;

import com.wizzdi.flexicore.billing.model.billing.ChargeReference;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;

@Entity
public class ContractItemChargeReference extends ChargeReference {

    @ManyToOne(targetEntity = ContractItem.class)
    private ContractItem contractItem;

    @ManyToOne(targetEntity = ContractItem.class)
    public ContractItem getContractItem() {
        return contractItem;
    }

    public <T extends ContractItemChargeReference> T setContractItem(ContractItem contractItem) {
        this.contractItem = contractItem;
        return (T) this;
    }
}
