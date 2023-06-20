/*
 * Copyright (c) 2023 FalsePattern, Ven
 * This work is licensed under the Creative Commons Attribution-NonCommercial-NoDerivatives 4.0 International License.
 * To view a copy of this license, visit http://creativecommons.org/licenses/by-nc-nd/4.0/
 * or send a letter to Creative Commons, PO Box 1866, Mountain View, CA 94042, USA.
 */

package com.falsepattern.rple.internal.mixin.mixins.common;

import com.falsepattern.rple.api.ColoredBlock;
import com.falsepattern.rple.api.LightConstants;
import com.falsepattern.rple.api.color.ColorAPI;
import com.falsepattern.rple.api.color.ColorChannel;
import com.falsepattern.rple.internal.Compat;
import com.falsepattern.rple.internal.mixin.helpers.MultipartColorHelper;
import com.falsepattern.rple.internal.mixin.interfaces.ColoredBlockInternal;
import lombok.val;
import lombok.var;
import net.minecraft.block.Block;
import net.minecraft.block.BlockStainedGlass;
import net.minecraft.world.IBlockAccess;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;


@Mixin(Block.class)
public abstract class BlockMixin implements ColoredBlock, ColoredBlockInternal {
    @Shadow
    public abstract int getLightValue();

    @Shadow
    public abstract int getLightOpacity();

    @Shadow
    protected boolean useNeighborBrightness;

    private int @Nullable [][] colorLightValue = null;
    private int @Nullable [][] colorOpacity = null;

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
            for (var i = 0; i <= meta; i++)
                setChannels(r, g, b, newArray[meta] = new int[3]);

            return newArray;
        }

        if (oldArray.length <= meta) {
            val oldLength = oldArray.length;
            val newArray = new int[meta + 1][];

            System.arraycopy(oldArray, 0, newArray, 0, oldLength);

            for (var i = oldLength; i <= meta; i++)
                setChannels(r, g, b, newArray[meta] = new int[3]);
            return newArray;
        }

        val element = oldArray[meta];
        setChannels(r, g, b, element);
        return oldArray;
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
        if (block != thiz())
            return block.getLightValue(world, x, y, z);
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
        if (block != thiz())
            return block.getLightOpacity(world, x, y, z);
        val meta = world.getBlockMetadata(x, y, z);

        val r = getColoredLightOpacity(world, meta, LightConstants.COLOR_CHANNEL_RED, x, y, z);
        val g = getColoredLightOpacity(world, meta, LightConstants.COLOR_CHANNEL_GREEN, x, y, z);
        val b = getColoredLightOpacity(world, meta, LightConstants.COLOR_CHANNEL_BLUE, x, y, z);

        return r < g ? Math.max(r, b) : Math.max(g, b);
    }

    @Override
    public int getColoredLightValue(IBlockAccess world, int meta, int colorChannel, int x, int y, int z) {
        if (world != null) {
            val block = world.getBlock(x, y, z);
            if (block != thiz())
                return ((ColoredBlock) block).getColoredLightValue(world, meta, colorChannel, x, y, z);

            if (Compat.isMultipart(block) && Compat.projRedLightsPresent()) {
                val res = MultipartColorHelper.getColoredLightValue(block, world, meta, colorChannel, x, y, z);
                if (res >= 0)
                    return res;
            }
        }

        if (colorLightValue != null)
            return getColoredLightValueRaw(meta, colorChannel);

        if (world != null)
            return getLightValue(world, x, y, z);
        return getLightValue();
    }

    @Override
    public int getColoredLightValueRaw(int meta, int colorChannel) {
        if (colorLightValue == null)
            return getLightValue();

        if (meta >= colorLightValue.length)
            meta = 0;
        return colorLightValue[meta][colorChannel];
    }

    @Override
    public int getColoredLightOpacity(IBlockAccess world, int meta, int colorChannel, int x, int y, int z) {
        // TODO: [PRE_RELEASE] Remove this back once the opacity configs are done, again very helpful for debugging right now!
        if (!(thiz() instanceof BlockStainedGlass))
            return getColoredLightOpacityRaw(meta, colorChannel);

        val lightColor = ColorAPI.ofBlockMeta(meta);
        val lightChannel = ColorChannel.values()[colorChannel].component(lightColor);

        return ColorAPI.COLOR_MAX - lightChannel;
    }

    @Override
    public int getColoredLightOpacityRaw(int meta, int colorChannel) {
        if (colorOpacity == null)
            return getLightOpacity();

        // TODO: [PRE_RELEASE] Ensure proper AIOOB check!
        if (meta >= colorOpacity.length)
            meta = 0;
        return colorOpacity[meta][colorChannel];
    }

    private Block thiz() {
        return (Block) (Object) this;
    }
}
