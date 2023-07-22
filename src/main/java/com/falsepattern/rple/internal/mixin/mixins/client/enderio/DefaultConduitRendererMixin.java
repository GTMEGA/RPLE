/*
 * Copyright (c) 2023 FalsePattern, Ven
 * This work is licensed under the Creative Commons Attribution-NonCommercial-NoDerivatives 4.0 International License.
 * To view a copy of this license, visit http://creativecommons.org/licenses/by-nc-nd/4.0/
 * or send a letter to Creative Commons, PO Box 1866, Mountain View, CA 94042, USA.
 */

package com.falsepattern.rple.internal.mixin.mixins.client.enderio;

import com.falsepattern.rple.internal.mixin.helper.EnderIOHelper;
import crazypants.enderio.conduit.render.ConduitRenderer;
import crazypants.enderio.conduit.render.DefaultConduitRenderer;
import net.minecraft.client.renderer.Tessellator;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(DefaultConduitRenderer.class)
public abstract class DefaultConduitRendererMixin implements ConduitRenderer {
    @Redirect(method = "renderEntity",
              at = @At(value = "INVOKE",
                       target = "Lnet/minecraft/client/renderer/Tessellator;setBrightness(I)V"),
              require = 2)
    public void loadTessellatorBrightness(Tessellator instance, int brightnessBase) {
        instance.setBrightness(EnderIOHelper.loadTessellatorBrightness());
    }
}
