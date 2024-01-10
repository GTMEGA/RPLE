/*
 * Copyright (c) 2023 FalsePattern, Ven
 * This work is licensed under the Creative Commons Attribution-NonCommercial-NoDerivatives 4.0 International License.
 * To view a copy of this license, visit http://creativecommons.org/licenses/by-nc-nd/4.0/
 * or send a letter to Creative Commons, PO Box 1866, Mountain View, CA 94042, USA.
 */

package com.falsepattern.rple.internal.common.config;

import com.falsepattern.lib.config.Config;
import com.falsepattern.lib.config.ConfigurationManager;

import static com.falsepattern.rple.internal.Tags.MOD_ID;

@Config(modid = MOD_ID)
public final class RPLEConfig {
    @Config.Comment("Set this to true if you want to enable the default lamps.\n" +
                    "(Based on ProjectRed's lamps, but much more optimized)")
    @Config.Name("craftableLamps")
    @Config.DefaultBoolean(true)
    public static boolean ENABLE_LAMPS;

    static {
        ConfigurationManager.selfInit();
    }
}
