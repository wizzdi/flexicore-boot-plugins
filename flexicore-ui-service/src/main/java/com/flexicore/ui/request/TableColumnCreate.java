package com.flexicore.ui.request;

public class TableColumnCreate extends UiFieldCreate {
	private String fieldPath;
	private Boolean sortable;
	private Boolean filterable;
	private Double defaultColumnWidth;

	public TableColumnCreate() {
	}

	public String getFieldPath() {
		return fieldPath;
	}

	public <T extends TableColumnCreate> T setFieldPath(String fieldPath) {
		this.fieldPath = fieldPath;
		return (T) this;
	}

	public Boolean getSortable() {
		return sortable;
	}

	public <T extends TableColumnCreate> T setSortable(Boolean sortable) {
		this.sortable = sortable;
		return (T) this;
	}

	public Boolean getFilterable() {
		return filterable;
	}

	public <T extends TableColumnCreate> T setFilterable(Boolean filterable) {
		this.filterable = filterable;
		return (T) this;
	}

	public Double getDefaultColumnWidth() {
		return defaultColumnWidth;
	}

	public <T extends TableColumnCreate> T setDefaultColumnWidth(
			Double defaultColumnWidth) {
		this.defaultColumnWidth = defaultColumnWidth;
		return (T) this;
	}
}
