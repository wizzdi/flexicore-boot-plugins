package com.flexicore.category.request;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.flexicore.category.model.Category;
import com.flexicore.model.Baseclass;
import com.flexicore.model.Clazz;
import com.wizzdi.flexicore.security.request.BasicCreate;

public class CategoryToClazzCreate extends BasicCreate {

    private String categoryId;
    @JsonIgnore
    private Category category;
    private String clazzId;
    @JsonIgnore
    private Clazz clazz;
    private Boolean enabled;

    public String getCategoryId() {
        return categoryId;
    }

    public <T extends CategoryToClazzCreate> T setCategoryId(String categoryId) {
        this.categoryId = categoryId;
        return (T) this;
    }

    @JsonIgnore
    public Category getCategory() {
        return category;
    }

    public <T extends CategoryToClazzCreate> T setCategory(Category category) {
        this.category = category;
        return (T) this;
    }


    public String getClazzId() {
        return clazzId;
    }

    public <T extends CategoryToClazzCreate> T setClazzId(String clazzId) {
        this.clazzId = clazzId;
        return (T) this;
    }

    @JsonIgnore
    public Clazz getClazz() {
        return clazz;
    }

    public <T extends CategoryToClazzCreate> T setClazz(Clazz clazz) {
        this.clazz = clazz;
        return (T) this;
    }

    public Boolean getEnabled() {
        return enabled;
    }

    public <T extends CategoryToClazzCreate> T setEnabled(Boolean enabled) {
        this.enabled = enabled;
        return (T) this;
    }
}
