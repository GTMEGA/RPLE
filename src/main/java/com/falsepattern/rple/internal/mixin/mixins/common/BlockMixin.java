package com.falsepattern.rple.internal.mixin.mixins.common;

import com.falsepattern.rple.internal.storage.ColoredBlock;
import com.falsepattern.rple.internal.storage.ColoredLightChannel;
import com.falsepattern.rple.internal.storage.ColoredLightWorld;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

import net.minecraft.block.Block;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

@Mixin(Block.class)
public abstract class BlockMixin implements ColoredBlock {
    @Shadow public abstract int getLightValue(IBlockAccess world, int x, int y, int z);

    @Shadow public abstract int getLightOpacity(IBlockAccess world, int x, int y, int z);

    @Override
    public int getColoredLightValue(ColoredLightWorld world, int x, int y, int z) {
        return getLightValue((World) world.root(), x, y, z);
    }

    @Override
    public int getColoredLightOpacity(ColoredLightWorld world, int x, int y, int z) {
        return getLightOpacity((World) world.root(), x, y, z);
    }
}
