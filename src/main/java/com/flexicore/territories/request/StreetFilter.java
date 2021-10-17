package com.flexicore.territories.request;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.flexicore.annotations.TypeRetention;
import com.flexicore.model.territories.City;
import com.flexicore.model.territories.Country;
import com.flexicore.model.territories.Neighbourhood;
import com.wizzdi.flexicore.security.request.BasicPropertiesFilter;
import com.wizzdi.flexicore.security.request.PaginationFilter;

import java.util.HashSet;
import java.util.List;
import java.util.Set;


public class StreetFilter extends PaginationFilter {



	private BasicPropertiesFilter basicPropertiesFilter;
	private Set<String> externalIds=new HashSet<>();
	private Set<String> citiesIds=new HashSet<>();
	@JsonIgnore
	@TypeRetention(City.class)
	private List<City> cities;
	private Set<String> neighbourhoodsIds=new HashSet<>();
	@JsonIgnore
	@TypeRetention(Neighbourhood.class)
	private List<Neighbourhood> neighbourhoods;
	private Set<String> countriesIds=new HashSet<>();
	@JsonIgnore
	@TypeRetention(Country.class)
	private List<Country> countries;


	public Set<String> getExternalIds() {
		return externalIds;
	}

	public <T extends StreetFilter> T setExternalIds(Set<String> externalIds) {
		this.externalIds = externalIds;
		return (T) this;
	}

	public Set<String> getCitiesIds() {
		return citiesIds;
	}

	public <T extends StreetFilter> T setCitiesIds(Set<String> citiesIds) {
		this.citiesIds = citiesIds;
		return (T) this;
	}

	@JsonIgnore
	public List<City> getCities() {
		return cities;
	}

	public <T extends StreetFilter> T setCities(List<City> cities) {
		this.cities = cities;
		return (T) this;
	}

	public Set<String> getNeighbourhoodsIds() {
		return neighbourhoodsIds;
	}

	public <T extends StreetFilter> T setNeighbourhoodsIds(Set<String> neighbourhoodsIds) {
		this.neighbourhoodsIds = neighbourhoodsIds;
		return (T) this;
	}

	@JsonIgnore
	public List<Neighbourhood> getNeighbourhoods() {
		return neighbourhoods;
	}

	public <T extends StreetFilter> T setNeighbourhoods(List<Neighbourhood> neighbourhoods) {
		this.neighbourhoods = neighbourhoods;
		return (T) this;
	}

	public Set<String> getCountriesIds() {
		return countriesIds;
	}

	public <T extends StreetFilter> T setCountriesIds(Set<String> countriesIds) {
		this.countriesIds = countriesIds;
		return (T) this;
	}

	@JsonIgnore
	public List<Country> getCountries() {
		return countries;
	}

	public <T extends StreetFilter> T setCountries(List<Country> countries) {
		this.countries = countries;
		return (T) this;
	}

	public BasicPropertiesFilter getBasicPropertiesFilter() {
		return basicPropertiesFilter;
	}

	public <T extends StreetFilter> T setBasicPropertiesFilter(BasicPropertiesFilter basicPropertiesFilter) {
		this.basicPropertiesFilter = basicPropertiesFilter;
		return (T) this;
	}
}
