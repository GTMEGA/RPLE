/*
 * Copyright (c) 2023 FalsePattern, Ven
 * This work is licensed under the Creative Commons Attribution-NonCommercial-NoDerivatives 4.0 International License.
 * To view a copy of this license, visit http://creativecommons.org/licenses/by-nc-nd/4.0/
 * or send a letter to Creative Commons, PO Box 1866, Mountain View, CA 94042, USA.
 */

package com.falsepattern.rple.api.lightmap.vanilla;

import com.falsepattern.rple.api.lightmap.LightMapMask;
import com.falsepattern.rple.api.lightmap.LightMapStrip;
import lombok.val;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.potion.Potion;
import net.minecraft.util.MathHelper;

public class NightVisionMask implements LightMapMask {
    @Override
    public boolean generateBlockLightMapMask(LightMapStrip output, float partialTick) {
        return generateNightVisionMask(output, partialTick);
    }

    @Override
    public boolean generateSkyLightMapMask(LightMapStrip output, float partialTick) {
        return generateNightVisionMask(output, partialTick);
    }

    protected boolean generateNightVisionMask(LightMapStrip output, float partialTick) {
        val player = Minecraft.getMinecraft().thePlayer;
        if (!player.isPotionActive(Potion.nightVision))
            return false;

        val amplitude = nightVisionIntensity(player, partialTick);
        output.fillLightMap(amplitude);

        return true;
    }

    protected float nightVisionIntensity(EntityPlayer player, float partialTick) {
        return nightVisionBrightness(player, partialTick) * 3F;
    }

    protected float nightVisionBrightness(EntityPlayer player, float partialTick) {
        val duration = player.getActivePotionEffect(Potion.nightVision).getDuration();
        return duration > 200 ? 1.0F : 0.7F + MathHelper.sin(((float) duration - partialTick) * (float) Math.PI * 0.2F) * 0.3F;
    }
}
