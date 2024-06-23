/*
 * Copyright (c) 2023 FalsePattern, Ven
 * This work is licensed under the Creative Commons Attribution-NonCommercial-NoDerivatives 4.0 International License.
 * To view a copy of this license, visit http://creativecommons.org/licenses/by-nc-nd/4.0/
 * or send a letter to Creative Commons, PO Box 1866, Mountain View, CA 94042, USA.
 */

package com.falsepattern.rple.api.client;

import lombok.experimental.UtilityClass;
import lombok.val;

/**
 * Utility class for managing minecraft brightness values, and packed long RGB versions of these brightness values.
 */
@UtilityClass
public final class ClientColorHelper {
    // fields

    //region vanilla constants
    private static final int VANILLA_BLOCK_MASK = 0x000000FF;
    private static final int VANILLA_SKY_MASK = 0x00FF0000;

    private static final int CHANNEL_8BIT_TO_VANILLA_BLOCK = 0;
    private static final int CHANNEL_4BIT_TO_VANILLA_BLOCK = 4;
    private static final int CHANNEL_8BIT_TO_VANILLA_SKY = 16;
    private static final int CHANNEL_4BIT_TO_VANILLA_SKY = 20;
    //endregion

    //region RGB64 constants
    private static final int COMPRESSED_BLOCK_MASK = 0x000000FF;
    private static final int COMPRESSED_SKY_MASK = 0x0000FF00;
    private static final int COMPRESSED_MASK = COMPRESSED_BLOCK_MASK | COMPRESSED_SKY_MASK;

    // Long format (hex):
    // 0000 RRrr GGgg BBbb
    private static final int RGB64_RED_OFFSET = 32;
    private static final int RGB64_GREEN_OFFSET = 16;
    private static final int RGB64_BLUE_OFFSET = 0;

    private static final int CHANNEL_8BIT_TO_RGB64_RED_SKY = 40;
    private static final int CHANNEL_4BIT_TO_RGB64_RED_SKY = 44;
    private static final int CHANNEL_8BIT_TO_RGB64_RED_BLOCK = 32;
    private static final int CHANNEL_4BIT_TO_RGB64_RED_BLOCK = 36;
    private static final int CHANNEL_8BIT_TO_RGB64_GREEN_SKY = 24;
    private static final int CHANNEL_4BIT_TO_RGB64_GREEN_SKY = 28;
    private static final int CHANNEL_8BIT_TO_RGB64_GREEN_BLOCK = 16;
    private static final int CHANNEL_4BIT_TO_RGB64_GREEN_BLOCK = 20;
    private static final int CHANNEL_8BIT_TO_RGB64_BLUE_SKY = 8;
    private static final int CHANNEL_4BIT_TO_RGB64_BLUE_SKY = 12;
    private static final int CHANNEL_8BIT_TO_RGB64_BLUE_BLOCK = 0;
    private static final int CHANNEL_4BIT_TO_RGB64_BLUE_BLOCK = 4;

    private static final long RGB64_4BIT_LOSSY_CHECK_MASK = (0xFL << CHANNEL_8BIT_TO_RGB64_RED_SKY) |
            (0xFL << CHANNEL_8BIT_TO_RGB64_RED_BLOCK) |
            (0xFL << CHANNEL_8BIT_TO_RGB64_GREEN_SKY) |
            (0xFL << CHANNEL_8BIT_TO_RGB64_GREEN_BLOCK) |
            (0xFL << CHANNEL_8BIT_TO_RGB64_BLUE_SKY) |
            (0xFL << CHANNEL_8BIT_TO_RGB64_BLUE_BLOCK);
    //endregion

    //region RGB32 constants
    private static final int CHANNEL_4BIT_TO_RGB32_RED_SKY = 12;
    private static final int CHANNEL_4BIT_TO_RGB32_GREEN_SKY = 16;
    private static final int CHANNEL_4BIT_TO_RGB32_BLUE_SKY = 20;
    private static final int CHANNEL_4BIT_TO_RGB32_RED_BLOCK = 0;
    private static final int CHANNEL_4BIT_TO_RGB32_GREEN_BLOCK = 4;
    private static final int CHANNEL_4BIT_TO_RGB32_BLUE_BLOCK = 8;

