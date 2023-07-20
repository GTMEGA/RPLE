/*
 * Copyright (c) 2023 FalsePattern, Ven
 * This work is licensed under the Creative Commons Attribution-NonCommercial-NoDerivatives 4.0 International License.
 * To view a copy of this license, visit http://creativecommons.org/licenses/by-nc-nd/4.0/
 * or send a letter to Creative Commons, PO Box 1866, Mountain View, CA 94042, USA.
 */

package com.falsepattern.rple.internal.mixin.mixins.client.carpentersblocks;

import com.carpentersblocks.renderer.helper.LightingHelper;
import com.falsepattern.rple.internal.common.helper.CookieWrappers;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;

@Mixin(LightingHelper.class)
public abstract class LightingHelperMixin {
    /**
     * @author FalsePattern
     * @reason Colorize
     */
    @Overwrite
    public static int getAverageBrightness(int brightnessA, int brightnessB) {
        return CookieWrappers.average(false, brightnessA, brightnessB);
    }
}
