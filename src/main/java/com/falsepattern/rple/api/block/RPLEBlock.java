/*
 * Copyright (c) 2023 FalsePattern, Ven
 * All Rights Reserved
 *
 * The above copyright notice and this permission notice shall be included
 * in all copies or substantial portions of the Software.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
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
