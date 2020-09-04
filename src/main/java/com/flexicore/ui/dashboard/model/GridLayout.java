package com.flexicore.ui.dashboard.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.flexicore.model.Baseclass;
import com.flexicore.security.SecurityContext;

import javax.persistence.Entity;
import javax.persistence.Lob;
import javax.persistence.OneToMany;
import java.util.ArrayList;
import java.util.List;

@Entity
public class GridLayout extends Baseclass {



    @JsonIgnore
    @OneToMany(targetEntity = GridLayoutCell.class,mappedBy = "gridLayout")
    private List<GridLayoutCell> cells=new ArrayList<>();

    public GridLayout() {
        super();
    }

    public GridLayout(String name, SecurityContext securityContext) {
        super(name, securityContext);
    }

    @JsonIgnore
    @OneToMany(targetEntity = GridLayoutCell.class,mappedBy = "gridLayout")
    public List<GridLayoutCell> getCells() {
        return cells;
    }

    public <T extends GridLayout> T setCells(List<GridLayoutCell> cells) {
        this.cells = cells;
        return (T) this;
    }

}
