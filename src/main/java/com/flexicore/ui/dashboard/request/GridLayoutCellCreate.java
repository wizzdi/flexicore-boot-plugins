package com.flexicore.ui.dashboard.request;

import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.wizzdi.flexicore.security.request.BasicCreate;
import com.flexicore.ui.dashboard.model.GridLayout;

import java.util.HashMap;
import java.util.Map;

public class GridLayoutCellCreate extends BasicCreate{


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


   private Map<String, Object> jsonNode=new HashMap<>();

    @JsonIgnore
    public Map<String, Object> getJsonNode() {
        return this.jsonNode;
    }

    @JsonAnyGetter
    public Map<String, Object> any() {
        return this.jsonNode;
    }

    @JsonAnySetter
    public void add(String key, Object value) {
        jsonNode.put(key, value);
    }

    public <T extends GridLayoutCellCreate> T setJsonNode(Map<String, Object> jsonNode) {
        this.jsonNode = jsonNode;
        return (T) this;
    }
}
