/*
 * Copyright (c) 2023 FalsePattern, Ven
 * This work is licensed under the Creative Commons Attribution-NonCommercial-NoDerivatives 4.0 International License.
 * To view a copy of this license, visit http://creativecommons.org/licenses/by-nc-nd/4.0/
 * or send a letter to Creative Commons, PO Box 1866, Mountain View, CA 94042, USA.
 */

package com.falsepattern.rple.internal.mixin.mixins.common.rple;

import com.falsepattern.rple.api.common.block.RPLEBlockRoot;
import com.falsepattern.rple.api.common.color.LightValueColor;
import com.falsepattern.rple.api.common.color.RPLEColor;
import com.falsepattern.rple.internal.mixin.interfaces.RPLERenamedBlockLightMethods;
import net.minecraft.block.Block;
import net.minecraft.world.IBlockAccess;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

import static com.falsepattern.rple.internal.mixin.plugin.MixinPlugin.RPLE_ROOT_MIXIN_PRIORITY;

@Unique
@Mixin(value = Block.class, priority = RPLE_ROOT_MIXIN_PRIORITY)
@SuppressWarnings("unused")
public abstract class RPLEBlockRootImplMixin implements RPLEBlockRoot, RPLERenamedBlockLightMethods {
    @Override
    public short rple$getRawInternalColoredBrightness() {
        return packGreyscale(rple$renamed$getLightValue());
    }

    @Override
    public short rple$getRawInternalColoredBrightness(IBlockAccess world, int posX, int posY, int posZ) {
        return packGreyscale(rple$renamed$getLightValue(world, posX, posY, posZ));
    }

    @Override
    public short rple$getRawInternalColoredOpacity() {
        return packGreyscale(rple$renamed$getLightOpacity());
    }

    @Override
    public short rple$getRawInternalColoredOpacity(IBlockAccess world, int posX, int posY, int posZ) {
        return packGreyscale(rple$renamed$getLightOpacity(world, posX, posY, posZ));
    }

    @Override
    public RPLEColor rple$getInternalColoredBrightness() {
        return LightValueColor.fromVanillaLightValue(rple$renamed$getLightValue());
    }

    @Override
    public RPLEColor rple$getInternalColoredBrightness(IBlockAccess world, int posX, int posY, int posZ) {
        return LightValueColor.fromVanillaLightValue(rple$renamed$getLightValue(world, posX, posY, posZ));
    }

    @Override
    public RPLEColor rple$getInternalColoredTranslucency() {
        return LightValueColor.fromVanillaLightOpacity(rple$renamed$getLightOpacity());
    }

    @Override
    public RPLEColor rple$getInternalColoredTranslucency(IBlockAccess world, int posX, int posY, int posZ) {
        return LightValueColor.fromVanillaLightOpacity(rple$renamed$getLightOpacity(world, posX, posY, posZ));
    }

    private static short packGreyscale(int color) {
        color = color & 0xf;
        return (short) ((color << 8) | (color << 4) | color);
    }
}
