/*
 * Copyright (c) 2023 FalsePattern
 * This work is licensed under the Creative Commons Attribution-NonCommercial-NoDerivatives 4.0 International License.
 * To view a copy of this license, visit http://creativecommons.org/licenses/by-nc-nd/4.0/
 * or send a letter to Creative Commons, PO Box 1866, Mountain View, CA 94042, USA.
 *
 */

package com.falsepattern.rple.internal.mixin.mixins.client.projredcore;

import codechicken.lib.render.CCRenderState;
import com.falsepattern.rple.internal.Tags;
import com.falsepattern.rple.internal.color.BrightnessUtil;
import com.falsepattern.rple.internal.mixin.helpers.OpenGlHelperPacked;
import mrtjp.projectred.core.RenderHalo$;
import org.lwjgl.opengl.GL11;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.minecraft.util.ResourceLocation;

@Mixin(value = RenderHalo$.class,
       remap = false)
public abstract class RenderHaloMixin {
    private static ResourceLocation glowTex;
    private int oldTexture;
    @Inject(method = "prepareRenderState()V",
            at = @At("HEAD"),
            require = 1)
    private void prepareFixColor(CallbackInfo ci) {
        GL11.glPushAttrib(GL11.GL_ALL_ATTRIB_BITS);
        GL11.glColor4f(1, 1, 1, 1);
        OpenGlHelperPacked.setLightMapTextureCoordsPacked(BrightnessUtil.monochromeBrightnessToPackedLong(BrightnessUtil.lightLevelsToBrightness(15, 15)));
        oldTexture = GL11.glGetInteger(GL11.GL_TEXTURE_BINDING_2D);
        CCRenderState.changeTexture(glowTex == null ? glowTex = new ResourceLocation(Tags.MODID, "textures/blocks/glow_solid.png") : glowTex);
    }
    @Inject(method = "restoreRenderState()V",
            at = @At("RETURN"),
            require = 1)
    private void restoreFixColor(CallbackInfo ci) {
        GL11.glBindTexture(GL11.GL_TEXTURE_2D, oldTexture);
        GL11.glPopAttrib();
    }
}
