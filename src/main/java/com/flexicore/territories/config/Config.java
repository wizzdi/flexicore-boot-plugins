package com.flexicore.territories.config;

import com.wizzdi.flexicore.boot.base.interfaces.Plugin;
import org.pf4j.Extension;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.web.client.RestTemplate;

@Extension
@Configuration
@EnableTransactionManagement
public class Config implements Plugin {

	@Bean
	public RestTemplate restTemplate(){
		return new RestTemplate();
	}
}
