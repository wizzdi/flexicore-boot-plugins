package com.wizzdi.flexicore.billing.request;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.wizzdi.flexicore.contract.model.ContractItem;

public class ContractItemUpdate extends ContractItemCreate {
	private String id;
	@JsonIgnore
	private ContractItem contractItem;

	public String getId() {
		return id;
	}

	public ContractItemUpdate setId(String id) {
		this.id = id;
		return this;
	}

	@JsonIgnore
	public ContractItem getContractItem() {
		return contractItem;
	}

	public ContractItemUpdate setContractItem(ContractItem contractItem) {
		this.contractItem = contractItem;
		return this;
	}
}
