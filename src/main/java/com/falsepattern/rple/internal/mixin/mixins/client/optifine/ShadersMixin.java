/*
 * Copyright (c) 2023 FalsePattern, Ven
 * This work is licensed under the Creative Commons Attribution-NonCommercial-NoDerivatives 4.0 International License.
 * To view a copy of this license, visit http://creativecommons.org/licenses/by-nc-nd/4.0/
 * or send a letter to Creative Commons, PO Box 1866, Mountain View, CA 94042, USA.
 */

package com.falsepattern.rple.internal.mixin.mixins.client.optifine;

import com.falsepattern.rple.api.client.RPLEShaderConstants;
import com.falsepattern.rple.internal.Common;
import com.falsepattern.rple.internal.client.render.CookieMonster;
import com.falsepattern.rple.internal.client.render.TessellatorBrightnessHelper;
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
import shadersmod.client.ShaderParser;
import shadersmod.client.Shaders;

@Mixin(value = Shaders.class, remap = false)
public abstract class ShadersMixin {
    @Shadow
    public static int checkGLError(String location) {return 0;}

    @Shadow
    public static void setProgramUniform1i(String name, int value) {}

    @Inject(method = "setProgramUniform1i",
            at = @At("HEAD"),
            require = 1)
    private static void setProgramUniform1iHijack(String name, int value, CallbackInfo ci) {
        if ("lightmap".equals(name)) {
            setProgramUniform1i(RPLEShaderConstants.RED_LIGHT_MAP_UNIFORM_NAME,
                                Common.RED_LIGHT_MAP_SHADER_TEXTURE_SAMPLER);
            setProgramUniform1i(RPLEShaderConstants.GREEN_LIGHT_MAP_UNIFORM_NAME,
                                Common.GREEN_LIGHT_MAP_SHADER_TEXTURE_SAMPLER);
            setProgramUniform1i(RPLEShaderConstants.BLUE_LIGHT_MAP_UNIFORM_NAME,
                                Common.BLUE_LIGHT_MAP_SHADER_TEXTURE_SAMPLER);
        }
    }

    @Redirect(method = "createVertShader",
              at = @At(value = "INVOKE",
                       target = "Lshadersmod/client/ShaderParser;parseLine(Ljava/lang/String;)Lshadersmod/client/ShaderLine;"),
              require = 1)
    private static ShaderLine parseLineHook(String line) {
        val sl = ShaderParser.parseLine(line);
        if (sl != null) {
            if (sl.isAttribute(RPLEShaderConstants.EDGE_TEX_COORD_ATTRIB_NAME)) {
                RPLEShaderConstants.useRPLEEdgeTexCoordAttrib = true;
                RPLEShaderConstants.progUseRPLEEdgeTexCoordAttrib = true;
            }
        }
        return sl;
    }

    @Inject(method = "init",
            at = @At(value = "FIELD",
                     target = "Lshadersmod/client/Shaders;useMidTexCoordAttrib:Z"),
            require = 1)
    private static void initHook(CallbackInfo ci) {
        RPLEShaderConstants.useRPLEEdgeTexCoordAttrib = false;
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
        RPLEShaderConstants.progUseRPLEEdgeTexCoordAttrib = false;
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
        if (RPLEShaderConstants.progUseRPLEEdgeTexCoordAttrib) {
            ARBVertexShader.glBindAttribLocationARB(programid,
                                                    RPLEShaderConstants.edgeTexCoordAttrib,
                                                    RPLEShaderConstants.EDGE_TEX_COORD_ATTRIB_NAME);
            checkGLError(RPLEShaderConstants.EDGE_TEX_COORD_ATTRIB_NAME);
        }
    }

    @Redirect(method = "beginRender",
              at = @At(value = "INVOKE",
                       target = "Lnet/minecraft/entity/EntityLivingBase;getBrightnessForRender(F)I"),
              require = 1,
              remap = true)
    private static int getEyeBrightness(EntityLivingBase instance, float partialTick) {
        val result = instance.getBrightnessForRender(partialTick);
        return TessellatorBrightnessHelper.getBrightestChannelFromPacked(CookieMonster.cookieToPackedLong(result));
    }
}
