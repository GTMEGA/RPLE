package com.falsepattern.rple.internal.mixin.mixins.client;

import lombok.val;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.entity.RendererLivingEntity;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Unique
@Mixin(RendererLivingEntity.class)
public abstract class RendererLivingEntityMixin extends Render {
    @Redirect(method = "passSpecialRender",
              at = @At(value = "INVOKE", target = "Lorg/lwjgl/opengl/GL11;glDisable(I)V"),
              require = 1)
    private void disableTexture(int cap) {
        if (cap != GL11.GL_TEXTURE)
            return;

        val lastActiveTexture = GL11.glGetInteger(GL13.GL_ACTIVE_TEXTURE);
        GL13.glActiveTexture(0);
        GL11.glDisable(GL11.GL_TEXTURE_2D);
        GL13.glActiveTexture(lastActiveTexture);
    }

    @Redirect(method = "passSpecialRender",
              at = @At(value = "INVOKE", target = "Lorg/lwjgl/opengl/GL11;glEnable(I)V"),
              require = 1)
    private void enableTexture(int cap) {
        if (cap != GL11.GL_TEXTURE)
            return;

        val lastActiveTexture = GL11.glGetInteger(GL13.GL_ACTIVE_TEXTURE);
        GL13.glActiveTexture(0);
        GL11.glEnable(GL11.GL_TEXTURE_2D);
        GL13.glActiveTexture(lastActiveTexture);
    }
}
