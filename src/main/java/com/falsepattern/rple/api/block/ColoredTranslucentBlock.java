/*
 * Copyright (c) 2023 FalsePattern, Ven
 * This work is licensed under the Creative Commons Attribution-NonCommercial-NoDerivatives 4.0 International License.
 * To view a copy of this license, visit http://creativecommons.org/licenses/by-nc-nd/4.0/
 * or send a letter to Creative Commons, PO Box 1866, Mountain View, CA 94042, USA.
 */

package com.falsepattern.rple.api.block;

import com.falsepattern.rple.api.color.RPLEColor;
import net.minecraft.world.IBlockAccess;

public interface ColoredTranslucentBlock {
    RPLEColor getColoredTranslucency();

    default RPLEColor getColoredTranslucency(int blockMeta) {
        return getColoredTranslucency();
    }

    default RPLEColor getColoredTranslucency(IBlockAccess world, int blockMeta, int posX, int posY, int posZ) {
        return getColoredTranslucency(blockMeta);
    }
}
