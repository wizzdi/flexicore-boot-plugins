package com.wizzdi.flexicore.pricing.request;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.wizzdi.flexicore.pricing.model.product.PricedProduct;

public class PricedProductUpdate extends PricedProductCreate {
	private String id;
	@JsonIgnore
	private PricedProduct pricedProduct;

	public String getId() {
		return id;
	}

	public PricedProductUpdate setId(String id) {
		this.id = id;
		return this;
	}

	@JsonIgnore
	public PricedProduct getPricedProduct() {
		return pricedProduct;
	}

	public PricedProductUpdate setPricedProduct(PricedProduct pricedProduct) {
		this.pricedProduct = pricedProduct;
		return this;
	}
}
