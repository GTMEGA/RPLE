/*
 * Copyright (c) 2023 FalsePattern, Ven
 * This work is licensed under the Creative Commons Attribution-NonCommercial-NoDerivatives 4.0 International License.
 * To view a copy of this license, visit http://creativecommons.org/licenses/by-nc-nd/4.0/
 * or send a letter to Creative Commons, PO Box 1866, Mountain View, CA 94042, USA.
 */

package com.falsepattern.rple.internal.mixin.helpers;

import com.falsepattern.rple.internal.Common;
import com.falsepattern.rple.internal.color.BrightnessUtil;
import lombok.val;
import net.minecraft.client.renderer.OpenGlHelper;

public class OpenGlHelperPacked {
    public static long prevLightValuePacked;
    public static void setLightMapTextureCoordsPacked(long packed) {
        OpenGlHelperPacked.prevLightValuePacked = packed;
        val red = BrightnessUtil.getBrightnessRed(packed);
        val green = BrightnessUtil.getBrightnessGreen(packed);
        val blue = BrightnessUtil.getBrightnessBlue(packed);
        OpenGlHelper.setLightmapTextureCoords(Common.RED_LIGHT_MAP_TEXTURE_UNIT, red & 0xFFFF, red >>> 16);
        OpenGlHelper.setLightmapTextureCoords(Common.GREEN_LIGHT_MAP_TEXTURE_UNIT, green & 0xFFFF, green >>> 16);
        OpenGlHelper.setLightmapTextureCoords(Common.BLUE_LIGHT_MAP_TEXTURE_UNIT, blue & 0xFFFF, blue >>> 16);
    }
}
