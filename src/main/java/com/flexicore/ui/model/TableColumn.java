package com.flexicore.ui.model;

import com.flexicore.security.SecurityContext;

import javax.persistence.Entity;


@Entity
public class TableColumn extends UiField {
    static TableColumn s_Singleton = new TableColumn();
    public static TableColumn s() { return s_Singleton; }

    public TableColumn() {
    }

    public TableColumn(String name, SecurityContext securityContext) {
        super(name, securityContext);
    }

    private boolean sortable;
    private boolean filterable;
    private double defaultColumnWidth;



    public boolean isSortable() {
        return sortable;
    }

    public TableColumn setSortable(boolean sortable) {
        this.sortable = sortable;
        return this;
    }

    public boolean isFilterable() {
        return filterable;
    }

    public TableColumn setFilterable(boolean filterable) {
        this.filterable = filterable;
        return this;
    }

    public double getDefaultColumnWidth() {
        return defaultColumnWidth;
    }

    public <T extends TableColumn> T setDefaultColumnWidth(double defaultColumnWidth) {
        this.defaultColumnWidth = defaultColumnWidth;
        return (T) this;
    }

    @Override
    public GridPreset getPreset() {
        return (GridPreset) super.getPreset();
    }

}
