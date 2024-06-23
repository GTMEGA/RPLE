/*
 * Copyright (c) 2023 FalsePattern, Ven
 * This work is licensed under the Creative Commons Attribution-NonCommercial-NoDerivatives 4.0 International License.
 * To view a copy of this license, visit http://creativecommons.org/licenses/by-nc-nd/4.0/
 * or send a letter to Creative Commons, PO Box 1866, Mountain View, CA 94042, USA.
 */

package com.falsepattern.rple.internal.mixin.mixins.client.appliedenergistics2;

import appeng.client.render.RenderBlocksWorkaround;
import com.falsepattern.rple.api.client.RPLEAOHelper;
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
        val out = RPLEAOHelper.mixAOBrightness(brightnessTopLeft,
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
