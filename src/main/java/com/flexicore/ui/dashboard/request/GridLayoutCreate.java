package com.flexicore.ui.dashboard.request;

import com.flexicore.request.BaseclassCreate;

public class GridLayoutCreate extends BaseclassCreate {

    private String contextString;

    public String getContextString() {
        return contextString;
    }

    public <T extends GridLayoutCreate> T setContextString(String contextString) {
        this.contextString = contextString;
        return (T) this;
    }
}
