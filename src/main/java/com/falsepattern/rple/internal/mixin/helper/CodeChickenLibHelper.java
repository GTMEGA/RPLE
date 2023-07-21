/*
 * Copyright (c) 2023 FalsePattern, Ven
 * This work is licensed under the Creative Commons Attribution-NonCommercial-NoDerivatives 4.0 International License.
 * To view a copy of this license, visit http://creativecommons.org/licenses/by-nc-nd/4.0/
 * or send a letter to Creative Commons, PO Box 1866, Mountain View, CA 94042, USA.
 */

package com.falsepattern.rple.internal.mixin.helper;

import com.falsepattern.rple.internal.Common;
import com.falsepattern.rple.internal.common.helper.BrightnessUtil;
import lombok.experimental.UtilityClass;
import lombok.val;
import net.minecraft.client.renderer.OpenGlHelper;

@UtilityClass
public final class CodeChickenLibHelper {
    private static long LAST_PACKED_BRIGHTNESS = 0;

    public static void setLightMapTextureCoordsPacked(long packedBrightness) {
        LAST_PACKED_BRIGHTNESS = packedBrightness;

        val redBrightness = BrightnessUtil.getBrightnessRed(packedBrightness);
        val greenBrightness = BrightnessUtil.getBrightnessGreen(packedBrightness);
        val blueBrightness = BrightnessUtil.getBrightnessBlue(packedBrightness);
        OpenGlHelper.setLightmapTextureCoords(Common.RED_LIGHT_MAP_TEXTURE_UNIT,
                                              redBrightness & 0xFFFF,
                                              redBrightness >>> 16);
        OpenGlHelper.setLightmapTextureCoords(Common.GREEN_LIGHT_MAP_TEXTURE_UNIT,
                                              greenBrightness & 0xFFFF,
                                              greenBrightness >>> 16);
        OpenGlHelper.setLightmapTextureCoords(Common.BLUE_LIGHT_MAP_TEXTURE_UNIT,
                                              blueBrightness & 0xFFFF,
                                              blueBrightness >>> 16);
    }

    public static long lastPackedBrightness() {
        return LAST_PACKED_BRIGHTNESS;
    }
}
