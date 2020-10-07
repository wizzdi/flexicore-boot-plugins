package com.flexicore.category.invokers;

import com.flexicore.annotations.plugins.PluginInfo;
import com.flexicore.category.model.CategoryFilter;
import com.flexicore.category.service.CategoryService;
import com.flexicore.data.jsoncontainers.PaginationResponse;
import com.flexicore.interfaces.dynamic.InvokerInfo;
import com.flexicore.interfaces.dynamic.InvokerMethodInfo;
import com.flexicore.interfaces.dynamic.ListingInvoker;
import com.flexicore.category.model.Category;
import com.flexicore.security.SecurityContext;
import org.pf4j.Extension;
import org.springframework.beans.factory.annotation.Autowired;

@PluginInfo(version = 1)
@InvokerInfo(displayName = "Category Invoker",description = "Lists Categories")
@Extension
public class CategoriesInvoker implements ListingInvoker<Category, CategoryFilter> {

    @Autowired
    @PluginInfo(version = 1)
    private CategoryService categoryService;

    @Override
    @InvokerMethodInfo(displayName = "listAllCategories",description = "lists all categories")
    public PaginationResponse<Category> listAll(CategoryFilter filter, SecurityContext securityContext) {
        return categoryService.getAllCategories(filter,securityContext);
    }

    @Override
    public Class<CategoryFilter> getFilterClass() {
        return CategoryFilter.class;
    }

    @Override
    public Class<?> getHandlingClass() {
        return Category.class;
    }
}
