/*
 * Copyright (c) 2023 FalsePattern
 * This work is licensed under the Creative Commons Attribution-NonCommercial-NoDerivatives 4.0 International License.
 * To view a copy of this license, visit http://creativecommons.org/licenses/by-nc-nd/4.0/
 * or send a letter to Creative Commons, PO Box 1866, Mountain View, CA 94042, USA.
 *
 */

package com.falsepattern.rple.internal.color;

import lombok.val;

/**
 * Utility class for managing minecraft brightness values, and packed long RGB versions of these brightness values.
 */
public class BrightnessUtil {
    private static final int BLOCKLIGHT_MASK = 0x000000FF;
    private static final int SKYLIGHT_MASK = 0x00FF0000;
    //Shared with CookieMonster.
    static final int BRIGHTNESS_MASK = BLOCKLIGHT_MASK | SKYLIGHT_MASK;

    private static final int BLOCKLIGHT_BRIGHTNESS_OFFSET = 4;
    private static final int SKYLIGHT_BRIGHTNESS_OFFSET = 20;

    private static final int COMPRESSED_BLOCKLIGHT_MASK = 0x000000FF;
    private static final int COMPRESSED_SKYLIGHT_MASK = 0x0000FF00;
    private static final int COMPRESSED_BRIGHTNESS_MASK = COMPRESSED_BLOCKLIGHT_MASK | COMPRESSED_SKYLIGHT_MASK;

    // Long format (hex):
    // 0000 RRrr GGgg BBbb
    private static final int PACKED_RED_OFFSET = 32;
    private static final int PACKED_GREEN_OFFSET = 16;
    private static final int PACKED_BLUE_OFFSET = 0;

    public static int lightLevelsToBrightness(int block, int sky) {
        return (sky & 0xF) << SKYLIGHT_BRIGHTNESS_OFFSET | (block & 0xF) << BLOCKLIGHT_BRIGHTNESS_OFFSET;
    }

    public static int getBlocklightFromBrightness(int brightness) {
        return (brightness & BLOCKLIGHT_MASK) >>> BLOCKLIGHT_BRIGHTNESS_OFFSET;
    }

    public static int getSkylightFromBrightness(int brightness) {
        return (brightness & SKYLIGHT_MASK) >>> SKYLIGHT_BRIGHTNESS_OFFSET;
    }

    public static long brightnessesToPackedLong(int red, int green, int blue) {
        return (long) compressBrightness(red) << PACKED_RED_OFFSET |
               (long) compressBrightness(green) << PACKED_GREEN_OFFSET |
               (long) compressBrightness(blue) << PACKED_BLUE_OFFSET;
    }

    public static int getBrightnessRed(long packed) {
        return decompressBrightness((int)((packed >>> PACKED_RED_OFFSET) & COMPRESSED_BRIGHTNESS_MASK));
    }

    public static int getBrightnessGreen(long packed) {
        return decompressBrightness((int)((packed >>> PACKED_GREEN_OFFSET) & COMPRESSED_BRIGHTNESS_MASK));
    }

    public static int getBrightnessBlue(long packed) {
        return decompressBrightness((int)((packed >>> PACKED_BLUE_OFFSET) & COMPRESSED_BRIGHTNESS_MASK));
    }

    /**
     * Unpacks the given value, and gets the brightest skylight and blocklight channels as a regular brightness value.
     * Used for compatibility.
     */
    public static int getBrightestChannelFromPacked(long packed) {
        val redCompressed = (int) ((packed >>> PACKED_RED_OFFSET) & COMPRESSED_BRIGHTNESS_MASK);
        val greenCompressed = (int) ((packed >>> PACKED_GREEN_OFFSET) & COMPRESSED_BRIGHTNESS_MASK);
        val blueCompressed = (int) ((packed >>> PACKED_BLUE_OFFSET) & COMPRESSED_BRIGHTNESS_MASK);
        return decompressBrightness(max3(redCompressed, greenCompressed, blueCompressed, COMPRESSED_SKYLIGHT_MASK) |
                                    max3(redCompressed, greenCompressed, blueCompressed, COMPRESSED_BLOCKLIGHT_MASK));
    }

    /**
     * Takes the per-channel maximum of the two packed colors.
     */
    public static long packedMax(long packedA, long packedB) {
        //Optimized algorithm, given that internally we have a tightly packed sequence of identical elements.
        long result = 0L;
        for (int i = 0; i <= 40; i += 8) {
            val mask = (0xFFL << i);
            val a = packedA & mask;
            val b = packedB & mask;
            result |= Math.max(a, b);
        }
        return result;
    }

    private static int max3(int red, int green, int blue, int mask) {
        return Math.max(Math.max(red & mask, green & mask), blue & mask);
    }

    private static int compressBrightness(int brightness) {
        return ((brightness & SKYLIGHT_MASK) >>> 8) | (brightness & BLOCKLIGHT_MASK);
    }

    private static int decompressBrightness(int compressedBrightness) {
        return ((compressedBrightness & COMPRESSED_SKYLIGHT_MASK) << 8) | (compressedBrightness & COMPRESSED_BLOCKLIGHT_MASK);
    }
}
