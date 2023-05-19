/*
 * Copyright (c) 2023 FalsePattern
 * This work is licensed under the Creative Commons Attribution-NonCommercial-NoDerivatives 4.0 International License.
 * To view a copy of this license, visit http://creativecommons.org/licenses/by-nc-nd/4.0/
 * or send a letter to Creative Commons, PO Box 1866, Mountain View, CA 94042, USA.
 *
 */

package com.falsepattern.rple.internal.mixin.mixins.client;

import com.falsepattern.rple.internal.lightmap.LightMapHook;
import org.lwjgl.opengl.GL11;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.Slice;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.minecraft.client.renderer.entity.RendererLivingEntity;
import net.minecraft.entity.EntityLivingBase;

@Mixin(RendererLivingEntity.class)
public abstract class RendererLivingEntityMixin {
    @Inject(method = "doRender(Lnet/minecraft/entity/EntityLivingBase;DDDFF)V",
            at = @At("HEAD"),
            require = 1)
    private void pushBeforeRender(EntityLivingBase p_76986_1_, double p_76986_2_, double p_76986_4_, double p_76986_6_, float p_76986_8_, float p_76986_9_, CallbackInfo ci) {
        GL11.glPushAttrib(GL11.GL_ALL_ATTRIB_BITS);
    }
    @Inject(method = "doRender(Lnet/minecraft/entity/EntityLivingBase;DDDFF)V",
            at = @At("RETURN"),
            require = 1)
    private void popAfterRender(EntityLivingBase p_76986_1_, double p_76986_2_, double p_76986_4_, double p_76986_6_, float p_76986_8_, float p_76986_9_, CallbackInfo ci) {
        GL11.glPopAttrib();
    }
    @Redirect(method = "doRender(Lnet/minecraft/entity/EntityLivingBase;DDDFF)V",
            at = @At(value = "INVOKE",
                     target = "Lorg/lwjgl/opengl/GL11;glDisable(I)V",
                     ordinal = 0),
            slice = @Slice(from = @At(value = "FIELD",
                                      target = "Lnet/minecraft/client/renderer/OpenGlHelper;lightmapTexUnit:I",
                                      ordinal = 0)),
            require = 1)
    private void disableLightMap(int cap) {
        LightMapHook.disableAll();
    }

    @Redirect(method = "doRender(Lnet/minecraft/entity/EntityLivingBase;DDDFF)V",
              at = @At(value = "INVOKE",
                     target = "Lorg/lwjgl/opengl/GL11;glEnable(I)V",
                     ordinal = 0),
              slice = @Slice(from = @At(value = "FIELD",
                                      target = "Lnet/minecraft/client/renderer/OpenGlHelper;lightmapTexUnit:I",
                                      ordinal = 1)),
              require = 1)
    private void enableLightMap(int cap) {
        LightMapHook.enableAll();
    }
}
