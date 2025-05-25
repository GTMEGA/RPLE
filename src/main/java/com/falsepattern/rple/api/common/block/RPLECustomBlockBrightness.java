/*
 * Right Proper Lighting Engine
 *
 * Copyright (C) 2023-2025 FalsePattern, Ven
 * All Rights Reserved
 *
 * The above copyright notice and this permission notice
 * shall be included in all copies or substantial portions of the Software.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, only version 3 of the License.
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
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.IBlockAccess;

import com.falsepattern.lib.StableAPI;
import org.jetbrains.annotations.NotNull;

/**
 * This interface can be implemented onto any {@link Block} or {@link TileEntity} to provide custom colored brightness.
 * <p>
 * If this interface is found on the target {@link Block}, the {@link TileEntity} implementation will only be used as fallback.
 *
 * @see RPLECustomBlockTranslucency
 */
@StableAPI(since = "1.0.0")
public interface RPLECustomBlockBrightness {
    @StableAPI.Expose
    short rple$getCustomBrightnessColor();

    @StableAPI.Expose
    short rple$getCustomBrightnessColor(int blockMeta);

    @StableAPI.Expose
    short rple$getCustomBrightnessColor(@NotNull IBlockAccess world,
                                            int blockMeta,
                                            int posX,
                                            int posY,
                                            int posZ);
}
