/*
 * Right Proper Lighting Engine
 *
 * Copyright (C) 2023-2024 FalsePattern, Ven
 * All Rights Reserved
 *
 * The above copyright notice and this permission notice
 * shall be included in all copies or substantial portions of the Software.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program. If not, see <https://www.gnu.org/licenses/>.
 *
 * This program comes with additional permissions according to Section 7 of the
 * GNU Affero General Public License. See the full LICENSE file for details.
 */

package com.falsepattern.rple.api.client.lightmap.vanilla;

import com.falsepattern.lib.util.MathUtil;
import com.falsepattern.rple.api.client.lightmap.RPLELightMapBase;
import com.falsepattern.rple.api.client.lightmap.RPLELightMapStrip;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.WorldClient;
import net.minecraft.potion.Potion;
import net.minecraft.world.WorldProvider;

import com.falsepattern.rple.internal.common.config.RPLEConfig;
import lombok.val;
import org.jetbrains.annotations.NotNull;

import static com.falsepattern.rple.api.client.lightmap.RPLELightMapStrip.LIGHT_MAP_STRIP_LENGTH;

public class VanillaLightMapBase implements RPLELightMapBase {
    protected static final int NETHER_DIMENSION_ID = -1;
    protected static final int END_DIMENSION_ID = 1;
    private static final float DARK_NETHER_REVERSAL = 1 / 0.9f;

