package com.flexicore.ui.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.flexicore.model.Baseclass;
import jakarta.persistence.Entity;
import jakarta.persistence.ManyToOne;

@Entity
public class GridPresetRuntimeFilter extends Baseclass {

    @ManyToOne(targetEntity = GridPreset.class)
    @JsonIgnore
    private GridPreset gridPreset;

    private String fieldPath;
    private int priority;

    public GridPresetRuntimeFilter() {
    }

    public GridPresetRuntimeFilter(GridPresetRuntimeFilter other) {
        if (other != null) {
            setName(other.getName());
            setDescription(other.getDescription());
            this.fieldPath = other.getFieldPath();
            this.priority = other.getPriority();
        }
    }

    @ManyToOne(targetEntity = GridPreset.class)
    @JsonIgnore
    public GridPreset getGridPreset() {
        return gridPreset;
    }

    public <T extends GridPresetRuntimeFilter> T setGridPreset(GridPreset gridPreset) {
        this.gridPreset = gridPreset;
        return (T) this;
    }

    public String getFieldPath() {
        return fieldPath;
    }

    public <T extends GridPresetRuntimeFilter> T setFieldPath(String fieldPath) {
        this.fieldPath = fieldPath;
        return (T) this;
    }

    public int getPriority() {
        return priority;
    }

    public <T extends GridPresetRuntimeFilter> T setPriority(int priority) {
        this.priority = priority;
        return (T) this;
    }
}
