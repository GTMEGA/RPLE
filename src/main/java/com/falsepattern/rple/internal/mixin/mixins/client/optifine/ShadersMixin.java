package com.falsepattern.rple.internal.mixin.mixins.client.optifine;

import com.falsepattern.rple.internal.Common;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.*;
import shadersmod.client.Shaders;

@Mixin(value = Shaders.class,
       remap = false)
public abstract class ShadersMixin {
    @Inject(method = "setProgramUniform1i",
            at = @At("HEAD"),
            require = 1)
    private static void setProgramUniform1iHijack(String name, int value, CallbackInfo ci) {
        if ("lightmap".equals(name)) {
            setProgramUniform1i(Common.RED_LIGHT_MAP_UNIFORM_NAME, Common.RED_LIGHT_MAP_TEXTURE_SAMPLER);
            setProgramUniform1i(Common.GREEN_LIGHT_MAP_UNIFORM_NAME, Common.GREEN_LIGHT_MAP_TEXTURE_SAMPLER);
            setProgramUniform1i(Common.BLUE_LIGHT_MAP_UNIFORM_NAME, Common.BLUE_LIGHT_MAP_TEXTURE_SAMPLER);
        }
    }

    @Shadow
    public static void setProgramUniform1i(String name, int value) {
    }
}