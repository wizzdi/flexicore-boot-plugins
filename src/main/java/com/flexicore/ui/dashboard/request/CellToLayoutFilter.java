package com.flexicore.ui.dashboard.request;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.wizzdi.flexicore.security.request.PaginationFilter;
import com.flexicore.ui.dashboard.model.CellContent;
import com.flexicore.ui.dashboard.model.DashboardPreset;
import com.flexicore.ui.dashboard.model.GridLayoutCell;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class CellToLayoutFilter extends PaginationFilter {

    private Set<String> gridLayoutCellIds=new HashSet<>();
    @JsonIgnore
    private List<GridLayoutCell> gridLayoutCells;
    private Set<String> cellContentIds=new HashSet<>();
    @JsonIgnore
    private List<CellContent> cellContents;
    private Set<String> dashboardPresetIds=new HashSet<>();
    @JsonIgnore
    private List<DashboardPreset> dashboardPresets;


    public CellToLayoutFilter() {
    }


    public Set<String> getGridLayoutCellIds() {
        return gridLayoutCellIds;
    }

    public <T extends CellToLayoutFilter> T setGridLayoutCellIds(Set<String> gridLayoutCellIds) {
        this.gridLayoutCellIds = gridLayoutCellIds;
        return (T) this;
    }

    @JsonIgnore
    public List<GridLayoutCell> getGridLayoutCells() {
        return gridLayoutCells;
    }

    public <T extends CellToLayoutFilter> T setGridLayoutCells(List<GridLayoutCell> gridLayoutCells) {
        this.gridLayoutCells = gridLayoutCells;
        return (T) this;
    }

    public Set<String> getCellContentIds() {
        return cellContentIds;
    }

    public <T extends CellToLayoutFilter> T setCellContentIds(Set<String> cellContentIds) {
        this.cellContentIds = cellContentIds;
        return (T) this;
    }

    @JsonIgnore
    public List<CellContent> getCellContents() {
        return cellContents;
    }

    public <T extends CellToLayoutFilter> T setCellContents(List<CellContent> cellContents) {
        this.cellContents = cellContents;
        return (T) this;
    }

    public Set<String> getDashboardPresetIds() {
        return dashboardPresetIds;
    }

    public <T extends CellToLayoutFilter> T setDashboardPresetIds(Set<String> dashboardPresetIds) {
        this.dashboardPresetIds = dashboardPresetIds;
        return (T) this;
    }

    @JsonIgnore
    public List<DashboardPreset> getDashboardPresets() {
        return dashboardPresets;
    }

    public <T extends CellToLayoutFilter> T setDashboardPresets(List<DashboardPreset> dashboardPresets) {
        this.dashboardPresets = dashboardPresets;
        return (T) this;
    }

}
