/*
 * Copyright (c) 2023 FalsePattern, Ven
 * This work is licensed under the Creative Commons Attribution-NonCommercial-NoDerivatives 4.0 International License.
 * To view a copy of this license, visit http://creativecommons.org/licenses/by-nc-nd/4.0/
 * or send a letter to Creative Commons, PO Box 1866, Mountain View, CA 94042, USA.
 */

package com.falsepattern.rple.internal.mixin.mixins.common;

import com.falsepattern.rple.api.ColoredBlock;
import com.falsepattern.rple.api.LightConstants;
import com.falsepattern.rple.internal.Compat;
import com.falsepattern.rple.internal.mixin.helpers.MultipartColorHelper;
import com.falsepattern.rple.internal.mixin.interfaces.ColoredBlockInternal;
import lombok.val;
import net.minecraft.block.Block;
import net.minecraft.block.BlockStainedGlass;
import net.minecraft.world.IBlockAccess;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(Block.class)
public abstract class BlockMixin implements ColoredBlock, ColoredBlockInternal {
    @Shadow public abstract int getLightValue();

    @Shadow public abstract int getLightOpacity();

    @Shadow protected boolean useNeighborBrightness;

    private int[][] colorLightValue;
    private int[][] colorOpacity;

    @Override
    public void setColoredLightValue(int meta, int r, int g, int b) {
        colorLightValue = add(colorLightValue, meta, r, g, b);
    }

    @Override
    public void setColoredLightOpacity(int meta, int r, int g, int b) {
        colorOpacity = add(colorOpacity, meta, r, g, b);
    }

    private static int[][] add(int[][] oldArray, int meta, int r, int g, int b) {
        if (oldArray == null) {
            val newArray = new int[meta + 1][];
            for (int i = 0; i <= meta; i++) {
                setChannels(r, g, b, newArray[meta] = new int[3]);
            }
            return newArray;
        } else if (oldArray.length <= meta) {
            int oldLength = oldArray.length;
            val newArray = new int[meta + 1][];
            for (int i = 0; i < oldLength; i++) {
                newArray[i] = oldArray[i];
            }
            for (int i = oldLength; i <= meta; i++) {
                setChannels(r, g, b, newArray[meta] = new int[3]);
            }
            return newArray;
        } else {
            val element = oldArray[meta];
            setChannels(r, g, b, element);
            return oldArray;
        }
    }

    private static void setChannels(int r, int g, int b, int[] element) {
        element[LightConstants.COLOR_CHANNEL_RED] = r;
        element[LightConstants.COLOR_CHANNEL_GREEN] = g;
        element[LightConstants.COLOR_CHANNEL_BLUE] = b;
    }

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
        val r = getColoredLightValueRaw(meta, LightConstants.COLOR_CHANNEL_RED);
        val g = getColoredLightValueRaw(meta, LightConstants.COLOR_CHANNEL_GREEN);
        val b = getColoredLightValueRaw(meta, LightConstants.COLOR_CHANNEL_BLUE);
        return r > g ? Math.max(r, b) : Math.max(g, b);
    }

    //TODO Implement this. Cut feature due to lack of time.
//    /**
//     * @author FalsePattern
//     * @reason TODO
//     */
//    @Overwrite
//    public boolean getUseNeighborBrightness() {
//        return ((Object)this) instanceof BlockStainedGlass ? false : useNeighborBrightness;
//    }

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
        val thiz = (Block) (Object) this;
        if (world != null) {
            val block = world.getBlock(x, y, z);
            if (block != thiz) {
                return ((ColoredBlock)block).getColoredLightValue(world, meta, colorChannel, x, y, z);
            } else if (Compat.isMultipart(block) && Compat.projRedLightsPresent()) {
                int res = MultipartColorHelper.getColoredLightValue(block, world, meta, colorChannel, x, y, z);
                if (res >= 0) {
                    return res;
                }
            }
        }
        if (colorLightValue == null) {
            if (world != null) {
                return getLightValue(world, x, y, z);
            } else {
                return getLightValue();
            }
        }

        return getColoredLightValueRaw(meta, colorChannel);
    }

    @Override
    public int getColoredLightValueRaw(int meta, int colorChannel) {
        if (colorLightValue == null) {
            return getLightValue();
        } else {
            if (meta >= colorLightValue.length) {
                meta = 0;
            }
            return colorLightValue[meta][colorChannel];
        }
    }

    @Override
    public int getColoredLightOpacity(IBlockAccess world, int meta, int colorChannel, int x, int y, int z) {
        // TODO: Remove this back once the opacity configs are done, again very helpful for debugging right now!
        val thiz = (Block)(Object)this;
        if (thiz instanceof BlockStainedGlass) {
            val c = LightConstants.colors[colorChannel][(~meta) & 0xF];
            return 15 - c;
        }
        return getColoredLightOpacityRaw(meta, colorChannel);
    }

    @Override
    public int getColoredLightOpacityRaw(int meta, int colorChannel) {
        if (colorOpacity == null) {
            return getLightOpacity();
        } else {
            if (meta >= colorOpacity.length) {
                meta = 0;
            }
            return colorOpacity[meta][colorChannel];
        }
    }
}
