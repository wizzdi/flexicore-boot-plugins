package com.flexicore.ui.dashboard.request;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.flexicore.model.FilteringInformationHolder;
import com.flexicore.model.dynamic.DynamicExecution;
import com.flexicore.ui.dashboard.model.CellContentElement;
import com.flexicore.ui.dashboard.model.CellToLayout;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class DataMapperFiltering extends FilteringInformationHolder {

    private Set<String> cellToLayoutIds=new HashSet<>();
    @JsonIgnore
    private List<CellToLayout> cellToLayouts;
    private Set<String> dynamicExecutionIds=new HashSet<>();
    @JsonIgnore
    private List<DynamicExecution> dynamicExecutions;
    private Set<String> cellContentElementIds=new HashSet<>();
    @JsonIgnore
    private List<CellContentElement> cellContentElements;

    public Set<String> getCellToLayoutIds() {
        return cellToLayoutIds;
    }

    public <T extends DataMapperFiltering> T setCellToLayoutIds(Set<String> cellToLayoutIds) {
        this.cellToLayoutIds = cellToLayoutIds;
        return (T) this;
    }

    public Set<String> getDynamicExecutionIds() {
        return dynamicExecutionIds;
    }

    public <T extends DataMapperFiltering> T setDynamicExecutionIds(Set<String> dynamicExecutionIds) {
        this.dynamicExecutionIds = dynamicExecutionIds;
        return (T) this;
    }

    public Set<String> getCellContentElementIds() {
        return cellContentElementIds;
    }

    public <T extends DataMapperFiltering> T setCellContentElementIds(Set<String> cellContentElementIds) {
        this.cellContentElementIds = cellContentElementIds;
        return (T) this;
    }

    @JsonIgnore
    public List<CellToLayout> getCellToLayouts() {
        return cellToLayouts;
    }

    public <T extends DataMapperFiltering> T setCellToLayouts(List<CellToLayout> cellToLayouts) {
        this.cellToLayouts = cellToLayouts;
        return (T) this;
    }

    @JsonIgnore
    public List<DynamicExecution> getDynamicExecutions() {
        return dynamicExecutions;
    }

    public <T extends DataMapperFiltering> T setDynamicExecutions(List<DynamicExecution> dynamicExecutions) {
        this.dynamicExecutions = dynamicExecutions;
        return (T) this;
    }

    @JsonIgnore
    public List<CellContentElement> getCellContentElements() {
        return cellContentElements;
    }

    public <T extends DataMapperFiltering> T setCellContentElements(List<CellContentElement> cellContentElements) {
        this.cellContentElements = cellContentElements;
        return (T) this;
    }
}
