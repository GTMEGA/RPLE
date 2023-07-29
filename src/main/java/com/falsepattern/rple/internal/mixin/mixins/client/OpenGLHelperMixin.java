/*
 * Copyright (c) 2023 FalsePattern, Ven
 * This work is licensed under the Creative Commons Attribution-NonCommercial-NoDerivatives 4.0 International License.
 * To view a copy of this license, visit http://creativecommons.org/licenses/by-nc-nd/4.0/
 * or send a letter to Creative Commons, PO Box 1866, Mountain View, CA 94042, USA.
 */

package com.falsepattern.rple.internal.mixin.mixins.client;

import com.falsepattern.rple.internal.Compat;
import com.falsepattern.rple.internal.client.lightmap.LightMapConstants;
import com.falsepattern.rple.internal.client.render.CookieMonster;
import com.falsepattern.rple.internal.mixin.helper.CodeChickenLibHelper;
import lombok.val;
import net.minecraft.client.renderer.OpenGlHelper;
import org.lwjgl.opengl.ARBMultitexture;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(OpenGlHelper.class)
public abstract class OpenGLHelperMixin {
    @Shadow
    public static int lightmapTexUnit;
    @Shadow
    private static boolean field_153215_z;

    @Shadow
    public static void setLightmapTextureCoords(int textureUnit, float textureU, float textureV) {}

    @Inject(method = "setLightmapTextureCoords",
            at = @At("HEAD"),
            cancellable = true,
            require = 1)
    private static void onSet(int textureUnit, float textureU, float textureV, CallbackInfo ci) {
        val brightness = (int) textureU | ((int) textureV << 16);
        if (CookieMonster.inspectValue(brightness) == CookieMonster.IntType.COOKIE) {
            val packedBrightness = CookieMonster.cookieToPackedLong(brightness);
            CodeChickenLibHelper.setLightMapTextureCoordsPacked(packedBrightness);
            ci.cancel();
        }
        if (textureUnit == lightmapTexUnit) {
            if (lightmapTexUnit != LightMapConstants.R_LIGHT_MAP_FIXED_TEXTURE_UNIT_BINDING)
                setLightmapTextureCoords(LightMapConstants.R_LIGHT_MAP_FIXED_TEXTURE_UNIT_BINDING, textureU, textureV);
            setLightmapTextureCoords(LightMapConstants.G_LIGHT_MAP_FIXED_TEXTURE_UNIT_BINDING, textureU, textureV);
            setLightmapTextureCoords(LightMapConstants.B_LIGHT_MAP_FIXED_TEXTURE_UNIT_BINDING, textureU, textureV);
        }
    }

    @Redirect(method = "setLightmapTextureCoords",
              at = @At(value = "INVOKE",
                       target = "Lorg/lwjgl/opengl/GL13;glMultiTexCoord2f(IFF)V"),
              require = 1)
    private static void testA(int target, float s, float t) {
        GL13.glMultiTexCoord2f(target, s * (32767F / 255F), t * (32767F / 255F));
//        GL13.glMultiTexCoord2f(target, 0.75F, 0.75F);
    }

    @Redirect(method = "setLightmapTextureCoords",
              at = @At(value = "INVOKE",
                       target = "Lorg/lwjgl/opengl/ARBMultitexture;glMultiTexCoord2fARB(IFF)V"),
              require = 1)
    private static void testB(int target, float s, float t) {
        GL13.glMultiTexCoord2f(target, s * (32767F / 255F), t * (32767F / 255F));
    }

    /**
     * @author FalsePattern
     * @reason Colorize
     */
    @Overwrite
    public static void setActiveTexture(int texture) {
        val lastTexture = GL11.glGetInteger(field_153215_z ?
                                            ARBMultitexture.GL_ACTIVE_TEXTURE_ARB : GL13.GL_ACTIVE_TEXTURE);
        if (lastTexture == lightmapTexUnit && texture != lightmapTexUnit) {
            val isTexture2DEnabled = GL11.glGetBoolean(GL11.GL_TEXTURE_2D);
            toggleTexture(isTexture2DEnabled);
            if (Compat.shadersEnabled()) {
                Compat.toggleLightMapShaders(isTexture2DEnabled);
                doSetActiveTexture(LightMapConstants.G_LIGHT_MAP_SHADER_TEXTURE_SAMPLER_BINDING);
                toggleTexture(isTexture2DEnabled);
                doSetActiveTexture(LightMapConstants.B_LIGHT_MAP_SHADER_TEXTURE_SAMPLER_BINDING);
                toggleTexture(isTexture2DEnabled);
            } else {
                doSetActiveTexture(LightMapConstants.G_LIGHT_MAP_FIXED_TEXTURE_UNIT_BINDING);
                toggleTexture(isTexture2DEnabled);
                doSetActiveTexture(LightMapConstants.B_LIGHT_MAP_FIXED_TEXTURE_UNIT_BINDING);
                toggleTexture(isTexture2DEnabled);
            }
        }
        if (Compat.shadersEnabled())
            Compat.optiFineSetActiveTexture(texture);
        doSetActiveTexture(texture);
    }

    private static void toggleTexture(boolean state) {
        if (state) {
            GL11.glEnable(GL11.GL_TEXTURE_2D);
        } else {
            GL11.glDisable(GL11.GL_TEXTURE_2D);
        }
    }

    private static void doSetActiveTexture(int texture) {
        if (field_153215_z) {
            ARBMultitexture.glActiveTextureARB(texture);
        } else {
            GL13.glActiveTexture(texture);
        }
    }
}
