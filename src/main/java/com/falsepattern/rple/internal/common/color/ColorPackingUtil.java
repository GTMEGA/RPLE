/*
 * Copyright (c) 2023 FalsePattern, Ven
 * This work is licensed under the Creative Commons Attribution-NonCommercial-NoDerivatives 4.0 International License.
 * To view a copy of this license, visit http://creativecommons.org/licenses/by-nc-nd/4.0/
 * or send a letter to Creative Commons, PO Box 1866, Mountain View, CA 94042, USA.
 *
 */

package com.falsepattern.rple.internal.common.color;

import com.falsepattern.lumina.api.lighting.LightType;
import com.falsepattern.rple.api.common.RPLEColorUtil;
import com.falsepattern.rple.api.common.color.RPLEColor;
import lombok.val;
import lombok.var;

public class ColorPackingUtil {
    public static final int CACHE_CHANNEL_BITMASK = 0xf;
    public static final int CACHE_ENTRY_RED_OFFSET = 0;
    public static final int CACHE_ENTRY_GREEN_OFFSET = 4;
    public static final int CACHE_ENTRY_BLUE_OFFSET = 8;

    // TODO: Validate new bit-wise logic
    public static int packedLightValueRGBMax(int lightValueA,
                                             int lightValueB,
                                             int lightValueC,
                                             int lightValueD,
                                             int lightValueE,
                                             int lightValueF) {
        var maxLightValue = 0x00000000;
        for (var shift = 0; shift <= 20; shift += 4) {
            val a = (lightValueA >>> shift) & 0xF;
            val b = (lightValueB >>> shift) & 0xF;
            val c = (lightValueC >>> shift) & 0xF;
            val d = (lightValueD >>> shift) & 0xF;
            val e = (lightValueE >>> shift) & 0xF;
            val f = (lightValueF >>> shift) & 0xF;

            var max = 0;
            //noinspection DataFlowIssue
            max = Math.max(max, a);
            max = Math.max(max, b);
            max = Math.max(max, c);
            max = Math.max(max, d);
            max = Math.max(max, e);
            max = Math.max(max, f);

            maxLightValue |= max << shift;
        }
        return maxLightValue;
    }

    // TODO: Validate new bit-wise logic
    public static int packLightValueRGB(int blkR, int blkG, int blkB, int skyR, int skyG, int skyB) {
        blkR &= 0xF;
        blkG &= 0xF;
        blkB &= 0xF;
        skyR &= 0xF;
        skyG &= 0xF;
        skyB &= 0xF;

        blkR <<= 0;
        blkG <<= 4;
        blkB <<= 8;
        skyR <<= 12;
        skyG <<= 16;
        skyB <<= 20;

        return blkR | blkG | blkB | skyR | skyG | skyB;
    }

    // TODO: Validate new bit-wise logic
    public static int packLightValueRGB(LightType type, int r, int g, int b) {
        r &= 0xF;
        g &= 0xF;
        b &= 0xF;

        if (type.isBlock()) {
            r <<= 0;
            g <<= 4;
            b <<= 8;
        } else {
            r <<= 12;
            g <<= 16;
            b <<= 20;
        }

        return r | g | b;
    }

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
