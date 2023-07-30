package com.flexicore.ui.request;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.flexicore.model.SecurityTenant;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class PresetToTenantFilter extends PresetLinkFilter {

	private Set<String> tenantIdsForPreset = new HashSet<>();
	@JsonIgnore
	private List<SecurityTenant> tenants;

	public <T extends PresetToTenantFilter> T setTenantIdsForPreset(
			Set<String> tenantIdsForPreset) {
		this.tenantIdsForPreset = tenantIdsForPreset;
		return (T) this;
	}

	@JsonIgnore
	public List<SecurityTenant> getTenants() {
		return tenants;
	}

	public <T extends PresetToTenantFilter> T setTenants(List<SecurityTenant> tenants) {
		this.tenants = tenants;
		return (T) this;
	}

	public Set<String> getTenantIdsForPreset() {
		return tenantIdsForPreset;
	}
}
