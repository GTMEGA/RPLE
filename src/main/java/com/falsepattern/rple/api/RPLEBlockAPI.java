/*
 * Copyright (c) 2023 FalsePattern, Ven
 * This work is licensed under the Creative Commons Attribution-NonCommercial-NoDerivatives 4.0 International License.
 * To view a copy of this license, visit http://creativecommons.org/licenses/by-nc-nd/4.0/
 * or send a letter to Creative Commons, PO Box 1866, Mountain View, CA 94042, USA.
 */

package com.falsepattern.rple.api;

import com.falsepattern.rple.api.color.RPLEColor;
import lombok.experimental.UtilityClass;
import net.minecraft.block.Block;

@UtilityClass
public final class RPLEBlockAPI {
    public static void setBlockFallbackBrightness(RPLEColor color, String blockName) {
//        GameData.getBlockRegistry().getRaw()
    }

    public static void setBlockFallbackBrightness(RPLEColor color, Block block) {
    }

    public static void setBlockFallbackBrightnessWithMeta(RPLEColor color, String blockName, int blockMeta) {
    }

    public static void setBlockFallbackBrightnessWithMeta(RPLEColor color, Block block, int blockMeta) {
    }

    public static void setBlockFallbackTranslucency(RPLEColor color, String blockName) {
    }

    public static void setBlockFallbackTranslucency(RPLEColor color, Block block) {
    }

    public static void setBlockFallbackTranslucencyWithMeta(RPLEColor color, String blockName, int blockMeta) {
    }

    public static void setBlockFallbackTranslucencyWithMeta(RPLEColor color, Block block, int blockMeta) {
    }
}
