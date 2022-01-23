package com.flexicore.territories.config;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.wizzdi.flexicore.boot.base.interfaces.Plugin;
import org.pf4j.Extension;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.web.client.RestTemplate;

@Extension
@Configuration
@EnableTransactionManagement(proxyTargetClass = true)
public class TerritoriesConfig implements Plugin {

	@Bean
	public RestTemplate restTemplate(){
		return new RestTemplate();
	}

	@Primary
	@Bean
	public ObjectMapper objectMapper(){
		return new ObjectMapper()
				.registerModule(new JavaTimeModule())
				.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES,false);
	}

	@Bean
	public XmlMapper xmlMapper(){
		return new XmlMapper();
	}
}
