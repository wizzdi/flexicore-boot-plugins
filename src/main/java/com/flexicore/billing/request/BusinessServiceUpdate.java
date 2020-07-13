package com.flexicore.billing.request;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.flexicore.billing.model.BusinessService;

public class BusinessServiceUpdate extends BusinessServiceCreate {
	private String id;
	@JsonIgnore
	private BusinessService businessService;

	public String getId() {
		return id;
	}

	public BusinessServiceUpdate setId(String id) {
		this.id = id;
		return this;
	}

	@JsonIgnore
	public BusinessService getBusinessService() {
		return businessService;
	}

	public BusinessServiceUpdate setBusinessService(BusinessService businessService) {
		this.businessService = businessService;
		return this;
	}
}
