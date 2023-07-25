/*
 * Copyright (c) 2023 FalsePattern, Ven
 * This work is licensed under the Creative Commons Attribution-NonCommercial-NoDerivatives 4.0 International License.
 * To view a copy of this license, visit http://creativecommons.org/licenses/by-nc-nd/4.0/
 * or send a letter to Creative Commons, PO Box 1866, Mountain View, CA 94042, USA.
 */

package com.falsepattern.rple.api.client.lightmap.vanilla;

import com.falsepattern.lib.util.MathUtil;
import com.falsepattern.rple.api.client.lightmap.RPLELightMapMask;
import com.falsepattern.rple.api.client.lightmap.RPLELightMapStrip;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityClientPlayerMP;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.potion.Potion;
import org.jetbrains.annotations.NotNull;

public class NightVisionMask implements RPLELightMapMask {
    @Override
    public boolean generateBlockLightMapMask(@NotNull RPLELightMapStrip output, float partialTick) {
        return generateNightVisionMask(output, partialTick);
    }

    @Override
    public boolean generateSkyLightMapMask(@NotNull RPLELightMapStrip output, float partialTick) {
        return generateNightVisionMask(output, partialTick);
    }

    protected boolean generateNightVisionMask(RPLELightMapStrip output, float partialTick) {
        final EntityClientPlayerMP player = Minecraft.getMinecraft().thePlayer;
        if (player == null)
            return false;
        if (!player.isPotionActive(Potion.nightVision))
            return false;

        final float amplitude = nightVisionIntensity(player, partialTick);
        output.fillLightMap(amplitude);

        return true;
    }

    protected float nightVisionIntensity(EntityPlayer player, float partialTick) {
        return nightVisionBrightness(player, partialTick) * 3F;
    }

    protected float nightVisionBrightness(EntityPlayer player, float partialTick) {
        final float duration = (float) player.getActivePotionEffect(Potion.nightVision).getDuration();
        if (duration > 200F)
            return 1.0F;
        return 0.7F + MathUtil.sin((duration - partialTick) * (float) Math.PI * 0.2F) * 0.3F;
    }
}
