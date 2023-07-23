package com.falsepattern.rple.api.client;

import com.falsepattern.rple.internal.client.render.CookieMonster;
import lombok.val;

import static com.falsepattern.rple.api.client.RPLEPackedBrightnessUtil.*;

@SuppressWarnings("unused")
public final class RPLECookieUtil {
    private RPLECookieUtil() {
        throw new UnsupportedOperationException("This is a utility class and cannot be instantiated");
    }

    public static int brightnessCookieFromPacked(long packed) {
        return CookieMonster.packedLongToCookie(packed);
    }

    public static long packedBrightnessFromCookie(int cookie) {
        return CookieMonster.cookieToPackedLong(cookie);
    }

    public static boolean isBrightnessCookie(int potentialCookie) {
        return CookieMonster.inspectValue(potentialCookie) == CookieMonster.IntType.COOKIE;
    }

    public static int minBrightnessCookie(int cookieA, int cookieB) {
        val packedA = CookieMonster.cookieToPackedLong(cookieA);
        val packedB = CookieMonster.cookieToPackedLong(cookieB);
        val minPacked = minPackedBrightness(packedA, packedB);
        return CookieMonster.packedLongToCookie(minPacked);
    }

    public static int maxBrightnessCookie(int cookieA, int cookieB) {
        val packedA = CookieMonster.cookieToPackedLong(cookieA);
        val packedB = CookieMonster.cookieToPackedLong(cookieB);
        val maxPacked = maxPackedBrightness(packedA, packedB);
        return CookieMonster.packedLongToCookie(maxPacked);
    }

    public static int avgBrightnessCookie(int cookieA, int cookieB) {
        val packedA = CookieMonster.cookieToPackedLong(cookieA);
        val packedB = CookieMonster.cookieToPackedLong(cookieB);
        val avgPacked = avgPackedBrightness(packedA, packedB);
        return CookieMonster.packedLongToCookie(avgPacked);
    }

    public static int mixAOBrightnessCookie(int cookieA, int cookieB, double multA, double multB) {
        val packedA = CookieMonster.cookieToPackedLong(cookieA);
        val packedB = CookieMonster.cookieToPackedLong(cookieB);
        val mixedAOPacked = mixAOPackedBrightness(packedA, packedB, multA, multB);
        return CookieMonster.packedLongToCookie(mixedAOPacked);
    }

    public static int mixAOBrightnessCookie(int cookieAC,
                                            int cookieBC,
                                            int cookieBD,
                                            int cookieAD,
                                            double alphaAB,
                                            double alphaCD) {
        val packedAC = CookieMonster.cookieToPackedLong(cookieAC);
        val packedBC = CookieMonster.cookieToPackedLong(cookieBC);
        val packedBD = CookieMonster.cookieToPackedLong(cookieBD);
        val packedAD = CookieMonster.cookieToPackedLong(cookieAD);
        val mixedAOPacked = mixAOPackedBrightness(packedAC, packedBC, packedBD, packedAD, alphaAB, alphaCD);
        return CookieMonster.packedLongToCookie(mixedAOPacked);
    }

    public static int mixAOBrightnessCookie(int cookieA,
                                            int cookieB,
                                            int cookieC,
                                            int cookieD,
                                            double multA,
                                            double multB,
                                            double multC,
                                            double multD) {
        val packedA = CookieMonster.cookieToPackedLong(cookieA);
        val packedB = CookieMonster.cookieToPackedLong(cookieB);
        val packedC = CookieMonster.cookieToPackedLong(cookieC);
        val packedD = CookieMonster.cookieToPackedLong(cookieD);
        val mixedAOPacked = mixAOPackedBrightness(packedA, packedB, packedC, packedD, multA, multB, multC, multD);
        return CookieMonster.packedLongToCookie(mixedAOPacked);
    }
}
