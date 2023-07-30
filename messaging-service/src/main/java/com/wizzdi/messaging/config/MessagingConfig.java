package com.wizzdi.messaging.config;


import com.wizzdi.flexicore.boot.base.interfaces.Plugin;
import org.pf4j.Extension;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@Component
@Extension
@EnableTransactionManagement(proxyTargetClass = true)
public class MessagingConfig implements Plugin {


}
