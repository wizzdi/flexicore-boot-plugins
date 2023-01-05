package com.wizzdi.maps.service.reverse.geocode.config;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.wizzdi.flexicore.boot.base.interfaces.Plugin;
import org.pf4j.Extension;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.DefaultUriBuilderFactory;

@Configuration
@Extension
public class NominatimConfig implements Plugin {
    @Value("${wizzdi.maps.reverse.geocode.nominatim:https://nominatim.openstreetmap.org/}")
    private String baseAddress;

    @Bean
    @Qualifier("nominatimRestTemplate")
    public RestTemplate nominatimRestTemplate(){

        ObjectMapper objectMapper = new ObjectMapper().registerModule(new JavaTimeModule()).disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
        MappingJackson2HttpMessageConverter converter = new MappingJackson2HttpMessageConverter();
        converter.setObjectMapper(objectMapper);
        RestTemplate restTemplate = new RestTemplate();
        restTemplate.getMessageConverters().add(0, converter);

        restTemplate.setUriTemplateHandler(new DefaultUriBuilderFactory(baseAddress));


        return restTemplate;

    }
}
