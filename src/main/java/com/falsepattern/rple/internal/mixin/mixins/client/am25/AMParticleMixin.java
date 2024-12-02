package com.falsepattern.rple.internal.mixin.mixins.client.am25;

import am2.particles.AMParticle;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;

@Mixin(value = AMParticle.class)
public abstract class AMParticleMixin {
    @ModifyConstant(method = "renderParticle",
                    constant = @Constant(intValue = 0x0F00_00F0),
                    require = 0,
                    expect = 0)
    private int fixInt(int constant) {
        return 0x00F0_00F0;
    }
}
