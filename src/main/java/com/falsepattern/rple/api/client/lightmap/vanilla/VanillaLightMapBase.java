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

import com.falsepattern.rple.api.client.lightmap.RPLELightMapBase;
import com.falsepattern.rple.api.client.lightmap.RPLELightMapStrip;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.WorldClient;
import net.minecraft.world.WorldProvider;
import org.jetbrains.annotations.NotNull;

import static com.falsepattern.rple.api.client.lightmap.RPLELightMapStrip.LIGHT_MAP_STRIP_LENGTH;

public class VanillaLightMapBase implements RPLELightMapBase {
    protected static final int END_DIMENSION_ID = 1;

    @Override
    public boolean generateBlockLightMapBase(@NotNull RPLELightMapStrip output, float partialTick) {
        final WorldClient world = Minecraft.getMinecraft().theWorld;
        if (world == null)
            return false;
        final WorldProvider worldProvider = world.provider;
        if (worldProvider == null)
            return false;

        for (int i = 0; i < LIGHT_MAP_STRIP_LENGTH; i++) {
            float brightness = worldProvider.lightBrightnessTable[i];
            brightness *= 0.96F;
            brightness += 0.03F;
            if (brightness > 1F)
                brightness = 1F;
            output.setLightMap(i, brightness);
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

        if (worldProvider.dimensionId == END_DIMENSION_ID) {
            output.fillLightMap(0.22F);
            return true;
        }

        final float brightness = world.getSunBrightness(1F);
        for (int i = 0; i < LIGHT_MAP_STRIP_LENGTH; i++) {
            float blue = 0F;

            if (world.lastLightningBolt > 0) {
                blue = world.provider.lightBrightnessTable[i];
            } else {
                blue = world.provider.lightBrightnessTable[i] * (brightness * 0.95F + 0.05F);
            }

            final float red = blue * (brightness * 0.65F + 0.35F);
            final float green = blue * (brightness * 0.65F + 0.35F);

            output.setLightMapRGB(i, red, green, blue);
        }

        return true;
    }
}
