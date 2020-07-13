package com.flexicore.billing.request;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.flexicore.billing.model.Contract;

public class ContractUpdate extends ContractCreate {
	private String id;
	@JsonIgnore
	private Contract contract;

	public String getId() {
		return id;
	}

	public ContractUpdate setId(String id) {
		this.id = id;
		return this;
	}

	@JsonIgnore
	public Contract getContract() {
		return contract;
	}

	public ContractUpdate setContract(Contract contract) {
		this.contract = contract;
		return this;
	}
}
