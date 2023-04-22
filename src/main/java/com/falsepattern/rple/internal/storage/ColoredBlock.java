package com.falsepattern.rple.internal.storage;

import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

public interface ColoredBlock {
    int getColoredLightValue(IBlockAccess world, ColoredLightChannel channel, int x, int y, int z);
    int getColoredLightOpacity(IBlockAccess world, ColoredLightChannel channel, int x, int y, int z);
    int getLightValuePacked(IBlockAccess world, int x, int y, int z);
}
