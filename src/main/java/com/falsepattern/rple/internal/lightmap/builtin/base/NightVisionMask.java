/*
 * Copyright (c) 2023 FalsePattern
 * This work is licensed under the Creative Commons Attribution-NonCommercial-NoDerivatives 4.0 International License.
 * To view a copy of this license, visit http://creativecommons.org/licenses/by-nc-nd/4.0/
 * or send a letter to Creative Commons, PO Box 1866, Mountain View, CA 94042, USA.
 *
 */

package com.falsepattern.rple.internal.lightmap.builtin.base;

import com.falsepattern.rple.api.lightmap.LightMapChannel;
import com.falsepattern.rple.api.lightmap.LightMapMask;
import lombok.val;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.potion.Potion;
import net.minecraft.util.MathHelper;

public class NightVisionMask implements LightMapMask {
    private float getNightVisionBrightness(EntityPlayer player, float partialTickTime) {
        int i = player.getActivePotionEffect(Potion.nightVision).getDuration();
        return i > 200 ? 1.0F : 0.7F + MathHelper.sin(((float)i - partialTickTime) * (float)Math.PI * 0.2F) * 0.3F;
    }
    @Override
    public void generateMask(LightMapChannel mask, float partialTickTime) {
        val mc = Minecraft.getMinecraft();
        if (mc.thePlayer.isPotionActive(Potion.nightVision)) {
            val amplitude = getNightVisionBrightness(mc.thePlayer, partialTickTime) * 3;
            for (int i = 0; i < LightMapChannel.LIGHT_MAP_SIZE; i++) {
                mask.R[i] = amplitude;
                mask.G[i] = amplitude;
                mask.B[i] = amplitude;
            }
        }
    }
}
