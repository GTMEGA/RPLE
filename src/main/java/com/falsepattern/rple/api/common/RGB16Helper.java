/*
 * Copyright (c) 2023 FalsePattern, Ven
 * This work is licensed under the Creative Commons Attribution-NonCommercial-NoDerivatives 4.0 International License.
 * To view a copy of this license, visit http://creativecommons.org/licenses/by-nc-nd/4.0/
 * or send a letter to Creative Commons, PO Box 1866, Mountain View, CA 94042, USA.
 *
 */

package com.falsepattern.rple.api.common;

import com.falsepattern.rple.api.common.color.RPLEColor;
import lombok.experimental.UtilityClass;

@UtilityClass
public final class RGB16Helper {
    public static final int CACHE_CHANNEL_BITMASK = 0xf;
    public static final int CACHE_ENTRY_RED_OFFSET = 0;
    public static final int CACHE_ENTRY_GREEN_OFFSET = 4;
    public static final int CACHE_ENTRY_BLUE_OFFSET = 8;

    public static short brightnessToCache(RPLEColor brightness) {
        int red = brightness.red();
        int green = brightness.green();
        int blue = brightness.blue();

        return packRGB(red, green, blue);
    }

    public static short translucencyToOpacityCache(RPLEColor translucency) {
        int red = RPLEColorUtil.invertColorComponent(translucency.red());
        int green = RPLEColorUtil.invertColorComponent(translucency.green());
        int blue = RPLEColorUtil.invertColorComponent(translucency.blue());

        return packRGB(red, green, blue);
    }

    public static short packRGB(int red, int green, int blue) {
        return (short) ((red & CACHE_CHANNEL_BITMASK) << CACHE_ENTRY_RED_OFFSET |
                        (green & CACHE_CHANNEL_BITMASK) << CACHE_ENTRY_GREEN_OFFSET |
                        (blue & CACHE_CHANNEL_BITMASK) << CACHE_ENTRY_BLUE_OFFSET);
    }

    public static int cacheToChannel(short cacheableS, int shift) {
        return (((int) cacheableS) >>> shift) & CACHE_CHANNEL_BITMASK;
    }
}
