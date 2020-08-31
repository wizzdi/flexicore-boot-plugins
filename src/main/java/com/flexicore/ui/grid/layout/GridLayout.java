package com.flexicore.ui.grid.layout;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.flexicore.model.Baseclass;

import javax.persistence.Entity;
import javax.persistence.OneToMany;
import java.util.ArrayList;
import java.util.List;

@Entity
public class GridLayout extends Baseclass {

    private String contextString;

    @JsonIgnore
    @OneToMany(targetEntity = GridLayoutCell.class,mappedBy = "gridLayout")
    private List<GridLayoutCell> cells=new ArrayList<>();

    @JsonIgnore
    @OneToMany(targetEntity = GridLayoutCell.class,mappedBy = "gridLayout")
    public List<GridLayoutCell> getCells() {
        return cells;
    }

    public <T extends GridLayout> T setCells(List<GridLayoutCell> cells) {
        this.cells = cells;
        return (T) this;
    }

    public String getContextString() {
        return contextString;
    }

    public <T extends GridLayout> T setContextString(String contextString) {
        this.contextString = contextString;
        return (T) this;
    }
}
