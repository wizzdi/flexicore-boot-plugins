package com.flexicore.model.territories;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.flexicore.model.SecuredBasic;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import java.util.ArrayList;
import java.util.List;

@Entity
public class Neighbourhood extends SecuredBasic {


	public Neighbourhood() {
	}


	private String externalId;
	@OneToMany(targetEntity = Address.class, mappedBy = "neighbourhood", cascade = {
			CascadeType.MERGE, CascadeType.PERSIST})
	@JsonIgnore
	private List<Address> addresses = new ArrayList<>();

	@ManyToOne(targetEntity = City.class)
	private City city;

	@ManyToOne(targetEntity = City.class)
	public City getCity() {
		return city;
	}

	public Neighbourhood setCity(City city) {
		this.city = city;
		return this;
	}

	@OneToMany(targetEntity = Address.class, mappedBy = "neighbourhood", cascade = {
			CascadeType.MERGE, CascadeType.PERSIST})
	@JsonIgnore
	public List<Address> getAddresses() {
		return addresses;
	}

	public Neighbourhood setAddresses(List<Address> addresses) {
		this.addresses = addresses;
		return this;
	}

	public String getExternalId() {
		return externalId;
	}

	public Neighbourhood setExternalId(String externalId) {
		this.externalId = externalId;
		return this;
	}

}