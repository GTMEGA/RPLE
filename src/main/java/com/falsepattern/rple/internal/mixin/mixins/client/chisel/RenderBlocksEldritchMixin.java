/*
 * Copyright (c) 2023 FalsePattern, Ven
 * This work is licensed under the Creative Commons Attribution-NonCommercial-NoDerivatives 4.0 International License.
 * To view a copy of this license, visit http://creativecommons.org/licenses/by-nc-nd/4.0/
 * or send a letter to Creative Commons, PO Box 1866, Mountain View, CA 94042, USA.
 */

package com.falsepattern.rple.internal.mixin.mixins.client.chisel;

import com.falsepattern.rple.internal.client.render.CookieMonsterHelper;
import net.minecraft.client.renderer.RenderBlocks;
import net.minecraft.util.IIcon;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import team.chisel.client.render.RenderBlocksEldritch;

@Mixin(RenderBlocksEldritch.class)
public abstract class RenderBlocksEldritchMixin extends RenderBlocks {
    @Shadow
    int[] L;

    @Inject(method = "setupSides",
            at = @At("RETURN"),
            remap = false,
            require = 1)
    private void colouredSidesAverageBrightness(IIcon icon,
                                                int a,
                                                int b,
                                                int c,
                                                int d,
                                                int e,
                                                int ta,
                                                int tb,
                                                int tc,
                                                int td,
                                                CallbackInfo ci) {
        L[e] = CookieMonsterHelper.average(false,
                                           brightnessBottomLeft,
                                           brightnessTopLeft,
                                           brightnessTopRight,
                                           brightnessBottomRight);
    }
}
