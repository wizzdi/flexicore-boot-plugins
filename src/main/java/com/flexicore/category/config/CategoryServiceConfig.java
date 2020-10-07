package com.flexicore.category.config;

import com.flexicore.annotations.plugins.PluginInfo;
import com.flexicore.category.model.Category;
import com.flexicore.category.model.CategoryFilter;
import com.flexicore.events.PluginsLoadedEvent;
import com.flexicore.interfaces.ServicePlugin;
import com.flexicore.service.BaseclassService;
import org.springframework.context.event.EventListener;

@PluginInfo(version = 1)
public class CategoryServiceConfig implements ServicePlugin {

    @EventListener
    public void init(PluginsLoadedEvent pluginsLoadedEvent){
        BaseclassService.registerFilterClass(CategoryFilter.class, Category.class);

    }
}
