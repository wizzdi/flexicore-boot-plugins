package com.flexicore.rules.service;

import java.io.File;
import java.io.FileOutputStream;
import java.nio.channels.FileChannel;
import java.util.Arrays;
import java.util.Date;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.*;

public class LogHolder {

	private static final Map<String, Logger> loggers = new ConcurrentHashMap<>();
	private static final Logger logger = Logger.getLogger(LogHolder.class
			.getCanonicalName());

	public static Logger getLogger(String id,String path) {
		return loggers.computeIfAbsent(id,
				f -> createLogger(id,path));
	}

	public static void clearLogger(String id,String path) {
		Logger scriptLogger = getLogger(id,path);
		synchronized (scriptLogger) {
			closeAndRemoveLogger(scriptLogger);

			if (path!= null) {
				File file = new File(path);
				try (FileChannel outChan = new FileOutputStream(file, true)
						.getChannel()) {
					outChan.truncate(0);
				} catch (Exception e) {
					logger.log(Level.SEVERE, "failed clearing log file", e);
				}
			}
		}

	}

	public static class CustomFormatter extends SimpleFormatter {
		private static final String format = "[%1$tF %1$tT] [%2$-7s] %3$s %n";

		@Override
		public synchronized String format(LogRecord logRecord) {
			return String.format(format, new Date(logRecord.getMillis()),
					logRecord.getLevel().getLocalizedName(),
					logRecord.getMessage());
		}
	}

	private static void closeAndRemoveLogger(Logger logger) {
		synchronized (logger) {
			for (Handler handler : Arrays.asList(logger.getHandlers())) {
				handler.flush();
				handler.close();
				logger.removeHandler(handler);
			}
		}
		loggers.remove(logger.getName(), logger);

	}

	public static void flush(Logger logger) {
		for (Handler handler : logger.getHandlers()) {
			handler.flush();
		}
	}

	private static Logger createLogger(String id,String path) {
		Logger scriptLogger = Logger.getLogger(id);
		try {
			if (path != null) {
				boolean hasFileHandler = false;
				for (Handler handler : scriptLogger.getHandlers()) {
					if (handler instanceof FileHandler) {
						hasFileHandler = true;
						break;
					}
				}
				if (!hasFileHandler) {
					FileHandler handler = new FileHandler(path, 0, 1, true);
					handler.setFormatter(new CustomFormatter());
					scriptLogger.addHandler(handler);
					scriptLogger.setUseParentHandlers(false);

				}
			}
		} catch (Exception e) {
			logger.log(Level.SEVERE, "failed getting script logger", e);
		}
		return scriptLogger;
	}
}
