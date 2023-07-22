package com.falsepattern.rple.api.client;

import com.falsepattern.lumina.api.lighting.LightType;
import net.minecraft.client.renderer.Tessellator;
import org.jetbrains.annotations.NotNull;

@SuppressWarnings("unused")
public final class RPLETessellatorAPI {
    private RPLETessellatorAPI() {
        throw new UnsupportedOperationException("This is a utility class and cannot be instantiated");
    }

    public static void setBrightnessWithPackedBrightness(@NotNull Tessellator tess, int packedBrightness) {
    }

    public static void setBrightnessWithRGBLightValues(@NotNull Tessellator tess,
                                                       @NotNull LightType lightType,
                                                       int redLightValue,
                                                       int greenLightValue,
                                                       int blueLightValue) {
    }

    public static void setBrightnessWithRGBLightValues(@NotNull Tessellator tess,
                                                       int redBlockLightValue,
                                                       int greenBlockLightValue,
                                                       int blueBlockLightValue,
                                                       int redSkyLightValue,
                                                       int greenSkyLightValue,
                                                       int blueSkyLightValue) {
    }

    public static void setBrightnessWithLightValue(@NotNull Tessellator tess, @NotNull LightType lightType) {
    }

    public static void setBrightnessWithLightValues(@NotNull Tessellator tess, int blockLightValue, int skyLightValue) {
    }

    public static long getCurrentPackedBrightnessValue(@NotNull Tessellator tess) {
        return 0;
    }
}
