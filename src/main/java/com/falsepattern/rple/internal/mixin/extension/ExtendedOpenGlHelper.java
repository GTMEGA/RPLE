/*
 * Copyright (c) 2023 FalsePattern, Ven
 * This work is licensed under the Creative Commons Attribution-NonCommercial-NoDerivatives 4.0 International License.
 * To view a copy of this license, visit http://creativecommons.org/licenses/by-nc-nd/4.0/
 * or send a letter to Creative Commons, PO Box 1866, Mountain View, CA 94042, USA.
 */

package com.falsepattern.rple.internal.mixin.extension;

import com.falsepattern.rple.api.client.ClientColorHelper;
import com.falsepattern.rple.internal.Compat;
import com.falsepattern.rple.internal.client.lightmap.LightMapConstants;
import lombok.experimental.UtilityClass;
import lombok.val;
import net.minecraft.client.renderer.OpenGlHelper;

@UtilityClass
// TODO: [ENTITY] Expose this for users to set colors when rendering entities etc
public final class ExtendedOpenGlHelper {
    private static long LAST_PACKED_BRIGHTNESS = 0;

    public static boolean BYPASS = false;

    public static void setPackedBrightnessFromMonochrome(int monochrome) {
        LAST_PACKED_BRIGHTNESS = ClientColorHelper.RGB64FromVanillaMonochrome(monochrome);
    }

    public static void setLightMapTextureCoordsPacked(long packedBrightness) {
        BYPASS = true;
        LAST_PACKED_BRIGHTNESS = packedBrightness;

        val redBrightness = ClientColorHelper.vanillaFromRGB64Red(packedBrightness);
        val greenBrightness = ClientColorHelper.vanillaFromRGB64Green(packedBrightness);
        val blueBrightness = ClientColorHelper.vanillaFromRGB64Blue(packedBrightness);

        if (Compat.shadersEnabled()) {
            OpenGlHelper.setLightmapTextureCoords(LightMapConstants.R_LIGHT_MAP_SHADER_TEXTURE_COORDS_BINDING,
                                                  redBrightness & 0xFFFF,
                                                  redBrightness >>> 16);
            OpenGlHelper.setLightmapTextureCoords(LightMapConstants.G_LIGHT_MAP_SHADER_TEXTURE_COORDS_BINDING,
                                                  greenBrightness & 0xFFFF,
                                                  greenBrightness >>> 16);
            OpenGlHelper.setLightmapTextureCoords(LightMapConstants.B_LIGHT_MAP_SHADER_TEXTURE_COORDS_BINDING,
                                                  blueBrightness & 0xFFFF,
                                                  blueBrightness >>> 16);
        } else {
            OpenGlHelper.setLightmapTextureCoords(LightMapConstants.R_LIGHT_MAP_FIXED_TEXTURE_UNIT_BINDING,
                                                  redBrightness & 0xFFFF,
                                                  redBrightness >>> 16);
            OpenGlHelper.setLightmapTextureCoords(LightMapConstants.G_LIGHT_MAP_FIXED_TEXTURE_UNIT_BINDING,
                                                  greenBrightness & 0xFFFF,
                                                  greenBrightness >>> 16);
            OpenGlHelper.setLightmapTextureCoords(LightMapConstants.B_LIGHT_MAP_FIXED_TEXTURE_UNIT_BINDING,
                                                  blueBrightness & 0xFFFF,
                                                  blueBrightness >>> 16);
        }
        BYPASS = false;
    }

    public static long lastPackedBrightness() {
        return LAST_PACKED_BRIGHTNESS;
    }
}
