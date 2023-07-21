/*
 * Copyright (c) 2023 FalsePattern, Ven
 * This work is licensed under the Creative Commons Attribution-NonCommercial-NoDerivatives 4.0 International License.
 * To view a copy of this license, visit http://creativecommons.org/licenses/by-nc-nd/4.0/
 * or send a letter to Creative Commons, PO Box 1866, Mountain View, CA 94042, USA.
 */

package com.falsepattern.rple.internal.mixin.mixins.client.storagedrawers;

import com.falsepattern.rple.internal.client.render.CookieMonsterHelper;
import com.jaquadro.minecraft.storagedrawers.util.RenderHelperAO;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;

@Mixin(value = RenderHelperAO.class, remap = false)
public abstract class RenderHelperAOMixin {
    /**
     * @author FalsePattern
     * @reason Colorize
     */
    @Overwrite
    public static int getAOBrightness(int brightnessA, int brightnessB, int brightnessC, int brightnessD) {
        return CookieMonsterHelper.average(true, brightnessA, brightnessB, brightnessC, brightnessD);
    }

    /**
     * @author FalsePattern
     * @reason Colorize
     */
    @Overwrite
    public static int mixAOBrightness(int part1,
                                      int part2,
                                      int part3,
                                      int part4,
                                      double weight1,
                                      double weight2,
                                      double weight3,
                                      double weight4) {
        return CookieMonsterHelper.mixAOBrightness(part1, part2, part3, part4, weight1, weight2, weight3, weight4);
    }

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
