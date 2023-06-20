/*
 * Copyright (c) 2023 FalsePattern, Ven
 * This work is licensed under the Creative Commons Attribution-NonCommercial-NoDerivatives 4.0 International License.
 * To view a copy of this license, visit http://creativecommons.org/licenses/by-nc-nd/4.0/
 * or send a letter to Creative Commons, PO Box 1866, Mountain View, CA 94042, USA.
 */

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
