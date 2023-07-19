/*
 * Copyright (c) 2023 FalsePattern, Ven
 * This work is licensed under the Creative Commons Attribution-NonCommercial-NoDerivatives 4.0 International License.
 * To view a copy of this license, visit http://creativecommons.org/licenses/by-nc-nd/4.0/
 * or send a letter to Creative Commons, PO Box 1866, Mountain View, CA 94042, USA.
 */

package com.falsepattern.rple.internal.common.helper;

import lombok.val;
import net.minecraft.block.Block;
import net.minecraft.world.IBlockAccess;

import static com.falsepattern.rple.api.RPLEBlockAPI.getColoredBrightnessSafe;
import static com.falsepattern.rple.api.color.ColorChannel.*;

/**
 * Shared logic used by multiple mixins, couldn't find a better place for these.
 */
public class BlockLightUtil {
    public static int getCompactRGBLightValue(IBlockAccess world,
                                              Block block,
                                              int blockMeta,
                                              int posX,
                                              int posY,
                                              int posZ) {
        val color = getColoredBrightnessSafe(world, block, blockMeta, posX, posY, posZ);

        // TODO: [PRE-RELEASE] 100% broken now.
        // This is here if we already generated a packed value somewhere deep inside another mod
        // Current usages:
        // AE2 CLApi
//        val potentialCookie = color.red();
//        if (CookieMonster.inspectValue(potentialCookie) == CookieMonster.IntType.COOKIE)
//            return potentialCookie;

        val red = RED_CHANNEL.componentFromColor(color);
        val green = GREEN_CHANNEL.componentFromColor(color);
        val blue = BLUE_CHANNEL.componentFromColor(color);

        return createCompactRGBLightValue(red, green, blue);
    }

    public static int createCompactRGBLightValue(int r, int g, int b) {
        val R = BrightnessUtil.lightLevelsToBrightnessForTessellator(r, 0);
        val G = BrightnessUtil.lightLevelsToBrightnessForTessellator(g, 0);
        val B = BrightnessUtil.lightLevelsToBrightnessForTessellator(b, 0);
        val packed = BrightnessUtil.brightnessesToPackedLong(R, G, B);
        return CookieMonster.packedLongToCookie(packed);
    }
}
