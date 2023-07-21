/*
 * Copyright (c) 2023 FalsePattern, Ven
 * This work is licensed under the Creative Commons Attribution-NonCommercial-NoDerivatives 4.0 International License.
 * To view a copy of this license, visit http://creativecommons.org/licenses/by-nc-nd/4.0/
 * or send a letter to Creative Commons, PO Box 1866, Mountain View, CA 94042, USA.
 */

package com.falsepattern.rple.api.lightmap.vanilla;

import com.falsepattern.rple.api.lightmap.RPLELightMapMask;
import com.falsepattern.rple.api.lightmap.RPLELightMapStrip;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.EntityRenderer;
import org.jetbrains.annotations.NotNull;

public class BossColorModifierMask implements RPLELightMapMask {
    @Override
    public boolean generateBlockLightMapMask(@NotNull RPLELightMapStrip output, float partialTick) {
        generateBossColorModifierMask(output, partialTick);
        return true;
    }

    @Override
    public boolean generateSkyLightMapMask(@NotNull RPLELightMapStrip output, float partialTick) {
        generateBossColorModifierMask(output, partialTick);
        return true;
    }

    protected void generateBossColorModifierMask(RPLELightMapStrip output, float partialTick) {
        final float intensity = bossColorModifierIntensity(partialTick);
        final float red = (1F - intensity) + 0.7F * intensity;
        final float green = (1F - intensity) + 0.6F * intensity;
        final float blue = (1F - intensity) + 0.6F * intensity;

        output.fillLightMapRGB(red, green, blue);
    }

    protected float bossColorModifierIntensity(float partialTick) {
        final EntityRenderer entityRenderer = Minecraft.getMinecraft().entityRenderer;
        return entityRenderer.bossColorModifierPrev +
               (entityRenderer.bossColorModifier - entityRenderer.bossColorModifierPrev) * partialTick;
    }
}
