package com.wizzdi.flexicore.billing.request;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.wizzdi.flexicore.contract.model.ContractItemChargeReference;

public class ContractItemChargeReferenceUpdate extends ContractItemChargeReferenceCreate {
	private String id;
	@JsonIgnore
	private ContractItemChargeReference contractItemChargeReference;

	public String getId() {
		return id;
	}

	public ContractItemChargeReferenceUpdate setId(String id) {
		this.id = id;
		return this;
	}

	@JsonIgnore
	public ContractItemChargeReference getContractItemChargeReference() {
		return contractItemChargeReference;
	}

	public ContractItemChargeReferenceUpdate setContractItemChargeReference(ContractItemChargeReference contractItemChargeReference) {
		this.contractItemChargeReference = contractItemChargeReference;
		return this;
	}
}
