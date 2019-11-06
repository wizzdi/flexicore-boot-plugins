package com.flexicore.ui.model;

import com.flexicore.security.SecurityContext;

import javax.persistence.Entity;


@Entity
public class Column extends UiField {
    static Column s_Singleton = new Column();
    public static Column s() { return s_Singleton; }

    public Column() {
    }

    public Column(String name, SecurityContext securityContext) {
        super(name, securityContext);
    }

    private boolean sortable;
    private boolean filterable;
    private double defaultColumnWidth;



    public boolean isSortable() {
        return sortable;
    }

    public Column setSortable(boolean sortable) {
        this.sortable = sortable;
        return this;
    }

    public boolean isFilterable() {
        return filterable;
    }

    public Column setFilterable(boolean filterable) {
        this.filterable = filterable;
        return this;
    }

    public double getDefaultColumnWidth() {
        return defaultColumnWidth;
    }

    public <T extends Column> T setDefaultColumnWidth(double defaultColumnWidth) {
        this.defaultColumnWidth = defaultColumnWidth;
        return (T) this;
    }

    @Override
    public GridPreset getPreset() {
        return (GridPreset) super.getPreset();
    }

}
