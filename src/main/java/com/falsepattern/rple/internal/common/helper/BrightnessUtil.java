/*
 * Copyright (c) 2023 FalsePattern, Ven
 * This work is licensed under the Creative Commons Attribution-NonCommercial-NoDerivatives 4.0 International License.
 * To view a copy of this license, visit http://creativecommons.org/licenses/by-nc-nd/4.0/
 * or send a letter to Creative Commons, PO Box 1866, Mountain View, CA 94042, USA.
 */

package com.falsepattern.rple.internal.common.helper;

import lombok.val;

/**
 * Utility class for managing minecraft brightness values, and packed long RGB versions of these brightness values.
 */
public class BrightnessUtil {
    private static final int BLOCK_LIGHT_MASK = 0x000000FF;
    private static final int SKYLIGHT_MASK = 0x00FF0000;

    private static final int BLOCK_LIGHT_BRIGHTNESS_OFFSET = 4;
    private static final int SKY_LIGHT_BRIGHTNESS_OFFSET = 20;

    private static final int BLOCKLIGHT_BRIGHTNESS_OFFSET_RENDER = 0;
    private static final int SKYLIGHT_BRIGHTNESS_OFFSET_RENDER = 16;

    private static final int COMPRESSED_BLOCK_LIGHT_MASK = 0x000000FF;
    private static final int COMPRESSED_SKY_LIGHT_MASK = 0x0000FF00;
    private static final int COMPRESSED_BRIGHTNESS_MASK = COMPRESSED_BLOCK_LIGHT_MASK | COMPRESSED_SKY_LIGHT_MASK;

    // Long format (hex):
    // 0000 RRrr GGgg BBbb
    private static final int PACKED_RED_OFFSET = 32;
    private static final int PACKED_GREEN_OFFSET = 16;
    private static final int PACKED_BLUE_OFFSET = 0;

    /**
     * Packs two 0-15 values into a vanilla-style brightness value.
     */
    public static int lightLevelsToBrightnessForTessellator(int blockLightValue, int skyLightValue) {
        return (blockLightValue & 0xF) << BLOCK_LIGHT_BRIGHTNESS_OFFSET |
               (skyLightValue & 0xF) << SKY_LIGHT_BRIGHTNESS_OFFSET;
    }

    /**
     * The 0-15 light level inside the packed brightness.
     */
    public static int getBlockLightFromBrightness(int brightness) {
        return (brightness & BLOCK_LIGHT_MASK) >>> BLOCK_LIGHT_BRIGHTNESS_OFFSET;
    }

    /**
     * The 0-15 light level inside the packed brightness.
     */
    public static int getSkylightFromBrightness(int brightness) {
        return (brightness & SKYLIGHT_MASK) >>> SKY_LIGHT_BRIGHTNESS_OFFSET;
    }

    /**
     * Packs two 0-240 values into a vanilla-style brightness value. Only relevant when messing with render logic.
     */
    public static int channelsToBrightnessRender(int block, int sky) {
        return (sky & 0xFF) << SKYLIGHT_BRIGHTNESS_OFFSET_RENDER | (block & 0xFF) << BLOCKLIGHT_BRIGHTNESS_OFFSET_RENDER;
    }

    /**
     * The raw render-specific brightness value inside the packed brightness.
     */
    public static int getBlockLightChannelFromBrightnessRender(int brightness) {
        return (brightness & BLOCK_LIGHT_MASK) >>> BLOCKLIGHT_BRIGHTNESS_OFFSET_RENDER;
    }

    /**
     * The raw render-specific brightness value inside the packed brightness.
     */
    public static int getSkyLightChannelFromBrightnessRender(int brightness) {
        return (brightness & SKYLIGHT_MASK) >>> SKYLIGHT_BRIGHTNESS_OFFSET_RENDER;
    }

    public static long packedBrightnessFromTessellatorBrightnessChannels(int red, int green, int blue) {
        return (long) packTessellatorBrightness(red) << PACKED_RED_OFFSET |
               (long) packTessellatorBrightness(green) << PACKED_GREEN_OFFSET |
               (long) packTessellatorBrightness(blue) << PACKED_BLUE_OFFSET;
    }

