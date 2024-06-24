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

import com.falsepattern.rple.api.client.lightmap.RPLELightMapMask;
import com.falsepattern.rple.api.client.lightmap.RPLELightMapStrip;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.EntityRenderer;
import org.jetbrains.annotations.NotNull;

public class BossColorModifierMask implements RPLELightMapMask {
    @Override
    public boolean generateBlockLightMapMask(@NotNull RPLELightMapStrip output, float partialTick) {
        return generateBossColorModifierMask(output, partialTick);
    }

    @Override
    public boolean generateSkyLightMapMask(@NotNull RPLELightMapStrip output, float partialTick) {
        return generateBossColorModifierMask(output, partialTick);
    }

    protected boolean generateBossColorModifierMask(RPLELightMapStrip output, float partialTick) {
        final EntityRenderer entityRenderer = Minecraft.getMinecraft().entityRenderer;
        if (entityRenderer == null)
            return false;

        final float intensity = bossColorModifierIntensity(entityRenderer, partialTick);
        final float red = (1F - intensity) + 0.7F * intensity;
        final float green = (1F - intensity) + 0.6F * intensity;
        final float blue = (1F - intensity) + 0.6F * intensity;

        output.fillLightMapRGB(red, green, blue);
        return true;
    }

    protected float bossColorModifierIntensity(EntityRenderer entityRenderer, float partialTick) {
        return entityRenderer.bossColorModifierPrev +
               (entityRenderer.bossColorModifier - entityRenderer.bossColorModifierPrev) * partialTick;
    }
}
