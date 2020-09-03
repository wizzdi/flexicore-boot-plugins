package com.flexicore.ui.dashboard.request;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.flexicore.model.FilteringInformationHolder;
import com.flexicore.ui.dashboard.model.CellContent;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class CellContentElementFiltering extends FilteringInformationHolder {

    private Set<String> cellContentIds=new HashSet<>();
    @JsonIgnore
    private List<CellContent> cellContents;

    public Set<String> getCellContentIds() {
        return cellContentIds;
    }

    public <T extends CellContentElementFiltering> T setCellContentIds(Set<String> cellContentIds) {
        this.cellContentIds = cellContentIds;
        return (T) this;
    }

    @JsonIgnore
    public List<CellContent> getCellContents() {
        return cellContents;
    }

    public <T extends CellContentElementFiltering> T setCellContents(List<CellContent> cellContents) {
        this.cellContents = cellContents;
        return (T) this;
    }
}
