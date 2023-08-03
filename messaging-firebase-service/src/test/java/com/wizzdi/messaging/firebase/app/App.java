package com.wizzdi.messaging.firebase.app;

import com.flexicore.model.Baseclass;
import com.flexicore.model.Basic;
import com.flexicore.model.security.SecurityPolicy;
import com.google.auth.oauth2.GoogleCredentials;
import com.wizzdi.dynamic.properties.converter.EnableDynamicProperties;
import com.wizzdi.dynamic.properties.converter.JsonConverter;
import com.wizzdi.dynamic.properties.converter.postgresql.PostgresqlJsonConverter;
import com.wizzdi.flexicore.boot.base.annotations.plugins.EnableFlexiCorePlugins;
import com.wizzdi.flexicore.boot.jpa.annotations.EnableFlexiCoreJPAPlugins;
import com.wizzdi.flexicore.boot.jpa.service.EntitiesHolder;
import com.wizzdi.flexicore.boot.rest.annotations.EnableFlexiCoreRESTPlugins;
import com.wizzdi.flexicore.security.annotations.EnableFlexiCoreSecurity;
import com.wizzdi.messaging.connectors.firebase.model.FirebaseEnabledDevice;
import com.wizzdi.messaging.model.Chat;
import com.wizzdi.messaging.model.Message;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.ApplicationPidFileWriter;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Bean;

import java.util.Arrays;
import java.util.HashSet;

@EnableFlexiCorePlugins
@EnableFlexiCoreJPAPlugins
@EnableFlexiCoreSecurity
@EnableFlexiCoreRESTPlugins
@EnableDynamicProperties
@SpringBootApplication(scanBasePackages = {"com.wizzdi.messaging"})
public class App {


	private static final Logger logger= LoggerFactory.getLogger(App.class);
	public static void main(String[] args) {

		SpringApplication app = new SpringApplication(App.class);
		app.addListeners(new ApplicationPidFileWriter());
		ConfigurableApplicationContext context=app.run(args);

	}

@Bean
public GoogleCredentials googleCredentials() throws Exception {
		return GoogleCredentials.newBuilder().build();
}
	@Bean
	public EntitiesHolder entitiesHolder(PostgresqlJsonConverter postgresqlJsonConverter){
		return new EntitiesHolder(new HashSet<>(Arrays.asList(Baseclass.class, Basic.class, SecurityPolicy.class, Chat.class, Message.class, JsonConverter.class, FirebaseEnabledDevice.class)));
	}


	@Bean
	public CommandLineRunner commandLineRunner(ApplicationContext ctx) {
		return args -> {

			System.out.println("Let's inspect the beans provided by Spring Boot:");

			String[] beanNames = ctx.getBeanDefinitionNames();
			Arrays.sort(beanNames);
			for (String beanName : beanNames) {
				System.out.println(beanName);
			}
			System.out.println("total of "+beanNames.length +" beans");



		};
	}
}
