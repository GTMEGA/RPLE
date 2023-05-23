/*
 * Copyright (c) 2023 FalsePattern
 * This work is licensed under the Creative Commons Attribution-NonCommercial-NoDerivatives 4.0 International License.
 * To view a copy of this license, visit http://creativecommons.org/licenses/by-nc-nd/4.0/
 * or send a letter to Creative Commons, PO Box 1866, Mountain View, CA 94042, USA.
 *
 */

package com.falsepattern.rple.internal.lightmap.builtin.base;

import com.falsepattern.rple.api.lightmap.LightMapBase;
import com.falsepattern.rple.api.lightmap.LightMapStrip;
import com.falsepattern.rple.internal.Common;
import lombok.val;

import lombok.var;
import net.minecraft.client.Minecraft;

import static com.falsepattern.rple.api.lightmap.LightMapStrip.LIGHT_MAP_STRIP_LENGTH;

public class VanillaLightMapBase implements LightMapBase {
    private static final int END_DIMENSION_ID = 1;

    @Override
    public boolean generateBlockLightMapBase(LightMapStrip output, float partialTick) {
        val worldProvider = Minecraft.getMinecraft().theWorld.provider;

        for (var i = 0; i < LIGHT_MAP_STRIP_LENGTH; i++) {
            val brightness = worldProvider.lightBrightnessTable[i];
            output.setLightMap(i, brightness);
        }

        return true;
    }

    @Override
    public boolean generateSkyLightMapBase(LightMapStrip output, float partialTick) {
        val world = Minecraft.getMinecraft().theWorld;
        val worldProvider = world.provider;

        if (worldProvider.dimensionId == END_DIMENSION_ID) {
            output.fillLightMap(0.22F);
            return true;
        }

        val brightness = world.getSunBrightness(1F);
        for (var i = 0; i < LIGHT_MAP_STRIP_LENGTH; i++) {
            var blue = 0F;

            if (world.lastLightningBolt > 0) {
                blue = world.provider.lightBrightnessTable[i];
            } else {
                blue = world.provider.lightBrightnessTable[i] * (brightness * 0.95F + 0.05F);
            }

            val red = blue * (brightness * 0.65F + 0.35F);
            val green = blue * (brightness * 0.65F + 0.35F);

            output.setLightMapRGB(i, red, green, blue);
        }

        return true;
    }
}
