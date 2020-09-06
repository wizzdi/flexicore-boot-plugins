package com.flexicore.ui.dashboard.request;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.flexicore.model.FilteringInformationHolder;
import com.flexicore.model.dynamic.DynamicExecution;
import com.flexicore.ui.dashboard.model.CellContent;
import com.flexicore.ui.dashboard.model.DashboardPreset;
import com.flexicore.ui.dashboard.model.GridLayoutCell;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class CellToLayoutFiltering extends FilteringInformationHolder {

    private Set<String> gridLayoutCellIds=new HashSet<>();
    @JsonIgnore
    private List<GridLayoutCell> gridLayoutCells;
    private Set<String> cellContentIds=new HashSet<>();
    @JsonIgnore
    private List<CellContent> cellContents;
    private Set<String> dashboardPresetIds=new HashSet<>();
    @JsonIgnore
    private List<DashboardPreset> dashboardPresets;


    public CellToLayoutFiltering() {
    }


    public Set<String> getGridLayoutCellIds() {
        return gridLayoutCellIds;
    }

    public <T extends CellToLayoutFiltering> T setGridLayoutCellIds(Set<String> gridLayoutCellIds) {
        this.gridLayoutCellIds = gridLayoutCellIds;
        return (T) this;
    }

    @JsonIgnore
    public List<GridLayoutCell> getGridLayoutCells() {
        return gridLayoutCells;
    }

    public <T extends CellToLayoutFiltering> T setGridLayoutCells(List<GridLayoutCell> gridLayoutCells) {
        this.gridLayoutCells = gridLayoutCells;
        return (T) this;
    }

    public Set<String> getCellContentIds() {
        return cellContentIds;
    }

    public <T extends CellToLayoutFiltering> T setCellContentIds(Set<String> cellContentIds) {
        this.cellContentIds = cellContentIds;
        return (T) this;
    }

    @JsonIgnore
    public List<CellContent> getCellContents() {
        return cellContents;
    }

    public <T extends CellToLayoutFiltering> T setCellContents(List<CellContent> cellContents) {
        this.cellContents = cellContents;
        return (T) this;
    }

    public Set<String> getDashboardPresetIds() {
        return dashboardPresetIds;
    }

    public <T extends CellToLayoutFiltering> T setDashboardPresetIds(Set<String> dashboardPresetIds) {
        this.dashboardPresetIds = dashboardPresetIds;
        return (T) this;
    }

    @JsonIgnore
    public List<DashboardPreset> getDashboardPresets() {
        return dashboardPresets;
    }

    public <T extends CellToLayoutFiltering> T setDashboardPresets(List<DashboardPreset> dashboardPresets) {
        this.dashboardPresets = dashboardPresets;
        return (T) this;
    }

}
