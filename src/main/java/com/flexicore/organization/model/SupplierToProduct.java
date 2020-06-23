package com.flexicore.organization.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.flexicore.model.Baseclass;
import com.flexicore.model.TimedLink;
import com.flexicore.product.model.Product;
import com.flexicore.security.SecurityContext;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.ManyToOne;

@Entity
public class SupplierToProduct extends TimedLink {


	public SupplierToProduct() {
	}

	public SupplierToProduct(String name, SecurityContext securityContext) {
		super(name, securityContext);
	}

	private double price;

	@Override
	@ManyToOne(targetEntity = Supplier.class, cascade = {CascadeType.MERGE,
			CascadeType.PERSIST})
	public Supplier getLeftside() {
		return (Supplier) super.getLeftside();
	}
	@JsonIgnore
	public void setLeftside(Supplier leftside) {
		super.setLeftside(leftside);
	}

	@Override
	@JsonIgnore
	public void setLeftside(Baseclass leftside) {
		super.setLeftside(leftside);
	}

	@Override
	@ManyToOne(targetEntity = Product.class, cascade = {CascadeType.MERGE,
			CascadeType.PERSIST})
	public Product getRightside() {
		return (Product) super.getRightside();
	}

	@JsonIgnore
	public void setRightside(Product rightside) {
		super.setRightside(rightside);
	}

	@Override
	@JsonIgnore
	public void setRightside(Baseclass rightside) {
		super.setRightside(rightside);
	}

	public double getPrice() {
		return price;
	}

	public SupplierToProduct setPrice(double price) {
		this.price = price;
		return this;
	}
}
