/*
 * Copyright (c) 2023 FalsePattern, Ven
 * This work is licensed under the Creative Commons Attribution-NonCommercial-NoDerivatives 4.0 International License.
 * To view a copy of this license, visit http://creativecommons.org/licenses/by-nc-nd/4.0/
 * or send a letter to Creative Commons, PO Box 1866, Mountain View, CA 94042, USA.
 */

package com.falsepattern.rple.internal.mixin.mixins.common.projectred;

import com.falsepattern.rple.api.block.RPLEBlockBrightnessColorProvider;
import com.falsepattern.rple.api.color.DefaultColor;
import com.falsepattern.rple.api.color.RPLEColor;
import mrtjp.projectred.illumination.TileLamp;
import net.minecraft.world.IBlockAccess;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.Shadow;

import static com.falsepattern.rple.api.color.LightValueColor.LIGHT_VALUE_0;

@Pseudo
@Mixin(value = TileLamp.class, remap = false)
public abstract class TileLampMixin implements RPLEBlockBrightnessColorProvider {
    @Shadow
    public abstract int getColor();

    @Shadow
    public abstract boolean isOn();

    @Override
    public @NotNull RPLEColor rple$getCustomBrightnessColor() {
        return rple$getCustomBrightnessColor(0);
    }

    @Override
    public @NotNull RPLEColor rple$getCustomBrightnessColor(@NotNull IBlockAccess world,
                                                            int blockMeta,
                                                            int posX,
                                                            int posY,
                                                            int posZ) {
        return rple$getCustomBrightnessColor(blockMeta);
    }

    @Override
    public @NotNull RPLEColor rple$getCustomBrightnessColor(int blockMeta) {
        if (isOn())
            return DefaultColor.fromVanillaBlockMeta(blockMeta);
        return LIGHT_VALUE_0;
    }
}