    public static long monochromeBrightnessToPackedLong(int brightness) {
        val compressed = (long) packTessellatorBrightness(brightness);
        return compressed << PACKED_RED_OFFSET |
               compressed << PACKED_GREEN_OFFSET |
               compressed << PACKED_BLUE_OFFSET;
    }

    public static int getBrightnessRed(long packed) {
        return unpackTessellatorBrightness((int) ((packed >>> PACKED_RED_OFFSET) & COMPRESSED_BRIGHTNESS_MASK));
    }

    public static int getBrightnessGreen(long packed) {
        return unpackTessellatorBrightness((int) ((packed >>> PACKED_GREEN_OFFSET) & COMPRESSED_BRIGHTNESS_MASK));
    }

    public static int getBrightnessBlue(long packed) {
        return unpackTessellatorBrightness((int) ((packed >>> PACKED_BLUE_OFFSET) & COMPRESSED_BRIGHTNESS_MASK));
    }

    /**
     * Unpacks the given value, and gets the brightest skylight and blocklight channels as a regular brightness value.
     * Used for compatibility.
     */
    public static int getBrightestChannelFromPacked(long packed) {
        val redCompressed = (int) ((packed >>> PACKED_RED_OFFSET) & COMPRESSED_BRIGHTNESS_MASK);
        val greenCompressed = (int) ((packed >>> PACKED_GREEN_OFFSET) & COMPRESSED_BRIGHTNESS_MASK);
        val blueCompressed = (int) ((packed >>> PACKED_BLUE_OFFSET) & COMPRESSED_BRIGHTNESS_MASK);
        return unpackTessellatorBrightness(max3(redCompressed, greenCompressed, blueCompressed, COMPRESSED_SKY_LIGHT_MASK) |
                                           max3(redCompressed, greenCompressed, blueCompressed, COMPRESSED_BLOCK_LIGHT_MASK));
    }

    /**
     * Takes the per-channel maximum of the two packed colors.
     */
    public static long packedMax(long packedA, long packedB) {
        //Optimized algorithm, given that internally we have a tightly packed sequence of identical elements.
        long result = 0L;
        for (int i = 0; i <= 40; i += 8) {
            val mask = 0xFFL << i;
            val a = packedA & mask;
            val b = packedB & mask;
            result |= Math.max(a, b);
        }
        return result;
    }

    public static long mixAOBrightness(long packedA, long packedB, double aMul, double bMul) {
        long packedResult = 0;
        for (int i = 0; i <= 40; i += 8) {
            packedResult |= lerpChannel(packedA, packedB, aMul, bMul, i);
        }
        return packedResult;
    }

    private static long lerpChannel(long packedA, long packedB, double aMul, double bMul, int offset) {
        val a = (double) unit(packedA, offset) * aMul;
        val b = (double) unit(packedB, offset) * bMul;
        return (((long) (a + b)) & 0xFF) << offset;
    }

    public static long mixAOBrightness(long packedTL,
                                       long packedBL,
                                       long packedBR,
                                       long packedTR,
                                       double lerpTB,
                                       double lerpLR) {
        val lTL = (1.0 - lerpTB) * (1.0 - lerpLR);
        val lTR = (1.0 - lerpTB) * lerpLR;
        val lBL = lerpTB * (1.0 - lerpLR);
        val lBR = lerpTB * lerpLR;
        return mixAOBrightness(packedTL, packedTR, packedBL, packedBR, lTL, lTR, lBL, lBR);
    }

    public static long mixAOBrightness(long packedA,
                                       long packedB,
                                       long packedC,
                                       long packedD,
                                       double aMul,
                                       double bMul,
                                       double cMul,
                                       double dMul) {
        long packedResult = 0;
        for (int i = 0; i <= 40; i += 8) {
            packedResult |= mixAoBrightnessChannel(packedA, packedB, packedC, packedD, aMul, bMul, cMul, dMul, i);
        }
        return packedResult;
    }

    /**
     * Takes the average of the passed in brightnesses. Faster than doing it by hand through the API.
     */
    public static long packedAverage(long a, long b, boolean ignoreZero) {
        long resultPacked = 0;
        for (int i = 0; i <= 40; i += 8) {
            resultPacked |= getAverageChannel(a, b, i, ignoreZero);
        }
        return resultPacked;
    }

