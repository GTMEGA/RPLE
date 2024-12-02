package com.falsepattern.rple.internal.mixin.mixins.client.hd;

import com.falsepattern.rple.internal.common.config.RPLEConfig;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import net.minecraft.util.Vec3;
import net.minecraft.world.WorldProviderHell;

@Mixin(WorldProviderHell.class)
public abstract class WorldProviderHellMixin {

    @WrapOperation(method = "getFogColor",
                   at = @At(value = "INVOKE",
                            target = "Lnet/minecraft/util/Vec3;createVectorHelper(DDD)Lnet/minecraft/util/Vec3;"),
                   require = 1)
    private Vec3 hardcoreFogColor(double x, double y, double z, Operation<Vec3> original) {
        if (RPLEConfig.HD.MODE != RPLEConfig.HD.Mode.Disabled && RPLEConfig.HD.DARK_NETHER) {
            return original.call(0d, 0d, 0d);
        } else {
            return original.call(x, y, z);
        }
    }
}
