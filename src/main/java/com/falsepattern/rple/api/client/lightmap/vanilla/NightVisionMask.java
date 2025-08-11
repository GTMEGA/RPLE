/*
 * Right Proper Lighting Engine
 *
 * Copyright (C) 2023-2025 FalsePattern, Ven
 * All Rights Reserved
 *
 * The above copyright notice and this permission notice
 * shall be included in all copies or substantial portions of the Software.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, only version 3 of the License.
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
import com.falsepattern.rple.api.client.lightmap.RPLELightMapMask;
import com.falsepattern.rple.api.client.lightmap.RPLELightMapStrip;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityClientPlayerMP;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.potion.Potion;

import lombok.val;
import lombok.var;
import org.jetbrains.annotations.NotNull;

import static com.falsepattern.rple.api.client.lightmap.RPLELightMapStrip.LIGHT_MAP_STRIP_LENGTH;

public class NightVisionMask implements RPLELightMapMask {
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

    @Override
    public void mutateBlockLightMap(@NotNull RPLELightMapStrip output, float partialTick) {
        mutateLightMap(output, partialTick);
    }

    @Override
    public void mutateSkyLightMap(@NotNull RPLELightMapStrip output, float partialTick) {
        mutateLightMap(output, partialTick);
    }

    protected void mutateLightMap(RPLELightMapStrip output, float partialTick) {
        val mc = Minecraft.getMinecraft();
        val player = mc.thePlayer;
        if (player == null)
            return;
        if (!player.isPotionActive(Potion.nightVision))
            return;
        val power = nightVisionBrightness(player, partialTick);
        val powerInv = 1 - power;
        val R = output.lightMapRedData();
        val G = output.lightMapGreenData();
        val B = output.lightMapBlueData();
        for (int i = 0; i < LIGHT_MAP_STRIP_LENGTH; i++) {
            var r = MathUtil.clamp(R[i], 0.0001F, 1);
            var g = MathUtil.clamp(G[i], 0.0001F, 1);
            var b = MathUtil.clamp(B[i], 0.0001F, 1);
            val minInverse = Math.min(1 / r, Math.min(1 / g, 1 / b)) * power;
            r = r * powerInv + r * minInverse;
            g = g * powerInv + g * minInverse;
            b = b * powerInv + b * minInverse;
            R[i] = MathUtil.clamp(r, 0, 1);
            G[i] = MathUtil.clamp(g, 0, 1);
            B[i] = MathUtil.clamp(b, 0, 1);
        }
    }

    protected float nightVisionIntensity(EntityPlayer player, float partialTick) {
        return nightVisionBrightness(player, partialTick) * 3F;
    }

    protected float nightVisionBrightness(EntityPlayer player, float partialTick) {
        return Minecraft.getMinecraft().entityRenderer.getNightVisionBrightness(player, partialTick);
    }
}
