/*
 * Copyright (c) 2023 FalsePattern, Ven
 * This work is licensed under the Creative Commons Attribution-NonCommercial-NoDerivatives 4.0 International License.
 * To view a copy of this license, visit http://creativecommons.org/licenses/by-nc-nd/4.0/
 * or send a letter to Creative Commons, PO Box 1866, Mountain View, CA 94042, USA.
 */

package com.falsepattern.rple.internal.mixin.mixins.client;

import com.falsepattern.rple.internal.Common;
import com.falsepattern.rple.internal.Compat;
import com.falsepattern.rple.internal.common.helper.CookieMonster;
import com.falsepattern.rple.internal.mixin.helpers.OpenGlHelperPacked;
import net.minecraft.client.renderer.OpenGlHelper;
import org.lwjgl.opengl.ARBMultitexture;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(OpenGlHelper.class)
public abstract class OpenGLHelperMixin {
    @Shadow public static int lightmapTexUnit;

    @Shadow
    public static void setLightmapTextureCoords(int p_77475_0_, float p_77475_1_, float p_77475_2_) {
    }

    @Shadow private static boolean field_153215_z;

    @Inject(method = "setLightmapTextureCoords",
            at = @At(value = "HEAD"),
            cancellable = true,
            require = 1)
    private static void onSet(int texture, float u, float v, CallbackInfo ci) {
        int value = (int)(u) | ((int)(v) << 16);
        if (CookieMonster.inspectValue(value) == CookieMonster.IntType.COOKIE) {
            long packed = CookieMonster.cookieToPackedLong(value);
            OpenGlHelperPacked.setLightMapTextureCoordsPacked(packed);
            ci.cancel();
        } else {
            if (texture == lightmapTexUnit) {
                if (lightmapTexUnit != Common.RED_LIGHT_MAP_TEXTURE_UNIT) {
                    setLightmapTextureCoords(Common.RED_LIGHT_MAP_TEXTURE_UNIT, u, v);
                }
                setLightmapTextureCoords(Common.GREEN_LIGHT_MAP_TEXTURE_UNIT, u, v);
                setLightmapTextureCoords(Common.BLUE_LIGHT_MAP_TEXTURE_UNIT, u, v);
            }
        }
    }

    private static void doSetActiveTexture(int texture) {
        if (field_153215_z) {
            ARBMultitexture.glActiveTextureARB(texture);
        } else {
            GL13.glActiveTexture(texture);
        }
    }

    private static void toggleTexture(boolean state) {
        if (state) {
            GL11.glEnable(GL11.GL_TEXTURE_2D);
        } else {
            GL11.glDisable(GL11.GL_TEXTURE_2D);
        }
    }

    /**
     * @author FalsePattern
     * @reason general lightmap enable/disable solution
     */
    @Overwrite
    public static void setActiveTexture(int texture) {
        int prevTexture = GL11.glGetInteger(field_153215_z ? ARBMultitexture.GL_ACTIVE_TEXTURE_ARB : GL13.GL_ACTIVE_TEXTURE);
        if (prevTexture == lightmapTexUnit && texture != lightmapTexUnit) {
            boolean prevTexState = GL11.glGetBoolean(GL11.GL_TEXTURE_2D);
            toggleTexture(prevTexState);
            if (Compat.shadersEnabled()) {
                Compat.toggleLightMapShaders(prevTexState);
                doSetActiveTexture(Common.GREEN_LIGHT_MAP_SHADER_TEXTURE_UNIT);
                toggleTexture(prevTexState);
                doSetActiveTexture(Common.BLUE_LIGHT_MAP_SHADER_TEXTURE_UNIT);
                toggleTexture(prevTexState);
            } else {
                doSetActiveTexture(Common.GREEN_LIGHT_MAP_TEXTURE_UNIT);
                toggleTexture(prevTexState);
                doSetActiveTexture(Common.BLUE_LIGHT_MAP_TEXTURE_UNIT);
                toggleTexture(prevTexState);
            }
        }
        if (Compat.shadersEnabled()) {
            Compat.optiFineSetActiveTexture(texture);
        }
        doSetActiveTexture(texture);
    }
}
