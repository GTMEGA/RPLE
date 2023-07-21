/*
 * Copyright (c) 2023 FalsePattern, Ven
 * This work is licensed under the Creative Commons Attribution-NonCommercial-NoDerivatives 4.0 International License.
 * To view a copy of this license, visit http://creativecommons.org/licenses/by-nc-nd/4.0/
 * or send a letter to Creative Commons, PO Box 1866, Mountain View, CA 94042, USA.
 */

package com.falsepattern.rple.api;

import com.falsepattern.rple.internal.common.helper.CookieMonster;

@SuppressWarnings("unused")
public final class RPLEBrightnessPackingAPI {
    private RPLEBrightnessPackingAPI() {
        throw new UnsupportedOperationException("This is a utility class and cannot be instantiated");
    }

    public static int tessellatorBrightnessFromPackedBrightness(long packedBrightness) {
        return CookieMonster.packedLongToCookie(packedBrightness);
    }

    public static long packedBrightnessToTessellatorBrightness(int tessellatorBrightness) {
        return CookieMonster.cookieToPackedLong(tessellatorBrightness);
    }
}
