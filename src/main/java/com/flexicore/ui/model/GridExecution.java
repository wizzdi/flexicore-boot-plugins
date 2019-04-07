package com.flexicore.ui.model;

import com.flexicore.model.dynamic.DynamicExecution;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;


@Entity
public class GridExecution extends DynamicExecution {
    static GridExecution s_Singleton = new GridExecution();
    public static GridExecution s() {
        return s_Singleton;
    }


    @ManyToOne(targetEntity = GridElement.class)
    private GridElement gridElement;

    @ManyToOne(targetEntity = GridElement.class)
    public GridElement getGridElement() {
        return gridElement;
    }

    public <T extends GridExecution> T setGridElement(GridElement gridElement) {
        this.gridElement = gridElement;
        return (T) this;
    }
}
