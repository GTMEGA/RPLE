package com.falsepattern.rple.internal.mixin.mixins.client;

import com.falsepattern.rple.internal.Compat;
import com.falsepattern.rple.internal.mixin.helper.ShaderRenderHelper;
import net.minecraft.client.renderer.entity.Render;
import org.lwjgl.opengl.GL11;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(Render.class)
public abstract class RenderMixin {
    @Redirect(method = "func_147906_a",
              at = @At(value = "INVOKE", target = "Lorg/lwjgl/opengl/GL11;glDisable(I)V"),
              require = 1)
    private void disableTexture(int cap) {
        GL11.glDisable(cap);
        if (cap == GL11.GL_TEXTURE_2D && Compat.shadersEnabled())
            ShaderRenderHelper.disableTexturing();
    }

    @Redirect(method = "func_147906_a",
              at = @At(value = "INVOKE", target = "Lorg/lwjgl/opengl/GL11;glEnable(I)V"),
              require = 1)
    private void enableTexture(int cap) {
        GL11.glEnable(cap);
        if (cap == GL11.GL_TEXTURE_2D && Compat.shadersEnabled())
            ShaderRenderHelper.enableTexturing();
    }

    @ModifyConstant(method = "func_147906_a",
                    constant = @Constant(intValue = 553648127))
    private static int strideSizeInts(int constant) {
        return -1;
    }
}
