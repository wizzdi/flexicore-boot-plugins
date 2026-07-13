package com.flexicore.ui.request;

import com.flexicore.annotations.TypeRetention;

import java.util.Set;

public class GridPresetFiltering extends PresetFiltering {

	@TypeRetention(String.class)
	private Set<String> dynamicInvokerCanonicalNames;
	@TypeRetention(String.class)
	private Set<String> dynamicInvokerMethodNames;
	@TypeRetention(String.class)
	private Set<String> createOperationMethodNames;
	@TypeRetention(String.class)
	private Set<String> updateOperationMethodNames;
	@TypeRetention(String.class)
	private Set<String> gridPresetStyleIds;

	public Set<String> getDynamicInvokerCanonicalNames() {
		return dynamicInvokerCanonicalNames;
	}

	public <T extends GridPresetFiltering> T setDynamicInvokerCanonicalNames(Set<String> dynamicInvokerCanonicalNames) {
		this.dynamicInvokerCanonicalNames = dynamicInvokerCanonicalNames;
		return (T) this;
	}

	public Set<String> getDynamicInvokerMethodNames() {
		return dynamicInvokerMethodNames;
	}

	public <T extends GridPresetFiltering> T setDynamicInvokerMethodNames(Set<String> dynamicInvokerMethodNames) {
		this.dynamicInvokerMethodNames = dynamicInvokerMethodNames;
		return (T) this;
	}

	public Set<String> getCreateOperationMethodNames() {
		return createOperationMethodNames;
	}

	public <T extends GridPresetFiltering> T setCreateOperationMethodNames(Set<String> createOperationMethodNames) {
		this.createOperationMethodNames = createOperationMethodNames;
		return (T) this;
	}

	public Set<String> getUpdateOperationMethodNames() {
		return updateOperationMethodNames;
	}

	public <T extends GridPresetFiltering> T setUpdateOperationMethodNames(Set<String> updateOperationMethodNames) {
		this.updateOperationMethodNames = updateOperationMethodNames;
		return (T) this;
	}

	public Set<String> getGridPresetStyleIds() {
		return gridPresetStyleIds;
	}

	public <T extends GridPresetFiltering> T setGridPresetStyleIds(Set<String> gridPresetStyleIds) {
		this.gridPresetStyleIds = gridPresetStyleIds;
		return (T) this;
	}
}
