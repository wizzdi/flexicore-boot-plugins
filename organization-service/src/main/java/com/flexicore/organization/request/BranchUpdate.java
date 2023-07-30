package com.flexicore.organization.request;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.flexicore.organization.model.Branch;

public class BranchUpdate extends BranchCreate {
	private String id;
	@JsonIgnore
	private Branch branch;

	public String getId() {
		return id;
	}

	public BranchUpdate setId(String id) {
		this.id = id;
		return this;
	}

	@JsonIgnore
	public Branch getBranch() {
		return branch;
	}

	public BranchUpdate setBranch(Branch branch) {
		this.branch = branch;
		return this;
	}
}
