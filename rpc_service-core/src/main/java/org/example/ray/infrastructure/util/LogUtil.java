package org.example.ray.infrastructure.util;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;


/**
 * @author zhoulei
 * @create 2023/5/18
 * @description:
 */
public class LogUtil {

    private static final Logger logger = LogManager.getLogger(LogUtil.class);

    public static void info(String message, Object... args) {
        logger.info(message, args);
    }

    public static void error(String message, Object... args) {
        logger.error(message, args);
    }
}
