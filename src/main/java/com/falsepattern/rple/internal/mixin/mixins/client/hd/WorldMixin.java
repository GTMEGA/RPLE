package com.falsepattern.rple.internal.mixin.mixins.client.hd;

import com.falsepattern.rple.internal.HardcoreDarkness;
import com.falsepattern.rple.internal.common.config.RPLEConfig;
import lombok.val;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;
import org.spongepowered.asm.mixin.injection.Slice;

import net.minecraft.client.Minecraft;
import net.minecraft.world.World;

@Mixin(World.class)
public abstract class WorldMixin {
    private static float value2;
    private static RPLEConfig.HD.Mode skyModeCache;

    @ModifyConstant(method = "getSunBrightnessBody",
                    constant = @Constant(floatValue = 0.8F),
                    remap = false,
                    require = 0)
    private float sky1(float constant) {
        if (!HardcoreDarkness.INSTANCE.isEnabled()) {
            return constant;
        }

        skyModeCache = RPLEConfig.HD.MODE;

        switch (skyModeCache) {
            case Both:
                return 1;
            case DynamicMoonlight:
                val lightList = RPLEConfig.HD.MOON_LIGHT_LIST;
                float moon = Minecraft.getMinecraft().theWorld.getCurrentMoonPhaseFactor();
                return 1 - (value2 = (float) lightList[(int) Math.round(moon / 0.25f)]);
            default:
                return constant;
        }
    }
    @ModifyConstant(method = "getSunBrightnessBody",
                    constant = @Constant(floatValue = 0.2F),
                    slice = @Slice(from = @At(value = "CONSTANT",
                                              args = "floatValue=0.8")),
                    remap = false,
                    require = 0)
    private float sky2(float constant) {
        if (!HardcoreDarkness.INSTANCE.isEnabled()) {
            return constant;
        }

        switch(skyModeCache) {
            case Both: return 0;
            case DynamicMoonlight: return value2;
            default: return constant;
        }
    }
}
