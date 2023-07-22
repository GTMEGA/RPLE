package com.falsepattern.rple.api.client;

import com.falsepattern.lumina.api.lighting.LightType;
import lombok.val;
import net.minecraft.client.renderer.Tessellator;
import org.jetbrains.annotations.NotNull;

import static com.falsepattern.rple.api.client.RPLEPackedBrightnessUtil.*;
import static com.falsepattern.rple.internal.client.render.RPLETessellator.rple$wrapTessellator;

@SuppressWarnings("unused")
public final class RPLETessellatorUtil {
    private RPLETessellatorUtil() {
        throw new UnsupportedOperationException("This is a utility class and cannot be instantiated");
    }

    public static void setBrightnessWithRGBLightValues(@NotNull Tessellator tess,
                                                       @NotNull LightType lightType,
                                                       int redLightValue,
                                                       int greenLightValue,
                                                       int blueLightValue) {
        val packedBrightness = packedBrightnessFromRGBLightValues(lightType,
                                                                  redLightValue,
                                                                  greenLightValue,
                                                                  blueLightValue);
        setBrightnessWithPackedBrightness(tess, packedBrightness);
    }

    public static void setBrightnessWithRGBLightValues(@NotNull Tessellator tess,
                                                       int redBlockLightValue,
                                                       int greenBlockLightValue,
                                                       int blueBlockLightValue,
                                                       int redSkyLightValue,
                                                       int greenSkyLightValue,
                                                       int blueSkyLightValue) {
        val packedBrightness = packedBrightnessFromRGBLightValues(redBlockLightValue,
                                                                  greenBlockLightValue,
                                                                  blueBlockLightValue,
                                                                  redSkyLightValue,
                                                                  greenSkyLightValue,
                                                                  blueSkyLightValue);
        setBrightnessWithPackedBrightness(tess, packedBrightness);
    }

    public static void setBrightnessWithLightValue(@NotNull Tessellator tess,
                                                   @NotNull LightType lightType,
                                                   int lightValue) {
        val packedBrightness = packedBrightnessFromLightValue(lightType, lightValue);
        setBrightnessWithPackedBrightness(tess, packedBrightness);
    }

    public static void setBrightnessWithLightValues(@NotNull Tessellator tess, int blockLightValue, int skyLightValue) {
        val packedBrightness = packedBrightnessFromLightValues(blockLightValue, skyLightValue);
        setBrightnessWithPackedBrightness(tess, packedBrightness);
    }

    public static void setBrightnessWithPackedBrightness(@NotNull Tessellator tess, long packedBrightness) {
        rple$wrapTessellator(tess).rple$packedBrightness(packedBrightness);
    }

    public static long getCurrentPackedBrightnessValue(@NotNull Tessellator tess) {
        return rple$wrapTessellator(tess).rple$packedBrightness();
    }
}
