package com.flexicore.category.model;

import com.flexicore.interfaces.dynamic.FieldInfo;
import com.flexicore.model.BaseclassOnlyIdFiltering;
import com.flexicore.model.FilteringInformationHolder;

import javax.persistence.Entity;
import javax.persistence.OneToMany;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Entity
public class CategoryFilter extends FilteringInformationHolder {

    @OneToMany(targetEntity = CategoryNameFiltering.class, mappedBy = "filteringInformationHolder")
    @FieldInfo(displayName = "category names", description = "category names")
    private Set<CategoryNameFiltering> names=new HashSet<>();

    @OneToMany(targetEntity = CategoryNameFiltering.class, mappedBy = "filteringInformationHolder")
    @FieldInfo(displayName = "category names", description = "category names")
    public Set<CategoryNameFiltering> getNames() {
        return names;
    }

    public <T extends CategoryFilter> T setNames(Set<CategoryNameFiltering> names) {
        this.names = names;
        return (T) this;
    }
}
