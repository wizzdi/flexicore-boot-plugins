package com.flexicore.ui.request;

import com.flexicore.annotations.TypeRetention;
import com.wizzdi.flexicore.security.request.BasicCreate;

import java.util.ArrayList;
import java.util.List;

public class UiStyleCreate extends BasicCreate {

    @TypeRetention(UiStylePropertyCreate.class)
    private List<UiStylePropertyCreate> properties;

    public List<UiStylePropertyCreate> getProperties() {
        return properties;
    }

    public <T extends UiStyleCreate> T setProperties(List<UiStylePropertyCreate> properties) {
        this.properties = properties != null ? new ArrayList<>(properties) : null;
        return (T) this;
    }
}