    private static final int RGB32_SHIFT_MAX = 20;
    private static final int RGB32_BLOCK_REMOVE_BITMASK = ~((0xF << CHANNEL_4BIT_TO_RGB32_RED_BLOCK) | (0xF << CHANNEL_4BIT_TO_RGB32_GREEN_BLOCK) | (0xF << CHANNEL_4BIT_TO_RGB32_BLUE_BLOCK));

    public static final int RGB32_MAX_SKYLIGHT_NO_BLOCKLIGHT = (0xF << CHANNEL_4BIT_TO_RGB32_RED_SKY) |
            (0xF << CHANNEL_4BIT_TO_RGB32_GREEN_SKY) |
            (0xF << CHANNEL_4BIT_TO_RGB32_BLUE_SKY);

    public static final int RGB32_NO_SKYLIGHT_NO_BLOCKLIGHT = 0x000_000;

    //endregion

    // methods

    //region RGB32
    public static int RGB32FromChannels4Bit(int skyR, int skyG, int skyB, int blockR, int blockG, int blockB) {
        skyR = (skyR & 0xF) << CHANNEL_4BIT_TO_RGB32_RED_SKY;
        skyG = (skyG & 0xF) << CHANNEL_4BIT_TO_RGB32_GREEN_SKY;
        skyB = (skyB & 0xF) << CHANNEL_4BIT_TO_RGB32_BLUE_SKY;
        blockR = (blockR & 0xF) << CHANNEL_4BIT_TO_RGB32_RED_BLOCK;
        blockG = (blockG & 0xF) << CHANNEL_4BIT_TO_RGB32_GREEN_BLOCK;
        blockB = (blockB & 0xF) << CHANNEL_4BIT_TO_RGB32_BLUE_BLOCK;

        return skyR | skyG | skyB | blockR | blockG | blockB;
    }

    public static int RGB32FromChannels4BitBlock(int blockR, int blockG, int blockB) {
        blockR = (blockR & 0xF) << CHANNEL_4BIT_TO_RGB32_RED_BLOCK;
        blockG = (blockG & 0xF) << CHANNEL_4BIT_TO_RGB32_GREEN_BLOCK;
        blockB = (blockB & 0xF) << CHANNEL_4BIT_TO_RGB32_BLUE_BLOCK;

        return blockR | blockG | blockB;
    }

    public static int tryRGB32FromRGB64(long packed) {
        if ((packed & RGB64_4BIT_LOSSY_CHECK_MASK) != 0)
            return -1;
        int rgb = 0;
        rgb |= ((int) (packed >>> CHANNEL_4BIT_TO_RGB64_RED_BLOCK) & 0xF) << CHANNEL_4BIT_TO_RGB32_RED_BLOCK;
        rgb |= ((int) (packed >>> CHANNEL_4BIT_TO_RGB64_GREEN_BLOCK) & 0xF) << CHANNEL_4BIT_TO_RGB32_GREEN_BLOCK;
        rgb |= ((int) (packed >>> CHANNEL_4BIT_TO_RGB64_BLUE_BLOCK) & 0xF) << CHANNEL_4BIT_TO_RGB32_BLUE_BLOCK;
        rgb |= ((int) (packed >>> CHANNEL_4BIT_TO_RGB64_RED_SKY) & 0xF) << CHANNEL_4BIT_TO_RGB32_RED_SKY;
        rgb |= ((int) (packed >>> CHANNEL_4BIT_TO_RGB64_GREEN_SKY) & 0xF) << CHANNEL_4BIT_TO_RGB32_GREEN_SKY;
        rgb |= ((int) (packed >>> CHANNEL_4BIT_TO_RGB64_BLUE_SKY) & 0xF) << CHANNEL_4BIT_TO_RGB32_BLUE_SKY;
        return rgb;
    }

