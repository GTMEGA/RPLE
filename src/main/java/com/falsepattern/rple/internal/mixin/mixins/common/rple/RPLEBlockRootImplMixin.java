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
import com.falsepattern.rple.internal.common.block.TransparencyRecursionDoctor;
import lombok.val;
import net.minecraft.block.Block;
import net.minecraft.world.IBlockAccess;
import org.spongepowered.asm.mixin.Dynamic;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;

import static com.falsepattern.rple.internal.mixin.plugin.MixinPlugin.RPLE_ROOT_MIXIN_PRIORITY;

@Unique
@Mixin(value = Block.class, priority = RPLE_ROOT_MIXIN_PRIORITY)
@SuppressWarnings("unused")
public abstract class RPLEBlockRootImplMixin implements RPLEBlockRoot {
    @Shadow
    public abstract int getLightValue();

    @Shadow(remap = false)
    public abstract int getLightValue(IBlockAccess world, int posX, int posY, int posZ);

    @Shadow
    public abstract int getLightOpacity();

    @Shadow(remap = false)
    public abstract int getLightOpacity(IBlockAccess world, int posX, int posY, int posZ);

    @Shadow protected int lightOpacity;
    @Dynamic("Initialized in: [com.falsepattern.rple.internal.mixin.mixins.common.rple.RPLEBlockInitImplMixin]")
    private ThreadLocal<Boolean> rple$passInternalBrightness;
    @Dynamic("Initialized in: [com.falsepattern.rple.internal.mixin.mixins.common.rple.RPLEBlockInitImplMixin]")
    private ThreadLocal<Boolean> rple$passInternalOpacity;

    @Override
    public RPLEColor rple$getInternalColoredBrightness() {
        rple$passInternalBrightness.set(true);
        val lightValue = getLightValue();
        return LightValueColor.fromVanillaLightValue(lightValue);
    }

    @Override
    public RPLEColor rple$getInternalColoredBrightness(IBlockAccess world, int posX, int posY, int posZ) {
        rple$passInternalBrightness.set(true);
        val lightValue = getLightValue(world, posX, posY, posZ);
        return LightValueColor.fromVanillaLightValue(lightValue);
    }

    @Override
    public RPLEColor rple$getInternalColoredTranslucency() {
        rple$passInternalOpacity.set(true);
        val lightOpacity = getLightOpacity();
        return LightValueColor.fromVanillaLightOpacity(lightOpacity);
    }

    @Override
    public RPLEColor rple$getInternalColoredTranslucency(IBlockAccess world, int posX, int posY, int posZ) {
        rple$passInternalOpacity.set(true);
        int lightOpacity;
        if (TransparencyRecursionDoctor.isOnBlockList(TransparencyRecursionDoctor.Variant.Positional, getClass())) {
            lightOpacity = getLightOpacity();
        } else {
            lightOpacity = getLightOpacity(world, posX, posY, posZ);
        }
        return LightValueColor.fromVanillaLightOpacity(lightOpacity);
    }
}
