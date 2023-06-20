/*
 * Copyright (c) 2023 FalsePattern
 * This work is licensed under the Creative Commons Attribution-NonCommercial-NoDerivatives 4.0 International License.
 * To view a copy of this license, visit http://creativecommons.org/licenses/by-nc-nd/4.0/
 * or send a letter to Creative Commons, PO Box 1866, Mountain View, CA 94042, USA.
 */

package com.falsepattern.rple.internal.lightmap.vanilla;

import com.falsepattern.rple.api.lightmap.LightMapMask;
import com.falsepattern.rple.api.lightmap.LightMapStrip;
import lombok.val;

import net.minecraft.client.Minecraft;

public class BossColorModifierMask implements LightMapMask {
    @Override
    public boolean generateBlockLightMapMask(LightMapStrip output, float partialTick) {
        generateBossColorModifierMask(output, partialTick);
        return true;
    }

    @Override
    public boolean generateSkyLightMapMask(LightMapStrip output, float partialTick) {
        generateBossColorModifierMask(output, partialTick);
        return true;
    }

    private static void generateBossColorModifierMask(LightMapStrip output, float partialTick) {
        val intensity = bossColorModifierIntensity(partialTick);
        val red = (1F - intensity) + 0.7F * intensity;
        val green = (1F - intensity) + 0.6F * intensity;
        val blue = (1F - intensity) + 0.6F * intensity;

        output.fillLightMapRGB(red, green, blue);
    }

    private static float bossColorModifierIntensity(float partialTick) {
        val entityRenderer = Minecraft.getMinecraft().entityRenderer;
        return entityRenderer.bossColorModifierPrev +
                (entityRenderer.bossColorModifier - entityRenderer.bossColorModifierPrev) * partialTick;
    }
}
