package com.flexicore.category.request;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.flexicore.category.model.CategoryToClazz;


public class CategoryToClazzUpdate extends CategoryToClazzCreate {

    private String id;
    @JsonIgnore
    private CategoryToClazz categoryToClazz;

    public String getId() {
        return id;
    }

    public <T extends CategoryToClazzUpdate> T setId(String id) {
        this.id = id;
        return (T) this;
    }

    @JsonIgnore
    public CategoryToClazz getCategoryToClazz() {
        return categoryToClazz;
    }

    public <T extends CategoryToClazzUpdate> T setCategoryToClazz(CategoryToClazz categoryToClazz) {
        this.categoryToClazz = categoryToClazz;
        return (T) this;
    }
}
