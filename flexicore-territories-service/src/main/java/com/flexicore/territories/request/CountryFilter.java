package com.flexicore.territories.request;

import com.wizzdi.flexicore.security.request.BasicPropertiesFilter;
import com.wizzdi.flexicore.security.request.PaginationFilter;

import java.util.Set;


public class CountryFilter extends PaginationFilter {

	private BasicPropertiesFilter basicPropertiesFilter;
	private Set<String> countryCodes;

	public BasicPropertiesFilter getBasicPropertiesFilter() {
		return basicPropertiesFilter;
	}

	public <T extends CountryFilter> T setBasicPropertiesFilter(BasicPropertiesFilter basicPropertiesFilter) {
		this.basicPropertiesFilter = basicPropertiesFilter;
		return (T) this;
	}

	public Set<String> getCountryCodes() {
		return countryCodes;
	}

	public <T extends CountryFilter> T setCountryCodes(Set<String> countryCodes) {
		this.countryCodes = countryCodes;
		return (T) this;
	}
}