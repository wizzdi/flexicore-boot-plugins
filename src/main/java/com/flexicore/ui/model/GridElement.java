package com.flexicore.ui.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.flexicore.model.Baseclass;
import javax.persistence.Entity;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import java.util.ArrayList;
import java.util.List;


@Entity
public class GridElement extends Baseclass {
    static GridElement s_Singleton = new GridElement();
    public static GridElement s() {
        return s_Singleton;
    }

    @Lob
    private String contextString;

    @JsonIgnore
    @OneToMany(targetEntity = GridExecution.class,mappedBy = "gridElement")
    private List<GridExecution> invokers=new ArrayList<>();

    @ManyToOne(targetEntity = Grid.class)
    private Grid grid;

    @ManyToOne(targetEntity = Grid.class)
    public Grid getGrid() {
        return grid;
    }

    public <T extends GridElement> T setGrid(Grid grid) {
        this.grid = grid;
        return (T) this;
    }

    @JsonIgnore
    @OneToMany(targetEntity = GridExecution.class,mappedBy = "gridElement")
    public List<GridExecution> getInvokers() {
        return invokers;
    }

    public <T extends GridElement> T setInvokers(List<GridExecution> invokers) {
        this.invokers = invokers;
        return (T) this;
    }

    @Lob
    public String getContextString() {
        return contextString;
    }

    public <T extends GridElement> T setContextString(String contextString) {
        this.contextString = contextString;
        return (T) this;
    }


}
