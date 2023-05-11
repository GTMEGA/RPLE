package com.falsepattern.rple.internal.mixin.mixins.client.optifine;

import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.*;
import shadersmod.client.Shaders;

@Mixin(value = Shaders.class,
       remap = false)
public abstract class ShadersMixin {
    @Shadow
    public static void setProgramUniform1i(String name, int value) {
    }

    // TODO: Add constants
    @Inject(method = "setProgramUniform1i",
            at = @At("HEAD"),
            require = 1)
    private static void setProgramUniform1iHijack(String name, int value, CallbackInfo ci) {
        if ("lightmap".equals(name)) {
            setProgramUniform1i("lightmap_red", 1);
            setProgramUniform1i("lightmap_green", 30);
            setProgramUniform1i("lightmap_blue", 31);
        }
    }
}