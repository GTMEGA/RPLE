/*
 * Copyright (c) 2023 FalsePattern, Ven
 * This work is licensed under the Creative Commons Attribution-NonCommercial-NoDerivatives 4.0 International License.
 * To view a copy of this license, visit http://creativecommons.org/licenses/by-nc-nd/4.0/
 * or send a letter to Creative Commons, PO Box 1866, Mountain View, CA 94042, USA.
 */

package com.falsepattern.rple.internal.mixin.mixins.client;

import com.falsepattern.rple.api.ColoredBlock;
import com.falsepattern.rple.internal.color.BlockLightUtil;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.block.Block;
import net.minecraft.block.BlockSlab;
import net.minecraft.world.IBlockAccess;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;

@Mixin(Block.class)
public abstract class BlockMixin {
    /**
     * @author FalsePattern
     * @reason Colorize
     */
    @SideOnly(Side.CLIENT)
    @Overwrite
    public int getMixedBrightnessForBlock(IBlockAccess world, int x, int y, int z) {
        Block block = world.getBlock(x, y, z);
        int meta = world.getBlockMetadata(x, y, z);
        int l = world.getLightBrightnessForSkyBlocks(x, y, z, BlockLightUtil.getCompactRGBLightValue(world, (ColoredBlock) block, meta, x, y, z));

        if (l == 0 && block instanceof BlockSlab) {
            --y;
            block = world.getBlock(x, y, z);
            meta = world.getBlockMetadata(x, y, z);
            return world.getLightBrightnessForSkyBlocks(x, y, z, BlockLightUtil.getCompactRGBLightValue(world, (ColoredBlock) block, meta, x, y, z));
        } else {
            return l;
        }
    }
}
