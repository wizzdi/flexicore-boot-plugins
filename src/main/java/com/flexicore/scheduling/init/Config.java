package com.flexicore.scheduling.init;

import com.flexicore.annotations.InjectProperties;
import com.flexicore.annotations.plugins.PluginInfo;
import com.flexicore.interfaces.InitPlugin;

import javax.inject.Inject;
import java.util.Properties;
import java.util.concurrent.atomic.AtomicBoolean;

@PluginInfo(version = 1,autoInstansiate = true)
public class Config implements InitPlugin {
    public static int maxSchedulingThreads=5;
    private static AtomicBoolean init=new AtomicBoolean(false);

    @Inject
    @InjectProperties
    private Properties properties;

    @Override
    public void init() {
        if(init.compareAndSet(false,true)){
            maxSchedulingThreads=Integer.parseInt(properties.getProperty("maxSchedulingThreads",maxSchedulingThreads+""));
        }
    }


}