    public static int RGB32ClampMinBlockChannels(int packed, int minRedBlockLight, int minGreenBlockLight, int minBlueBlockLight) {
        //extract
        int containedRedBlockLight = (packed >>> CHANNEL_4BIT_TO_RGB32_RED_BLOCK) & 0xF;
        int containedGreenBlockLight = (packed >>> CHANNEL_4BIT_TO_RGB32_GREEN_BLOCK) & 0xF;
        int containedBlueBlockLight = (packed >>> CHANNEL_4BIT_TO_RGB32_BLUE_BLOCK) & 0xF;

        //align
        minRedBlockLight &= 0xF;
        minGreenBlockLight &= 0xF;
        minBlueBlockLight &= 0xF;

        //max
        int redBlockLight = Math.max(minRedBlockLight, containedRedBlockLight);
        int greenBlockLight = Math.max(minGreenBlockLight, containedGreenBlockLight);
        int blueBlockLight = Math.max(minBlueBlockLight, containedBlueBlockLight);

        //mask out bits
        packed &= RGB32_BLOCK_REMOVE_BITMASK;
        //weave
        packed |= redBlockLight << CHANNEL_4BIT_TO_RGB32_RED_BLOCK;
        packed |= greenBlockLight << CHANNEL_4BIT_TO_RGB32_GREEN_BLOCK;
        packed |= blueBlockLight << CHANNEL_4BIT_TO_RGB32_BLUE_BLOCK;

        return packed;
    }

    public static int RGB32Max(int lightValueA, int lightValueB, int lightValueC, int lightValueD, int lightValueE) {
        int result = 0;
        for (int shift = 0; shift <= RGB32_SHIFT_MAX; shift += 4) {
            int a = (lightValueA >>> shift) & 0xF;
            int b = (lightValueB >>> shift) & 0xF;
            int c = (lightValueC >>> shift) & 0xF;
            int d = (lightValueD >>> shift) & 0xF;
            int e = (lightValueE >>> shift) & 0xF;

            int max = 0;
            //noinspection DataFlowIssue
            max = Math.max(max, a);
            max = Math.max(max, b);
            max = Math.max(max, c);
            max = Math.max(max, d);
            max = Math.max(max, e);

            result |= max << shift;
        }
        return result;
    }
    //endregion

    //region vanilla
    /**
     * Packs two 0-15 values into a vanilla-style brightness value.
     */
    public static int vanillaFromBlockSky4Bit(int blockLightValue, int skyLightValue) {
        return (blockLightValue & 0xF) << CHANNEL_4BIT_TO_VANILLA_BLOCK |
                (skyLightValue & 0xF) << CHANNEL_4BIT_TO_VANILLA_SKY;
    }

    /**
     * The 0-15 light level inside the packed brightness.
     */
    public static int block4BitFromVanilla(int brightness) {
        return (brightness & VANILLA_BLOCK_MASK) >>> CHANNEL_4BIT_TO_VANILLA_BLOCK;
    }

    /**
     * The 0-15 light level inside the packed brightness.
     */
    public static int sky4BitFromVanilla(int brightness) {
        return (brightness & VANILLA_SKY_MASK) >>> CHANNEL_4BIT_TO_VANILLA_SKY;
    }

    /**
     * Packs two 0-240 values into a vanilla-style brightness value. Only relevant when messing with render logic.
     */
    public static int vanillaFromBlockSky8Bit(int block, int sky) {
        return (sky & 0xFF) << CHANNEL_8BIT_TO_VANILLA_SKY | (block & 0xFF) << CHANNEL_8BIT_TO_VANILLA_BLOCK;
    }

    /**
     * The raw render-specific brightness value inside the packed brightness.
     */
    public static int block8BitFromVanilla(int brightness) {
        return (brightness & VANILLA_BLOCK_MASK) >>> CHANNEL_8BIT_TO_VANILLA_BLOCK;
    }

    /**
     * The raw render-specific brightness value inside the packed brightness.
     */
    public static int sky8BitFromVanilla(int brightness) {
        return (brightness & VANILLA_SKY_MASK) >>> CHANNEL_8BIT_TO_VANILLA_SKY;
    }

    public static int vanillaFromRGB64Red(long packed) {
        return vanillaFromCompressed((int) ((packed >>> RGB64_RED_OFFSET) & COMPRESSED_MASK));
    }

