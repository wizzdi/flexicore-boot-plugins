package com.flexicore.territories.config;

import com.flexicore.annotations.InjectProperties;
import com.flexicore.annotations.plugins.PluginInfo;
import com.flexicore.interfaces.InitPlugin;

import javax.inject.Inject;
import java.util.Properties;
import java.util.concurrent.atomic.AtomicBoolean;

@PluginInfo(version = 1,autoInstansiate = true)
public class Config implements InitPlugin {

    @Inject
    @InjectProperties
    private Properties properties;

    private static String countriesImportUrl="https://pkgstore.datahub.io/core/country-list/data_json/data/8c458f2d15d9f2119654b29ede6e45b8/data_json.json";

    private static final AtomicBoolean init=new AtomicBoolean(false);
    @Override
    public void init() {
        if(init.compareAndSet(false,true)){
            countriesImportUrl=properties.getProperty("countriesImportUrl",countriesImportUrl);
        }
    }

    public static String getCountriesImportUrl() {
        return countriesImportUrl;
    }
}
