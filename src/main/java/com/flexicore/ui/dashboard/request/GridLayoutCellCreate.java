package com.flexicore.ui.dashboard.request;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.flexicore.request.BaseclassCreate;
import com.flexicore.ui.dashboard.model.GridLayout;

public class GridLayoutCellCreate extends BaseclassCreate {


    private String gridLayoutId;
    @JsonIgnore
    private GridLayout gridLayout;


    public String getGridLayoutId() {
        return gridLayoutId;
    }

    public <T extends GridLayoutCellCreate> T setGridLayoutId(String gridLayoutId) {
        this.gridLayoutId = gridLayoutId;
        return (T) this;
    }

    @JsonIgnore
    public GridLayout getGridLayout() {
        return gridLayout;
    }

    public <T extends GridLayoutCellCreate> T setGridLayout(GridLayout gridLayout) {
        this.gridLayout = gridLayout;
        return (T) this;
    }


    @Override
    public boolean supportingDynamic() {
        return true;
    }
}
