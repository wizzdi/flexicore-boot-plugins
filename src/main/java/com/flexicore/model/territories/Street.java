package com.flexicore.model.territories;

import com.flexicore.model.Baseclass;
import javax.persistence.Entity;
import javax.persistence.OneToMany;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.flexicore.model.Basic;
import com.flexicore.model.SecuredBasic;

import javax.persistence.ManyToOne;
import java.util.ArrayList;
import java.util.List;

@Entity
public class Street extends SecuredBasic {


	public Street() {
	}


	private String externalId;

	@ManyToOne(targetEntity = City.class)
	private City city;
	@OneToMany(targetEntity = Address.class, mappedBy = "street")
	@JsonIgnore
	private List<Address> addresss = new ArrayList<>();

	@ManyToOne(targetEntity = City.class)
	public City getCity() {
		return city;
	}

	public void setCity(City city) {
		this.city = city;
	}

	@OneToMany(targetEntity = Address.class, mappedBy = "street")
	@JsonIgnore
	public List<Address> getAddresss() {
		return addresss;
	}

	public void setAddresss(List<Address> addresss) {
		this.addresss = addresss;
	}

	public String getExternalId() {
		return externalId;
	}

	public Street setExternalId(String externalId) {
		this.externalId = externalId;
		return this;
	}

}