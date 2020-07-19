package com.flexicore.ui.model;

import com.flexicore.model.Baseclass;
import com.flexicore.security.SecurityContext;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;

@Entity
public class FilterProperties extends Baseclass {

    private boolean externalize;
    @ManyToOne(targetEntity = GridPreset.class)
    private GridPreset gridPreset;

    public FilterProperties() {
    }

    public FilterProperties(String name, SecurityContext securityContext) {
        super(name, securityContext);
    }

    public boolean isExternalize() {
        return externalize;
    }

    public <T extends FilterProperties> T setExternalize(boolean externalize) {
        this.externalize = externalize;
        return (T) this;
    }

    @ManyToOne(targetEntity = GridPreset.class)
    public GridPreset getGridPreset() {
        return gridPreset;
    }

    public <T extends FilterProperties> T setGridPreset(GridPreset gridPreset) {
        this.gridPreset = gridPreset;
        return (T) this;
    }
}
