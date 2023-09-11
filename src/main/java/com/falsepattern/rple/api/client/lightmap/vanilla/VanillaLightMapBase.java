/*
 * Copyright (c) 2023 FalsePattern, Ven
 * This work is licensed under the Creative Commons Attribution-NonCommercial-NoDerivatives 4.0 International License.
 * To view a copy of this license, visit http://creativecommons.org/licenses/by-nc-nd/4.0/
 * or send a letter to Creative Commons, PO Box 1866, Mountain View, CA 94042, USA.
 */

package com.falsepattern.rple.api.client.lightmap.vanilla;

import com.falsepattern.rple.api.client.lightmap.RPLELightMapBase;
import com.falsepattern.rple.api.client.lightmap.RPLELightMapStrip;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.WorldClient;
import net.minecraft.world.WorldProvider;
import org.jetbrains.annotations.NotNull;

import static com.falsepattern.rple.api.client.lightmap.RPLELightMapStrip.LIGHT_MAP_STRIP_LENGTH;

public class VanillaLightMapBase implements RPLELightMapBase {
    protected static final int END_DIMENSION_ID = 1;

    @Override
    public boolean generateBlockLightMapBase(@NotNull RPLELightMapStrip output, float partialTick) {
        final WorldClient world = Minecraft.getMinecraft().theWorld;
        if (world == null)
            return false;
        final WorldProvider worldProvider = world.provider;
        if (worldProvider == null)
            return false;

        for (int i = 0; i < LIGHT_MAP_STRIP_LENGTH; i++) {
            float brightness = worldProvider.lightBrightnessTable[i];
            brightness *= 0.96F;
            brightness += 0.03F;
            if (brightness > 1F)
                brightness = 1F;
            output.setLightMap(i, brightness);
        }
        return true;
    }

    @Override
    public boolean generateSkyLightMapBase(@NotNull RPLELightMapStrip output, float partialTick) {
        final WorldClient world = Minecraft.getMinecraft().theWorld;
        if (world == null)
            return false;
        final WorldProvider worldProvider = world.provider;
        if (worldProvider == null)
            return false;

        if (worldProvider.dimensionId == END_DIMENSION_ID) {
            output.fillLightMap(0.22F);
            return true;
        }

        final float brightness = world.getSunBrightness(1F);
        for (int i = 0; i < LIGHT_MAP_STRIP_LENGTH; i++) {
            float blue = 0F;

            if (world.lastLightningBolt > 0) {
                blue = world.provider.lightBrightnessTable[i];
            } else {
                blue = world.provider.lightBrightnessTable[i] * (brightness * 0.95F + 0.05F);
            }

            final float red = blue * (brightness * 0.65F + 0.35F);
            final float green = blue * (brightness * 0.65F + 0.35F);

            output.setLightMapRGB(i, red, green, blue);
        }

        return true;
    }
}
