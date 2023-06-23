/*
 * Copyright (c) 2023 FalsePattern, Ven
 * This work is licensed under the Creative Commons Attribution-NonCommercial-NoDerivatives 4.0 International License.
 * To view a copy of this license, visit http://creativecommons.org/licenses/by-nc-nd/4.0/
 * or send a letter to Creative Commons, PO Box 1866, Mountain View, CA 94042, USA.
 */

package com.falsepattern.rple.internal.mixin.mixins.client.cclib;

import codechicken.lib.colour.ColourRGBA;
import codechicken.lib.lighting.LC;
import codechicken.lib.lighting.LightMatrix;
import codechicken.lib.render.CCRenderState;
import com.falsepattern.rple.internal.common.color.CookieWrappers;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(value = LightMatrix.class,
       remap = false)
public abstract class LightMatrixMixin {

    @Shadow public abstract float[] ao(int side);

    @Shadow public abstract int[] brightness(int side);

    /**
     * @author FalsePattern
     * @reason Colorize
     */
    @Overwrite
    public static int interpBrightness(int a, int b, int c, int d) {
        return CookieWrappers.average(true, a, b, c, d);
    }

    /**
     * @author FalsePattern
     * @reason Colorize
     */
    @Overwrite
    public void operate() {
        LC lc = CCRenderState.lc;
        float[] a = ao(lc.side);
        float f = (a[0] * lc.fa + a[1] * lc.fb + a[2] * lc.fc + a[3] * lc.fd);
        int[] b = brightness(lc.side);
        CCRenderState.setColour(ColourRGBA.multiplyC(CCRenderState.colour, f));
        CCRenderState.setBrightness(CookieWrappers.mixAOBrightness(b[0], b[1], b[2], b[3], lc.fa, lc.fb, lc.fc, lc.fd));
    }
}
