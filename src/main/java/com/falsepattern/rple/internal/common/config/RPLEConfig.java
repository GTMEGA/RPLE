/*
 * Copyright (c) 2023 FalsePattern, Ven
 * This work is licensed under the Creative Commons Attribution-NonCommercial-NoDerivatives 4.0 International License.
 * To view a copy of this license, visit http://creativecommons.org/licenses/by-nc-nd/4.0/
 * or send a letter to Creative Commons, PO Box 1866, Mountain View, CA 94042, USA.
 */

package com.falsepattern.rple.internal.common.config;

import com.falsepattern.lib.config.Config;
import com.falsepattern.lib.config.ConfigurationManager;
import lombok.SneakyThrows;

import static com.falsepattern.rple.internal.Tags.MOD_ID;

public final class RPLEConfig {
    // region Init
    public static void poke() {}

    static {
        init();
    }

    public static Class<?>[] configClasses() {
        return new Class[]{General.class, Debug.class};
    }

    @SneakyThrows
    private static void init() {
        ConfigurationManager.initialize((a, b) -> {}, configClasses());
    }
    // endregion

    @Config(modid = MOD_ID)
    public static final class General {
        static {poke();}

        @Config.Name("craftableLamps")
        @Config.Comment({"Set this to true if you want to enable the default lamps.",
                         "(Based on ProjectRed's lamps, but much more optimized)"})
        @Config.LangKey("config.rple.general.craftableLamps")
        @Config.RequiresMcRestart
        @Config.DefaultBoolean(true)
        public static boolean ENABLE_LAMPS;
    }

    @Config(modid = MOD_ID, category = "debug")
    public static final class Debug {
        static {poke();}

        @Config.Name("logMode")
        @Config.Comment({"Determines how the debug logging configs a treated.",
                         "[CUSTOM] User defined configs are respected",
                         "[ALL] Forcibly enables all debug logging. (Most Verbose)",
                         "[NONE] Fully disables debug logging. (Best Performance)"})
        @Config.LangKey("config.rple.debug.logMode")
        @Config.DefaultEnum("CUSTOM")
        public static LogMode DEBUG_LOG_MODE;

        @Config.Name("forceLogToConsole")
        @Config.Comment({"Forces logs at levels [DEBUG] and [TRACE] to appear in the console.",
                         "Usually, these will only appear in your 'fml-client-*.log' file."})
        @Config.LangKey("config.rple.debug.forceLogToConsole")
        @Config.RequiresMcRestart
        @Config.DefaultBoolean(false)
        public static boolean DEBUG_FORCE_LOG_TO_CONSOLE;

        @Config.Name("asmTransformer")
        @Config.Comment("Toggles ASM transformation logging.")
        @Config.LangKey("config.rple.debug.asmTransformer")
        @Config.RequiresMcRestart
        @Config.DefaultBoolean(false)
        public static boolean DEBUG_ASM_TRANSFORMER;

        @Config.Name("cookieMonster")
        @Config.Comment("Toggles the logging of the Cookie Monster class, which handles the 'packing' of brightness values for rendering.")
        @Config.LangKey("config.rple.debug.cookieMonster")
        @Config.DefaultBoolean(false)
        public static boolean DEBUG_COOKIE_MONSTER;

        @Config.Name("classBlockList")
        @Config.Comment({"Toggles the logging of classes being blocked or permitted to handle cookie brightness.",
                         "This relates to the colorization of entities and particles."})
        @Config.LangKey("config.rple.debug.classBlockList")
        @Config.DefaultBoolean(false)
        public static boolean DEBUG_CLASS_BLOCK_LIST;
    }

    public enum LogMode {CUSTOM, ALL, NONE}
}
