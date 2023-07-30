package com.flexicore.organization.request;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.flexicore.annotations.TypeRetention;
import com.flexicore.model.territories.Address;
import com.wizzdi.flexicore.security.request.BasicPropertiesFilter;
import com.wizzdi.flexicore.security.request.PaginationFilter;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class SiteFiltering extends PaginationFilter {

	private BasicPropertiesFilter basicPropertiesFilter;


	private Set<String> addressIds = new HashSet<>();
	@JsonIgnore
	@TypeRetention(Address.class)
	private List<Address> addresses;

	private Set<String> externalIds = new HashSet<>();

	public BasicPropertiesFilter getBasicPropertiesFilter() {
		return basicPropertiesFilter;
	}

	public <T extends SiteFiltering> T setBasicPropertiesFilter(BasicPropertiesFilter basicPropertiesFilter) {
		this.basicPropertiesFilter = basicPropertiesFilter;
		return (T) this;
	}

	public Set<String> getAddressIds() {
		return addressIds;
	}

	public <T extends SiteFiltering> T setAddressIds(Set<String> addressIds) {
		this.addressIds = addressIds;
		return (T) this;
	}

	@JsonIgnore
	public List<Address> getAddresses() {
		return addresses;
	}

	public <T extends SiteFiltering> T setAddresses(List<Address> addresses) {
		this.addresses = addresses;
		return (T) this;
	}

	public Set<String> getExternalIds() {
		return externalIds;
	}

	public <T extends SiteFiltering> T setExternalIds(Set<String> externalIds) {
		this.externalIds = externalIds;
		return (T) this;
	}
}
