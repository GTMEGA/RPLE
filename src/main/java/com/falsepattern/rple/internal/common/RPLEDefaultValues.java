/*
 * Copyright (c) 2023 FalsePattern, Ven
 * This work is licensed under the Creative Commons Attribution-NonCommercial-NoDerivatives 4.0 International License.
 * To view a copy of this license, visit http://creativecommons.org/licenses/by-nc-nd/4.0/
 * or send a letter to Creative Commons, PO Box 1866, Mountain View, CA 94042, USA.
 */

package com.falsepattern.rple.internal.common;

import com.falsepattern.rple.api.block.BlockColorRegistry;
import com.falsepattern.rple.api.lightmap.LightMapRegistry;
import com.falsepattern.rple.api.lightmap.vanilla.BossColorModifierMask;
import com.falsepattern.rple.api.lightmap.vanilla.NightVisionMask;
import com.falsepattern.rple.api.lightmap.vanilla.VanillaLightMapBase;
import com.falsepattern.rple.internal.config.container.BlockColorConfig;
import lombok.experimental.UtilityClass;

@UtilityClass
public final class RPLEDefaultValues {
    public static void registerDefaultLightMaps(LightMapRegistry registry) {
        registry.registerLightMapBase(new VanillaLightMapBase(), 1000);
        registry.registerLightMapMask(new NightVisionMask());
        registry.registerLightMapMask(new BossColorModifierMask());
    }

    public static void preloadDefaultColorPalette(BlockColorConfig config) {
        // Pre-load the DefaultColor enum
        // Pre-load the GreyscaleColor enum
    }

    public static void registerDefaultBlockColors(BlockColorRegistry registry) {

    }
}
