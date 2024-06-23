/*
 * Copyright (c) 2023 FalsePattern, Ven
 * This work is licensed under the Creative Commons Attribution-NonCommercial-NoDerivatives 4.0 International License.
 * To view a copy of this license, visit http://creativecommons.org/licenses/by-nc-nd/4.0/
 * or send a letter to Creative Commons, PO Box 1866, Mountain View, CA 94042, USA.
 */

package com.falsepattern.rple.api.common.block;

import com.falsepattern.rple.api.common.color.RPLEColor;
import net.minecraft.block.Block;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.IBlockAccess;
import org.jetbrains.annotations.NotNull;

/**
 * This interface can be implemented onto any {@link Block} or {@link TileEntity} to provide custom colored translucency.
 * <p>
 * If this interface is found on the target {@link Block}, the {@link TileEntity} implementation will only be used as fallback.
 *
 * @see RPLECustomBlockBrightness
 */
public interface RPLECustomBlockTranslucency {
    @NotNull RPLEColor rple$getCustomTranslucencyColor();

    @NotNull RPLEColor rple$getCustomTranslucencyColor(int blockMeta);

    @NotNull
    RPLEColor rple$getCustomTranslucencyColor(@NotNull IBlockAccess world,
                                              int blockMeta,
                                              int posX,
                                              int posY,
                                              int posZ);
}
