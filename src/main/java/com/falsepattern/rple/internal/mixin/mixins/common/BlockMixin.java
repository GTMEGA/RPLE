/*
 * Copyright (c) 2023 FalsePattern
 * This work is licensed under the Creative Commons Attribution-NonCommercial-NoDerivatives 4.0 International License.
 * To view a copy of this license, visit http://creativecommons.org/licenses/by-nc-nd/4.0/
 * or send a letter to Creative Commons, PO Box 1866, Mountain View, CA 94042, USA.
 *
 */

package com.falsepattern.rple.internal.mixin.mixins.common;

import com.falsepattern.rple.api.ColoredBlock;
import com.falsepattern.rple.api.LightConstants;
import lombok.val;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

import net.minecraft.block.Block;
import net.minecraft.block.BlockStainedGlass;
import net.minecraft.init.Blocks;
import net.minecraft.world.IBlockAccess;

@Mixin(Block.class)
public abstract class BlockMixin implements ColoredBlock {
    @Shadow public abstract int getLightValue();

    @Shadow public abstract int getLightOpacity();

    @Shadow protected boolean useNeighborBrightness;

    /**
     * @author FalsePattern
     * @reason Return max of colored
     */
    @Overwrite(remap = false)
    public int getLightValue(IBlockAccess world, int x, int y, int z) {
        val block = world.getBlock(x, y, z);
        val thiz = (Block)(Object)this;
        if (block != thiz) {
            return block.getLightValue(world, x, y, z);
        }
        val meta = world.getBlockMetadata(x, y, z);
        val r = getColoredLightValueChecked(world, meta, LightConstants.COLOR_CHANNEL_RED, x, y, z);
        val g = getColoredLightValueChecked(world, meta, LightConstants.COLOR_CHANNEL_GREEN, x, y, z);
        val b = getColoredLightValueChecked(world, meta, LightConstants.COLOR_CHANNEL_BLUE, x, y, z);
        return r > g ? Math.max(r, b) : Math.max(g, b);
    }

    /**
     * @author FalsePattern
     * @reason TODO
     */
    @Overwrite
    public boolean getUseNeighborBrightness() {
        return ((Object)this) instanceof BlockStainedGlass ? false : useNeighborBrightness;
    }

    /**
     * @author FalsePattern
     * @reason Return min of colored
     */
    @Overwrite(remap = false)
    public int getLightOpacity(IBlockAccess world, int x, int y, int z) {
        val block = world.getBlock(x, y, z);
        val thiz = (Block)(Object)this;
        if (block != thiz) {
            return block.getLightOpacity(world, x, y, z);
        }
        val meta = world.getBlockMetadata(x, y, z);
        val r = getColoredLightOpacity(world, meta, LightConstants.COLOR_CHANNEL_RED, x, y, z);
        val g = getColoredLightOpacity(world, meta, LightConstants.COLOR_CHANNEL_GREEN, x, y, z);
        val b = getColoredLightOpacity(world, meta, LightConstants.COLOR_CHANNEL_BLUE, x, y, z);
        return r < g ? Math.max(r, b) : Math.max(g, b);
    }

    @Override
    public int getColoredLightValue(IBlockAccess world, int meta, int colorChannel, int x, int y, int z) {
        val block = world.getBlock(x, y, z);
        val thiz = (Block)(Object)this;
        if (block != thiz) {
            return ((ColoredBlock)block).getColoredLightValue(world, meta, colorChannel, x, y, z);
        }
        return getColoredLightValueChecked(world, meta, colorChannel, x, y, z);
    }

    private int getColoredLightValueChecked(IBlockAccess world, int meta, int colorChannel, int x, int y, int z) {
        val thiz = (Block)(Object)this;
        if (thiz == Blocks.torch) {
            return new int[]{14,12,6}[colorChannel];
        } else if (thiz == Blocks.redstone_block) {
            return new int[]{15,4,3}[colorChannel];
        } else if (thiz == Blocks.lapis_block) {
            return new int[]{3,4,15}[colorChannel];
        }
        return getLightValue();
    }

    @Override
    public int getColoredLightOpacity(IBlockAccess world, int meta, int colorChannel, int x, int y, int z) {
        val thiz = (Block)(Object)this;
        if (thiz instanceof BlockStainedGlass) {
            val c = LightConstants.colors[colorChannel][(~meta) & 0xF];
            return 15 - c;
        }
        return getLightOpacity();
    }
}
