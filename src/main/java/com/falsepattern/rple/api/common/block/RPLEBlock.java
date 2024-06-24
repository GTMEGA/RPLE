/*
 * Right Proper Lighting Engine
 *
 * Copyright (C) 2023-2024 FalsePattern, Ven
 * All Rights Reserved
 *
 * The above copyright notice and this permission notice
 * shall be included in all copies or substantial portions of the Software.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program. If not, see <https://www.gnu.org/licenses/>.
 *
 * This program comes with additional permissions according to Section 7 of the
 * GNU Affero General Public License. See the full LICENSE file for details.
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
