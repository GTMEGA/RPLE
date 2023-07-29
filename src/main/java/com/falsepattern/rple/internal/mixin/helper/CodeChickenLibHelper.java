/*
 * Copyright (c) 2023 FalsePattern, Ven
 * This work is licensed under the Creative Commons Attribution-NonCommercial-NoDerivatives 4.0 International License.
 * To view a copy of this license, visit http://creativecommons.org/licenses/by-nc-nd/4.0/
 * or send a letter to Creative Commons, PO Box 1866, Mountain View, CA 94042, USA.
 */

package com.falsepattern.rple.internal.mixin.helper;

import com.falsepattern.rple.internal.client.lightmap.LightMapConstants;
import com.falsepattern.rple.internal.client.render.TessellatorBrightnessHelper;
import lombok.experimental.UtilityClass;
import lombok.val;
import net.minecraft.client.renderer.OpenGlHelper;

@UtilityClass
public final class CodeChickenLibHelper {
    private static long LAST_PACKED_BRIGHTNESS = 0;

    public static void setLightMapTextureCoordsPacked(long packedBrightness) {
        LAST_PACKED_BRIGHTNESS = packedBrightness;

        val redBrightness = TessellatorBrightnessHelper.getBrightnessRed(packedBrightness);
        val greenBrightness = TessellatorBrightnessHelper.getBrightnessGreen(packedBrightness);
        val blueBrightness = TessellatorBrightnessHelper.getBrightnessBlue(packedBrightness);
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

    public static long lastPackedBrightness() {
        return LAST_PACKED_BRIGHTNESS;
    }
}
