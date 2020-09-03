package com.flexicore.ui.dashboard.request;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.flexicore.request.BaseclassCreate;
import com.flexicore.ui.dashboard.model.CellContent;

public class CellContentElementCreate extends BaseclassCreate {

    private String contextString;
    private String cellContentId;
    @JsonIgnore
    private CellContent cellContent;

    public String getContextString() {
        return contextString;
    }

    public <T extends CellContentElementCreate> T setContextString(String contextString) {
        this.contextString = contextString;
        return (T) this;
    }

    public String getCellContentId() {
        return cellContentId;
    }

    public <T extends CellContentElementCreate> T setCellContentId(String cellContentId) {
        this.cellContentId = cellContentId;
        return (T) this;
    }

    @JsonIgnore
    public CellContent getCellContent() {
        return cellContent;
    }

    public <T extends CellContentElementCreate> T setCellContent(CellContent cellContent) {
        this.cellContent = cellContent;
        return (T) this;
    }
}
