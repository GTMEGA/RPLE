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
import com.falsepattern.rple.internal.common.config.RPLEConfig;
import lombok.val;
import lombok.var;
import org.jetbrains.annotations.NotNull;

import net.minecraft.client.Minecraft;

import static com.falsepattern.rple.api.client.lightmap.RPLELightMapStrip.LIGHT_MAP_STRIP_LENGTH;

public class GammaMask implements RPLELightMapMask {
    @Override
    public void mutateBlockLightMap(@NotNull RPLELightMapStrip output, float partialTick) {
        mutateLightMap(output, partialTick);
    }

    @Override
    public void mutateSkyLightMap(@NotNull RPLELightMapStrip output, float partialTick) {
        mutateLightMap(output, partialTick);
    }

    protected void mutateLightMap(@NotNull RPLELightMapStrip output, float partialTick) {
        val hd = RPLEConfig.HD.MODE != RPLEConfig.HD.Mode.Disabled;
        float gamma;
        if (hd && RPLEConfig.HD.GAMMA_OVERRIDE != -1.0) {
            gamma = MathUtil.clamp((float)RPLEConfig.HD.GAMMA_OVERRIDE, 0, 1);
        } else {
            gamma = MathUtil.clamp(Minecraft.getMinecraft().gameSettings.gammaSetting, 0, 1);
        }
        val gammaInv = 1 - gamma;

        val R = output.lightMapRedData();
        val G = output.lightMapGreenData();
        val B = output.lightMapBlueData();
        for (int i = 0; i < LIGHT_MAP_STRIP_LENGTH; i++) {
            var r = R[i];
            var g = G[i];
            var b = B[i];
            var rI = 1 - r;
            var gI = 1 - g;
            var bI = 1 - b;
            rI = 1 - rI * rI * rI * rI;
            gI = 1 - gI * gI * gI * gI;
            bI = 1 - bI * bI * bI * bI;
            r = r * gammaInv + rI * gamma;
            g = g * gammaInv + gI * gamma;
            b = b * gammaInv + bI * gamma;
            r = MathUtil.clamp(r, 0, 1);
            g = MathUtil.clamp(g, 0, 1);
            b = MathUtil.clamp(b, 0, 1);
            R[i] = r;
            G[i] = g;
            B[i] = b;
        }
    }
}
