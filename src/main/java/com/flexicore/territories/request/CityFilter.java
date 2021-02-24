package com.flexicore.territories.request;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.flexicore.model.territories.Country;
import com.flexicore.model.territories.State;
import com.wizzdi.flexicore.security.request.BasicPropertiesFilter;
import com.wizzdi.flexicore.security.request.PaginationFilter;

import java.util.HashSet;
import java.util.List;
import java.util.Set;


public class CityFilter extends PaginationFilter {

	private BasicPropertiesFilter basicPropertiesFilter;
	private Set<String> countriesIds =new HashSet<>();
	@JsonIgnore
	private List<Country> countries;



	private Set<String> statesIds =new HashSet<>();
	@JsonIgnore
	private List<State> states;




	public Set<String> getCountriesIds() {
		return countriesIds;
	}

	public <T extends CityFilter> T setCountriesIds(Set<String> countriesIds) {
		this.countriesIds = countriesIds;
		return (T) this;
	}

	@JsonIgnore

	public List<Country> getCountries() {
		return countries;
	}

	public <T extends CityFilter> T setCountries(List<Country> countries) {
		this.countries = countries;
		return (T) this;
	}



	public Set<String> getStatesIds() {
		return statesIds;
	}

	public <T extends CityFilter> T setStatesIds(Set<String> statesIds) {
		this.statesIds = statesIds;
		return (T) this;
	}

	@JsonIgnore
	public List<State> getStates() {
		return states;
	}

	public <T extends CityFilter> T setStates(List<State> states) {
		this.states = states;
		return (T) this;
	}

	public BasicPropertiesFilter getBasicPropertiesFilter() {
		return basicPropertiesFilter;
	}

	public <T extends CityFilter> T setBasicPropertiesFilter(BasicPropertiesFilter basicPropertiesFilter) {
		this.basicPropertiesFilter = basicPropertiesFilter;
		return (T) this;
	}
}