package com.flexicore.billing.request;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.flexicore.billing.model.PriceListToService;

public class PriceListToServiceUpdate extends PriceListToServiceCreate {
	private String id;
	@JsonIgnore
	private PriceListToService priceListToService;

	public String getId() {
		return id;
	}

	public PriceListToServiceUpdate setId(String id) {
		this.id = id;
		return this;
	}

	@JsonIgnore
	public PriceListToService getPriceListToService() {
		return priceListToService;
	}

	public PriceListToServiceUpdate setPriceListToService(PriceListToService priceListToService) {
		this.priceListToService = priceListToService;
		return this;
	}
}
