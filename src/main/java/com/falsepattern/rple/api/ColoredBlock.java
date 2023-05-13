/*
 * Copyright (c) 2023 FalsePattern
 * This work is licensed under the Creative Commons Attribution-NonCommercial-NoDerivatives 4.0 International License.
 * To view a copy of this license, visit http://creativecommons.org/licenses/by-nc-nd/4.0/
 * or send a letter to Creative Commons, PO Box 1866, Mountain View, CA 94042, USA.
 *
 */

package com.falsepattern.rple.api;

import net.minecraft.block.Block;
import net.minecraft.world.IBlockAccess;

/**
 * This class is implicitly implemented on {@link Block} using a mixin. You can safely cast any class that extends Block
 * to this interface.
 * @apiNote Only override these methods if you know what you are doing! The default implementation is good enough for
 * 99% of cases.
 */
public interface ColoredBlock {
    int getColoredLightValue(IBlockAccess world, int meta, int colorChannel, int x, int y, int z);
    int getColoredLightOpacity(IBlockAccess world, int meta, int colorChannel, int x, int y, int z);

    void setColoredLightValue(int meta, int r, int g, int b);
    void setColoredLightOpacity(int meta, int r, int g, int b);
}
