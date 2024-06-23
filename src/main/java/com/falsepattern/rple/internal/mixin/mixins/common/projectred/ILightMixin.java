/*
 * Copyright (c) 2023 FalsePattern, Ven
 * This work is licensed under the Creative Commons Attribution-NonCommercial-NoDerivatives 4.0 International License.
 * To view a copy of this license, visit http://creativecommons.org/licenses/by-nc-nd/4.0/
 * or send a letter to Creative Commons, PO Box 1866, Mountain View, CA 94042, USA.
 */

package com.falsepattern.rple.internal.mixin.mixins.common.projectred;

import com.falsepattern.rple.api.common.block.RPLECustomBlockBrightness;
import com.falsepattern.rple.api.common.color.RPLEColor;
import com.falsepattern.rple.internal.mixin.helper.ProjectRedHelper;
import mrtjp.projectred.illumination.ILight;
import net.minecraft.world.IBlockAccess;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

@Unique
@Mixin(ILight.class)
public interface ILightMixin extends RPLECustomBlockBrightness {
    @Override
    default @NotNull RPLEColor rple$getCustomBrightnessColor() {
        return ProjectRedHelper.getLightBrightnessColor((ILight) this);
    }

    @Override
    default @NotNull RPLEColor rple$getCustomBrightnessColor(int blockMeta) {
        return ProjectRedHelper.getLightBrightnessColor((ILight) this);
    }

    @Override
    default @NotNull RPLEColor rple$getCustomBrightnessColor(@NotNull IBlockAccess world,
                                                             int blockMeta,
                                                             int posX,
                                                             int posY,
                                                             int posZ) {
        return ProjectRedHelper.getLightBrightnessColor((ILight) this);
    }
}
