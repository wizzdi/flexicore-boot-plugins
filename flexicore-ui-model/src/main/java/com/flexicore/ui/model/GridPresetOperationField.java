package com.flexicore.ui.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.flexicore.model.Baseclass;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.ManyToOne;

@Entity
public class GridPresetOperationField extends Baseclass {

    @ManyToOne(targetEntity = GridPreset.class)
    @JsonIgnore
    private GridPreset gridPreset;

    @Enumerated(EnumType.STRING)
    private GridPresetOperationType operationType;

    private String fieldPath;
    private boolean visible = true;
    private int priority;

    public GridPresetOperationField() {
    }

    public GridPresetOperationField(String fieldPath, boolean visible) {
        this.fieldPath = fieldPath;
        this.visible = visible;
    }

    public GridPresetOperationField(GridPresetOperationField other) {
        if (other != null) {
            this.fieldPath = other.getFieldPath();
            this.visible = other.isVisible();
            this.priority = other.getPriority();
            this.operationType = other.getOperationType();
        }
    }

    @ManyToOne(targetEntity = GridPreset.class)
    @JsonIgnore
    public GridPreset getGridPreset() {
        return gridPreset;
    }

    public <T extends GridPresetOperationField> T setGridPreset(GridPreset gridPreset) {
        this.gridPreset = gridPreset;
        return (T) this;
    }

    @Enumerated(EnumType.STRING)
    public GridPresetOperationType getOperationType() {
        return operationType;
    }

    public <T extends GridPresetOperationField> T setOperationType(GridPresetOperationType operationType) {
        this.operationType = operationType;
        return (T) this;
    }

    public String getFieldPath() {
        return fieldPath;
    }

    public <T extends GridPresetOperationField> T setFieldPath(String fieldPath) {
        this.fieldPath = fieldPath;
        return (T) this;
    }

    public boolean isVisible() {
        return visible;
    }

    public <T extends GridPresetOperationField> T setVisible(boolean visible) {
        this.visible = visible;
        return (T) this;
    }

    public int getPriority() {
        return priority;
    }

    public <T extends GridPresetOperationField> T setPriority(int priority) {
        this.priority = priority;
        return (T) this;
    }
}
