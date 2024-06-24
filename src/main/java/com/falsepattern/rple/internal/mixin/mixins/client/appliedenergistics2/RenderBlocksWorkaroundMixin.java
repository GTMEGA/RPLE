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

package com.falsepattern.rple.internal.mixin.mixins.client.appliedenergistics2;

import appeng.client.render.RenderBlocksWorkaround;
import com.falsepattern.rple.api.client.ClientColorHelper;
import com.falsepattern.rple.internal.Compat;
import lombok.val;
import net.minecraft.client.renderer.RenderBlocks;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(value = RenderBlocksWorkaround.class,
       remap = false)
public abstract class RenderBlocksWorkaroundMixin extends RenderBlocks {
    @Shadow
    protected abstract float getOpacity();

    /**
     * @author FalsePattern
     * @reason Colorize
     */
    @Overwrite
    private void partialLightingColoring(double textureU, double textureV) {
        val r = uvMix(colorRedTopLeft,
                      colorRedTopRight,
                      colorRedBottomLeft,
                      colorRedBottomRight,
                      textureU,
                      textureV);
        val g = uvMix(colorGreenTopLeft,
                      colorGreenTopRight,
                      colorGreenBottomLeft,
                      colorGreenBottomRight,
                      textureU,
                      textureV);
        val b = uvMix(colorBlueTopLeft,
                      colorBlueTopRight,
                      colorBlueBottomLeft,
                      colorBlueBottomRight,
                      textureU,
                      textureV);
        val out = ClientColorHelper.cookieMixAOBrightness(brightnessTopLeft,
                                               brightnessTopRight,
                                               brightnessBottomLeft,
                                               brightnessBottomRight,
                                                      textureU * textureV,
                                                      (1D - textureU) * textureV,
                                                      textureU * (1D - textureV),
                                                      (1D - textureU) * (1D - textureV));
        val tess = Compat.tessellator();
        tess.setColorRGBA_F(r, g, b, getOpacity());
        tess.setBrightness(out);
    }

    private static float uvMix(double topLeft,
                               double topRight,
                               double bottomLeft,
                               double bottomRight,
                               double textureU,
                               double textureV) {
        val top = topLeft * textureU + topRight * (1D - textureU);
        val bottom = bottomLeft * textureU + bottomRight * (1D - textureU);
        return (float) (top * textureV + bottom * (1D - textureV));
    }
}
