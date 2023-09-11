/*
 * Copyright (c) 2023 FalsePattern, Ven
 * This work is licensed under the Creative Commons Attribution-NonCommercial-NoDerivatives 4.0 International License.
 * To view a copy of this license, visit http://creativecommons.org/licenses/by-nc-nd/4.0/
 * or send a letter to Creative Commons, PO Box 1866, Mountain View, CA 94042, USA.
 */

package com.falsepattern.rple.internal.mixin.mixins.client.projectred;

import mrtjp.projectred.illumination.LightObject;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(value = LightObject.class, remap = false)
public abstract class LightObjectMixin {
    @Redirect(method = "renderInv",
              at = @At(value = "INVOKE", target = "Lcodechicken/lib/render/CCRenderState;pullLightmap()V"),
              remap = false,
              require = 1)
    private void skipPullingLightCoords() {
    }
}
