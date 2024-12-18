package com.flexicore.ui.dashboard.model;

import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.flexicore.model.Baseclass;

import com.wizzdi.dynamic.properties.converter.JsonConverter;
import com.wizzdi.flexicore.boot.dynamic.invokers.model.DynamicExecution;

import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.persistence.Entity;
import jakarta.persistence.ManyToOne;
import java.util.Map;

@Entity
public class DataMapper extends Baseclass {

    @Column(columnDefinition = "jsonb")
   
    @Convert(converter = JsonConverter.class)
    private Map<String, Object> jsonNode;

    @ManyToOne(targetEntity = CellToLayout.class)
    private CellToLayout cellToLayout;
    @ManyToOne(targetEntity = DynamicExecution.class)
    private DynamicExecution dynamicExecution;
    private String fieldPath;
    private String staticData;
    @ManyToOne(targetEntity = CellContentElement.class)
    private CellContentElement cellContentElement;

    public DataMapper() {
        super();
    }

    @ManyToOne(targetEntity = CellToLayout.class)
    public CellToLayout getCellToLayout() {
        return cellToLayout;
    }

    public <T extends DataMapper> T setCellToLayout(CellToLayout cellToLayout) {
        this.cellToLayout = cellToLayout;
        return (T) this;
    }

    public String getFieldPath() {
        return fieldPath;
    }

    public <T extends DataMapper> T setFieldPath(String fieldPath) {
        this.fieldPath = fieldPath;
        return (T) this;
    }

    @ManyToOne(targetEntity = CellContentElement.class)
    public CellContentElement getCellContentElement() {
        return cellContentElement;
    }

    public <T extends DataMapper> T setCellContentElement(CellContentElement cellContentElement) {
        this.cellContentElement = cellContentElement;
        return (T) this;
    }

    public String getStaticData() {
        return staticData;
    }

    public <T extends DataMapper> T setStaticData(String staticData) {
        this.staticData = staticData;
        return (T) this;
    }

    @ManyToOne(targetEntity = DynamicExecution.class)
    public DynamicExecution getDynamicExecution() {
        return dynamicExecution;
    }

    public <T extends DataMapper> T setDynamicExecution(DynamicExecution dynamicExecution) {
        this.dynamicExecution = dynamicExecution;
        return (T) this;
    }

    @Column(columnDefinition = "jsonb")
    @JsonIgnore
   
    @Convert(converter = JsonConverter.class)
    public Map<String, Object> getJsonNode() {
        return jsonNode;
    }

    @JsonAnyGetter
    public Map<String, Object> any() {
        return jsonNode;
    }

    public <T extends Baseclass> T setJsonNode(Map<String, Object> jsonNode) {
        this.jsonNode = jsonNode;
        return (T) this;
    }
}
