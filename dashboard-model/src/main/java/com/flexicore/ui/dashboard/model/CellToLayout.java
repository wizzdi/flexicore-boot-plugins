package com.flexicore.ui.dashboard.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.flexicore.model.Baseclass;

import jakarta.persistence.Entity;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
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
    @JsonIgnore
    @OneToMany(targetEntity = DataMapper.class,mappedBy = "cellToLayout")
    private List<DataMapper> dataMappers=new ArrayList<>();
    private String listFieldPath;


    public CellToLayout() {
        super();
    }

    @ManyToOne(targetEntity = GridLayoutCell.class)

    public GridLayoutCell getGridLayoutCell() {
        return gridLayoutCell;
    }

    public <T extends CellToLayout> T setGridLayoutCell(GridLayoutCell gridLayoutCell) {
        this.gridLayoutCell = gridLayoutCell;
        return (T) this;
    }

    @ManyToOne(targetEntity = CellContent.class)
    public CellContent getCellContent() {
        return cellContent;
    }

    public <T extends CellToLayout> T setCellContent(CellContent cellContent) {
        this.cellContent = cellContent;
        return (T) this;
    }

    @ManyToOne(targetEntity = DashboardPreset.class)
    public DashboardPreset getDashboardPreset() {
        return dashboardPreset;
    }

    public <T extends CellToLayout> T setDashboardPreset(DashboardPreset dashboardPreset) {
        this.dashboardPreset = dashboardPreset;
        return (T) this;
    }


    @JsonIgnore
    @OneToMany(targetEntity = DataMapper.class,mappedBy = "cellToLayout")
    public List<DataMapper> getDataMappers() {
        return dataMappers;
    }

    public <T extends CellToLayout> T setDataMappers(List<DataMapper> dataMappers) {
        this.dataMappers = dataMappers;
        return (T) this;
    }

    public String getListFieldPath() {
        return listFieldPath;
    }

    public <T extends CellToLayout> T setListFieldPath(String listFieldPath) {
        this.listFieldPath = listFieldPath;
        return (T) this;
    }
}
