/*
 * Copyright (c) 2023 FalsePattern
 * This work is licensed under the Creative Commons Attribution-NonCommercial-NoDerivatives 4.0 International License.
 * To view a copy of this license, visit http://creativecommons.org/licenses/by-nc-nd/4.0/
 * or send a letter to Creative Commons, PO Box 1866, Mountain View, CA 94042, USA.
 *
 */

package com.falsepattern.rple.internal.color;

import lombok.val;

public class CookieWrappers {
    /**
     * Convenience method, identical to {@link BrightnessUtil#packedMax(long, long)}, but automatically decodes and
     * encodes the input/output into cookies.
     */
    public static int packedMax(int cookieA, int cookieB) {
        return CookieMonster.packedLongToCookie(BrightnessUtil.packedMax(CookieMonster.cookieToPackedLong(cookieA), CookieMonster.cookieToPackedLong(cookieB)));
    }

    public static int mixAOBrightness(int a, int b, int c, int d, double aMul, double bMul, double cMul, double dMul) {
        val packedA = CookieMonster.cookieToPackedLong(a);
        val packedB = CookieMonster.cookieToPackedLong(b);
        val packedC = CookieMonster.cookieToPackedLong(c);
        val packedD = CookieMonster.cookieToPackedLong(d);
        return CookieMonster.packedLongToCookie(BrightnessUtil.mixAOBrightness(packedA, packedB, packedC, packedD, aMul, bMul, cMul, dMul));
    }

    public static int average(boolean ignoreZero, int a, int b) {
        val packedA = CookieMonster.cookieToPackedLong(a);
        val packedB = CookieMonster.cookieToPackedLong(b);
        return CookieMonster.packedLongToCookie(BrightnessUtil.packedAverage(packedA, packedB, ignoreZero));
    }

    public static int average(boolean ignoreZero, int a, int b, int c, int d) {
        val packedA = CookieMonster.cookieToPackedLong(a);
        val packedB = CookieMonster.cookieToPackedLong(b);
        val packedC = CookieMonster.cookieToPackedLong(c);
        val packedD = CookieMonster.cookieToPackedLong(d);
        return CookieMonster.packedLongToCookie(BrightnessUtil.packedAverage(packedA, packedB, packedC, packedD, ignoreZero));
    }

    public static int average(boolean ignoreZero, int... values) {
        long[] packed = new long[values.length];
        for (int i = 0; i < values.length; i++) {
            packed[i] = CookieMonster.cookieToPackedLong(values[i]);
        }
        return CookieMonster.packedLongToCookie(BrightnessUtil.packedAverage(packed, packed.length, ignoreZero));
    }
}
