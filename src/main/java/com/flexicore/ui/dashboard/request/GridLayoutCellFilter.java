package com.flexicore.ui.dashboard.request;

import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.wizzdi.flexicore.security.request.PaginationFilter;
import com.flexicore.ui.dashboard.model.GridLayout;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class GridLayoutCellFilter extends PaginationFilter {

    private Set<String> gridLayoutIds=new HashSet<>();

    @JsonIgnore
    private List<GridLayout> gridLayouts;


    public Set<String> getGridLayoutIds() {
        return gridLayoutIds;
    }

    public <T extends GridLayoutCellFilter> T setGridLayoutIds(Set<String> gridLayoutIds) {
        this.gridLayoutIds = gridLayoutIds;
        return (T) this;
    }

    @JsonIgnore
    public List<GridLayout> getGridLayouts() {
        return gridLayouts;
    }

    public <T extends GridLayoutCellFilter> T setGridLayouts(List<GridLayout> gridLayouts) {
        this.gridLayouts = gridLayouts;
        return (T) this;
    }

   private Map<String, Object> jsonNode;

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

    public <T extends GridLayoutCellFilter> T setJsonNode(Map<String, Object> jsonNode) {
        this.jsonNode = jsonNode;
        return (T) this;
    }
}
