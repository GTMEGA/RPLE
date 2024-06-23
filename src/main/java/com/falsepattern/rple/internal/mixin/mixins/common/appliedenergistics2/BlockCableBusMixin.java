/*
 * Copyright (c) 2023 FalsePattern, Ven
 * This work is licensed under the Creative Commons Attribution-NonCommercial-NoDerivatives 4.0 International License.
 * To view a copy of this license, visit http://creativecommons.org/licenses/by-nc-nd/4.0/
 * or send a letter to Creative Commons, PO Box 1866, Mountain View, CA 94042, USA.
 */

package com.falsepattern.rple.internal.mixin.mixins.common.appliedenergistics2;

import appeng.block.AEBaseTileBlock;
import appeng.block.networking.BlockCableBus;
import appeng.parts.ICableBusContainer;
import com.falsepattern.rple.api.common.block.RPLEBlock;
import com.falsepattern.rple.api.common.block.RPLECustomBlockBrightness;
import com.falsepattern.rple.api.common.color.LightValueColor;
import com.falsepattern.rple.internal.mixin.interfaces.appliedenergistics2.ICableBusContainerMixin;
import lombok.val;
import net.minecraft.block.material.Material;
import net.minecraft.world.IBlockAccess;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

import static com.falsepattern.rple.api.common.color.LightValueColor.LIGHT_VALUE_0;

@Mixin(value = BlockCableBus.class,
       remap = false)
public abstract class BlockCableBusMixin extends AEBaseTileBlock implements RPLECustomBlockBrightness {
    public BlockCableBusMixin(Material mat) {
        super(mat);
    }

    @Shadow
    protected abstract ICableBusContainer cb(IBlockAccess world, int posX, int posY, int posZ);

    @Override
    public short rple$getCustomBrightnessColor() {
        return LIGHT_VALUE_0.rgb16();
    }

    @Override
    public short rple$getCustomBrightnessColor(int blockMeta) {
        return LIGHT_VALUE_0.rgb16();
    }

    @Override
    public short rple$getCustomBrightnessColor(@NotNull IBlockAccess world,
                                                            int blockMeta,
                                                            int posX,
                                                            int posY,
                                                            int posZ) {
        val otherBlock = world.getBlock(posX, posY, posZ);
        if (otherBlock != this) {
            val otherBlockMeta = world.getBlockMetadata(posX, posY, posZ);
            return RPLEBlock.of(otherBlock).rple$getBrightnessColor(world, otherBlockMeta, posX, posY, posZ);
        }
        val cb = cb(world, posX, posY, posZ);
        if (cb instanceof ICableBusContainerMixin)
            return ((ICableBusContainerMixin) cb).rple$getColoredBrightness();
        return LightValueColor.fromVanillaLightValue(cb.getLightValue()).rgb16();
    }
}
