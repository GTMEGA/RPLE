package com.falsepattern.rple.internal.mixin.mixins.client.notenoughitems;


import codechicken.nei.WorldOverlayRenderer;
import com.falsepattern.rple.internal.Compat;
import com.falsepattern.rple.internal.mixin.helper.ShaderRenderHelper;
import org.lwjgl.opengl.GL11;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import static codechicken.nei.KeyManager.IKeyStateTracker;

@Mixin(value = WorldOverlayRenderer.class, remap = false)
public abstract class WorldOverlayRendererMixin implements IKeyStateTracker {
    @Redirect(method = {"renderMobSpawnOverlay", "renderChunkBounds"},
              at = @At(value = "INVOKE", target = "Lorg/lwjgl/opengl/GL11;glDisable(I)V"),
              require = 1)
    private static void disableTexture(int cap) {
        GL11.glDisable(cap);
        if (cap == GL11.GL_TEXTURE_2D && Compat.shadersEnabled())
            ShaderRenderHelper.disableTexturing();
    }

    @Redirect(method = {"renderMobSpawnOverlay", "renderChunkBounds"},
              at = @At(value = "INVOKE", target = "Lorg/lwjgl/opengl/GL11;glEnable(I)V"),
              require = 1)
    private static void enableTexture(int cap) {
        GL11.glEnable(cap);
        if (cap == GL11.GL_TEXTURE_2D && Compat.shadersEnabled())
            ShaderRenderHelper.enableTexturing();
    }
}
