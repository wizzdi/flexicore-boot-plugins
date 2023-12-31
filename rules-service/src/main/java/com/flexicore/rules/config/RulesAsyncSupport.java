package com.flexicore.rules.config;

import com.wizzdi.flexicore.boot.base.interfaces.Plugin;
import org.pf4j.Extension;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;

@Configuration
@Extension
@EnableAsync(proxyTargetClass = true)
public class RulesAsyncSupport implements Plugin {

}