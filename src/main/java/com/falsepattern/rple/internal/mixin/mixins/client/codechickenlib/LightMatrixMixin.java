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

package com.falsepattern.rple.internal.mixin.mixins.client.codechickenlib;

import codechicken.lib.colour.ColourRGBA;
import codechicken.lib.lighting.LightMatrix;
import codechicken.lib.render.CCRenderState;
import com.falsepattern.rple.api.client.ClientColorHelper;
import lombok.val;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(value = LightMatrix.class, remap = false)
public abstract class LightMatrixMixin {
    @Shadow
    public abstract int[] brightness(int side);

    @Shadow
    public abstract float[] ao(int side);

    /**
     * @author FalsePattern
     * @reason Colorize
     */
    @Overwrite
    public static int interpBrightness(int brightnessA, int brightnessB, int brightnessC, int brightnessD) {
        return ClientColorHelper.cookieAverage(true, brightnessA, brightnessB, brightnessC, brightnessD);
    }

    /**
     * @author FalsePattern
     * @reason Colorize
     */
    @Overwrite
    public void operate() {
        val lc = stubpackage.codechicken.lib.render.CCRenderState.lc;
        val a = ao(lc.side);
        val f = (a[0] * lc.fa) + (a[1] * lc.fb) + (a[2] * lc.fc) + (a[3] * lc.fd);
        val b = brightness(lc.side);
        CCRenderState.setColour(ColourRGBA.multiplyC(stubpackage.codechicken.lib.render.CCRenderState.colour, f));
        CCRenderState.setBrightness(ClientColorHelper.cookieMixAOBrightness(b[0], b[1], b[2], b[3], lc.fa, lc.fb, lc.fc, lc.fd));
    }
}
