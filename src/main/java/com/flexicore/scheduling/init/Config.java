package com.flexicore.scheduling.init;

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
	public static int maxSchedulingThreads = 5;
	private static AtomicBoolean init = new AtomicBoolean(false);

	@Autowired
	private Environment properties;

	@Override
	public void init() {
		if (init.compareAndSet(false, true)) {
			maxSchedulingThreads = Integer.parseInt(properties.getProperty(
					"maxSchedulingThreads", maxSchedulingThreads + ""));
		}
	}

}
