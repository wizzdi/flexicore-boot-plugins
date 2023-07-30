package com.flexicore.ui.dashboard.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.flexicore.ui.model.Preset;

import jakarta.persistence.Entity;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import java.util.ArrayList;
import java.util.List;

@Entity
public class DashboardPreset extends Preset {

    @ManyToOne(targetEntity = GridLayout.class)
    private GridLayout gridLayout;

    @JsonIgnore
    @OneToMany(targetEntity = CellToLayout.class,mappedBy = "dashboardPreset")
    private List<CellToLayout> cellToLayouts=new ArrayList<>();

    public DashboardPreset() {
    }



    @ManyToOne(targetEntity = GridLayout.class)

    public GridLayout getGridLayout() {
        return gridLayout;
    }

    public <T extends DashboardPreset> T setGridLayout(GridLayout gridLayout) {
        this.gridLayout = gridLayout;
        return (T) this;
    }

    @JsonIgnore
    @OneToMany(targetEntity = CellToLayout.class,mappedBy = "dashboardPreset")
    public List<CellToLayout> getCellToLayouts() {
        return cellToLayouts;
    }

    public <T extends DashboardPreset> T setCellToLayouts(List<CellToLayout> cellToLayouts) {
        this.cellToLayouts = cellToLayouts;
        return (T) this;
    }
}
