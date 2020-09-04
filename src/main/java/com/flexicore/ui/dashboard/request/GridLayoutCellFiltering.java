package com.flexicore.ui.dashboard.request;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.flexicore.model.FilteringInformationHolder;
import com.flexicore.ui.dashboard.model.GridLayout;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class GridLayoutCellFiltering extends FilteringInformationHolder {

    private Set<String> gridLayoutIds=new HashSet<>();

    @JsonIgnore
    private List<GridLayout> gridLayouts;


    public Set<String> getGridLayoutIds() {
        return gridLayoutIds;
    }

    public <T extends GridLayoutCellFiltering> T setGridLayoutIds(Set<String> gridLayoutIds) {
        this.gridLayoutIds = gridLayoutIds;
        return (T) this;
    }

    @JsonIgnore
    public List<GridLayout> getGridLayouts() {
        return gridLayouts;
    }

    public <T extends GridLayoutCellFiltering> T setGridLayouts(List<GridLayout> gridLayouts) {
        this.gridLayouts = gridLayouts;
        return (T) this;
    }

    @Override
    public boolean supportingDynamic() {
        return true;
    }
}
