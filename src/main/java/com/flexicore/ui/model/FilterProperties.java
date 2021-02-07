package com.flexicore.ui.model;

import com.flexicore.model.Baseclass;
import com.flexicore.security.SecurityContext;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;

@Entity
public class FilterProperties extends Baseclass {

    private String filterPath;
    private boolean externalize;
    @ManyToOne(targetEntity = Baseclass.class)
    private Baseclass relatedBaseclass;

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

    @ManyToOne(targetEntity = Baseclass.class)
    public Baseclass getRelatedBaseclass() {
        return relatedBaseclass;
    }

    public <T extends FilterProperties> T setRelatedBaseclass(Baseclass gridPreset) {
        this.relatedBaseclass = gridPreset;
        return (T) this;
    }

    public String getFilterPath() {
        return filterPath;
    }

    public <T extends FilterProperties> T setFilterPath(String filterPath) {
        this.filterPath = filterPath;
        return (T) this;
    }
}
