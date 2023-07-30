package com.flexicore.territories.request;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.flexicore.annotations.TypeRetention;
import com.flexicore.model.territories.City;
import com.flexicore.model.territories.Street;
import com.wizzdi.flexicore.security.request.BasicPropertiesFilter;
import com.wizzdi.flexicore.security.request.PaginationFilter;

import java.util.HashSet;
import java.util.List;
import java.util.Set;


public class NeighbourhoodFilter extends PaginationFilter {

	private BasicPropertiesFilter basicPropertiesFilter;
	private Set<String> externalIds=new HashSet<>();
	private Set<String> citiesIds=new HashSet<>();
	@JsonIgnore
	@TypeRetention(City.class)
	private List<City> cities;


	public Set<String> getExternalIds() {
		return externalIds;
	}

	public <T extends NeighbourhoodFilter> T setExternalIds(Set<String> externalIds) {
		this.externalIds = externalIds;
		return (T) this;
	}

	public Set<String> getCitiesIds() {
		return citiesIds;
	}

	public <T extends NeighbourhoodFilter> T setCitiesIds(Set<String> citiesIds) {
		this.citiesIds = citiesIds;
		return (T) this;
	}

	@JsonIgnore
	public List<City> getCities() {
		return cities;
	}

	public <T extends NeighbourhoodFilter> T setCities(List<City> cities) {
		this.cities = cities;
		return (T) this;
	}

	public BasicPropertiesFilter getBasicPropertiesFilter() {
		return basicPropertiesFilter;
	}

	public <T extends NeighbourhoodFilter> T setBasicPropertiesFilter(BasicPropertiesFilter basicPropertiesFilter) {
		this.basicPropertiesFilter = basicPropertiesFilter;
		return (T) this;
	}
}
