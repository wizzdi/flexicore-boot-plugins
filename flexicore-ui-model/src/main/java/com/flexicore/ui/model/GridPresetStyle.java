package com.flexicore.ui.model;

import com.flexicore.model.Baseclass;
import com.wizzdi.dynamic.properties.converter.JsonConverter;
import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.persistence.Entity;

import java.util.LinkedHashMap;
import java.util.Map;

@Entity
public class GridPresetStyle extends Baseclass {

    @Column(columnDefinition = "jsonb")
    @Convert(converter = JsonConverter.class)
    private Map<String, Object> titleStyle = new LinkedHashMap<>();

    @Column(columnDefinition = "jsonb")
    @Convert(converter = JsonConverter.class)
    private Map<String, Object> tableStyle = new LinkedHashMap<>();

    public GridPresetStyle() {
    }

    @Column(columnDefinition = "jsonb")
    @Convert(converter = JsonConverter.class)
    public Map<String, Object> getTitleStyle() {
        return titleStyle;
    }

    public <T extends GridPresetStyle> T setTitleStyle(Map<String, Object> titleStyle) {
        this.titleStyle = titleStyle != null ? new LinkedHashMap<>(titleStyle) : new LinkedHashMap<>();
        return (T) this;
    }

    @Column(columnDefinition = "jsonb")
    @Convert(converter = JsonConverter.class)
    public Map<String, Object> getTableStyle() {
        return tableStyle;
    }

    public <T extends GridPresetStyle> T setTableStyle(Map<String, Object> tableStyle) {
        this.tableStyle = tableStyle != null ? new LinkedHashMap<>(tableStyle) : new LinkedHashMap<>();
        return (T) this;
    }
}