    public static int vanillaFromRGB64Green(long packed) {
        return vanillaFromCompressed((int) ((packed >>> RGB64_GREEN_OFFSET) & COMPRESSED_MASK));
    }

    public static int vanillaFromRGB64Blue(long packed) {
        return vanillaFromCompressed((int) ((packed >>> RGB64_BLUE_OFFSET) & COMPRESSED_MASK));
    }

    /**
     * Unpacks the given value, and gets the brightest skylight and blocklight channels as a regular brightness value.
     * Used for compatibility.
     */
    public static int vanillaFromRGB64Max(long packed) {
        val redCompressed = (int) ((packed >>> RGB64_RED_OFFSET) & COMPRESSED_MASK);
        val greenCompressed = (int) ((packed >>> RGB64_GREEN_OFFSET) & COMPRESSED_MASK);
        val blueCompressed = (int) ((packed >>> RGB64_BLUE_OFFSET) & COMPRESSED_MASK);
        return vanillaFromCompressed(max3(redCompressed, greenCompressed, blueCompressed, COMPRESSED_SKY_MASK) |
                max3(redCompressed, greenCompressed, blueCompressed, COMPRESSED_BLOCK_MASK));
    }
    //endregion

    //region RGB64

    public static long RGB64FromVanillaRGB(int red, int green, int blue) {
        return (long) compressedFromVanilla(red) << RGB64_RED_OFFSET |
               (long) compressedFromVanilla(green) << RGB64_GREEN_OFFSET |
               (long) compressedFromVanilla(blue) << RGB64_BLUE_OFFSET;
    }

    public static long RGB64FromVanillaMonochrome(int brightness) {
        val compressed = (long) compressedFromVanilla(brightness);
        return compressed << RGB64_RED_OFFSET |
                compressed << RGB64_GREEN_OFFSET |
                compressed << RGB64_BLUE_OFFSET;
    }

    //Fast paths for converting light levels to packed values

    public static long RGB64FromRGB32(int rgb) {
        long packed = 0L;
        packed |= (long) ((rgb >>> CHANNEL_4BIT_TO_RGB32_RED_BLOCK) & 0xF) << CHANNEL_4BIT_TO_RGB64_RED_BLOCK;
        packed |= (long) ((rgb >>> CHANNEL_4BIT_TO_RGB32_GREEN_BLOCK) & 0xF) << CHANNEL_4BIT_TO_RGB64_GREEN_BLOCK;
        packed |= (long) ((rgb >>> CHANNEL_4BIT_TO_RGB32_BLUE_BLOCK) & 0xF) << CHANNEL_4BIT_TO_RGB64_BLUE_BLOCK;
        packed |= (long) ((rgb >>> CHANNEL_4BIT_TO_RGB32_RED_SKY) & 0xF) << CHANNEL_4BIT_TO_RGB64_RED_SKY;
        packed |= (long) ((rgb >>> CHANNEL_4BIT_TO_RGB32_GREEN_SKY) & 0xF) << CHANNEL_4BIT_TO_RGB64_GREEN_SKY;
        packed |= (long) ((rgb >>> CHANNEL_4BIT_TO_RGB32_BLUE_SKY) & 0xF) << CHANNEL_4BIT_TO_RGB64_BLUE_SKY;
        return packed;
    }

