package com.falsepattern.rple.api.client;

import com.falsepattern.rple.internal.client.render.CookieMonster;
import lombok.val;

import static com.falsepattern.rple.api.client.RPLEPackedBrightnessUtil.*;

@SuppressWarnings("unused")
public final class RPLEBrightnessCookieUtil {
    private RPLEBrightnessCookieUtil() {
        throw new UnsupportedOperationException("This is a utility class and cannot be instantiated");
    }

    public static int brightnessCookieFromPackedBrightness(long packedBrightness) {
        return CookieMonster.packedLongToCookie(packedBrightness);
    }

    public static long packedBrightnessFromBrightnessCookie(int brightnessCookie) {
        return CookieMonster.cookieToPackedLong(brightnessCookie);
    }

    public static boolean isBrightnessCookie(int potentialBrightnessCookie) {
        return CookieMonster.inspectValue(potentialBrightnessCookie) == CookieMonster.IntType.COOKIE;
    }

    public static int minBrightnessCookie(int brightnessCookieA, int brightnessCookieB) {
        val packedBrightnessA = CookieMonster.cookieToPackedLong(brightnessCookieA);
        val packedBrightnessB = CookieMonster.cookieToPackedLong(brightnessCookieB);
        val minPackedBrightness = minPackedBrightness(packedBrightnessA, packedBrightnessB);
        return CookieMonster.packedLongToCookie(minPackedBrightness);
    }

    public static int maxBrightnessCookie(int brightnessCookieA, int brightnessCookieB) {
        val packedBrightnessA = CookieMonster.cookieToPackedLong(brightnessCookieA);
        val packedBrightnessB = CookieMonster.cookieToPackedLong(brightnessCookieB);
        val maxPackedBrightness = maxPackedBrightness(packedBrightnessA, packedBrightnessB);
        return CookieMonster.packedLongToCookie(maxPackedBrightness);
    }

    public static int avgBrightnessCookie(int brightnessCookieA, int brightnessCookieB) {
        val packedBrightnessA = CookieMonster.cookieToPackedLong(brightnessCookieA);
        val packedBrightnessB = CookieMonster.cookieToPackedLong(brightnessCookieB);
        val avgPackedBrightness = avgPackedBrightness(packedBrightnessA, packedBrightnessB);
        return CookieMonster.packedLongToCookie(avgPackedBrightness);
    }

    public static int mixAOBrightnessCookie(int brightnessCookieA, int brightnessCookieB, double multA, double multB) {
        val packedBrightnessA = CookieMonster.cookieToPackedLong(brightnessCookieA);
        val packedBrightnessB = CookieMonster.cookieToPackedLong(brightnessCookieB);
        val mixedAOPackedBrightness = mixAOPackedBrightness(packedBrightnessA, packedBrightnessB, multA, multB);
        return CookieMonster.packedLongToCookie(mixedAOPackedBrightness);
    }

    public static int mixAOBrightnessCookie(int brightnessCookieAC,
                                            int brightnessCookieBC,
                                            int brightnessCookieBD,
                                            int brightnessCookieAD,
                                            double alphaAB,
                                            double alphaCD) {
        val packedBrightnessAC = CookieMonster.cookieToPackedLong(brightnessCookieAC);
        val packedBrightnessBC = CookieMonster.cookieToPackedLong(brightnessCookieBC);
        val packedBrightnessBD = CookieMonster.cookieToPackedLong(brightnessCookieBD);
        val packedBrightnessAD = CookieMonster.cookieToPackedLong(brightnessCookieAD);
        val mixedAOPackedBrightness = mixAOPackedBrightness(packedBrightnessAC,
                                                            packedBrightnessBC,
                                                            packedBrightnessBD,
                                                            packedBrightnessAD,
                                                            alphaAB,
                                                            alphaCD);
        return CookieMonster.packedLongToCookie(mixedAOPackedBrightness);
    }

    public static int mixAOBrightnessCookie(int brightnessCookieA,
                                            int brightnessCookieB,
                                            int brightnessCookieC,
                                            int brightnessCookieD,
                                            double multA,
                                            double multB,
                                            double multC,
                                            double multD) {
        val packedBrightnessA = CookieMonster.cookieToPackedLong(brightnessCookieA);
        val packedBrightnessB = CookieMonster.cookieToPackedLong(brightnessCookieB);
        val packedBrightnessC = CookieMonster.cookieToPackedLong(brightnessCookieC);
        val packedBrightnessD = CookieMonster.cookieToPackedLong(brightnessCookieD);
        val mixedAOPackedBrightness = mixAOPackedBrightness(packedBrightnessA,
                                                            packedBrightnessB,
                                                            packedBrightnessC,
                                                            packedBrightnessD,
                                                            multA,
                                                            multB,
                                                            multC,
                                                            multD);
        return CookieMonster.packedLongToCookie(mixedAOPackedBrightness);
    }
}
