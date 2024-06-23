package com.falsepattern.rple.api.common.block;

import net.minecraft.block.Block;
import net.minecraft.world.IBlockAccess;

public interface RPLEBlockRenamed {
    @SuppressWarnings("CastToIncompatibleInterface")
    static RPLEBlockRenamed of(Block block) {
        return (RPLEBlockRenamed) block;
    }

    int rple$renamed$getLightValue();

    int rple$renamed$getLightValue(IBlockAccess world, int posX, int posY, int posZ);

    int rple$renamed$getLightOpacity();

    int rple$renamed$getLightOpacity(IBlockAccess world, int posX, int posY, int posZ);
}