    @Override
    public boolean generateBlockLightMapBase(@NotNull RPLELightMapStrip output, float partialTick) {
        final WorldClient world = Minecraft.getMinecraft().theWorld;
        if (world == null)
            return false;
        final WorldProvider worldProvider = world.provider;
        if (worldProvider == null)
            return false;
        val dim = worldProvider.dimensionId;

        val hd = RPLEConfig.HD.MODE != RPLEConfig.HD.Mode.Disabled;
        for (int i = 0; i < LIGHT_MAP_STRIP_LENGTH; i++) {
            float brightness = worldProvider.lightBrightnessTable[i];
            if (!hd) {
                brightness *= 0.96F;
                brightness += 0.03F;
            } else {
                if (RPLEConfig.HD.DARK_NETHER && dim == NETHER_DIMENSION_ID) {
                    brightness = (brightness - 0.1f) * DARK_NETHER_REVERSAL;
                }
            }
            output.setLightMap(i, MathUtil.clamp(brightness, 0, 1));
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

        val hd = RPLEConfig.HD.MODE != RPLEConfig.HD.Mode.Disabled;
        if (worldProvider.dimensionId == END_DIMENSION_ID) {
            if (hd && RPLEConfig.HD.DARK_END) {
                output.fillLightMapRGB(0, 0, 0);
            } else {
                output.fillLightMapRGB(0.22F, 0.28F, 0.25F);
            }
            return true;
        }

        if (worldProvider.dimensionId == NETHER_DIMENSION_ID && hd && RPLEConfig.HD.DARK_END) {
            output.fillLightMapRGB(0, 0, 0);
            return true;
        }

        final float brightness = world.getSunBrightness(partialTick);
        for (int i = 0; i < LIGHT_MAP_STRIP_LENGTH; i++) {
            float blue = 0F;

            if (world.lastLightningBolt > 0) {
                blue = world.provider.lightBrightnessTable[i];
            } else {
                if (hd) {
                    blue = world.provider.lightBrightnessTable[i] * brightness;
                } else {
                    blue = world.provider.lightBrightnessTable[i] * (brightness * 0.95F + 0.05F);
                }
            }

            float red = blue * (brightness * 0.65F + 0.35F);
            float green = blue * (brightness * 0.65F + 0.35F);

            output.setLightMapRGB(i, red, green, blue);
        }

        return true;
    }
//
//    private void updateLightmap(float partialTicks)
//    {
//        WorldClient worldclient = this.mc.theWorld;
//
//        if (worldclient != null)
//        {
//            for (int i = 0; i < 256; ++i)
//            {
//                float sunBrightness = worldclient.getSunBrightness(1.0F) * 0.95F + 0.05F;
//                float sunBlue = worldclient.provider.lightBrightnessTable[i / 16] * sunBrightness;
//                float blockRed = worldclient.provider.lightBrightnessTable[i % 16] * (this.torchFlickerX * 0.1F + 1.5F);
//
//                if (worldclient.lastLightningBolt > 0)
//                {
//                    sunBlue = worldclient.provider.lightBrightnessTable[i / 16];
//                }
//
//                float sunRed = sunBlue * (worldclient.getSunBrightness(1.0F) * 0.65F + 0.35F);
//                float sunGreen = sunBlue * (worldclient.getSunBrightness(1.0F) * 0.65F + 0.35F);
//                float blockGreen = blockRed * ((blockRed * 0.6F + 0.4F) * 0.6F + 0.4F);
//                float blockBlue = blockRed * (blockRed * blockRed * 0.6F + 0.4F);
//                float cRed = sunRed + blockRed;
//                float cGreen = sunGreen + blockGreen;
//                float cBlue = sunBlue + blockBlue;
//                cRed = cRed * 0.96F + 0.03F;
//                cGreen = cGreen * 0.96F + 0.03F;
//                cBlue = cBlue * 0.96F + 0.03F;
//
//                if (this.bossColorModifier > 0.0F)
//                {
//                    float mod = this.bossColorModifierPrev + (this.bossColorModifier - this.bossColorModifierPrev) * partialTicks;
//                    cRed = cRed * (1.0F - mod) + cRed * 0.7F * mod;
//                    cGreen = cGreen * (1.0F - mod) + cGreen * 0.6F * mod;
//                    cBlue = cBlue * (1.0F - mod) + cBlue * 0.6F * mod;
//                }
//
//                if (worldclient.provider.dimensionId == 1)
//                {
//                    cRed = 0.22F + blockRed * 0.75F;
//                    cGreen = 0.28F + blockGreen * 0.75F;
//                    cBlue = 0.25F + blockBlue * 0.75F;
//                }
//
//                if (this.mc.thePlayer.isPotionActive(Potion.nightVision))
//                {
//                    float power = this.getNightVisionBrightness(this.mc.thePlayer, partialTicks);
//                    float minInverse = 1.0F / cRed;
//
//                    if (minInverse > 1.0F / cGreen)
//                    {
//                        minInverse = 1.0F / cGreen;
//                    }
//
//                    if (minInverse > 1.0F / cBlue)
//                    {
//                        minInverse = 1.0F / cBlue;
//                    }
//
//                    cRed = cRed * (1.0F - power) + cRed * minInverse * power;
//                    cGreen = cGreen * (1.0F - power) + cGreen * minInverse * power;
//                    cBlue = cBlue * (1.0F - power) + cBlue * minInverse * power;
//                }
//
//                if (cRed > 1.0F)
//                {
//                    cRed = 1.0F;
//                }
//
//                if (cGreen > 1.0F)
//                {
//                    cGreen = 1.0F;
//                }
//
//                if (cBlue > 1.0F)
//                {
//                    cBlue = 1.0F;
//                }
//
//                float gamma = this.mc.gameSettings.gammaSetting;
//                float redInv = 1.0F - cRed;
//                float greenInv = 1.0F - cGreen;
//                float blueInv = 1.0F - cBlue;
//                redInv = 1.0F - redInv * redInv * redInv * redInv;
//                greenInv = 1.0F - greenInv * greenInv * greenInv * greenInv;
//                blueInv = 1.0F - blueInv * blueInv * blueInv * blueInv;
//                cRed = cRed * (1.0F - gamma) + redInv * gamma;
//                cGreen = cGreen * (1.0F - gamma) + greenInv * gamma;
//                cBlue = cBlue * (1.0F - gamma) + blueInv * gamma;
//                cRed = cRed * 0.96F + 0.03F;
//                cGreen = cGreen * 0.96F + 0.03F;
//                cBlue = cBlue * 0.96F + 0.03F;
//
//                if (cRed > 1.0F)
//                {
//                    cRed = 1.0F;
//                }
//
//                if (cGreen > 1.0F)
//                {
//                    cGreen = 1.0F;
//                }
//
//                if (cBlue > 1.0F)
//                {
//                    cBlue = 1.0F;
//                }
//
//                if (cRed < 0.0F)
//                {
//                    cRed = 0.0F;
//                }
//
//                if (cGreen < 0.0F)
//                {
//                    cGreen = 0.0F;
//                }
//
//                if (cBlue < 0.0F)
//                {
//                    cBlue = 0.0F;
//                }
//
//                short short1 = 255;
//                int iRed = (int)(cRed * 255.0F);
//                int iGreen = (int)(cGreen * 255.0F);
//                int iBlue = (int)(cBlue * 255.0F);
//                this.lightmapColors[i] = short1 << 24 | iRed << 16 | iGreen << 8 | iBlue;
//            }
//
//            this.lightmapTexture.updateDynamicTexture();
//            this.lightmapUpdateNeeded = false;
//        }
//    }
}
