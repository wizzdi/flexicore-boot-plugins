package com.wizzdi.maps.service.config;

import com.wizzdi.flexicore.boot.base.interfaces.Plugin;
import org.pf4j.Extension;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@Configuration
@EnableAsync(proxyTargetClass = true)
@Extension
@EnableTransactionManagement(proxyTargetClass = true)
public class MapConfig implements Plugin {
}
