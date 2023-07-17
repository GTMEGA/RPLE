/*
 * Copyright (c) 2023 FalsePattern, Ven
 * This work is licensed under the Creative Commons Attribution-NonCommercial-NoDerivatives 4.0 International License.
 * To view a copy of this license, visit http://creativecommons.org/licenses/by-nc-nd/4.0/
 * or send a letter to Creative Commons, PO Box 1866, Mountain View, CA 94042, USA.
 */

package com.falsepattern.rple.internal.mixin.mixins.common.projectred;

import mrtjp.projectred.illumination.TileLamp;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.Shadow;

@Pseudo
@Mixin(value = TileLamp.class, remap = false)
public abstract class TileLampMixin {
    @Shadow
    public abstract boolean inverted();

    @Shadow
    public abstract boolean powered();

    @Shadow
    public abstract int getLightValue();

    @Shadow
    public abstract int getColor();

// TODO: [PRE_RELEASE] Fluid translucency tweaks belong in FalseTweaks

//    @Override
//    public int getColoredLightValue(IBlockAccess world, int meta, int colorChannel, int x, int y, int z) {
//        return this.inverted() != this.powered() ? LightConstants.colors[colorChannel][~this.getColor() & 0xF] : 0;
//    }
//
//    @Override
//    public int getColoredLightOpacity(IBlockAccess world, int meta, int colorChannel, int x, int y, int z) {
//        //noop
//        return 0;
//    }
//
//    @Override
//    public void setColoredLightValue(int meta, int r, int g, int b) {
//        //noop
//    }
//
//    @Override
//    public void setColoredLightOpacity(int meta, int r, int g, int b) {
//        //noop
//    }
}