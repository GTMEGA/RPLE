/*
 * Copyright (c) 2023 FalsePattern, Ven
 * This work is licensed under the Creative Commons Attribution-NonCommercial-NoDerivatives 4.0 International License.
 * To view a copy of this license, visit http://creativecommons.org/licenses/by-nc-nd/4.0/
 * or send a letter to Creative Commons, PO Box 1866, Mountain View, CA 94042, USA.
 */

package com.falsepattern.rple.internal.mixin.mixins.client.ae2;

import appeng.client.render.RenderBlocksWorkaround;
import com.falsepattern.rple.internal.common.helper.color.CookieWrappers;
import net.minecraft.client.renderer.RenderBlocks;
import net.minecraft.client.renderer.Tessellator;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(value = RenderBlocksWorkaround.class,
       remap = false)
public abstract class RenderBlocksWorkaroundMixin extends RenderBlocks {
    @Shadow protected abstract float getOpacity();

    /**
     * @author FalsePattern
     * @reason Colorize
     */
    @Overwrite
    private void partialLightingColoring(double u, double v) {
        float r = (float) uvMix(colorRedTopLeft, colorRedTopRight, colorRedBottomLeft, colorRedBottomRight, u, v);
        float g = (float) uvMix(colorGreenTopLeft, colorGreenTopRight, colorGreenBottomLeft, colorGreenBottomRight, u, v);
        float b = (float) uvMix(colorBlueTopLeft, colorBlueTopRight, colorBlueBottomLeft, colorBlueBottomRight, u, v);
        int out = CookieWrappers.mixAOBrightness(brightnessTopLeft, brightnessTopRight, brightnessBottomLeft, brightnessBottomRight, u * v, (1.0 - u) * v, u * (1.0 - v), (1.0 - u) * (1.0 - v));
        Tessellator.instance.setColorRGBA_F(r, g, b, this.getOpacity());
        Tessellator.instance.setBrightness(out);
    }

    private static double uvMix(double topLeft, double topRight, double bottomLeft, double bottomRight, double u, double v) {
        double top = topLeft * u + topRight * (1.0 - u);
        double bottom = bottomLeft * u + bottomRight * (1.0 - u);
        return top * v + bottom * (1.0 - v);
    }
}
