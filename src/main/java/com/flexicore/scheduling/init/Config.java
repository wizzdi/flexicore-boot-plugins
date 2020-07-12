package com.flexicore.scheduling.init;

import com.flexicore.annotations.plugins.PluginInfo;

import java.util.Properties;
import java.util.concurrent.atomic.AtomicBoolean;

import com.flexicore.events.PluginsLoadedEvent;
import com.flexicore.interfaces.ServicePlugin;
import org.pf4j.Extension;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;

@PluginInfo(version = 1)
@Extension
@Component
public class Config implements ServicePlugin {
	public static int maxSchedulingThreads = 5;
	private static AtomicBoolean init = new AtomicBoolean(false);

	@Autowired
	private Environment properties;

	@EventListener
	public void init(PluginsLoadedEvent e) {
		if (init.compareAndSet(false, true)) {
			maxSchedulingThreads = Integer.parseInt(properties.getProperty(
					"maxSchedulingThreads", maxSchedulingThreads + ""));
		}
	}

}
