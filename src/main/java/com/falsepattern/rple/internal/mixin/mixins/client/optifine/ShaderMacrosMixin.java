package com.falsepattern.rple.internal.mixin.mixins.client.optifine;

import com.llamalad7.mixinextras.sugar.Local;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import shadersmod.client.ShaderMacros;

import static com.falsepattern.rple.internal.client.optifine.Constants.RPLE_API_VERSION;

@Mixin(value = ShaderMacros.class,
       remap = false)
public abstract class ShaderMacrosMixin {
    @Shadow private static void addMacroLine(StringBuilder sb, String name) {

    }

    @Shadow private static void addMacroLine(StringBuilder sb, String name, int value) {

    }

    @Inject(method = "getMacroLines",
            at = @At(value = "INVOKE",
                     target = "Lshadersmod/client/ShaderMacros;addMacroLine(Ljava/lang/StringBuilder;Ljava/lang/String;)V"),
            require = 1)
    private static void addRPLEMacros(CallbackInfoReturnable<String> cir,
                                      @Local StringBuilder sb) {
        addMacroLine(sb, "RPLE");
        addMacroLine(sb, "RPLE_API", RPLE_API_VERSION);
    }
}
