package com.wizzdi.maps.service.email.config;

import com.sendgrid.SendGrid;
import com.wizzdi.flexicore.boot.base.interfaces.Plugin;
import org.pf4j.Extension;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;

@Configuration
@Extension
public class MappedPOISendGridConfig implements Plugin {

    @Value("${wizzdi.flexicore.sendgrid.mappedPoi.apiKey:#{null}}")
    private String sendGridApiKey;

    @Bean
    @Qualifier("mappedPOISendGrid")
    @Lazy
    public SendGrid sendGrid(){
        return new SendGrid(sendGridApiKey);
    }

}
