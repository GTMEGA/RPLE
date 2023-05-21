package com.falsepattern.rple.internal.mixin.mixins.client.enderio;

import com.falsepattern.rple.internal.mixin.extension.EnderIOConduitsBrightnessHolder;
import crazypants.enderio.conduit.render.ConduitRenderer;
import crazypants.enderio.conduit.render.DefaultConduitRenderer;
import net.minecraft.client.renderer.Tessellator;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.*;

@Mixin(value = DefaultConduitRenderer.class,
       remap = false)
public abstract class DefaultConduitRendererMixin implements ConduitRenderer {
    @Redirect(method = "renderEntity",
              at = @At(value = "INVOKE",
                       target = "Lnet/minecraft/client/renderer/Tessellator;setBrightness(I)V"),
              require = 1)
    public void cacheBrightness(Tessellator instance, int oldBrightness) {
        instance.setBrightness(EnderIOConduitsBrightnessHolder.getCookieBrightness());
    }
}
