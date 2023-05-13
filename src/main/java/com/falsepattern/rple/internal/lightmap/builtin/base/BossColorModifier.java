/*
 * Copyright (c) 2023 FalsePattern
 * This work is licensed under the Creative Commons Attribution-NonCommercial-NoDerivatives 4.0 International License.
 * To view a copy of this license, visit http://creativecommons.org/licenses/by-nc-nd/4.0/
 * or send a letter to Creative Commons, PO Box 1866, Mountain View, CA 94042, USA.
 *
 */

package com.falsepattern.rple.internal.lightmap.builtin.base;

import com.falsepattern.lib.util.MathUtil;
import com.falsepattern.rple.api.lightmap.LightMapChannel;
import com.falsepattern.rple.api.lightmap.LightMapMask;
import lombok.val;

import net.minecraft.client.Minecraft;

public class BossColorModifier implements LightMapMask {
    @Override
    public void generateMask(LightMapChannel mask, float partialTickTime) {
        val entityRenderer = Minecraft.getMinecraft().entityRenderer;
        val intensity = entityRenderer.bossColorModifierPrev + (entityRenderer.bossColorModifier - entityRenderer.bossColorModifierPrev) * partialTickTime;
        val r = (1.0F - intensity) + 0.7F * intensity;
        val g = (1.0F - intensity) + 0.6F * intensity;
        val b = (1.0F - intensity) + 0.6F * intensity;
        for (int i = 0; i < LightMapChannel.LIGHT_MAP_SIZE; i++) {
            mask.R[i] = r;
            mask.G[i] = g;
            mask.B[i] = b;
        }
    }
}
