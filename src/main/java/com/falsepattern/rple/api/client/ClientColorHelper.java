/*
 * Right Proper Lighting Engine
 *
 * Copyright (C) 2023-2025 FalsePattern, Ven
 * All Rights Reserved
 *
 * The above copyright notice and this permission notice
 * shall be included in all copies or substantial portions of the Software.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, only version 3 of the License.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program. If not, see <https://www.gnu.org/licenses/>.
 *
 * This program comes with additional permissions according to Section 7 of the
 * GNU Affero General Public License. See the full LICENSE file for details.
 */

package com.falsepattern.rple.api.client;

import com.falsepattern.lib.StableAPI;
import it.unimi.dsi.fastutil.ints.Int2IntFunction;

/**
 * Utility class for managing color values for rendering-related logic
 */
@StableAPI(since = "1.0.0")
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

    private static final long RGB64_4BIT_LOSSY_CHECK_MASK =
            (0xFL << CHANNEL_8BIT_TO_RGB64_RED_SKY) | (0xFL << CHANNEL_8BIT_TO_RGB64_RED_BLOCK) | (0xFL << CHANNEL_8BIT_TO_RGB64_GREEN_SKY) |
            (0xFL << CHANNEL_8BIT_TO_RGB64_GREEN_BLOCK) | (0xFL << CHANNEL_8BIT_TO_RGB64_BLUE_SKY) | (0xFL << CHANNEL_8BIT_TO_RGB64_BLUE_BLOCK);
    //endregion

    //region RGB32 constants
    private static final int CHANNEL_4BIT_TO_RGB32_RED_SKY = 12;
    private static final int CHANNEL_4BIT_TO_RGB32_GREEN_SKY = 16;
    private static final int CHANNEL_4BIT_TO_RGB32_BLUE_SKY = 20;
    private static final int CHANNEL_4BIT_TO_RGB32_RED_BLOCK = 0;
    private static final int CHANNEL_4BIT_TO_RGB32_GREEN_BLOCK = 4;
    private static final int CHANNEL_4BIT_TO_RGB32_BLUE_BLOCK = 8;

    private static final int RGB32_SHIFT_MAX = 20;
    private static final int RGB32_BLOCK_REMOVE_BITMASK =
            ~((0xF << CHANNEL_4BIT_TO_RGB32_RED_BLOCK) | (0xF << CHANNEL_4BIT_TO_RGB32_GREEN_BLOCK) | (0xF << CHANNEL_4BIT_TO_RGB32_BLUE_BLOCK));

    @StableAPI.Expose
    public static final int RGB32_MAX_SKYLIGHT_NO_BLOCKLIGHT =
            (0xF << CHANNEL_4BIT_TO_RGB32_RED_SKY) | (0xF << CHANNEL_4BIT_TO_RGB32_GREEN_SKY) | (0xF << CHANNEL_4BIT_TO_RGB32_BLUE_SKY);

    @StableAPI.Expose
    public static final int RGB32_NO_SKYLIGHT_NO_BLOCKLIGHT = 0x000_000;

    private ClientColorHelper() {
        throw new UnsupportedOperationException("This is a utility class and cannot be instantiated");
    }

    //endregion

    // methods

    //region RGB32
    @StableAPI.Expose
    public static int RGB32FromChannels4Bit(int skyR, int skyG, int skyB, int blockR, int blockG, int blockB) {
        skyR = (skyR & 0xF) << CHANNEL_4BIT_TO_RGB32_RED_SKY;
        skyG = (skyG & 0xF) << CHANNEL_4BIT_TO_RGB32_GREEN_SKY;
        skyB = (skyB & 0xF) << CHANNEL_4BIT_TO_RGB32_BLUE_SKY;
        blockR = (blockR & 0xF) << CHANNEL_4BIT_TO_RGB32_RED_BLOCK;
        blockG = (blockG & 0xF) << CHANNEL_4BIT_TO_RGB32_GREEN_BLOCK;
        blockB = (blockB & 0xF) << CHANNEL_4BIT_TO_RGB32_BLUE_BLOCK;

        return skyR | skyG | skyB | blockR | blockG | blockB;
    }

    @StableAPI.Expose
    public static int RGB32FromChannels4BitBlock(int blockR, int blockG, int blockB) {
        blockR = (blockR & 0xF) << CHANNEL_4BIT_TO_RGB32_RED_BLOCK;
        blockG = (blockG & 0xF) << CHANNEL_4BIT_TO_RGB32_GREEN_BLOCK;
        blockB = (blockB & 0xF) << CHANNEL_4BIT_TO_RGB32_BLUE_BLOCK;

        return blockR | blockG | blockB;
    }

    @StableAPI.Expose
    public static int tryRGB32FromRGB64(long rgb64) {
        if ((rgb64 & RGB64_4BIT_LOSSY_CHECK_MASK) != 0) {
            return -1;
        }
        int rgb32 = 0;
        rgb32 |= ((int) (rgb64 >>> CHANNEL_4BIT_TO_RGB64_RED_BLOCK) & 0xF) << CHANNEL_4BIT_TO_RGB32_RED_BLOCK;
        rgb32 |= ((int) (rgb64 >>> CHANNEL_4BIT_TO_RGB64_GREEN_BLOCK) & 0xF) << CHANNEL_4BIT_TO_RGB32_GREEN_BLOCK;
        rgb32 |= ((int) (rgb64 >>> CHANNEL_4BIT_TO_RGB64_BLUE_BLOCK) & 0xF) << CHANNEL_4BIT_TO_RGB32_BLUE_BLOCK;
        rgb32 |= ((int) (rgb64 >>> CHANNEL_4BIT_TO_RGB64_RED_SKY) & 0xF) << CHANNEL_4BIT_TO_RGB32_RED_SKY;
        rgb32 |= ((int) (rgb64 >>> CHANNEL_4BIT_TO_RGB64_GREEN_SKY) & 0xF) << CHANNEL_4BIT_TO_RGB32_GREEN_SKY;
        rgb32 |= ((int) (rgb64 >>> CHANNEL_4BIT_TO_RGB64_BLUE_SKY) & 0xF) << CHANNEL_4BIT_TO_RGB32_BLUE_SKY;
        return rgb32;
    }

    @StableAPI.Expose
    public static int RGB32ClampMinBlockChannels(int rgb32, int minRedBlockLight, int minGreenBlockLight, int minBlueBlockLight) {
        //extract
        int containedRedBlockLight = (rgb32 >>> CHANNEL_4BIT_TO_RGB32_RED_BLOCK) & 0xF;
        int containedGreenBlockLight = (rgb32 >>> CHANNEL_4BIT_TO_RGB32_GREEN_BLOCK) & 0xF;
        int containedBlueBlockLight = (rgb32 >>> CHANNEL_4BIT_TO_RGB32_BLUE_BLOCK) & 0xF;

        //align
        minRedBlockLight &= 0xF;
        minGreenBlockLight &= 0xF;
        minBlueBlockLight &= 0xF;

        //max
        int redBlockLight = Math.max(minRedBlockLight, containedRedBlockLight);
        int greenBlockLight = Math.max(minGreenBlockLight, containedGreenBlockLight);
        int blueBlockLight = Math.max(minBlueBlockLight, containedBlueBlockLight);

        //mask out bits
        rgb32 &= RGB32_BLOCK_REMOVE_BITMASK;
        //weave
        rgb32 |= redBlockLight << CHANNEL_4BIT_TO_RGB32_RED_BLOCK;
        rgb32 |= greenBlockLight << CHANNEL_4BIT_TO_RGB32_GREEN_BLOCK;
        rgb32 |= blueBlockLight << CHANNEL_4BIT_TO_RGB32_BLUE_BLOCK;

        return rgb32;
    }

    @StableAPI.Expose
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
    @StableAPI.Expose
    public static int vanillaFromBlockSky4Bit(int blockLightValue, int skyLightValue) {
        return (blockLightValue & 0xF) << CHANNEL_4BIT_TO_VANILLA_BLOCK | (skyLightValue & 0xF) << CHANNEL_4BIT_TO_VANILLA_SKY;
    }

    /**
     * The 0-15 light level inside the vanilla brightness.
     */
    @StableAPI.Expose
    public static int block4BitFromVanilla(int brightness) {
        return (brightness & VANILLA_BLOCK_MASK) >>> CHANNEL_4BIT_TO_VANILLA_BLOCK;
    }

    /**
     * The 0-15 light level inside the vanilla brightness.
     */
    @StableAPI.Expose
    public static int sky4BitFromVanilla(int brightness) {
        return (brightness & VANILLA_SKY_MASK) >>> CHANNEL_4BIT_TO_VANILLA_SKY;
    }

    /**
     * Packs two 0-240 values into a vanilla-style brightness value. Only relevant when messing with render logic.
     */
    @StableAPI.Expose
    public static int vanillaFromBlockSky8Bit(int block, int sky) {
        return (sky & 0xFF) << CHANNEL_8BIT_TO_VANILLA_SKY | (block & 0xFF) << CHANNEL_8BIT_TO_VANILLA_BLOCK;
    }

    /**
     * The raw render-specific brightness value inside the vanilla brightness.
     */
    @StableAPI.Expose
    public static int block8BitFromVanilla(int brightness) {
        return (brightness & VANILLA_BLOCK_MASK) >>> CHANNEL_8BIT_TO_VANILLA_BLOCK;
    }

    /**
     * The raw render-specific brightness value inside the vanilla brightness.
     */
    @StableAPI.Expose
    public static int sky8BitFromVanilla(int brightness) {
        return (brightness & VANILLA_SKY_MASK) >>> CHANNEL_8BIT_TO_VANILLA_SKY;
    }

    @StableAPI.Expose
    public static int vanillaFromRGB64Red(long rgb64) {
        return vanillaFromCompressed((int) ((rgb64 >>> RGB64_RED_OFFSET) & COMPRESSED_MASK));
    }

    @StableAPI.Expose
    public static int vanillaFromRGB64Green(long rgb64) {
        return vanillaFromCompressed((int) ((rgb64 >>> RGB64_GREEN_OFFSET) & COMPRESSED_MASK));
    }

    @StableAPI.Expose
    public static int vanillaFromRGB64Blue(long rgb64) {
        return vanillaFromCompressed((int) ((rgb64 >>> RGB64_BLUE_OFFSET) & COMPRESSED_MASK));
    }

    /**
     * Unpacks the given value, and gets the brightest skylight and blocklight channels as a regular brightness value.
     * Used for compatibility.
     */
    @StableAPI.Expose
    public static int vanillaFromRGB64Max(long rgb64) {
        final int redCompressed = (int) ((rgb64 >>> RGB64_RED_OFFSET) & COMPRESSED_MASK);
        final int greenCompressed = (int) ((rgb64 >>> RGB64_GREEN_OFFSET) & COMPRESSED_MASK);
        final int blueCompressed = (int) ((rgb64 >>> RGB64_BLUE_OFFSET) & COMPRESSED_MASK);
        return vanillaFromCompressed(
                max3(redCompressed, greenCompressed, blueCompressed, COMPRESSED_SKY_MASK) | max3(redCompressed, greenCompressed, blueCompressed, COMPRESSED_BLOCK_MASK));
    }
    //endregion

    //region RGB64

    @StableAPI.Expose
    public static long RGB64FromVanillaRGB(int red, int green, int blue) {
        return (long) compressedFromVanilla(red) << RGB64_RED_OFFSET | (long) compressedFromVanilla(green) << RGB64_GREEN_OFFSET |
               (long) compressedFromVanilla(blue) << RGB64_BLUE_OFFSET;
    }

    @StableAPI.Expose
    public static long RGB64FromVanillaMonochrome(int brightness) {
        final long compressed = (long) compressedFromVanilla(brightness);
        return compressed << RGB64_RED_OFFSET | compressed << RGB64_GREEN_OFFSET | compressed << RGB64_BLUE_OFFSET;
    }

    @StableAPI.Expose
    public static long RGB64FromRGB32(int rgb32) {
        long rgb64 = 0L;
        rgb64 |= (long) ((rgb32 >>> CHANNEL_4BIT_TO_RGB32_RED_BLOCK) & 0xF) << CHANNEL_4BIT_TO_RGB64_RED_BLOCK;
        rgb64 |= (long) ((rgb32 >>> CHANNEL_4BIT_TO_RGB32_GREEN_BLOCK) & 0xF) << CHANNEL_4BIT_TO_RGB64_GREEN_BLOCK;
        rgb64 |= (long) ((rgb32 >>> CHANNEL_4BIT_TO_RGB32_BLUE_BLOCK) & 0xF) << CHANNEL_4BIT_TO_RGB64_BLUE_BLOCK;
        rgb64 |= (long) ((rgb32 >>> CHANNEL_4BIT_TO_RGB32_RED_SKY) & 0xF) << CHANNEL_4BIT_TO_RGB64_RED_SKY;
        rgb64 |= (long) ((rgb32 >>> CHANNEL_4BIT_TO_RGB32_GREEN_SKY) & 0xF) << CHANNEL_4BIT_TO_RGB64_GREEN_SKY;
        rgb64 |= (long) ((rgb32 >>> CHANNEL_4BIT_TO_RGB32_BLUE_SKY) & 0xF) << CHANNEL_4BIT_TO_RGB64_BLUE_SKY;
        return rgb64;
    }

    /**
     * Takes the per-channel maximum of the two colors.
     */
    @StableAPI.Expose
    public static long RGB64Max(long a, long b) {
        //Optimized algorithm, given that internally we have a tight sequence of identical elements.
        //Hopefully, the JVM converts this into SIMD
        long result = 0L;
        for (int i = 0; i <= 40; i += 8) {
            final long mask = 0xFFL << i;
            final long cA = a & mask;
            final long cB = b & mask;
            result |= Math.max(cA, cB);
        }
        return result;
    }

    @StableAPI.Expose(since = "1.5.0")
    public static long RGB64ForEach(long x, Int2IntFunction f) {
        long result = 0L;
        for (int i = 0; i <= 40; i += 8) {
            final long c = (x >>> i) & 0xFFL;
            result |= (f.applyAsInt((int) c) & 0xFFL) << i;
        }
        return result;
    }

    @StableAPI.Expose
    public static long RGB64MixAOBrightness(long a, long b, double aMul, double bMul) {
        long result = 0;
        for (int i = 0; i <= 40; i += 8) {
            result |= RGB64LerpChannel(a, b, aMul, bMul, i);
        }
        return result;
    }

    @StableAPI.Expose
    public static long RGB64MixAOBrightness(long TL, long BL, long BR, long TR, double lerpTB, double lerpLR) {
        final double lTL = (1.0 - lerpTB) * (1.0 - lerpLR);
        final double lTR = (1.0 - lerpTB) * lerpLR;
        final double lBL = lerpTB * (1.0 - lerpLR);
        final double lBR = lerpTB * lerpLR;
        return RGB64MixAOBrightness(TL, TR, BL, BR, lTL, lTR, lBL, lBR);
    }

    @StableAPI.Expose
    public static long RGB64MixAOBrightness(long a, long b, long c, long d, double aMul, double bMul, double cMul, double dMul) {
        long result = 0;
        for (int i = 0; i <= 40; i += 8)
            result |= RGB64MixAoBrightnessChannel(a, b, c, d, aMul, bMul, cMul, dMul, i);
        return result;
    }

    /**
     * Takes the average of the passed in brightnesses. Faster than doing it by hand through the API.
     */
    @StableAPI.Expose
    public static long RGB64Average(long a, long b, boolean ignoreZero) {
        long result = 0;
        for (int i = 0; i <= 40; i += 8) {
            result |= RGB64AverageChannel(a, b, i, ignoreZero);
        }
        return result;
    }

    /**
     * Takes the average of the passed in brightnesses. Faster than doing it by hand through the API.
     */
    @StableAPI.Expose
    public static long RGB64Average(long a, long b, long c, long d, boolean ignoreZero) {
        long result = 0;
        if (ignoreZero) {
            for (int i = 0; i <= 40; i += 8) {
                result |= RGB64AverageChannelIgnoreZero(a, b, c, d, i);
            }
        } else {
            for (int i = 0; i <= 40; i += 8) {
                result |= RGB64AverageChannel(a, b, c, d, i);
            }
        }
        return result;
    }

    /**
     * Takes the average of the passed in brightnesses. Faster than doing it by hand through the API.
     */
    @StableAPI.Expose
    public static long RGB64Average(long[] values, int n, boolean ignoreZero) {
        long result = 0;
        for (int i = 0; i <= 40; i += 8) {
            result |= RGB64AverageChannel(values, n, i, ignoreZero);
        }
        return result;
    }

    public static long RGB64Mul(long x, float f) {
        long result = 0;
        for (int i = 0; i <= 40; i += 8) {
            result |= RGB64MulChannel(x, i, f);
        }
        return result;
    }

    //endregion

    //region tess

    @StableAPI.Expose
    public static int tessFromRGB64Red(long rgb64) {
        return tessFromVanilla(vanillaFromCompressed((int) ((rgb64 >>> RGB64_RED_OFFSET) & COMPRESSED_MASK)));
    }

    @StableAPI.Expose
    public static int tessFromRGB64Green(long rgb64) {
        return tessFromVanilla(vanillaFromCompressed((int) ((rgb64 >>> RGB64_GREEN_OFFSET) & COMPRESSED_MASK)));
    }

    @StableAPI.Expose
    public static int tessFromRGB64Blue(long rgb64) {
        return tessFromVanilla(vanillaFromCompressed((int) ((rgb64 >>> RGB64_BLUE_OFFSET) & COMPRESSED_MASK)));
    }

    @StableAPI.Expose
    public static int tessFromVanilla(int vanilla) {
        final short lightMapBlock = remapToShort(vanilla & 0xFF);
        final short lightMapSky = remapToShort((vanilla >> 16) & 0xFF);
        return ((int) lightMapBlock & 0xFFFF) | ((int) lightMapSky << 16);
    }

    //endregion

    //region cookie

    /**
     * Convenience method, identical to {@link ClientColorHelper#RGB64Max(long, long)}, but automatically decodes and
     * encodes the input/output into cookies.
     */
    @StableAPI.Expose
    public static int cookieMax(int cookieA, int cookieB) {
        return CookieMonster.cookieFromRGB64(RGB64Max(CookieMonster.RGB64FromCookie(cookieA), CookieMonster.RGB64FromCookie(cookieB)));
    }

    @StableAPI.Expose
    public static int cookieMixAOBrightness(int cookieTL, int cookieBL, int cookieBR, int cookieTR, double lerpTB, double lerpLR) {
        final long TL = CookieMonster.RGB64FromCookie(cookieTL);
        final long BL = CookieMonster.RGB64FromCookie(cookieBL);
        final long BR = CookieMonster.RGB64FromCookie(cookieBR);
        final long TR = CookieMonster.RGB64FromCookie(cookieTR);
        return CookieMonster.cookieFromRGB64(RGB64MixAOBrightness(TL, BL, BR, TR, lerpTB, lerpLR));
    }

    @StableAPI.Expose
    public static int cookieMixAOBrightness(int cookieA, int cookieB, int cookieC, int cookieD, double aMul, double bMul, double cMul, double dMul) {
        final long a = CookieMonster.RGB64FromCookie(cookieA);
        final long b = CookieMonster.RGB64FromCookie(cookieB);
        final long c = CookieMonster.RGB64FromCookie(cookieC);
        final long d = CookieMonster.RGB64FromCookie(cookieD);
        return CookieMonster.cookieFromRGB64(RGB64MixAOBrightness(a, b, c, d, aMul, bMul, cMul, dMul));
    }

    @StableAPI.Expose
    public static int cookieMixAOBrightness(int cookieA, int cookieB, double aMul, double bMul) {
        final long a = CookieMonster.RGB64FromCookie(cookieA);
        final long b = CookieMonster.RGB64FromCookie(cookieB);
        return CookieMonster.cookieFromRGB64(RGB64MixAOBrightness(a, b, aMul, bMul));
    }

    @StableAPI.Expose
    public static int cookieAverage(boolean ignoreZero, int cookieA, int cookieB) {
        final long a = CookieMonster.RGB64FromCookie(cookieA);
        final long b = CookieMonster.RGB64FromCookie(cookieB);
        return CookieMonster.cookieFromRGB64(RGB64Average(a, b, ignoreZero));
    }

    @StableAPI.Expose
    public static int cookieAverage(boolean ignoreZero, int cookieA, int cookieB, int cookieC, int cookieD) {
        final long a = CookieMonster.RGB64FromCookie(cookieA);
        final long b = CookieMonster.RGB64FromCookie(cookieB);
        final long c = CookieMonster.RGB64FromCookie(cookieC);
        final long d = CookieMonster.RGB64FromCookie(cookieD);
        return CookieMonster.cookieFromRGB64(RGB64Average(a, b, c, d, ignoreZero));
    }

    @StableAPI.Expose
    public static int cookieAverage(boolean ignoreZero, int... values) {
        long[] rgb64 = new long[values.length];
        for (int i = 0; i < values.length; i++) {
            rgb64[i] = CookieMonster.RGB64FromCookie(values[i]);
        }
        return CookieMonster.cookieFromRGB64(RGB64Average(rgb64, rgb64.length, ignoreZero));
    }

    public static int cookieMul(int cookie, float f) {
        return CookieMonster.cookieFromRGB64(RGB64Mul(CookieMonster.RGB64FromCookie(cookie), f));
    }

    //endregion

    // private

    //region internals

    private static short remapToShort(int n) {
        n = Math.min(n, 240);
        final float normalized = n / 240F;
        return (short) Math.round(normalized * (Short.MAX_VALUE - Short.MIN_VALUE) + Short.MIN_VALUE);
    }

    private static long RGB64LerpChannel(long a, long b, double aMul, double bMul, int offset) {
        final double resultA = (double) unit(a, offset) * aMul;
        final double resultB = (double) unit(b, offset) * bMul;
        return (((long) (resultA + resultB)) & 0xFF) << offset;
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

    private static long RGB64AverageChannel(long[] rgb64Values, int n, int shift, boolean ignoreZero) {
        int count = 0;
        float light = 0;
        for (int i = 0; i < n; i++) {
            final long rgb64 = rgb64Values[i];
            final int value = unit(rgb64, shift);
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

    private static long RGB64MulChannel(long x, int shift, float f) {
        float res = unit(x, shift) * f;
        return (long)((int)res & 0xFF) << shift;
    }

    private static int max3(int red, int green, int blue, int mask) {
        return Math.max(Math.max(red & mask, green & mask), blue & mask);
    }

    private static int compressedFromVanilla(int vanilla) {
        return ((vanilla & VANILLA_SKY_MASK) >>> 8) | (vanilla & VANILLA_BLOCK_MASK);
    }

    private static int vanillaFromCompressed(int compressed) {
        return ((compressed & COMPRESSED_SKY_MASK) << 8) | (compressed & COMPRESSED_BLOCK_MASK);
    }

    private static long RGB64MixAoBrightnessChannel(long a, long b, long c, long d, double aMul, double bMul, double cMul, double dMul, int channel) {
        final double fA = unit(a, channel) * aMul;
        final double fB = unit(b, channel) * bMul;
        final double fC = unit(c, channel) * cMul;
        final double fD = unit(d, channel) * dMul;
        return (long) ((int) (fA + fB + fC + fD) & 0xFF) << channel;
    }

    private static int unit(long val, int channel) {
        return (int) ((val >>> channel) & 0xFF);
    }

    //endregion
}
