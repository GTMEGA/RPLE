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

import com.falsepattern.lib.StableAPI;
import org.jetbrains.annotations.ApiStatus;
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
@ApiStatus.NonExtendable
@StableAPI(since = "1.0.0")
public interface RPLEBlock {
    @SuppressWarnings("CastToIncompatibleInterface")
    @StableAPI.Expose
    static RPLEBlock of(Block block) {
        return (RPLEBlock) block;
    }

    @StableAPI.Expose
    short rple$getRawBrightnessColor();

    @StableAPI.Expose
    short rple$getRawBrightnessColor(int blockMeta);

    @StableAPI.Expose
    short rple$getRawBrightnessColor(@NotNull IBlockAccess world,
                                     int blockMeta,
                                     int posX,
                                     int posY,
                                     int posZ);

    @StableAPI.Expose
    short rple$getRawOpacityColor();

    @StableAPI.Expose
    short rple$getRawOpacityColor(int blockMeta);

    @StableAPI.Expose
    short rple$getRawOpacityColor(@NotNull IBlockAccess world,
                                  int blockMeta,
                                  int posX,
                                  int posY,
                                  int posZ);

    @StableAPI.Expose
    short rple$getBrightnessColor();

    @StableAPI.Expose
    short rple$getBrightnessColor(int blockMeta);

    @StableAPI.Expose
    short rple$getBrightnessColor(@NotNull IBlockAccess world,
                                      int blockMeta,
                                      int posX,
                                      int posY,
                                      int posZ);

    @StableAPI.Expose
    short rple$getTranslucencyColor();

    @StableAPI.Expose
    short rple$getTranslucencyColor(int blockMeta);

    @StableAPI.Expose
    short rple$getTranslucencyColor(@NotNull IBlockAccess world,
                                        int blockMeta,
                                        int posX,
                                        int posY,
                                        int posZ);

    @StableAPI.Expose
    short rple$getFallbackBrightnessColor();

    @StableAPI.Expose
    short rple$getFallbackBrightnessColor(int blockMeta);

    @StableAPI.Expose
    short rple$getFallbackBrightnessColor(@NotNull IBlockAccess world,
                                              int blockMeta,
                                              int posX,
                                              int posY,
                                              int posZ);

    @StableAPI.Expose
    short rple$getFallbackTranslucencyColor();

    @StableAPI.Expose
    short rple$getFallbackTranslucencyColor(int blockMeta);

    @StableAPI.Expose
    short rple$getFallbackTranslucencyColor(@NotNull IBlockAccess world,
                                                int blockMeta,
                                                int posX,
                                                int posY,
                                                int posZ);

    @StableAPI.Expose
    short rple$getConfiguredBrightnessColor();

    @StableAPI.Expose
    short rple$getConfiguredBrightnessColor(int blockMeta);

    @StableAPI.Expose
    short rple$getConfiguredTranslucencyColor();

    @StableAPI.Expose
    short rple$getConfiguredTranslucencyColor(int blockMeta);
}
