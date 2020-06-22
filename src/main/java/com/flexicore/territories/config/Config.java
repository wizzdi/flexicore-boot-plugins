package com.flexicore.territories.config;

import com.flexicore.annotations.plugins.PluginInfo;
import com.flexicore.interfaces.InitPlugin;

import java.util.Properties;
import java.util.concurrent.atomic.AtomicBoolean;
import org.pf4j.Extension;
import org.springframework.stereotype.Component;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;

@PluginInfo(version = 1, autoInstansiate = true)
@Extension
@Component
public class Config implements InitPlugin {

	@Autowired
	private Environment properties;

	private static String countriesImportUrl = "https://pkgstore.datahub.io/core/country-list/data_json/data/8c458f2d15d9f2119654b29ede6e45b8/data_json.json";

	private static final AtomicBoolean init = new AtomicBoolean(false);
	@Override
	public void init() {
		if (init.compareAndSet(false, true)) {
			countriesImportUrl = properties.getProperty("countriesImportUrl",
					countriesImportUrl);
		}
	}

	public static String getCountriesImportUrl() {
		return countriesImportUrl;
	}
}
