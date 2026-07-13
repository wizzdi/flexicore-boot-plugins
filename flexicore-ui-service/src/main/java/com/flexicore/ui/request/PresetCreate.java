package com.flexicore.ui.request;

import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.flexicore.ui.model.UiStyle;
import com.wizzdi.flexicore.security.request.BasicCreate;

import java.util.HashMap;
import java.util.Map;

public class PresetCreate extends BasicCreate {

    private Map<String, Object> jsonNode=new HashMap<>();
    private String externalId;
    private String title;
    @JsonIgnore
    private boolean titleSet;
    private String uiStyleId;
    @JsonIgnore
    private boolean uiStyleIdSet;
    @JsonIgnore
    private UiStyle uiStyle;

    public String getTitle() {
        return title;
    }

    public <T extends PresetCreate> T setTitle(String title) {
        this.title = title;
        this.titleSet = true;
        return (T) this;
    }

    @JsonIgnore
    public boolean isTitleSet() {
        return titleSet;
    }

    public String getUiStyleId() {
        return uiStyleId;
    }

    public <T extends PresetCreate> T setUiStyleId(String uiStyleId) {
        this.uiStyleId = uiStyleId;
        this.uiStyleIdSet = true;
        return (T) this;
    }

    @JsonIgnore
    public boolean isUiStyleIdSet() {
        return uiStyleIdSet;
    }

    @JsonIgnore
    public UiStyle getUiStyle() {
        return uiStyle;
    }

    public <T extends PresetCreate> T setUiStyle(UiStyle uiStyle) {
        this.uiStyle = uiStyle;
        return (T) this;
    }

    public String getExternalId() {
        return externalId;
    }

    public <T extends PresetCreate> T setExternalId(String externalId) {
        this.externalId = externalId;
        return (T) this;
    }

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

    public <T extends PresetCreate> T setJsonNode(Map<String, Object> jsonNode) {
        this.jsonNode = jsonNode;
        return (T) this;
    }
}
