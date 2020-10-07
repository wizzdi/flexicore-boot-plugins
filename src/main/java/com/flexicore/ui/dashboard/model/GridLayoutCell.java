package com.flexicore.ui.dashboard.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.flexicore.model.Baseclass;
import com.flexicore.security.SecurityContext;

import javax.persistence.Entity;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import java.util.ArrayList;
import java.util.List;

@Entity
public class GridLayoutCell extends Baseclass {

    private String externalId;

    @ManyToOne(targetEntity = GridLayout.class)
    private GridLayout gridLayout;

    @JsonIgnore
    @OneToMany(targetEntity = CellToLayout.class)
    private List<CellToLayout> cellToLayouts=new ArrayList<>();

    public GridLayoutCell() {
        super();
    }

    public GridLayoutCell(String name, SecurityContext securityContext) {
        super(name, securityContext);
    }

    public String getExternalId() {
        return externalId;
    }

    public <T extends GridLayoutCell> T setExternalId(String externalId) {
        this.externalId = externalId;
        return (T) this;
    }

    @ManyToOne(targetEntity = GridLayout.class)
    public GridLayout getGridLayout() {
        return gridLayout;
    }

    public <T extends GridLayoutCell> T setGridLayout(GridLayout gridLayout) {
        this.gridLayout = gridLayout;
        return (T) this;
    }


    @JsonIgnore
    @OneToMany(targetEntity = CellToLayout.class)
    public List<CellToLayout> getCellToLayouts() {
        return cellToLayouts;
    }

    public <T extends GridLayoutCell> T setCellToLayouts(List<CellToLayout> cellToLayouts) {
        this.cellToLayouts = cellToLayouts;
        return (T) this;
    }


}
