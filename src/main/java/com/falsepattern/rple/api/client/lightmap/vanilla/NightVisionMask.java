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
