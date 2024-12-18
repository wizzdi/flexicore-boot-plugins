package com.flexicore.category.request;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.flexicore.category.model.Category;
import com.flexicore.model.Baseclass;
import com.wizzdi.flexicore.security.request.BasicCreate;

public class CategoryToBaseclassCreate extends BasicCreate {

    private String categoryId;
    @JsonIgnore
    private Category category;
    private String baseclassId;
    private String baseclassType;

    private Boolean enabled;

    public String getCategoryId() {
        return categoryId;
    }

    public <T extends CategoryToBaseclassCreate> T setCategoryId(String categoryId) {
        this.categoryId = categoryId;
        return (T) this;
    }

    @JsonIgnore
    public Category getCategory() {
        return category;
    }

    public <T extends CategoryToBaseclassCreate> T setCategory(Category category) {
        this.category = category;
        return (T) this;
    }

    public String getBaseclassId() {
        return baseclassId;
    }

    public <T extends CategoryToBaseclassCreate> T setBaseclassId(String baseclassId) {
        this.baseclassId = baseclassId;
        return (T) this;
    }

    public String getBaseclassType() {
        return baseclassType;
    }

    public <T extends CategoryToBaseclassCreate> T setBaseclassType(String baseclassType) {
        this.baseclassType = baseclassType;
        return (T) this;
    }

    public Boolean getEnabled() {
        return enabled;
    }

    public <T extends CategoryToBaseclassCreate> T setEnabled(Boolean enabled) {
        this.enabled = enabled;
        return (T) this;
    }
}
