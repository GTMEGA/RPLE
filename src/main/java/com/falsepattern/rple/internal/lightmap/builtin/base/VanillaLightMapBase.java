/*
 * Copyright (c) 2023 FalsePattern
 * This work is licensed under the Creative Commons Attribution-NonCommercial-NoDerivatives 4.0 International License.
 * To view a copy of this license, visit http://creativecommons.org/licenses/by-nc-nd/4.0/
 * or send a letter to Creative Commons, PO Box 1866, Mountain View, CA 94042, USA.
 *
 */

package com.falsepattern.rple.internal.lightmap.builtin.base;

import com.falsepattern.rple.api.lightmap.LightMapChannel;
import com.falsepattern.rple.api.lightmap.LightMapBase;
import lombok.RequiredArgsConstructor;
import lombok.val;

import net.minecraft.client.Minecraft;

@RequiredArgsConstructor
public class VanillaLightMapBase implements LightMapBase {
    @Override
    public void generateBaseBlock(LightMapChannel lightMap, float partialTickTime) {
        val world = Minecraft.getMinecraft().theWorld;
        for (int i = 0; i < LightMapChannel.LIGHT_MAP_SIZE; i++) {
            val blockRed = world.provider.lightBrightnessTable[i % 16];
            val blockGreen = blockRed * ((blockRed * 0.6F + 0.4F) * 0.6F + 0.4F);
            val blockBlue = blockRed * (blockRed * blockRed * 0.6F + 0.4F);
            lightMap.R[i] = blockRed;
            lightMap.G[i] = blockGreen;
            lightMap.B[i] = blockBlue;
        }
    }

    @Override
    public void generateBaseSky(LightMapChannel lightMap, float partialTickTime) {
        val world = Minecraft.getMinecraft().theWorld;

        if (world.provider.dimensionId == 1) {
            for (int i = 0; i < LightMapChannel.LIGHT_MAP_SIZE; i++) {
                lightMap.R[i] = 0.22F;
                lightMap.G[i] = 0.28F;
                lightMap.B[i] = 0.25F;
            }
            return;
        }

        for (int i = 0; i < LightMapChannel.LIGHT_MAP_SIZE; i++) {
            val baseBrightness = world.getSunBrightness(1F);
            float skyBlue;

            if (world.lastLightningBolt > 0) {
                skyBlue = world.provider.lightBrightnessTable[i];
            } else {
                skyBlue = world.provider.lightBrightnessTable[i] * (baseBrightness * 0.95F + 0.05F);
            }

            val skyRed = skyBlue * (baseBrightness * 0.65F + 0.35F);
            val skyGreen = skyBlue * (baseBrightness * 0.65F + 0.35F);
            lightMap.R[i] = skyRed;
            lightMap.G[i] = skyGreen;
            lightMap.B[i] = skyBlue;
        }
    }

    @Override
    public boolean enabled() {
        return true;
    }
}
