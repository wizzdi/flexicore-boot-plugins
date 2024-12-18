package com.flexicore.model.territories;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.flexicore.model.Baseclass;

import jakarta.persistence.Entity;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import java.util.ArrayList;
import java.util.List;

@Entity
public class State extends Baseclass {


	public State() {
	}

	private String externalId;

	@ManyToOne(targetEntity = Country.class)
	private Country country;

	@JsonIgnore
	@OneToMany(targetEntity = City.class, mappedBy = "state")
	private List<City> cities = new ArrayList<>();

	@ManyToOne(targetEntity = Country.class)
	public Country getCountry() {
		return country;
	}

	public void setCountry(Country country) {
		this.country = country;
	}

	public String getExternalId() {
		return externalId;
	}

	public State setExternalId(String externalId) {
		this.externalId = externalId;
		return this;
	}
	@JsonIgnore
	@OneToMany(targetEntity = City.class, mappedBy = "state")
	public List<City> getCities() {
		return cities;
	}

	public <T extends State> T setCities(List<City> cities) {
		this.cities = cities;
		return (T) this;
	}
}
