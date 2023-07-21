package com.falsepattern.rple.internal.mixin.helper;

import lombok.experimental.UtilityClass;

@UtilityClass
public final class EnderIOHelper {
    private static int CACHED_TESSELLATOR_BRIGHTNESS = 0;

    public static void cacheTessellatorBrightness(int tessellatorBrightness) {
        CACHED_TESSELLATOR_BRIGHTNESS = tessellatorBrightness;
    }

    public static int loadTessellatorBrightness() {
        return CACHED_TESSELLATOR_BRIGHTNESS;
    }
}
