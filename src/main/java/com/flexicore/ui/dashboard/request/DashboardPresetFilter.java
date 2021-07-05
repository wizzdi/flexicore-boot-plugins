package com.flexicore.ui.dashboard.request;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.flexicore.ui.dashboard.model.GridLayout;
import com.flexicore.ui.request.PresetFiltering;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class DashboardPresetFilter extends PresetFiltering {

    private Set<String> gridLayoutIds =new HashSet<>();
    @JsonIgnore
    private List<GridLayout> gridLayouts;

    public Set<String> getGridLayoutIds() {
        return gridLayoutIds;
    }

    public <T extends DashboardPresetFilter> T setGridLayoutIds(Set<String> gridLayoutIds) {
        this.gridLayoutIds = gridLayoutIds;
        return (T) this;
    }

    @JsonIgnore
    public List<GridLayout> getGridLayouts() {
        return gridLayouts;
    }

    public <T extends DashboardPresetFilter> T setGridLayouts(List<GridLayout> gridLayouts) {
        this.gridLayouts = gridLayouts;
        return (T) this;
    }
}
