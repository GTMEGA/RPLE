/*
 * Copyright (c) 2023 FalsePattern
 * This work is licensed under the Creative Commons Attribution-NonCommercial-NoDerivatives 4.0 International License.
 * To view a copy of this license, visit http://creativecommons.org/licenses/by-nc-nd/4.0/
 * or send a letter to Creative Commons, PO Box 1866, Mountain View, CA 94042, USA.
 *
 */

package com.falsepattern.rple.internal.color;

import com.falsepattern.rple.api.ColoredBlock;
import com.falsepattern.rple.api.LightConstants;
import com.falsepattern.rple.internal.storage.ColoredCarrierWorld;
import lombok.val;

import net.minecraft.world.ChunkCache;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

/**
 * Shared logic used by multiple mixins, couldn't find a better place for these.
 */
public class BlockLightUtil {
    /**
     * The method that makes all of this work. Replaces the regular brightness logic with our custom packed/cookie system.
     */
    public static int getRGBBrightnessAt(IBlockAccess access, int x, int y, int z, int minBlockLight) {
        //Unpack the minimum brightness
        int minRed, minGreen, minBlue;
        val packed = CookieMonster.cookieToPackedLong(minBlockLight);
        minRed = BrightnessUtil.getBlocklightFromBrightness(BrightnessUtil.getBrightnessRed(packed));
        minGreen = BrightnessUtil.getBlocklightFromBrightness(BrightnessUtil.getBrightnessGreen(packed));
        minBlue = BrightnessUtil.getBlocklightFromBrightness(BrightnessUtil.getBrightnessBlue(packed));

        //Grab the colored world accessor
        val carrier = access instanceof World ? ((ColoredCarrierWorld) access) : (ColoredCarrierWorld) (((ChunkCache) access).worldObj);

        //Grab the colored channels
        val red = carrier.getColoredWorld(LightConstants.COLOR_CHANNEL_RED).getLightBrightnessForSkyBlocksWorld(access, x, y, z, minRed);
        val green = carrier.getColoredWorld(LightConstants.COLOR_CHANNEL_GREEN).getLightBrightnessForSkyBlocksWorld(access, x, y, z, minGreen);
        val blue = carrier.getColoredWorld(LightConstants.COLOR_CHANNEL_BLUE).getLightBrightnessForSkyBlocksWorld(access, x, y, z, minBlue);

        //Pack it
        return CookieMonster.packedLongToCookie(BrightnessUtil.brightnessesToPackedLong(red, green, blue));
    }

    public static int getCompactRGBLightValue(IBlockAccess world, ColoredBlock block, int meta, int x, int y, int z) {
        val red = block.getColoredLightValue(world, meta, LightConstants.COLOR_CHANNEL_RED, x, y, z);
        //This is here if we already generated a packed value somewhere deep inside another mod
        //Current usages:
        //AE2 CLApi
        if (CookieMonster.inspectValue(red) == CookieMonster.IntType.COOKIE) {
            return red;
        }
        val green = block.getColoredLightValue(world, meta, LightConstants.COLOR_CHANNEL_GREEN, x, y, z);
        val blue = block.getColoredLightValue(world, meta, LightConstants.COLOR_CHANNEL_BLUE, x, y, z);
        return createCompactRGBLightValue(red, green, blue);
    }

    public static int createCompactRGBLightValue(int r, int g, int b) {
        val R = BrightnessUtil.lightLevelsToBrightness(r, 0);
        val G = BrightnessUtil.lightLevelsToBrightness(g, 0);
        val B = BrightnessUtil.lightLevelsToBrightness(b, 0);
        val packed = BrightnessUtil.brightnessesToPackedLong(R, G, B);
        return CookieMonster.packedLongToCookie(packed);
    }
}
