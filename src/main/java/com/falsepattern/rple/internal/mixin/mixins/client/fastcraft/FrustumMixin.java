/*
 * Copyright (c) 2023 FalsePattern, Ven
 * This work is licensed under the Creative Commons Attribution-NonCommercial-NoDerivatives 4.0 International License.
 * To view a copy of this license, visit http://creativecommons.org/licenses/by-nc-nd/4.0/
 * or send a letter to Creative Commons, PO Box 1866, Mountain View, CA 94042, USA.
 *
 */

package com.falsepattern.rple.internal.mixin.mixins.client.fastcraft;

import org.spongepowered.asm.mixin.Dynamic;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import net.minecraft.client.renderer.culling.ClippingHelper;
import net.minecraft.client.renderer.culling.Frustrum;

@Mixin(Frustrum.class)
public abstract class FrustumMixin {
    @Redirect(method = "isBoxInFrustum",
              at = @At(value = "INVOKE",
                       target = "Lfastcraft/HC;ag(Lnet/minecraft/client/renderer/culling/ClippingHelper;DDDDDD)Z",
                       remap = false),
              require = 0,
              expect = 0)
    @Dynamic
    private boolean undoFastCraftHooks1(ClippingHelper helper, double a, double b, double c, double d, double e, double f) {
        return helper.isBoxInFrustum(a, b, c, d, e, f);
    }
}
