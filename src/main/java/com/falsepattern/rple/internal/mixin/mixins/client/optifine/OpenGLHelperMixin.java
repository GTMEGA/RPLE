package com.falsepattern.rple.internal.mixin.mixins.client.optifine;

import net.minecraft.client.renderer.OpenGlHelper;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import stubpackage.GlStateManager;

@Mixin(OpenGlHelper.class)
public abstract class OpenGLHelperMixin {
    @Inject(method = "initializeTextures",
            at = @At("RETURN"),
            require = 1)
    private static void initGlStateManager(CallbackInfo ci) {
        GlStateManager.GL_FRAMEBUFFER = OpenGlHelper.field_153198_e;
        GlStateManager.GL_RENDERBUFFER = OpenGlHelper.field_153199_f;
        GlStateManager.GL_COLOR_ATTACHMENT0 = OpenGlHelper.field_153200_g;
        GlStateManager.GL_DEPTH_ATTACHMENT = OpenGlHelper.field_153201_h;
    }
}
