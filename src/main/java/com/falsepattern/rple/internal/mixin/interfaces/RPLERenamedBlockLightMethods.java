package com.falsepattern.rple.internal.mixin.interfaces;

import net.minecraft.world.IBlockAccess;

public interface RPLERenamedBlockLightMethods {
    int rple$renamed$getLightValue();

    int rple$renamed$getLightValue(IBlockAccess world, int posX, int posY, int posZ);

    int rple$renamed$getLightOpacity();

    int rple$renamed$getLightOpacity(IBlockAccess world, int posX, int posY, int posZ);
}
