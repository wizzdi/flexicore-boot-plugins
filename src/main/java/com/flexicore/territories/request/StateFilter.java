package com.flexicore.territories.request;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.flexicore.annotations.TypeRetention;
import com.flexicore.model.SecurityTenant;
import com.flexicore.model.territories.Country;
import com.wizzdi.flexicore.security.request.BasicPropertiesFilter;
import com.wizzdi.flexicore.security.request.PaginationFilter;

import java.util.HashSet;
import java.util.List;
import java.util.Set;


public class StateFilter extends PaginationFilter {



	private BasicPropertiesFilter basicPropertiesFilter;
	private Set<String> countriesIds =new HashSet<>();

	private Set<String> externalIds;
	@JsonIgnore
	@TypeRetention(Country.class)
	private List<Country> countries;


	public BasicPropertiesFilter getBasicPropertiesFilter() {
		return basicPropertiesFilter;
	}

	public <T extends StateFilter> T setBasicPropertiesFilter(BasicPropertiesFilter basicPropertiesFilter) {
		this.basicPropertiesFilter = basicPropertiesFilter;
		return (T) this;
	}

	public Set<String> getCountriesIds() {
		return countriesIds;
	}

	public <T extends StateFilter> T setCountriesIds(Set<String> countriesIds) {
		this.countriesIds = countriesIds;
		return (T) this;
	}

	@JsonIgnore
	public List<Country> getCountries() {
		return countries;
	}

	public <T extends StateFilter> T setCountries(List<Country> countries) {
		this.countries = countries;
		return (T) this;
	}

	public Set<String> getExternalIds() {
		return externalIds;
	}

	public <T extends StateFilter> T setExternalIds(Set<String> externalIds) {
		this.externalIds = externalIds;
		return (T) this;
	}
}
