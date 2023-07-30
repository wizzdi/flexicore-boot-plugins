package com.flexicore.ui.dashboard.request;

import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.flexicore.ui.request.PresetCreate;
import com.wizzdi.flexicore.security.request.BasicCreate;
import com.flexicore.ui.dashboard.model.CellContentElement;
import com.flexicore.ui.dashboard.model.CellToLayout;
import com.wizzdi.flexicore.boot.dynamic.invokers.model.DynamicExecution;

import java.util.HashMap;
import java.util.Map;

public class DataMapperCreate extends BasicCreate{
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

   private Map<String, Object> jsonNode=new HashMap<>();

    @JsonIgnore
    public Map<String, Object> getJsonNode() {
        return this.jsonNode;
    }

    @JsonAnyGetter
    public Map<String, Object> any() {
        return this.jsonNode;
    }

    @JsonAnySetter
    public void add(String key, Object value) {
        jsonNode.put(key, value);
    }

    public <T extends DataMapperCreate> T setJsonNode(Map<String, Object> jsonNode) {
        this.jsonNode = jsonNode;
        return (T) this;
    }
}
