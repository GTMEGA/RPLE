/*
 * Copyright (c) 2023 FalsePattern, Ven
 * This work is licensed under the Creative Commons Attribution-NonCommercial-NoDerivatives 4.0 International License.
 * To view a copy of this license, visit http://creativecommons.org/licenses/by-nc-nd/4.0/
 * or send a letter to Creative Commons, PO Box 1866, Mountain View, CA 94042, USA.
 */

package com.falsepattern.rple.internal.mixin.mixins.common.mrtjpcore;

import com.falsepattern.rple.api.block.RPLEBlock;
import com.falsepattern.rple.api.block.RPLEBlockBrightnessColorProvider;
import com.falsepattern.rple.api.color.LightValueColor;
import com.falsepattern.rple.api.color.RPLEColor;
import lombok.val;
import mrtjp.core.block.InstancedBlock;
import mrtjp.core.block.InstancedBlockTile;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.material.Material;
import net.minecraft.world.IBlockAccess;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.mixin.Mixin;

import java.lang.invoke.MethodHandles;

import static com.falsepattern.rple.api.color.LightValueColor.LIGHT_VALUE_0;

@Mixin(InstancedBlock.class)
public abstract class InstancedBlockMixin extends BlockContainer implements RPLEBlock, RPLEBlockBrightnessColorProvider {
    private static final MethodHandles.Lookup lookup = MethodHandles.lookup();

    protected InstancedBlockMixin(Material material) {
        super(material);
    }

    @Override
    public @NotNull RPLEColor rple$getCustomBrightnessColor() {
        return LIGHT_VALUE_0;
    }

    @Override
    public @NotNull RPLEColor rple$getCustomBrightnessColor(int blockMeta) {
        return LIGHT_VALUE_0;
    }

    @Override
    public @NotNull RPLEColor rple$getCustomBrightnessColor(@NotNull IBlockAccess world,
                                                            int blockMeta,
                                                            int posX,
                                                            int posY,
                                                            int posZ) {
        val tileEntity = world.getTileEntity(posX, posY, posZ);
        if (tileEntity instanceof RPLEBlockBrightnessColorProvider) {
            val rpleTileEntity = (RPLEBlockBrightnessColorProvider) tileEntity;
            return rpleTileEntity.rple$getCustomBrightnessColor(world, blockMeta, posX, posY, posZ);
        }
        if (tileEntity instanceof InstancedBlockTile) {
            val instancedBlockTile = (InstancedBlockTile) tileEntity;
            return LightValueColor.fromVanillaLightValue(instancedBlockTile.getLightValue());
        }
        return rple$getFallbackBrightnessColor(world, blockMeta, posX, posY, posZ);
    }
}
