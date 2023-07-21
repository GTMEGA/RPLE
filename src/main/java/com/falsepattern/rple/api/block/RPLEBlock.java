/*
 * Copyright (c) 2023 FalsePattern, Ven
 * This work is licensed under the Creative Commons Attribution-NonCommercial-NoDerivatives 4.0 International License.
 * To view a copy of this license, visit http://creativecommons.org/licenses/by-nc-nd/4.0/
 * or send a letter to Creative Commons, PO Box 1866, Mountain View, CA 94042, USA.
 */

package com.falsepattern.rple.api.block;

import com.falsepattern.rple.api.color.RPLEColor;
import net.minecraft.world.IBlockAccess;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public interface RPLEBlock {
    @NotNull RPLEColor rple$getBrightnessColor();

    @NotNull RPLEColor rple$getBrightnessColor(int blockMeta);

    @NotNull RPLEColor rple$getBrightnessColor(@NotNull IBlockAccess world,
                                               int blockMeta,
                                               int posX,
                                               int posY,
                                               int posZ);

    @NotNull RPLEColor rple$getTranslucencyColor();

    @NotNull RPLEColor rple$getTranslucencyColor(int blockMeta);

    @NotNull RPLEColor rple$getTranslucencyColor(@NotNull IBlockAccess world,
                                                 int blockMeta,
                                                 int posX,
                                                 int posY,
                                                 int posZ);

    @NotNull RPLEColor rple$getFallbackBrightnessColor();

    @NotNull RPLEColor rple$getFallbackBrightnessColor(int blockMeta);

    @NotNull RPLEColor rple$getFallbackBrightnessColor(@NotNull IBlockAccess world,
                                                       int blockMeta,
                                                       int posX,
                                                       int posY,
                                                       int posZ);

    @NotNull RPLEColor rple$getFallbackTranslucencyColor();

    @NotNull RPLEColor rple$getFallbackTranslucencyColor(int blockMeta);

    @NotNull RPLEColor rple$getFallbackTranslucencyColor(@NotNull IBlockAccess world,
                                                         int blockMeta,
                                                         int posX,
                                                         int posY,
                                                         int posZ);

    @Nullable RPLEColor rple$getConfiguredBrightnessColor();

    @Nullable RPLEColor rple$getConfiguredBrightnessColor(int blockMeta);

    @Nullable RPLEColor rple$getConfiguredTranslucencyColor();

    @Nullable RPLEColor rple$getConfiguredTranslucencyColor(int blockMeta);
}
