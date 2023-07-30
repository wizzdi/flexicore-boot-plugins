package com.flexicore.ui.model;

import com.flexicore.model.SecuredBasic;

import jakarta.persistence.Entity;
import jakarta.persistence.ManyToOne;

@Entity
public class FilterProperties extends SecuredBasic {

    private String filterPath;
    private boolean externalize;
    @ManyToOne(targetEntity = Preset.class)
    private Preset relatedBaseclass;

    public FilterProperties() {
    }


    public boolean isExternalize() {
        return externalize;
    }

    public <T extends FilterProperties> T setExternalize(boolean externalize) {
        this.externalize = externalize;
        return (T) this;
    }

    @ManyToOne(targetEntity = Preset.class)
    public Preset getRelatedBaseclass() {
        return relatedBaseclass;
    }

    public <T extends FilterProperties> T setRelatedBaseclass(Preset gridPreset) {
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
