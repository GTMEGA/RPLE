package com.falsepattern.rple.internal.mixin.mixins.client.hd;

import com.falsepattern.rple.internal.HardcoreDarkness;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;

import net.minecraft.world.WorldProvider;

@Mixin(WorldProvider.class)
public abstract class WorldProviderMixin {
    @ModifyConstant(method = "getFogColor",
                    constant = {
                            @Constant(floatValue = 0.94f),
                            @Constant(floatValue = 0.91f),
                    },
                    require = 0)
    private float hardcoreFog1(float constant) {
        if (HardcoreDarkness.INSTANCE.isEnabled()) {
            return 1;
        }
        return constant;
    }

    @ModifyConstant(method = "getFogColor",
                    constant = {
                            @Constant(floatValue = 0.06f),
                            @Constant(floatValue = 0.09f),
                    },
                    require = 0)
    private float hardcoreFog2(float constant) {
        if (HardcoreDarkness.INSTANCE.isEnabled()) {
            return 0;
        }
        return constant;
    }
}
