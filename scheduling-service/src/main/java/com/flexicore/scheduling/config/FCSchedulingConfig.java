package com.flexicore.scheduling.config;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.wizzdi.flexicore.boot.base.interfaces.Plugin;
import org.pf4j.Extension;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Configuration
@Extension
public class FCSchedulingConfig implements Plugin {
    @Value("${flexicore.scheduling.max.threads:5}")
    private int maxSchedulingThreads;

    @Bean
    @Qualifier("FCSchedulingExecutor")
    public ExecutorService FCSchedulingExecutor(){
       return Executors
                .newFixedThreadPool(maxSchedulingThreads);
    }
    @Bean
    @Primary
    public ObjectMapper restObjectMapper() {
        return new ObjectMapper()
                .disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES)
                .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS)
                .disable(DeserializationFeature.ADJUST_DATES_TO_CONTEXT_TIME_ZONE)
                .registerModule(new JavaTimeModule());

    }

    @Bean
    @Primary
    public MappingJackson2HttpMessageConverter jsonConverter(ObjectMapper restObjectMapper) {
        MappingJackson2HttpMessageConverter jsonConverter = new MappingJackson2HttpMessageConverter();
        jsonConverter.setObjectMapper(restObjectMapper);
        return jsonConverter;
    }

}
