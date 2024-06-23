/*
 * Copyright (c) 2023 FalsePattern, Ven
 * This work is licensed under the Creative Commons Attribution-NonCommercial-NoDerivatives 4.0 International License.
 * To view a copy of this license, visit http://creativecommons.org/licenses/by-nc-nd/4.0/
 * or send a letter to Creative Commons, PO Box 1866, Mountain View, CA 94042, USA.
 */

package com.falsepattern.rple.api.common.block;

import net.minecraft.block.Block;
import net.minecraft.world.IBlockAccess;
import org.jetbrains.annotations.NotNull;

/**
 * This interface is implemented onto the {@link Block Block Class} by RPLE.
 * <p>
 * It is used as the base of the
 * <p>
 * You <b>SHOULD NOT</b> implement it yourself, but you can cast any instance of {@link Block} into it.
 *
 * @see RPLECustomBlockBrightness
 * @see RPLECustomBlockTranslucency
 */
public interface RPLEBlock {
    @SuppressWarnings("CastToIncompatibleInterface")
    static RPLEBlock of(Block block) {
        return (RPLEBlock) block;
    }

    short rple$getRawBrightnessColor();

    short rple$getRawBrightnessColor(int blockMeta);

    short rple$getRawBrightnessColor(@NotNull IBlockAccess world,
                                     int blockMeta,
                                     int posX,
                                     int posY,
                                     int posZ);

    short rple$getRawOpacityColor();

    short rple$getRawOpacityColor(int blockMeta);

    short rple$getRawOpacityColor(@NotNull IBlockAccess world,
                                  int blockMeta,
                                  int posX,
                                  int posY,
                                  int posZ);

    short rple$getBrightnessColor();

    short rple$getBrightnessColor(int blockMeta);

    short rple$getBrightnessColor(@NotNull IBlockAccess world,
                                      int blockMeta,
                                      int posX,
                                      int posY,
                                      int posZ);

    short rple$getTranslucencyColor();

    short rple$getTranslucencyColor(int blockMeta);

    short rple$getTranslucencyColor(@NotNull IBlockAccess world,
                                        int blockMeta,
                                        int posX,
                                        int posY,
                                        int posZ);

    short rple$getFallbackBrightnessColor();

    short rple$getFallbackBrightnessColor(int blockMeta);

    short rple$getFallbackBrightnessColor(@NotNull IBlockAccess world,
                                              int blockMeta,
                                              int posX,
                                              int posY,
                                              int posZ);

    short rple$getFallbackTranslucencyColor();

    short rple$getFallbackTranslucencyColor(int blockMeta);

    short rple$getFallbackTranslucencyColor(@NotNull IBlockAccess world,
                                                int blockMeta,
                                                int posX,
                                                int posY,
                                                int posZ);

    short rple$getConfiguredBrightnessColor();

    short rple$getConfiguredBrightnessColor(int blockMeta);

    short rple$getConfiguredTranslucencyColor();

    short rple$getConfiguredTranslucencyColor(int blockMeta);
}