    /**
     * Takes the average of the passed in brightnesses. Faster than doing it by hand through the API.
     */
    public static long packedAverage(long a, long b, long c, long d, boolean ignoreZero) {
        long resultPacked = 0;
        if (ignoreZero) {
            for (int i = 0; i <= 40; i += 8) {
                resultPacked |= getAverageChannelIgnoreZero(a, b, c, d, i);
            }
        } else {
            for (int i = 0; i <= 40; i += 8) {
                resultPacked |= getAverageChannel(a, b, c, d, i);
            }
        }
        return resultPacked;
    }

    /**
     * Takes the average of the passed in brightnesses. Faster than doing it by hand through the API.
     */
    public static long packedAverage(long[] values, int n, boolean ignoreZero) {
        long resultPacked = 0;
        for (int i = 0; i <= 40; i += 8) {
            resultPacked |= getAverageChannel(values, n, i, ignoreZero);
        }
        return resultPacked;
    }

    private static long getAverageChannel(long a, long b, int shift, boolean ignoreZero) {
        int unitA = unit(a, shift);
        int unitB = unit(b, shift);
        if (ignoreZero) {
            if (unitA == 0) {
                return unitB;
            }
            return (long) unitA << shift;
        } else {
            return (long)((int)((unitA + unitB) / 2f) & 0xFF) << shift;
        }
    }

    private static long getAverageChannel(long a, long b, long c, long d, int shift) {
        float light = 0;
        light += unit(a, shift);
        light += unit(b, shift);
        light += unit(c, shift);
        light += unit(d, shift);
        light /= 4;
        return (long)((int)light & 0xFF) << shift;
    }

    private static long getAverageChannelIgnoreZero(long a, long b, long c, long d, int shift) {
        int count = 0;
        float light = 0;
        int unitA = unit(a, shift);
        int unitB = unit(b, shift);
        int unitC = unit(c, shift);
        int unitD = unit(d, shift);
        if (unitA != 0) {
            count++;
            light += unitA;
        }
        if (unitB != 0) {
            count++;
            light += unitB;
        }
        if (unitC != 0) {
            count++;
            light += unitC;
        }
        if (unitD != 0) {
            count++;
            light += unitD;
        }
        if (count != 0) {
            light /= count;
        }
        return (long)((int)light & 0xFF) << shift;
    }

    private static long getAverageChannel(long[] packedValues, int n, int shift, boolean ignoreZero) {
        int count = 0;
        float light = 0;
        for (int i = 0; i < n; i++) {
            val packed = packedValues[i];
            val value = unit(packed, shift);
            if (ignoreZero && value == 0) {
                continue;
            }
            count++;
            light += value;
        }
        if (count != 0) {
            light /= count;
        }
        return (long) ((int) light & 0xFF) << shift;
    }

    private static int max3(int red, int green, int blue, int mask) {
        return Math.max(Math.max(red & mask, green & mask), blue & mask);
    }

    private static int packTessellatorBrightness(int tessellatorBrightness) {
        return ((tessellatorBrightness & SKYLIGHT_MASK) >>> 8) |
               (tessellatorBrightness & BLOCK_LIGHT_MASK);
    }

    private static int unpackTessellatorBrightness(int packedTessellatorBrightness) {
        return ((packedTessellatorBrightness & COMPRESSED_SKY_LIGHT_MASK) << 8) |
               (packedTessellatorBrightness & COMPRESSED_BLOCK_LIGHT_MASK);
    }

    private static long mixAoBrightnessChannel(long a,
                                               long b,
                                               long c,
                                               long d,
                                               double aMul,
                                               double bMul,
                                               double cMul,
                                               double dMul,
                                               int channel) {
        val fA = unit(a, channel) * aMul;
        val fB = unit(b, channel) * bMul;
        val fC = unit(c, channel) * cMul;
        val fD = unit(d, channel) * dMul;
        return (long) ((int) (fA + fB + fC + fD) & 0xFF) << channel;
    }

    private static int unit(long val, int channel) {
        return (int) ((val >>> channel) & 0xFF);
    }
}
