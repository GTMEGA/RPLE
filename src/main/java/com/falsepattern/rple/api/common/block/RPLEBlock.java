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
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

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

    @Deprecated
    @NotNull
    RPLEColor rple$getBrightnessColor();

    @Deprecated
    @NotNull
    RPLEColor rple$getBrightnessColor(int blockMeta);

    @Deprecated
    @NotNull
    RPLEColor rple$getBrightnessColor(@NotNull IBlockAccess world,
                                      int blockMeta,
                                      int posX,
                                      int posY,
                                      int posZ);

    @Deprecated
    @NotNull
    RPLEColor rple$getTranslucencyColor();

    @Deprecated
    @NotNull
    RPLEColor rple$getTranslucencyColor(int blockMeta);

    @Deprecated
    @NotNull
    RPLEColor rple$getTranslucencyColor(@NotNull IBlockAccess world,
                                        int blockMeta,
                                        int posX,
                                        int posY,
                                        int posZ);

    @Deprecated
    @NotNull
    RPLEColor rple$getFallbackBrightnessColor();

    @Deprecated
    @NotNull
    RPLEColor rple$getFallbackBrightnessColor(int blockMeta);

    @Deprecated
    @NotNull
    RPLEColor rple$getFallbackBrightnessColor(@NotNull IBlockAccess world,
                                              int blockMeta,
                                              int posX,
                                              int posY,
                                              int posZ);

    @Deprecated
    @NotNull
    RPLEColor rple$getFallbackTranslucencyColor();

    @Deprecated
    @NotNull
    RPLEColor rple$getFallbackTranslucencyColor(int blockMeta);

    @Deprecated
    @NotNull
    RPLEColor rple$getFallbackTranslucencyColor(@NotNull IBlockAccess world,
                                                int blockMeta,
                                                int posX,
                                                int posY,
                                                int posZ);

    @Deprecated
    @Nullable
    RPLEColor rple$getConfiguredBrightnessColor();

    @Deprecated
    @Nullable
    RPLEColor rple$getConfiguredBrightnessColor(int blockMeta);

    @Deprecated
    @Nullable
    RPLEColor rple$getConfiguredTranslucencyColor();

    @Deprecated
    @Nullable
    RPLEColor rple$getConfiguredTranslucencyColor(int blockMeta);
}
