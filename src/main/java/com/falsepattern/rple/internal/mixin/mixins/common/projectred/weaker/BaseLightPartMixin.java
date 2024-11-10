package com.falsepattern.rple.internal.mixin.mixins.common.projectred.weaker;

import com.falsepattern.rple.api.common.block.RPLECustomBlockBrightness;
import com.falsepattern.rple.internal.mixin.helper.ProjectRedHelper;
import mrtjp.projectred.illumination.BaseLightPart;
import mrtjp.projectred.illumination.ILight;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.mixin.Mixin;

import net.minecraft.world.IBlockAccess;

@Mixin(BaseLightPart.class)
public abstract class BaseLightPartMixin implements RPLECustomBlockBrightness {
    @Override
    public short rple$getCustomBrightnessColor() {
        return ProjectRedHelper.getLightBrightnessColor((ILight) this);
    }

    @Override
    public short rple$getCustomBrightnessColor(int blockMeta) {
        return ProjectRedHelper.getLightBrightnessColor((ILight) this);
    }

    @Override
    public short rple$getCustomBrightnessColor(@NotNull IBlockAccess world,
                                                int blockMeta,
                                                int posX,
                                                int posY,
                                                int posZ) {
        return ProjectRedHelper.getLightBrightnessColor((ILight) this);
    }
}
