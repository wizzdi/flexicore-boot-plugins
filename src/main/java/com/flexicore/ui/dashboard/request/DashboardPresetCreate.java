package com.flexicore.ui.dashboard.request;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.flexicore.ui.dashboard.model.GridLayout;
import com.flexicore.ui.model.GridPreset;
import com.flexicore.ui.request.PresetCreate;

public class DashboardPresetCreate extends PresetCreate {

    private String gridLayoutId;
    @JsonIgnore
    private GridLayout gridLayout;

    public String getGridLayoutId() {
        return gridLayoutId;
    }

    public <T extends DashboardPresetCreate> T setGridLayoutId(String gridLayoutId) {
        this.gridLayoutId = gridLayoutId;
        return (T) this;
    }

    @JsonIgnore
    public GridLayout getGridLayout() {
        return gridLayout;
    }

    public <T extends DashboardPresetCreate> T setGridLayout(GridLayout gridLayout) {
        this.gridLayout = gridLayout;
        return (T) this;
    }
}
