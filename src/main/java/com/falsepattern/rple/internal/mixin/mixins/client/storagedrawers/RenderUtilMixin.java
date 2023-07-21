/*
 * Copyright (c) 2023 FalsePattern, Ven
 * This work is licensed under the Creative Commons Attribution-NonCommercial-NoDerivatives 4.0 International License.
 * To view a copy of this license, visit http://creativecommons.org/licenses/by-nc-nd/4.0/
 * or send a letter to Creative Commons, PO Box 1866, Mountain View, CA 94042, USA.
 */

package com.falsepattern.rple.internal.mixin.mixins.client.storagedrawers;

import com.falsepattern.rple.internal.client.render.CookieMonsterHelper;
import com.jaquadro.minecraft.storagedrawers.client.renderer.RenderUtil;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;

@Mixin(value = RenderUtil.class,
       remap = false)
public abstract class RenderUtilMixin {
    /**
     * @author FalsePattern
     * @reason Colorize
     */
    @Overwrite
    public static int mixAOBrightness(int brightTL,
                                      int brightBL,
                                      int brightBR,
                                      int brightTR,
                                      double lerpTB,
                                      double lerpLR) {
        return CookieMonsterHelper.mixAOBrightness(brightTL, brightBL, brightBR, brightTR, lerpTB, lerpLR);
    }

    /**
     * @author FalsePattern
     * @reason Colorize
     */
    @Overwrite
    public static int mixAOBrightness(int brightMin, int brightMax, float fMin, float fMax) {
        return CookieMonsterHelper.mixAOBrightness(brightMin, brightMax, fMin, fMax);
    }
}
