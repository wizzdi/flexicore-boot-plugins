package com.flexicore.license.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@Configuration
@EnableAsync(proxyTargetClass = true)
@EnableTransactionManagement(proxyTargetClass = true)
public class LicenseConfig {
}
