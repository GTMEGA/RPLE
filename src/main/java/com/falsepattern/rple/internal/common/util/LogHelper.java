/*
 * Right Proper Lighting Engine
 *
 * Copyright (C) 2023-2024 FalsePattern, Ven
 * All Rights Reserved
 *
 * The above copyright notice and this permission notice
 * shall be included in all copies or substantial portions of the Software.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program. If not, see <https://www.gnu.org/licenses/>.
 *
 * This program comes with additional permissions according to Section 7 of the
 * GNU Affero General Public License. See the full LICENSE file for details.
 */

package com.falsepattern.rple.internal.common.util;

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
