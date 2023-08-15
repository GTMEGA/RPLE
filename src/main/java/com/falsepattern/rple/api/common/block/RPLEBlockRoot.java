/*
 * Copyright (c) 2023 FalsePattern, Ven
 * This work is licensed under the Creative Commons Attribution-NonCommercial-NoDerivatives 4.0 International License.
 * To view a copy of this license, visit http://creativecommons.org/licenses/by-nc-nd/4.0/
 * or send a letter to Creative Commons, PO Box 1866, Mountain View, CA 94042, USA.
 */

package com.falsepattern.rple.api.common.block;

import com.falsepattern.rple.api.common.color.RPLEColor;
import net.minecraft.block.Block;
import net.minecraft.world.IBlockAccess;

/**
 * This interface is implemented onto the {@link Block Block Class} by RPLE.
 * <p>
 * It is used as the root of the RPLE implementation and will provide the internal brightness/translucency values as defined by the {@link Block} using it's base methods.
 * <p>
 * You <b>SHOULD NOT</b> implement it yourself, but you can cast any instance of {@link Block} into it.
 *
 * @see RPLEBlockBrightnessColorProvider
 * @see RPLEBlockTranslucencyColorProvider
 */
public interface RPLEBlockRoot {
    /**
     * Returns the translucency as defined by: {@link Block#getLightValue()}
     *
     * @return Internal brightness value
     */
    RPLEColor rple$getInternalColoredBrightness();

    /**
     * Returns the translucency as defined by: {@link Block#getLightValue(IBlockAccess, int, int, int)}
     *
     * @return Internal brightness value
     */
    RPLEColor rple$getInternalColoredBrightness(IBlockAccess world, int posX, int posY, int posZ);

    /**
     * Returns the translucency as defined by: {@link Block#getLightOpacity()}
     *
     * @return Internal translucency value
     */
    RPLEColor rple$getInternalColoredTranslucency();

    /**
     * Returns the translucency as defined by: {@link Block#getLightOpacity(IBlockAccess, int, int, int)}
     *
     * @return Internal translucency value
     */
    RPLEColor rple$getInternalColoredTranslucency(IBlockAccess world, int posX, int posY, int posZ);
}
