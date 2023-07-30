package com.flexicore.category.config;


import com.wizzdi.flexicore.boot.base.interfaces.Plugin;
import org.pf4j.Extension;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.annotation.EnableTransactionManagement;


@Configuration
@Extension
@EnableTransactionManagement(proxyTargetClass = true)
public class CategoryServiceConfig implements Plugin {
}
