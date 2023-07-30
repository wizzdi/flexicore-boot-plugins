package com.flexicore.license.request;

import java.util.Set;

public class ClazzCountRequest {
	private boolean groupByTenant;
	private Set<String> clazzIds;

	public boolean isGroupByTenant() {
		return groupByTenant;
	}

	public <T extends ClazzCountRequest> T setGroupByTenant(boolean groupByTenant) {
		this.groupByTenant = groupByTenant;
		return (T) this;
	}

	public <T extends ClazzCountRequest> T setClazzIds(Set<String> clazzIds) {
		this.clazzIds = clazzIds;
		return (T) this;
	}

	public Set<String> getClazzIds() {
		return clazzIds;
	}
}
