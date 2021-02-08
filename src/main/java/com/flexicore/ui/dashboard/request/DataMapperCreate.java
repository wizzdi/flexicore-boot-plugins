package com.flexicore.ui.dashboard.request;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.flexicore.request.BaseclassCreate;
import com.flexicore.ui.dashboard.model.CellContentElement;
import com.flexicore.ui.dashboard.model.CellToLayout;
import com.wizzdi.flexicore.boot.dynamic.invokers.model.DynamicExecution;

public class DataMapperCreate extends BaseclassCreate {
    private String cellToLayoutId;
    @JsonIgnore
    private CellToLayout cellToLayout;
    private String dynamicExecutionId;
    @JsonIgnore
    private DynamicExecution dynamicExecution;
    private String cellContentElementId;
    @JsonIgnore
    private CellContentElement cellContentElement;

    private String fieldPath;
    private String staticData;

    public String getCellToLayoutId() {
        return cellToLayoutId;
    }

    public <T extends DataMapperCreate> T setCellToLayoutId(String cellToLayoutId) {
        this.cellToLayoutId = cellToLayoutId;
        return (T) this;
    }

    @JsonIgnore
    public CellToLayout getCellToLayout() {
        return cellToLayout;
    }

    public <T extends DataMapperCreate> T setCellToLayout(CellToLayout cellToLayout) {
        this.cellToLayout = cellToLayout;
        return (T) this;
    }

    public String getDynamicExecutionId() {
        return dynamicExecutionId;
    }

    public <T extends DataMapperCreate> T setDynamicExecutionId(String dynamicExecutionId) {
        this.dynamicExecutionId = dynamicExecutionId;
        return (T) this;
    }

    @JsonIgnore
    public DynamicExecution getDynamicExecution() {
        return dynamicExecution;
    }

    public <T extends DataMapperCreate> T setDynamicExecution(DynamicExecution dynamicExecution) {
        this.dynamicExecution = dynamicExecution;
        return (T) this;
    }

    public String getCellContentElementId() {
        return cellContentElementId;
    }

    public <T extends DataMapperCreate> T setCellContentElementId(String cellContentElementId) {
        this.cellContentElementId = cellContentElementId;
        return (T) this;
    }

    @JsonIgnore
    public CellContentElement getCellContentElement() {
        return cellContentElement;
    }

    public <T extends DataMapperCreate> T setCellContentElement(CellContentElement cellContentElement) {
        this.cellContentElement = cellContentElement;
        return (T) this;
    }

    public String getFieldPath() {
        return fieldPath;
    }

    public <T extends DataMapperCreate> T setFieldPath(String fieldPath) {
        this.fieldPath = fieldPath;
        return (T) this;
    }

    public String getStaticData() {
        return staticData;
    }

    public <T extends DataMapperCreate> T setStaticData(String staticData) {
        this.staticData = staticData;
        return (T) this;
    }

    @Override
    public boolean supportingDynamic() {
        return true;
    }
}
