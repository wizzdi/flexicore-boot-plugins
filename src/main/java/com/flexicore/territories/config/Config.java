package com.flexicore.territories.config;

import com.flexicore.annotations.plugins.PluginInfo;
import com.flexicore.events.PluginsLoadedEvent;
import com.flexicore.interfaces.InitPlugin;

import java.util.Properties;
import java.util.concurrent.atomic.AtomicBoolean;

import com.flexicore.interfaces.ServicePlugin;
import com.flexicore.model.territories.*;
import com.flexicore.service.BaseclassService;
import com.flexicore.territories.request.*;
import org.pf4j.Extension;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;

@PluginInfo(version = 1)
@Extension
@Component
public class Config implements ServicePlugin {

	@Autowired
	private Environment properties;

	private static String countriesImportUrl = "https://pkgstore.datahub.io/core/country-list/data_json/data/8c458f2d15d9f2119654b29ede6e45b8/data_json.json";

	private static final AtomicBoolean init = new AtomicBoolean(false);
	@EventListener
	public void init(PluginsLoadedEvent pluginsLoadedEvent) {
		if (init.compareAndSet(false, true)) {
			countriesImportUrl = properties.getProperty("countriesImportUrl", countriesImportUrl);
			BaseclassService.registerFilterClass(NeighbourhoodFiltering.class, Neighbourhood.class);
			BaseclassService.registerFilterClass(StateFiltering.class, State.class);
			BaseclassService.registerFilterClass(StreetFiltering.class, Street.class);
			BaseclassService.registerFilterClass(CityFiltering.class, City.class);
			BaseclassService.registerFilterClass(CountryFiltering.class, Country.class);
			BaseclassService.registerFilterClass(AddressFiltering.class, Address.class);


		}
	}

	public static String getCountriesImportUrl() {
		return countriesImportUrl;
	}
}
