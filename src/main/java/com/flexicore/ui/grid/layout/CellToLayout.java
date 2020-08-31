package com.flexicore.ui.grid.layout;

import com.flexicore.model.Baseclass;
import com.flexicore.model.dynamic.DynamicExecution;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import java.util.ArrayList;
import java.util.List;

@Entity
public class CellToLayout extends Baseclass {
    @ManyToOne(targetEntity = GridLayoutCell.class)
    private GridLayoutCell gridLayoutCell;
    @ManyToOne(targetEntity = CellContent.class)
    private CellContent cellContent;
    @ManyToOne(targetEntity = DashboardPreset.class)
    private DashboardPreset dashboardPreset;
    @OneToMany(targetEntity = DataMapper.class,mappedBy = "cellToLayout")
    private List<DataMapper> dataMappers=new ArrayList<>();
    @ManyToOne(targetEntity = DynamicExecution.class)
    private DynamicExecution dynamicExecution;

    public GridLayoutCell getGridLayoutCell() {
        return gridLayoutCell;
    }

    public <T extends CellToLayout> T setGridLayoutCell(GridLayoutCell gridLayoutCell) {
        this.gridLayoutCell = gridLayoutCell;
        return (T) this;
    }

    public CellContent getCellContent() {
        return cellContent;
    }

    public <T extends CellToLayout> T setCellContent(CellContent cellContent) {
        this.cellContent = cellContent;
        return (T) this;
    }

    public DashboardPreset getDashboardPreset() {
        return dashboardPreset;
    }

    public <T extends CellToLayout> T setDashboardPreset(DashboardPreset dashboardPreset) {
        this.dashboardPreset = dashboardPreset;
        return (T) this;
    }

    @ManyToOne(targetEntity = DynamicExecution.class)
    public DynamicExecution getDynamicExecution() {
        return dynamicExecution;
    }

    public <T extends CellToLayout> T setDynamicExecution(DynamicExecution dynamicExecution) {
        this.dynamicExecution = dynamicExecution;
        return (T) this;
    }
}
