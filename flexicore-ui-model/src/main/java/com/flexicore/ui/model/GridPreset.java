package com.flexicore.ui.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.wizzdi.dynamic.properties.converter.JsonConverter;
import com.wizzdi.flexicore.boot.dynamic.invokers.model.DynamicExecution;

import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.persistence.Entity;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Transient;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Entity
public class GridPreset extends Preset {

    private String relatedClassCanonicalName;
    private String dynamicInvokerCanonicalName;
    private String dynamicInvokerMethodName;

    @ManyToOne(targetEntity = DynamicExecution.class)
    @JsonIgnore
    private DynamicExecution dynamicExecution;

    @Column(columnDefinition = "jsonb")
    @Convert(converter = JsonConverter.class)
    private Map<String, Object> filtering = new LinkedHashMap<>();

    private String createOperationMethodName;
    @Transient
    private List<GridPresetOperationField> createOperationFields = new ArrayList<>();
    private String updateOperationMethodName;
    @Transient
    private List<GridPresetOperationField> updateOperationFields = new ArrayList<>();
    @Transient
    private List<GridPresetRuntimeFilter> runtimeFilterFields = new ArrayList<>();

    @ManyToOne(targetEntity = GridPresetStyle.class)
    private GridPresetStyle gridPresetStyle;

    private String latMapping;
    private String lonMapping;


    public GridPreset() {
    }


    public String getRelatedClassCanonicalName() {
        return relatedClassCanonicalName;
    }

    public String getDynamicInvokerCanonicalName() {
        return dynamicInvokerCanonicalName;
    }

    public String getDynamicInvokerMethodName() {
        return dynamicInvokerMethodName;
    }

    @ManyToOne(targetEntity = DynamicExecution.class)
    @JsonIgnore
    public DynamicExecution getDynamicExecution() {
        return dynamicExecution;
    }

    @Column(columnDefinition = "jsonb")
    @Convert(converter = JsonConverter.class)
    public Map<String, Object> getFiltering() {
        return filtering;
    }

    public String getCreateOperationMethodName() {
        return createOperationMethodName;
    }

    @Transient
    public List<GridPresetOperationField> getCreateOperationFields() {
        return createOperationFields;
    }

    public String getUpdateOperationMethodName() {
        return updateOperationMethodName;
    }

    @Transient
    public List<GridPresetOperationField> getUpdateOperationFields() {
        return updateOperationFields;
    }

    @Transient
    public List<GridPresetRuntimeFilter> getRuntimeFilterFields() {
        return runtimeFilterFields;
    }

    @ManyToOne(targetEntity = GridPresetStyle.class)
    public GridPresetStyle getGridPresetStyle() {
        return gridPresetStyle;
    }

    @Transient
    public String getGridPresetStyleId() {
        return gridPresetStyle != null ? gridPresetStyle.getId() : null;
    }

    @Transient
    public String getGridPresetStyleName() {
        return gridPresetStyle != null ? gridPresetStyle.getName() : null;
    }

    public <T extends GridPreset> T setRelatedClassCanonicalName(String relatedClassCanonicalName) {
        this.relatedClassCanonicalName = relatedClassCanonicalName;
        return (T) this;
    }

    public <T extends GridPreset> T setDynamicInvokerCanonicalName(String dynamicInvokerCanonicalName) {
        this.dynamicInvokerCanonicalName = dynamicInvokerCanonicalName;
        return (T) this;
    }

    public <T extends GridPreset> T setDynamicInvokerMethodName(String dynamicInvokerMethodName) {
        this.dynamicInvokerMethodName = dynamicInvokerMethodName;
        return (T) this;
    }

    public <T extends GridPreset> T setDynamicExecution(DynamicExecution dynamicExecution) {
        this.dynamicExecution = dynamicExecution;
        return (T) this;
    }

    public <T extends GridPreset> T setFiltering(Map<String, Object> filtering) {
        this.filtering = filtering != null ? new LinkedHashMap<>(filtering) : new LinkedHashMap<>();
        return (T) this;
    }

    public <T extends GridPreset> T setCreateOperationMethodName(String createOperationMethodName) {
        this.createOperationMethodName = createOperationMethodName;
        return (T) this;
    }

    public <T extends GridPreset> T setCreateOperationFields(List<GridPresetOperationField> createOperationFields) {
        this.createOperationFields = createOperationFields != null ? new ArrayList<>(createOperationFields) : new ArrayList<>();
        return (T) this;
    }

    public <T extends GridPreset> T setUpdateOperationMethodName(String updateOperationMethodName) {
        this.updateOperationMethodName = updateOperationMethodName;
        return (T) this;
    }

    public <T extends GridPreset> T setUpdateOperationFields(List<GridPresetOperationField> updateOperationFields) {
        this.updateOperationFields = updateOperationFields != null ? new ArrayList<>(updateOperationFields) : new ArrayList<>();
        return (T) this;
    }

    public <T extends GridPreset> T setRuntimeFilterFields(List<GridPresetRuntimeFilter> runtimeFilterFields) {
        this.runtimeFilterFields = runtimeFilterFields != null ? new ArrayList<>(runtimeFilterFields) : new ArrayList<>();
        return (T) this;
    }

    @Override
    public GridPreset setTitle(String title) {
        super.setTitle(title);
        return this;
    }

    public <T extends GridPreset> T setGridPresetStyle(GridPresetStyle gridPresetStyle) {
        this.gridPresetStyle = gridPresetStyle;
        return (T) this;
    }

    public String getLatMapping() {
        return latMapping;
    }

    public <T extends GridPreset> T setLatMapping(String latMapping) {
        this.latMapping = latMapping;
        return (T) this;
    }

    public String getLonMapping() {
        return lonMapping;
    }

    public <T extends GridPreset> T setLonMapping(String lonMapping) {
        this.lonMapping = lonMapping;
        return (T) this;
    }

    public String getTenantName() {
        return Optional.ofNullable(getTenant()).map(f -> f.getName()).orElse(null);
    }

    public String getCreatorName() {
        return Optional.ofNullable(getCreator()).map(f -> f.getName()).orElse(null);
    }
}
