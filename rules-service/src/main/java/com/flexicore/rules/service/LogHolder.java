package com.flexicore.rules.service;

import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.classic.encoder.PatternLayoutEncoder;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.rolling.FixedWindowRollingPolicy;
import ch.qos.logback.core.rolling.RollingFileAppender;
import ch.qos.logback.core.rolling.SizeBasedTriggeringPolicy;
import ch.qos.logback.core.util.FileSize;
import org.slf4j.LoggerFactory;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;


public class LogHolder {

	private static final Map<String, Logger> loggers = new ConcurrentHashMap<>();
	private static final org.slf4j.Logger logger = LoggerFactory.getLogger(LogHolder.class);

	public static Logger getLogger(String id,String path) {
		return loggers.computeIfAbsent(id,
				f -> createLogger(id,path));
	}

	public static void clearLogger(String id,String path) {
	//do nothing

	}



	public static Logger createLogger(String id,String path) {
		LoggerContext context = (LoggerContext) LoggerFactory.getILoggerFactory();

		PatternLayoutEncoder encoder = new PatternLayoutEncoder();
		encoder.setContext(context);
		encoder.setPattern("[%d{yyyy-MM-dd HH:mm:ss}] [%-5level] - %msg%n");
		encoder.start();

		RollingFileAppender<ILoggingEvent> rollingFileAppender = new RollingFileAppender<>();
		rollingFileAppender.setContext(context);
		rollingFileAppender.setFile(path);

		FixedWindowRollingPolicy rollingPolicy = new FixedWindowRollingPolicy();
		rollingPolicy.setContext(context);
		rollingPolicy.setParent(rollingFileAppender);
		rollingPolicy.setFileNamePattern(path + ".%i.log.zip");
		rollingPolicy.setMinIndex(1);
		rollingPolicy.setMaxIndex(3);
		rollingPolicy.start();

		SizeBasedTriggeringPolicy<ILoggingEvent> triggeringPolicy = new SizeBasedTriggeringPolicy<>();
		triggeringPolicy.setMaxFileSize(FileSize.valueOf("500kb"));
		triggeringPolicy.start();

		rollingFileAppender.setEncoder(encoder);
		rollingFileAppender.setRollingPolicy(rollingPolicy);
		rollingFileAppender.setTriggeringPolicy(triggeringPolicy);
		rollingFileAppender.start();

		Logger logger = (Logger) LoggerFactory.getLogger(id);
		logger.addAppender(rollingFileAppender);
		logger.setAdditive(false);
		return logger;
	}


}
