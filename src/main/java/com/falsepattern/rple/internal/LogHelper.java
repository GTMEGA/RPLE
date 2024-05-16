package com.falsepattern.rple.internal;

import com.falsepattern.rple.internal.common.config.RPLEConfig;
import lombok.experimental.UtilityClass;
import lombok.val;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import static com.falsepattern.rple.internal.common.config.RPLEConfig.Debug.DEBUG_FORCE_LOG_TO_CONSOLE;

@UtilityClass
public final class LogHelper {
    public static final Logger RPLE_LOG = LogManager.getLogger("RPLE");

    static {
        if (DEBUG_FORCE_LOG_TO_CONSOLE)
            tryForcingLoggingToConsole(RPLE_LOG);
    }

    public static Logger createLogger(String name) {
        val logger = LogManager.getLogger("RPLE|" + name);
        if (DEBUG_FORCE_LOG_TO_CONSOLE)
            tryForcingLoggingToConsole(logger);
        return logger;
    }

    public static boolean shouldLogDebug(boolean configuredOption) {
        switch (RPLEConfig.Debug.DEBUG_LOG_MODE) {
            case ALL:
                return true;
            case NONE:
                return false;
            default:
                return configuredOption;
        }
    }

    private static void tryForcingLoggingToConsole(Logger logger) {
        try {
            ((org.apache.logging.log4j.core.Logger) logger).setLevel(Level.ALL);
        } catch (Throwable t) {
            logger.warn("Failed to force [DEBUG] and [TRACE] logging to console, messages will still appear in: 'fml-client-*.log'", t);
        }
    }
}
