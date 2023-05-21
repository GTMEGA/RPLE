package com.falsepattern.rple.internal.mixin.extension;

public final class EnderIOConduitsBrightnessHolder {
    private static final ThreadLocal<Integer> lastCookieBrightness;

    static {
        lastCookieBrightness = new ThreadLocal<>();
        lastCookieBrightness.set(0);
    }

    public static void setCookieBrightness(int cookieBrightness) {
        lastCookieBrightness.set(cookieBrightness);
    }

    public static int getCookieBrightness() {
        return lastCookieBrightness.get();
    }
}
