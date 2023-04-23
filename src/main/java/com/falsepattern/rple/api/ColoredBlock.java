package com.falsepattern.rple.api;

import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

public interface ColoredBlock {
    int getColoredLightValue(IBlockAccess world, int meta, int colorChannel, int x, int y, int z);
    int getColoredLightOpacity(IBlockAccess world, int meta, int colorChannel, int x, int y, int z);
}
