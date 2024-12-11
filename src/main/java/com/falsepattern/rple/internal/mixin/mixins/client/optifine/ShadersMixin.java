/*
 * Right Proper Lighting Engine
 *
 * Copyright (C) 2023-2024 FalsePattern, Ven
 * All Rights Reserved
 *
 * The above copyright notice and this permission notice
 * shall be included in all copies or substantial portions of the Software.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program. If not, see <https://www.gnu.org/licenses/>.
 *
 * This program comes with additional permissions according to Section 7 of the
 * GNU Affero General Public License. See the full LICENSE file for details.
 */

package com.falsepattern.rple.internal.mixin.mixins.client.optifine;

import com.falsepattern.rple.api.client.CookieMonster;
import com.falsepattern.rple.api.client.ClientColorHelper;
import com.falsepattern.rple.internal.client.lightmap.LightMapConstants;
import com.falsepattern.rple.internal.client.optifine.RPLEShaderPack;
import com.falsepattern.rple.internal.client.render.ShaderConstants;
import lombok.val;
import net.minecraft.entity.EntityLivingBase;
import org.lwjgl.opengl.ARBVertexShader;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;
import shadersmod.client.ShaderLine;
import shadersmod.client.ShaderPackDefault;
import shadersmod.client.ShaderParser;
import shadersmod.client.Shaders;

@Mixin(value = Shaders.class, remap = false)
public abstract class ShadersMixin {
    @Shadow
    public static int checkGLError(String location) {return 0;}

    @Shadow
    public static void setProgramUniform1i(String name, int value) {}

    @Shadow
    static String packNameDefault;

    @Redirect(method = "listOfShaders",
              at = @At(value = "FIELD",
                       target = "Lshadersmod/client/Shaders;packNameDefault:Ljava/lang/String;"),
              require = 1)
    private static String hijackDefaultShader() {
        return RPLEShaderPack.NAME;
    }

    @Redirect(method = "loadShaderPack",
              at = @At(value = "FIELD",
                       target = "Lshadersmod/client/Shaders;packNameDefault:Ljava/lang/String;"),
              require = 2)
    private static String hijackDefaultShader2() {
        return RPLEShaderPack.NAME;
    }

    @Redirect(method = "loadShaderPack",
              at = @At(value = "NEW",
                       target = "()Lshadersmod/client/ShaderPackDefault;"),
              require = 1)
    private static ShaderPackDefault injectRPLEShader() {
        return new RPLEShaderPack();
    }

    @Inject(method = "useProgram",
            at = @At(value = "CONSTANT",
                     args = "stringValue=lightmap"),
            require = 1)
    private static void setLightMapUniform(int program, CallbackInfo ci) {
        setProgramUniform1i(ShaderConstants.RED_LIGHT_MAP_UNIFORM_NAME,
                            LightMapConstants.R_LIGHT_MAP_SHADER_TEXTURE_SAMPLER);
        setProgramUniform1i(ShaderConstants.GREEN_LIGHT_MAP_UNIFORM_NAME,
                            LightMapConstants.G_LIGHT_MAP_SHADER_TEXTURE_SAMPLER);
        setProgramUniform1i(ShaderConstants.BLUE_LIGHT_MAP_UNIFORM_NAME,
                            LightMapConstants.B_LIGHT_MAP_SHADER_TEXTURE_SAMPLER);
    }

    @Inject(method = "useProgram",
            at = @At(value = "CONSTANT",
                     args = "stringValue=texture"),
            require = 1)
    private static void setTextureUniform(int program, CallbackInfo ci) {
        setProgramUniform1i(ShaderConstants.TEXTURING_ENABLED_ATTRIB_NAME, 1);
    }

    @Redirect(method = "createVertShader",
              at = @At(value = "INVOKE",
                       target = "Lshadersmod/client/ShaderParser;parseLine(Ljava/lang/String;)Lshadersmod/client/ShaderLine;"),
              require = 1)
    private static ShaderLine parseLineHook(String line) {
        val sl = ShaderParser.parseLine(line);
        if (sl != null) {
            if (sl.isAttribute(ShaderConstants.EDGE_TEX_COORD_ATTRIB_NAME)) {
                ShaderConstants.useRPLEEdgeTexCoordAttrib = true;
                ShaderConstants.progUseRPLEEdgeTexCoordAttrib = true;
            }
        }
        return sl;
    }

    @Inject(method = "init",
            at = @At(value = "FIELD",
                     target = "Lshadersmod/client/Shaders;useMidTexCoordAttrib:Z"),
            require = 1)
    private static void initHook(CallbackInfo ci) {
        ShaderConstants.useRPLEEdgeTexCoordAttrib = false;
    }

    @Inject(method = "setupProgram",
            at = @At(value = "FIELD",
                     target = "Lshadersmod/client/Shaders;progUseMidTexCoordAttrib:Z",
                     ordinal = 0),
            require = 1)
    private static void setupResetHook(int program,
                                       String vShaderPath,
                                       String fShaderPath,
                                       CallbackInfoReturnable<Integer> cir) {
        ShaderConstants.progUseRPLEEdgeTexCoordAttrib = false;
    }

    @Inject(method = "setupProgram",
            at = @At(value = "INVOKE",
                     target = "Lorg/lwjgl/opengl/ARBShaderObjects;glLinkProgramARB(I)V"),
            locals = LocalCapture.CAPTURE_FAILHARD,
            require = 1)
    private static void setupAttribLocation(int program,
                                            String vShaderPath,
                                            String fShaderPath,
                                            CallbackInfoReturnable<Integer> cir,
                                            int programid,
                                            int vShader,
                                            int fShader) {
        if (ShaderConstants.progUseRPLEEdgeTexCoordAttrib) {
            ARBVertexShader.glBindAttribLocationARB(programid,
                                                    ShaderConstants.edgeTexCoordAttrib,
                                                    ShaderConstants.EDGE_TEX_COORD_ATTRIB_NAME);
            checkGLError(ShaderConstants.EDGE_TEX_COORD_ATTRIB_NAME);
        }
    }

    @Redirect(method = "beginRender",
              at = @At(value = "INVOKE",
                       target = "Lnet/minecraft/entity/EntityLivingBase;getBrightnessForRender(F)I"),
              require = 1,
              remap = true)
    private static int getEyeBrightness(EntityLivingBase instance, float partialTick) {
        val result = instance.getBrightnessForRender(partialTick);
        return ClientColorHelper.vanillaFromRGB64Max(CookieMonster.RGB64FromCookie(result));
    }
}
