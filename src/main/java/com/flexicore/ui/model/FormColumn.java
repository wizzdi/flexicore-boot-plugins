package com.flexicore.ui.model;

import com.flexicore.security.SecurityContext;

import javax.persistence.Entity;


@Entity
public class FormColumn extends UiField {
    static FormColumn s_Singleton = new FormColumn();
    public static FormColumn s() { return s_Singleton; }

    public FormColumn() {
    }

    public FormColumn(String name, SecurityContext securityContext) {
        super(name, securityContext);
    }

    private boolean sortable;
    private boolean filterable;
    private double defaultColumnWidth;



    public boolean isSortable() {
        return sortable;
    }

    public FormColumn setSortable(boolean sortable) {
        this.sortable = sortable;
        return this;
    }

    public boolean isFilterable() {
        return filterable;
    }

    public FormColumn setFilterable(boolean filterable) {
        this.filterable = filterable;
        return this;
    }

    public double getDefaultColumnWidth() {
        return defaultColumnWidth;
    }

    public <T extends FormColumn> T setDefaultColumnWidth(double defaultColumnWidth) {
        this.defaultColumnWidth = defaultColumnWidth;
        return (T) this;
    }

    @Override
    public GridPreset getPreset() {
        return (GridPreset) super.getPreset();
    }

}
