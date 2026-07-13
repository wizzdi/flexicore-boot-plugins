package com.flexicore.ui.request;

import com.wizzdi.flexicore.security.request.BasicCreate;

import java.util.LinkedHashMap;
import java.util.Map;

public class GridPresetStyleCreate extends BasicCreate {

    private Map<String, Object> titleStyle;
    private Map<String, Object> tableStyle;

    public Map<String, Object> getTitleStyle() {
        return titleStyle;
    }

    public <T extends GridPresetStyleCreate> T setTitleStyle(Map<String, Object> titleStyle) {
        this.titleStyle = titleStyle != null ? new LinkedHashMap<>(titleStyle) : null;
        return (T) this;
    }

    public Map<String, Object> getTableStyle() {
        return tableStyle;
    }

    public <T extends GridPresetStyleCreate> T setTableStyle(Map<String, Object> tableStyle) {
        this.tableStyle = tableStyle != null ? new LinkedHashMap<>(tableStyle) : null;
        return (T) this;
    }
}
