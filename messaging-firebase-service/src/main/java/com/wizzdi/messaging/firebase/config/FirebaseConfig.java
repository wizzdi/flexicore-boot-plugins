package com.wizzdi.messaging.firebase.config;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.messaging.FirebaseMessaging;
import com.wizzdi.flexicore.boot.base.interfaces.Plugin;
import org.pf4j.Extension;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import java.io.FileInputStream;
import java.io.IOException;

@Configuration
@Extension
@EnableTransactionManagement(proxyTargetClass = true)
public class FirebaseConfig implements Plugin {

	@Value("${wizzdi.messaging.connectors.firebase.credentials.path}")
	private String firebaseCredentials;
	@Value("${wizzdi.messaging.connectors.firebase.appName:my-app}")
	private String appName;


	@Bean
	@ConditionalOnMissingBean(GoogleCredentials.class)
	@Scope(value = ConfigurableBeanFactory.SCOPE_SINGLETON)
	public GoogleCredentials googleCredentials() throws IOException {
		return GoogleCredentials
				.fromStream(new FileInputStream(firebaseCredentials));
	}

	@Bean
	@ConditionalOnMissingBean(FirebaseOptions.class)
	@Scope(value = ConfigurableBeanFactory.SCOPE_SINGLETON)
	public FirebaseOptions firebaseOptions(GoogleCredentials googleCredentials) {
	return  FirebaseOptions
				.builder()
				.setCredentials(googleCredentials)
				.build();
	}

	@Bean
	@ConditionalOnMissingBean(FirebaseApp.class)
	@Scope(value = ConfigurableBeanFactory.SCOPE_SINGLETON)
	public FirebaseApp firebaseApp(FirebaseOptions firebaseOptions){
		return FirebaseApp.initializeApp(firebaseOptions, appName);
	}
	@Bean
	@ConditionalOnMissingBean(FirebaseMessaging.class)
	@Scope(value = ConfigurableBeanFactory.SCOPE_SINGLETON)
	public FirebaseMessaging firebaseMessaging(FirebaseApp firebaseApp) {
		return FirebaseMessaging.getInstance(firebaseApp);
	}
}
