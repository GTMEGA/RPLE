package com.falsepattern.rple.api.client;

import com.falsepattern.rple.internal.client.render.CookieMonster;

@SuppressWarnings("unused")
public final class RPLEBrightnessCookieAPI {
    private RPLEBrightnessCookieAPI() {
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
        return 0;
    }

    public static int maxBrightnessCookie(int brightnessCookieA, int brightnessCookieB) {
        return 0;
    }

    public static int avgBrightnessCookie(int brightnessCookieA, int brightnessCookieB) {
        return 0;
    }

    public static int mixAOBrightnessCookie(int brightnessCookieA, int brightnessCookieB, double multA, double multB) {
        return 0;
    }

    public static int mixAOBrightnessCookie(int brightnessCookieAC,
                                            int brightnessCookieBC,
                                            int brightnessCookieBD,
                                            int brightnessCookieAD,
                                            double alphaAB,
                                            double alphaCD) {
        return 0;
    }

    public static int mixAOBrightnessCookie(int brightnessCookieA,
                                            int brightnessCookieB,
                                            int brightnessCookieC,
                                            int brightnessCookieD,
                                            double multA,
                                            double multB,
                                            double multC,
                                            double multD) {
        return 0;
    }
}
