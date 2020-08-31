package com.flexicore.ui.grid.layout;

import com.flexicore.model.Baseclass;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;

@Entity
public class GridLayoutCell extends Baseclass {

    private String externalId;
    private String contextString;
    @ManyToOne(targetEntity = GridLayout.class)
    private GridLayout gridLayout;

    public String getExternalId() {
        return externalId;
    }

    public <T extends GridLayoutCell> T setExternalId(String externalId) {
        this.externalId = externalId;
        return (T) this;
    }

    public String getContextString() {
        return contextString;
    }

    public <T extends GridLayoutCell> T setContextString(String contextString) {
        this.contextString = contextString;
        return (T) this;
    }

    public GridLayout getGridLayout() {
        return gridLayout;
    }

    public <T extends GridLayoutCell> T setGridLayout(GridLayout gridLayout) {
        this.gridLayout = gridLayout;
        return (T) this;
    }
}