    /**
     * Takes the per-channel maximum of the two packed colors.
     */
    public static long RGB64Max(long packedA, long packedB) {
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

    public static long RGB64MixAOBrightness(long packedA, long packedB, double aMul, double bMul) {
        long packedResult = 0;
        for (int i = 0; i <= 40; i += 8) {
            packedResult |= RGB64LerpChannel(packedA, packedB, aMul, bMul, i);
        }
        return packedResult;
    }

    private static long RGB64LerpChannel(long packedA, long packedB, double aMul, double bMul, int offset) {
        val a = (double) unit(packedA, offset) * aMul;
        val b = (double) unit(packedB, offset) * bMul;
        return (((long) (a + b)) & 0xFF) << offset;
    }

    public static long RGB64MixAOBrightness(long packedTL,
                                            long packedBL,
                                            long packedBR,
                                            long packedTR,
                                            double lerpTB,
                                            double lerpLR) {
        val lTL = (1.0 - lerpTB) * (1.0 - lerpLR);
        val lTR = (1.0 - lerpTB) * lerpLR;
        val lBL = lerpTB * (1.0 - lerpLR);
        val lBR = lerpTB * lerpLR;
        return RGB64MixAOBrightness(packedTL, packedTR, packedBL, packedBR, lTL, lTR, lBL, lBR);
    }

    public static long RGB64MixAOBrightness(long packedA,
                                            long packedB,
                                            long packedC,
                                            long packedD,
                                            double aMul,
                                            double bMul,
                                            double cMul,
                                            double dMul) {
        long packedResult = 0;
        for (int i = 0; i <= 40; i += 8) {
            packedResult |= RGB64MixAoBrightnessChannel(packedA, packedB, packedC, packedD, aMul, bMul, cMul, dMul, i);
        }
        return packedResult;
    }

    /**
     * Takes the average of the passed in brightnesses. Faster than doing it by hand through the API.
     */
    public static long RGB64Average(long a, long b, boolean ignoreZero) {
        long resultPacked = 0;
        for (int i = 0; i <= 40; i += 8) {
            resultPacked |= RGB64AverageChannel(a, b, i, ignoreZero);
        }
        return resultPacked;
    }

    /**
     * Takes the average of the passed in brightnesses. Faster than doing it by hand through the API.
     */
    public static long RGB64Average(long a, long b, long c, long d, boolean ignoreZero) {
        long resultPacked = 0;
        if (ignoreZero) {
            for (int i = 0; i <= 40; i += 8) {
                resultPacked |= RGB64AverageChannelIgnoreZero(a, b, c, d, i);
            }
        } else {
            for (int i = 0; i <= 40; i += 8) {
                resultPacked |= RGB64AverageChannel(a, b, c, d, i);
            }
        }
        return resultPacked;
    }

    /**
     * Takes the average of the passed in brightnesses. Faster than doing it by hand through the API.
     */
    public static long RGB64Average(long[] values, int n, boolean ignoreZero) {
        long resultPacked = 0;
        for (int i = 0; i <= 40; i += 8) {
            resultPacked |= RGB64AverageChannel(values, n, i, ignoreZero);
        }
        return resultPacked;
    }

    //endregion

    //region tess

    public static int tessFromRGB64Red(long packed) {
        return tessFromVanilla(vanillaFromCompressed((int) ((packed >>> RGB64_RED_OFFSET) & COMPRESSED_MASK)));
    }

    public static int tessFromRGB64Green(long packed) {
        return tessFromVanilla(vanillaFromCompressed((int) ((packed >>> RGB64_GREEN_OFFSET) & COMPRESSED_MASK)));
    }

    public static int tessFromRGB64Blue(long packed) {
        return tessFromVanilla(vanillaFromCompressed((int) ((packed >>> RGB64_BLUE_OFFSET) & COMPRESSED_MASK)));
    }

    public static int tessFromVanilla(int unpacked) {
        val lightMapBlock = remapToShort(unpacked & 0xFF);
        val lightMapSky = remapToShort((unpacked >> 16) & 0xFF);
        return ((int) lightMapBlock & 0xFFFF) | ((int) lightMapSky << 16);
    }

    //endregion

    //region cookie

    /**
     * Convenience method, identical to {@link ClientColorHelper#RGB64Max(long, long)}, but automatically decodes and
     * encodes the input/output into cookies.
     */
    public static int cookieMax(int cookieA, int cookieB) {
        return CookieMonster.cookieFromRGB64(RGB64Max(CookieMonster.RGB64FromCookie(cookieA), CookieMonster.RGB64FromCookie(cookieB)));
    }

    public static int cookieMixAOBrightness(int brightTL, int brightBL, int brightBR, int brightTR, double lerpTB, double lerpLR) {
        val packedTL = CookieMonster.RGB64FromCookie(brightTL);
        val packedBL = CookieMonster.RGB64FromCookie(brightBL);
        val packedBR = CookieMonster.RGB64FromCookie(brightBR);
        val packedTR = CookieMonster.RGB64FromCookie(brightTR);
        return CookieMonster.cookieFromRGB64(RGB64MixAOBrightness(packedTL, packedBL, packedBR, packedTR, lerpTB, lerpLR));
    }

    public static int cookieMixAOBrightness(int a, int b, int c, int d, double aMul, double bMul, double cMul, double dMul) {
        val packedA = CookieMonster.RGB64FromCookie(a);
        val packedB = CookieMonster.RGB64FromCookie(b);
        val packedC = CookieMonster.RGB64FromCookie(c);
        val packedD = CookieMonster.RGB64FromCookie(d);
        return CookieMonster.cookieFromRGB64(RGB64MixAOBrightness(packedA, packedB, packedC, packedD, aMul, bMul, cMul, dMul));
    }

    public static int cookieMixAOBrightness(int a, int b, double aMul, double bMul) {
        val packedA = CookieMonster.RGB64FromCookie(a);
        val packedB = CookieMonster.RGB64FromCookie(b);
        return CookieMonster.cookieFromRGB64(RGB64MixAOBrightness(packedA, packedB, aMul, bMul));
    }

    public static int cookieAverage(boolean ignoreZero, int a, int b) {
        val packedA = CookieMonster.RGB64FromCookie(a);
        val packedB = CookieMonster.RGB64FromCookie(b);
        return CookieMonster.cookieFromRGB64(RGB64Average(packedA, packedB, ignoreZero));
    }

    public static int cookieAverage(boolean ignoreZero, int a, int b, int c, int d) {
        val packedA = CookieMonster.RGB64FromCookie(a);
        val packedB = CookieMonster.RGB64FromCookie(b);
        val packedC = CookieMonster.RGB64FromCookie(c);
        val packedD = CookieMonster.RGB64FromCookie(d);
        return CookieMonster.cookieFromRGB64(RGB64Average(packedA, packedB, packedC, packedD, ignoreZero));
    }

    public static int cookieAverage(boolean ignoreZero, int... values) {
        long[] packed = new long[values.length];
        for (int i = 0; i < values.length; i++) {
            packed[i] = CookieMonster.RGB64FromCookie(values[i]);
        }
        return CookieMonster.cookieFromRGB64(RGB64Average(packed, packed.length, ignoreZero));
    }

    //endregion

    // private

    //region internals

    private static short remapToShort(int n) {
        n = Math.min(n, 240);
        val normalized = n / 240F;
        return (short) Math.round(normalized * (Short.MAX_VALUE - Short.MIN_VALUE) + Short.MIN_VALUE);
    }

    private static long RGB64AverageChannel(long a, long b, int shift, boolean ignoreZero) {
        int unitA = unit(a, shift);
        int unitB = unit(b, shift);
        if (ignoreZero) {
            if (unitA == 0) {
                return unitB;
            }
            return (long) unitA << shift;
        } else {
            return (long) ((int) ((unitA + unitB) / 2f) & 0xFF) << shift;
        }
    }

    private static long RGB64AverageChannel(long a, long b, long c, long d, int shift) {
        float light = 0;
        light += unit(a, shift);
        light += unit(b, shift);
        light += unit(c, shift);
        light += unit(d, shift);
        light /= 4;
        return (long) ((int) light & 0xFF) << shift;
    }

    private static long RGB64AverageChannelIgnoreZero(long a, long b, long c, long d, int shift) {
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
        return (long) ((int) light & 0xFF) << shift;
    }

    private static long RGB64AverageChannel(long[] packedValues, int n, int shift, boolean ignoreZero) {
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

    private static int compressedFromVanilla(int vanilla) {
        return ((vanilla & VANILLA_SKY_MASK) >>> 8) |
               (vanilla & VANILLA_BLOCK_MASK);
    }

    private static int vanillaFromCompressed(int compressed) {
        return ((compressed & COMPRESSED_SKY_MASK) << 8) |
               (compressed & COMPRESSED_BLOCK_MASK);
    }

    private static long RGB64MixAoBrightnessChannel(long a,
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

    //endregion
}
