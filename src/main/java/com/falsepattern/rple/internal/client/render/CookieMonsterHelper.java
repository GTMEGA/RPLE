/*
 * Copyright (c) 2023 FalsePattern, Ven
 * This work is licensed under the Creative Commons Attribution-NonCommercial-NoDerivatives 4.0 International License.
 * To view a copy of this license, visit http://creativecommons.org/licenses/by-nc-nd/4.0/
 * or send a letter to Creative Commons, PO Box 1866, Mountain View, CA 94042, USA.
 */

package com.falsepattern.rple.internal.client.render;

import lombok.experimental.UtilityClass;
import lombok.val;

@UtilityClass
public final class CookieMonsterHelper {
    /**
     * Convenience method, identical to {@link TessellatorBrightnessHelper#packedMax(long, long)}, but automatically decodes and
     * encodes the input/output into cookies.
     */
    public static int packedMax(int cookieA, int cookieB) {
        return CookieMonster.packedLongToCookie(TessellatorBrightnessHelper.packedMax(CookieMonster.cookieToPackedLong(cookieA), CookieMonster.cookieToPackedLong(cookieB)));
    }

    public static int mixAOBrightness(int brightTL, int brightBL, int brightBR, int brightTR, double lerpTB, double lerpLR) {
        val packedTL = CookieMonster.cookieToPackedLong(brightTL);
        val packedBL = CookieMonster.cookieToPackedLong(brightBL);
        val packedBR = CookieMonster.cookieToPackedLong(brightBR);
        val packedTR = CookieMonster.cookieToPackedLong(brightTR);
        return CookieMonster.packedLongToCookie(TessellatorBrightnessHelper.mixAOBrightness(packedTL, packedBL, packedBR, packedTR, lerpTB, lerpLR));
    }

    public static int mixAOBrightness(int a, int b, int c, int d, double aMul, double bMul, double cMul, double dMul) {
        val packedA = CookieMonster.cookieToPackedLong(a);
        val packedB = CookieMonster.cookieToPackedLong(b);
        val packedC = CookieMonster.cookieToPackedLong(c);
        val packedD = CookieMonster.cookieToPackedLong(d);
        return CookieMonster.packedLongToCookie(TessellatorBrightnessHelper.mixAOBrightness(packedA, packedB, packedC, packedD, aMul, bMul, cMul, dMul));
    }

    public static int mixAOBrightness(int a, int b, double aMul, double bMul) {
        val packedA = CookieMonster.cookieToPackedLong(a);
        val packedB = CookieMonster.cookieToPackedLong(b);
        return CookieMonster.packedLongToCookie(TessellatorBrightnessHelper.mixAOBrightness(packedA, packedB, aMul, bMul));
    }

    public static int average(boolean ignoreZero, int a, int b) {
        val packedA = CookieMonster.cookieToPackedLong(a);
        val packedB = CookieMonster.cookieToPackedLong(b);
        return CookieMonster.packedLongToCookie(TessellatorBrightnessHelper.packedAverage(packedA, packedB, ignoreZero));
    }

    public static int average(boolean ignoreZero, int a, int b, int c, int d) {
        val packedA = CookieMonster.cookieToPackedLong(a);
        val packedB = CookieMonster.cookieToPackedLong(b);
        val packedC = CookieMonster.cookieToPackedLong(c);
        val packedD = CookieMonster.cookieToPackedLong(d);
        return CookieMonster.packedLongToCookie(TessellatorBrightnessHelper.packedAverage(packedA, packedB, packedC, packedD, ignoreZero));
    }

    public static int average(boolean ignoreZero, int... values) {
        long[] packed = new long[values.length];
        for (int i = 0; i < values.length; i++) {
            packed[i] = CookieMonster.cookieToPackedLong(values[i]);
        }
        return CookieMonster.packedLongToCookie(TessellatorBrightnessHelper.packedAverage(packed, packed.length, ignoreZero));
    }
}
