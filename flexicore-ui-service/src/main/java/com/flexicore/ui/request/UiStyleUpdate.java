package com.flexicore.ui.request;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.flexicore.ui.model.UiStyle;

public class UiStyleUpdate extends UiStyleCreate {

    private String id;
    @JsonIgnore
    private UiStyle uiStyle;

    public String getId() {
        return id;
    }

    public <T extends UiStyleUpdate> T setId(String id) {
        this.id = id;
        return (T) this;
    }

    @JsonIgnore
    public UiStyle getUiStyle() {
        return uiStyle;
    }

    public <T extends UiStyleUpdate> T setUiStyle(UiStyle uiStyle) {
        this.uiStyle = uiStyle;
        return (T) this;
    }
}
