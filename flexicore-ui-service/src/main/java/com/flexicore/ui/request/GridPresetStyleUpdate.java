package com.flexicore.ui.request;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.flexicore.ui.model.GridPresetStyle;

public class GridPresetStyleUpdate extends GridPresetStyleCreate {

    private String id;
    @JsonIgnore
    private GridPresetStyle gridPresetStyle;

    public String getId() {
        return id;
    }

    public <T extends GridPresetStyleUpdate> T setId(String id) {
        this.id = id;
        return (T) this;
    }

    @JsonIgnore
    public GridPresetStyle getGridPresetStyle() {
        return gridPresetStyle;
    }

    public <T extends GridPresetStyleUpdate> T setGridPresetStyle(GridPresetStyle gridPresetStyle) {
        this.gridPresetStyle = gridPresetStyle;
        return (T) this;
    }
}
