/*
 * Right Proper Lighting Engine
 *
 * Copyright (C) 2023-2025 FalsePattern, Ven
 * All Rights Reserved
 *
 * The above copyright notice and this permission notice
 * shall be included in all copies or substantial portions of the Software.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, only version 3 of the License.
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
        return new Class[]{General.class, Debug.class, Compat.class, HD.class};
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
        public static boolean CRAFTABLE_LAMPS;
    }

    @Config(modid = MOD_ID, category = "compat")
    public static final class Compat {
        static {poke();}
        @Config.Name("weakerProjectRedMixins")
        @Config.Comment({"Set this to false if running on modern java"})
        @Config.LangKey("config.rple.compat.weakerProjectRed")
        @Config.RequiresMcRestart
        @Config.DefaultBoolean(true)
        public static boolean WEAKER_PROJECTRED_MIXINS;
    }

    @Config(modid = MOD_ID, category = "debug")
    public static final class Debug {
        static {poke();}

        @Config.Name("rgbLightOverlay")
        @Config.Comment("Toggles rendering a light level debug overlay in the world.")
        @Config.LangKey("config.rple.debug.rgbLightOverlay")
        @Config.DefaultBoolean(false)
        public static Boolean RGB_LIGHT_OVERLAY;

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

    @Config(modid = MOD_ID, category = "hardcore_darkness")
    @Config.Synchronize
    public static final class HD {
        static {
            poke();
        }

        @Config.Name("mode")
        @Config.Comment({
                "Disabled: No hardcore darkness",
                "BlockOnly: No minimum block light, normal skylight",
                "DynamicMoonlight: No minimum block light, skylight is dependent on moon phase",
                "Both: No minimum sky & block light"
        })
        @Config.LangKey("config.rple.hd.mode")
        @Config.DefaultEnum("Disabled")
        public static Mode MODE;

        @Config.Name("darkNether")
        @Config.Comment({
                "Whether the Nether is also supposed to have its minimum light removed"
        })
        @Config.LangKey("config.rple.hd.darkNether")
        @Config.DefaultBoolean(true)
        public static boolean DARK_NETHER;

        @Config.Name("darkEnd")
        @Config.Comment({
                "Whether the End is also supposed to have its minimum light removed"
        })
        @Config.LangKey("config.rple.hd.darkEnd")
        @Config.DefaultBoolean(false)
        public static boolean DARK_END;

        @Config.Name("gammaOverride")
        @Config.Comment({
                "Setting this to something other than -1 will lock the gamma config settings to that value. (0.0 - 1.0)"
        })
        @Config.LangKey("config.rple.hd.gamma")
        @Config.RangeDouble(min = -1.0, max = 1.0)
        @Config.DefaultDouble(0)
        public static double GAMMA_OVERRIDE;

        @Config.Name("moonLightList")
        @Config.Comment({
                "In mode 2 this list defines how much skylight there is when 0%/25%/50%/75%/100% of the moon is visible. (Values go from 0 (Total Darkness) to 1 (Total Brightness))."
        })
        @Config.LangKey("config.rple.hd.moonLight")
        @Config.RangeDouble(min = 0, max = 1)
        @Config.ListFixedLength
        @Config.DefaultDoubleList({0, 0.075, 0.15, 0.225, 0.3})
        public static double[] MOON_LIGHT_LIST;

        @Config.Name("dimensionBlacklist")
        @Config.Comment({
                "A list of dimension ids in which Hardcore Darkness will be completely disabled."
        })
        @Config.DefaultIntList({})
        @Config.ListMaxLength(value = Integer.MAX_VALUE)
        public static int[] DIMENSION_BLACKLIST;

        public enum Mode {
            Disabled,
            BlockOnly,
            DynamicMoonlight,
            Both
        }
    }

    public enum LogMode {CUSTOM, ALL, NONE}
}
