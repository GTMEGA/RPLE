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
import com.falsepattern.rple.internal.mixin.extension.ExtendedOpenGlHelper;
import lombok.val;
import net.minecraft.client.renderer.OpenGlHelper;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import static com.falsepattern.rple.internal.client.lightmap.LightMap.lightMap;

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
            ExtendedOpenGlHelper.setLightMapTextureCoordsPacked(packedBrightness);
            ci.cancel();
        }
        if (textureUnit == lightmapTexUnit) {
            if (Compat.shadersEnabled()) {
                if (lightmapTexUnit != LightMapConstants.R_LIGHT_MAP_SHADER_TEXTURE_COORDS_BINDING)
                    setLightmapTextureCoords(LightMapConstants.R_LIGHT_MAP_SHADER_TEXTURE_COORDS_BINDING, textureU, textureV);
                setLightmapTextureCoords(LightMapConstants.G_LIGHT_MAP_SHADER_TEXTURE_COORDS_BINDING, textureU, textureV);
                setLightmapTextureCoords(LightMapConstants.B_LIGHT_MAP_SHADER_TEXTURE_COORDS_BINDING, textureU, textureV);
            } else {
                if (lightmapTexUnit != LightMapConstants.R_LIGHT_MAP_FIXED_TEXTURE_UNIT_BINDING)
                    setLightmapTextureCoords(LightMapConstants.R_LIGHT_MAP_FIXED_TEXTURE_UNIT_BINDING, textureU, textureV);
                setLightmapTextureCoords(LightMapConstants.G_LIGHT_MAP_FIXED_TEXTURE_UNIT_BINDING, textureU, textureV);
                setLightmapTextureCoords(LightMapConstants.B_LIGHT_MAP_FIXED_TEXTURE_UNIT_BINDING, textureU, textureV);
            }
        }
    }

    @Redirect(method = "setLightmapTextureCoords",
              at = @At(value = "INVOKE",
                       target = "Lorg/lwjgl/opengl/GL13;glMultiTexCoord2f(IFF)V"),
              require = 1)
    private static void testA(int target, float s, float t) {
        zamnza(target, s, t);
    }

    @Redirect(method = "setLightmapTextureCoords",
              at = @At(value = "INVOKE",
                       target = "Lorg/lwjgl/opengl/ARBMultitexture;glMultiTexCoord2fARB(IFF)V"),
              require = 1)
    private static void testB(int target, float s, float t) {
        zamnza(target, s, t);
    }

    private static void zamnza(int target, float see, float tea) {
        val real = (see / 240F) * (Short.MAX_VALUE - Short.MIN_VALUE) + Short.MIN_VALUE;
        val fake = (tea / 240F) * (Short.MAX_VALUE - Short.MIN_VALUE) + Short.MIN_VALUE;

        GL13.glMultiTexCoord2f(target, real, fake);
    }

    /**
     * @author FalsePattern
     * @reason Colorize
     */
    @Overwrite
    public static void setActiveTexture(int texture) {
        val shadersEnabled = Compat.shadersEnabled();
        val lastTexture = GL11.glGetInteger(GL13.GL_ACTIVE_TEXTURE);

        if (lastTexture == lightmapTexUnit && texture != lightmapTexUnit) {
            val isTexture2DEnabled = GL11.glGetBoolean(GL11.GL_TEXTURE_2D);
            if (isTexture2DEnabled || !shadersEnabled) {
                GL11.glEnable(GL11.GL_TEXTURE_2D);
            } else {
                GL11.glDisable(GL11.GL_TEXTURE_2D);
            }

            lightMap().toggleEnabled(isTexture2DEnabled);
        }
        if (shadersEnabled)
            Compat.optiFineSetActiveTexture(texture);
        GL13.glActiveTexture(texture);
    }
}
