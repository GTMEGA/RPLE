/*
 * Copyright (c) 2023 FalsePattern
 * This work is licensed under the Creative Commons Attribution-NonCommercial-NoDerivatives 4.0 International License.
 * To view a copy of this license, visit http://creativecommons.org/licenses/by-nc-nd/4.0/
 * or send a letter to Creative Commons, PO Box 1866, Mountain View, CA 94042, USA.
 *
 */

package com.falsepattern.rple.api;

import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

public interface ColoredBlock {
    int getColoredLightValue(IBlockAccess world, int meta, int colorChannel, int x, int y, int z);
    int getColoredLightOpacity(IBlockAccess world, int meta, int colorChannel, int x, int y, int z);
}
