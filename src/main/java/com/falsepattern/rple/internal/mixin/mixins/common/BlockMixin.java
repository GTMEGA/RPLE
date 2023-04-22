package com.falsepattern.rple.internal.mixin.mixins.common;

import com.falsepattern.rple.internal.Utils;
import com.falsepattern.rple.internal.storage.ColoredBlock;
import com.falsepattern.rple.internal.storage.ColoredLightChannel;
import lombok.val;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.world.IBlockAccess;

@Mixin(Block.class)
public abstract class BlockMixin implements ColoredBlock {
    @Shadow public abstract int getLightValue(IBlockAccess world, int x, int y, int z);

    @Shadow public abstract int getLightOpacity(IBlockAccess world, int x, int y, int z);

    @Override
    public int getColoredLightValue(IBlockAccess world, ColoredLightChannel channel, int x, int y, int z) {
        val thiz = (Block)(Object)this;
        if (thiz == Blocks.torch) {
            switch (channel) {
                case RED: return 14;
                case GREEN: return 12;
                case BLUE: return 6;
            }
        } else if (thiz == Blocks.redstone_block) {
            switch (channel) {
                case RED: return 15;
                case GREEN: return 4;
                case BLUE: return 3;
            }
        } else if (thiz == Blocks.lapis_block) {
            switch (channel) {
                case RED: return 3;
                case GREEN: return 4;
                case BLUE: return 15;
            }
        }
        return getLightValue(world, x, y, z);
    }

    @Override
    public int getColoredLightOpacity(IBlockAccess world, ColoredLightChannel channel, int x, int y, int z) {
        return getLightOpacity(world, x, y, z);
    }

    @Override
    public int getLightValuePacked(IBlockAccess world, int x, int y, int z) {
        return Utils.PACKED_BIT_BLOCK_COLOR |
               getColoredLightValue(world, ColoredLightChannel.RED, x, y, z) << 8 |
               getColoredLightValue(world, ColoredLightChannel.GREEN, x, y, z) << 4 |
               getColoredLightValue(world, ColoredLightChannel.BLUE, x, y, z);
    }
}
