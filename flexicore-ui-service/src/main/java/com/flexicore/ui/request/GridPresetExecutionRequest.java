package com.flexicore.ui.request;

import java.util.LinkedHashMap;
import java.util.Map;

public class GridPresetExecutionRequest {
    private String gridPresetId;
    private Map<String, Object> runtimeFiltering = new LinkedHashMap<>();

    public String getGridPresetId() { return gridPresetId; }
    public <T extends GridPresetExecutionRequest> T setGridPresetId(String gridPresetId) { this.gridPresetId = gridPresetId; return (T) this; }
    public Map<String, Object> getRuntimeFiltering() { return runtimeFiltering; }
    public <T extends GridPresetExecutionRequest> T setRuntimeFiltering(Map<String, Object> runtimeFiltering) {
        this.runtimeFiltering = runtimeFiltering != null ? new LinkedHashMap<>(runtimeFiltering) : new LinkedHashMap<>();
        return (T) this;
    }
}
