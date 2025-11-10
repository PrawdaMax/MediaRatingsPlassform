package org.example.pkgUI;

//Apache Commons CLI

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AppLogger {
    public static Logger getLogger(Class<?> clazz) {
        return (Logger) LoggerFactory.getLogger(clazz);
    }

    public static final Logger SYSTEM = (Logger) LoggerFactory.getLogger("SystemLogger");

    public static void info(Logger logger, String msg) {
        logger.info(msg);
    }

    public static void warn(Logger logger, String msg) {
        logger.warn(msg);
    }

    public static void error(Logger logger, String msg, Throwable t) {
        logger.error(msg, t);
    }

    public static void debug(Logger logger, String msg) {
        logger.debug(msg);
    }
}
