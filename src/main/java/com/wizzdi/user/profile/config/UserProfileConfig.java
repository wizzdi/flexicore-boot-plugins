package com.wizzdi.user.profile.config;


import com.wizzdi.flexicore.boot.base.interfaces.Plugin;
import org.pf4j.Extension;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@Configuration
@Extension
@EnableTransactionManagement(proxyTargetClass = true)
public class UserProfileConfig implements Plugin {


}
