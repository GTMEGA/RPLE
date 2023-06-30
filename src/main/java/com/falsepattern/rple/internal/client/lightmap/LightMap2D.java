/*
 * Copyright (c) 2023 FalsePattern, Ven
 * This work is licensed under the Creative Commons Attribution-NonCommercial-NoDerivatives 4.0 International License.
 * To view a copy of this license, visit http://creativecommons.org/licenses/by-nc-nd/4.0/
 * or send a letter to Creative Commons, PO Box 1866, Mountain View, CA 94042, USA.
 */

package com.falsepattern.rple.internal.client.lightmap;

import lombok.Getter;
import lombok.experimental.Accessors;
import lombok.val;
import lombok.var;

import static com.falsepattern.rple.internal.Common.LIGHT_MAP_1D_SIZE;
import static com.falsepattern.rple.internal.Common.LIGHT_MAP_2D_SIZE;
import static net.minecraft.client.Minecraft.getMinecraft;

@Getter
@Accessors(fluent = true, chain = true)
@SuppressWarnings("MismatchedReadAndWriteOfArray")
public final class LightMap2D {
    private final LightMap1D blockLightMap = new LightMap1D();
    private final LightMap1D skyLightMap = new LightMap1D();

    private final int[] lightMapRGBData = new int[LIGHT_MAP_2D_SIZE];

    public void mixLightMaps() {
        val blockLightMapRed = blockLightMap.lightMapRedData();
        val blockLightMapGreen = blockLightMap.lightMapGreenData();
        val blockLightMapBlue = blockLightMap.lightMapBlueData();

        val skyLightMapRed = skyLightMap.lightMapRedData();
        val skyLightMapGreen = skyLightMap.lightMapGreenData();
        val skyLightMapBlue = skyLightMap.lightMapBlueData();

        val gamma = getMinecraft().gameSettings.gammaSetting;

        for (var index = 0; index < LIGHT_MAP_2D_SIZE; index++) {
            val blockIndex = index % LIGHT_MAP_1D_SIZE;
            val skyIndex = index / LIGHT_MAP_1D_SIZE;

            var red = blockLightMapRed[blockIndex] + skyLightMapRed[skyIndex];
            var green = blockLightMapGreen[blockIndex] + skyLightMapGreen[skyIndex];
            var blue = blockLightMapBlue[blockIndex] + skyLightMapBlue[skyIndex];

            red = gammaCorrect(red, gamma);
            green = gammaCorrect(green, gamma);
            blue = gammaCorrect(blue, gamma);

            lightMapRGBData[index] = colorToInt(red, green, blue);
        }
    }

    private static float gammaCorrect(float color, float gamma) {
        color = clamp(color);

        var colorPreGamma = 1F - color;
        colorPreGamma = 1F - (colorPreGamma * colorPreGamma * colorPreGamma * colorPreGamma);

        color = (color * (1F - gamma)) + (colorPreGamma * gamma);
        color = (color * 0.96F) + 0.03F;

        return color;
    }

    private static float clamp(float value) {
        return Math.max(Math.min(value, 1F), 0F);
    }

    private static int colorToInt(float red, float green, float blue) {
        val redByte = colorToByte(red) << 16;
        val greenByte = colorToByte(green) << 8;
        val blueByte = colorToByte(blue);

        return 0xFF000000 | redByte | greenByte | blueByte;
    }

    private static int colorToByte(float color) {
        return Math.round(color * 255F) & 0xFF;
    }
}
