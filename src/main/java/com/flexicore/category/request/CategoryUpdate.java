package com.flexicore.category.request;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.flexicore.category.model.Category;

public class CategoryUpdate extends CategoryCreate {
    private String id;
    @JsonIgnore
    private Category category;

    public String getId() {
        return id;
    }

    public <T extends CategoryUpdate> T setId(String id) {
        this.id = id;
        return (T) this;
    }

    @JsonIgnore
    public Category getCategory() {
        return category;
    }

    public <T extends CategoryUpdate> T setCategory(Category category) {
        this.category = category;
        return (T) this;
    }
}
