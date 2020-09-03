package com.flexicore.ui.dashboard.request;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.flexicore.model.dynamic.DynamicExecution;
import com.flexicore.request.BaseclassCreate;
import com.flexicore.ui.dashboard.model.CellContent;
import com.flexicore.ui.dashboard.model.DashboardPreset;
import com.flexicore.ui.dashboard.model.GridLayoutCell;



public class CellToLayoutCreate extends BaseclassCreate {

    private String gridLayoutCellId;
    @JsonIgnore
    private GridLayoutCell gridLayoutCell;
    private String cellContentId;
    @JsonIgnore
    private CellContent cellContent;
    private String dashboardPresetId;
    @JsonIgnore
    private DashboardPreset dashboardPreset;
    private String dynamicExecutionId;
    @JsonIgnore
    private DynamicExecution dynamicExecution;


    public String getGridLayoutCellId() {
        return gridLayoutCellId;
    }

    public <T extends CellToLayoutCreate> T setGridLayoutCellId(String gridLayoutCellId) {
        this.gridLayoutCellId = gridLayoutCellId;
        return (T) this;
    }

    @JsonIgnore
    public GridLayoutCell getGridLayoutCell() {
        return gridLayoutCell;
    }

    public <T extends CellToLayoutCreate> T setGridLayoutCell(GridLayoutCell gridLayoutCell) {
        this.gridLayoutCell = gridLayoutCell;
        return (T) this;
    }

    public String getCellContentId() {
        return cellContentId;
    }

    public <T extends CellToLayoutCreate> T setCellContentId(String cellContentId) {
        this.cellContentId = cellContentId;
        return (T) this;
    }

    @JsonIgnore
    public CellContent getCellContent() {
        return cellContent;
    }

    public <T extends CellToLayoutCreate> T setCellContent(CellContent cellContent) {
        this.cellContent = cellContent;
        return (T) this;
    }

    public String getDashboardPresetId() {
        return dashboardPresetId;
    }

    public <T extends CellToLayoutCreate> T setDashboardPresetId(String dashboardPresetId) {
        this.dashboardPresetId = dashboardPresetId;
        return (T) this;
    }

    @JsonIgnore
    public DashboardPreset getDashboardPreset() {
        return dashboardPreset;
    }

    public <T extends CellToLayoutCreate> T setDashboardPreset(DashboardPreset dashboardPreset) {
        this.dashboardPreset = dashboardPreset;
        return (T) this;
    }

    public String getDynamicExecutionId() {
        return dynamicExecutionId;
    }

    public <T extends CellToLayoutCreate> T setDynamicExecutionId(String dynamicExecutionId) {
        this.dynamicExecutionId = dynamicExecutionId;
        return (T) this;
    }

    @JsonIgnore
    public DynamicExecution getDynamicExecution() {
        return dynamicExecution;
    }

    public <T extends CellToLayoutCreate> T setDynamicExecution(DynamicExecution dynamicExecution) {
        this.dynamicExecution = dynamicExecution;
        return (T) this;
    }
}
