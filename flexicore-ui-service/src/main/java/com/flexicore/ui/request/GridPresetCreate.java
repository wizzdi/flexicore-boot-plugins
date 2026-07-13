package com.flexicore.ui.request;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.flexicore.annotations.TypeRetention;
import com.flexicore.ui.model.GridPresetOperationField;
import com.flexicore.ui.model.GridPresetRuntimeFilter;
import com.flexicore.ui.model.GridPresetStyle;
import com.wizzdi.flexicore.boot.dynamic.invokers.model.DynamicExecution;
import com.wizzdi.flexicore.boot.dynamic.invokers.request.DynamicExecutionCreate;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class GridPresetCreate extends PresetCreate {

	private String relatedClassCanonicalName;
	private String dynamicInvokerCanonicalName;
	private String dynamicInvokerMethodName;
	private Map<String, Object> filtering;
	private String createOperationMethodName;
	@TypeRetention(GridPresetOperationField.class)
	private List<GridPresetOperationField> createOperationFields;
	private String updateOperationMethodName;
	@TypeRetention(GridPresetOperationField.class)
	private List<GridPresetOperationField> updateOperationFields;
	@TypeRetention(GridPresetRuntimeFilter.class)
	private List<GridPresetRuntimeFilter> runtimeFilterFields;

	private String gridPresetStyleId;
	@JsonIgnore
	private GridPresetStyle gridPresetStyle;
	private String latMapping;
	private String lonMapping;

	@JsonIgnore
	private DynamicExecution dynamicExecution;
	@JsonIgnore
	private DynamicExecutionCreate dynamicExecutionCreate;
	@JsonIgnore
	private boolean definitionResolved;

	public String getRelatedClassCanonicalName() {
		return relatedClassCanonicalName;
	}

	public <T extends GridPresetCreate> T setRelatedClassCanonicalName(String relatedClassCanonicalName) {
		this.relatedClassCanonicalName = relatedClassCanonicalName;
		return (T) this;
	}

	public String getDynamicInvokerCanonicalName() {
		return dynamicInvokerCanonicalName;
	}

	public <T extends GridPresetCreate> T setDynamicInvokerCanonicalName(String dynamicInvokerCanonicalName) {
		this.dynamicInvokerCanonicalName = dynamicInvokerCanonicalName;
		return (T) this;
	}

	public String getDynamicInvokerMethodName() {
		return dynamicInvokerMethodName;
	}

	public <T extends GridPresetCreate> T setDynamicInvokerMethodName(String dynamicInvokerMethodName) {
		this.dynamicInvokerMethodName = dynamicInvokerMethodName;
		return (T) this;
	}

	public Map<String, Object> getFiltering() {
		return filtering;
	}

	public <T extends GridPresetCreate> T setFiltering(Map<String, Object> filtering) {
		this.filtering = filtering != null ? new LinkedHashMap<>(filtering) : null;
		return (T) this;
	}

	public String getCreateOperationMethodName() {
		return createOperationMethodName;
	}

	public <T extends GridPresetCreate> T setCreateOperationMethodName(String createOperationMethodName) {
		this.createOperationMethodName = createOperationMethodName;
		return (T) this;
	}

	public List<GridPresetOperationField> getCreateOperationFields() {
		return createOperationFields;
	}

	public <T extends GridPresetCreate> T setCreateOperationFields(List<GridPresetOperationField> createOperationFields) {
		this.createOperationFields = copyOperationFields(createOperationFields);
		return (T) this;
	}

	public String getUpdateOperationMethodName() {
		return updateOperationMethodName;
	}

	public <T extends GridPresetCreate> T setUpdateOperationMethodName(String updateOperationMethodName) {
		this.updateOperationMethodName = updateOperationMethodName;
		return (T) this;
	}

	public List<GridPresetOperationField> getUpdateOperationFields() {
		return updateOperationFields;
	}

	public <T extends GridPresetCreate> T setUpdateOperationFields(List<GridPresetOperationField> updateOperationFields) {
		this.updateOperationFields = copyOperationFields(updateOperationFields);
		return (T) this;
	}

	public List<GridPresetRuntimeFilter> getRuntimeFilterFields() {
		return runtimeFilterFields;
	}

	public <T extends GridPresetCreate> T setRuntimeFilterFields(List<GridPresetRuntimeFilter> runtimeFilterFields) {
		if (runtimeFilterFields == null) {
			this.runtimeFilterFields = null;
		} else {
			this.runtimeFilterFields = new ArrayList<>();
			for (GridPresetRuntimeFilter field : runtimeFilterFields) {
				this.runtimeFilterFields.add(field != null ? new GridPresetRuntimeFilter(field) : null);
			}
		}
		return (T) this;
	}


	public String getGridPresetStyleId() {
		return gridPresetStyleId;
	}

	public <T extends GridPresetCreate> T setGridPresetStyleId(String gridPresetStyleId) {
		this.gridPresetStyleId = gridPresetStyleId;
		return (T) this;
	}

	@JsonIgnore
	public GridPresetStyle getGridPresetStyle() {
		return gridPresetStyle;
	}

	public <T extends GridPresetCreate> T setGridPresetStyle(GridPresetStyle gridPresetStyle) {
		this.gridPresetStyle = gridPresetStyle;
		return (T) this;
	}

	private List<GridPresetOperationField> copyOperationFields(List<GridPresetOperationField> fields) {
		if (fields == null) {
			return null;
		}
		List<GridPresetOperationField> copied = new ArrayList<>();
		for (GridPresetOperationField field : fields) {
			copied.add(field != null ? new GridPresetOperationField(field) : null);
		}
		return copied;
	}

	@JsonIgnore
	public DynamicExecution getDynamicExecution() {
		return dynamicExecution;
	}

	public <T extends GridPresetCreate> T setDynamicExecution(DynamicExecution dynamicExecution) {
		this.dynamicExecution = dynamicExecution;
		return (T) this;
	}

	@JsonIgnore
	public DynamicExecutionCreate getDynamicExecutionCreate() {
		return dynamicExecutionCreate;
	}

	public <T extends GridPresetCreate> T setDynamicExecutionCreate(DynamicExecutionCreate dynamicExecutionCreate) {
		this.dynamicExecutionCreate = dynamicExecutionCreate;
		return (T) this;
	}


	@JsonIgnore
	public boolean isDefinitionResolved() {
		return definitionResolved;
	}

	public <T extends GridPresetCreate> T setDefinitionResolved(boolean definitionResolved) {
		this.definitionResolved = definitionResolved;
		return (T) this;
	}

	public String getLatMapping() {
		return latMapping;
	}

	public <T extends GridPresetCreate> T setLatMapping(String latMapping) {
		this.latMapping = latMapping;
		return (T) this;
	}

	public String getLonMapping() {
		return lonMapping;
	}

	public <T extends GridPresetCreate> T setLonMapping(String lonMapping) {
		this.lonMapping = lonMapping;
		return (T) this;
	}
}
