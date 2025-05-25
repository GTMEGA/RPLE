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

import com.falsepattern.lib.StableAPI;
import org.jetbrains.annotations.ApiStatus;

import net.minecraft.block.Block;
import net.minecraft.world.IBlockAccess;

/**
 * This interface is implemented onto the {@link Block Block Class} by RPLE.
 * <p>
 * It is used as the root of the RPLE implementation and will provide the internal brightness/translucency values as defined by the {@link Block} using it's base methods.
 * <p>
 * You <b>SHOULD NOT</b> implement it yourself, but you can cast any instance of {@link Block} into it.
 *
 * @see RPLECustomBlockBrightness
 * @see RPLECustomBlockTranslucency
 */
@ApiStatus.NonExtendable
@StableAPI(since = "1.0.0")
public interface RPLEBlockRoot {
    @SuppressWarnings("CastToIncompatibleInterface")
    @StableAPI.Expose
    static RPLEBlockRoot of(Block block) {
        return (RPLEBlockRoot) block;
    }

    /**
     * Returns the translucency as defined by: {@link Block#getLightValue()}
     *
     * @return Internal brightness value
     */
    @StableAPI.Expose
    short rple$getRawInternalColoredBrightness();

    /**
     * Returns the translucency as defined by: {@link Block#getLightValue(IBlockAccess, int, int, int)}
     *
     * @return Internal brightness value
     */
    @StableAPI.Expose
    short rple$getRawInternalColoredBrightness(IBlockAccess world, int posX, int posY, int posZ);

    /**
     * Returns the translucency as defined by: {@link Block#getLightOpacity()}
     *
     * @return Internal translucency value
     */
    @StableAPI.Expose
    short rple$getRawInternalColoredOpacity();

    /**
     * Returns the translucency as defined by: {@link Block#getLightOpacity(IBlockAccess, int, int, int)}
     *
     * @return Internal translucency value
     */
    @StableAPI.Expose
    short rple$getRawInternalColoredOpacity(IBlockAccess world, int posX, int posY, int posZ);

    /**
     * Returns the translucency as defined by: {@link Block#getLightValue()}
     *
     * @return Internal brightness value
     */
    @StableAPI.Expose
    short rple$getInternalColoredBrightness();

    /**
     * Returns the translucency as defined by: {@link Block#getLightValue(IBlockAccess, int, int, int)}
     *
     * @return Internal brightness value
     */
    @StableAPI.Expose
    short rple$getInternalColoredBrightness(IBlockAccess world, int posX, int posY, int posZ);

    /**
     * Returns the translucency as defined by: {@link Block#getLightOpacity()}
     *
     * @return Internal translucency value
     */
    @StableAPI.Expose
    short rple$getInternalColoredTranslucency();

    /**
     * Returns the translucency as defined by: {@link Block#getLightOpacity(IBlockAccess, int, int, int)}
     *
     * @return Internal translucency value
     */
    @StableAPI.Expose
    short rple$getInternalColoredTranslucency(IBlockAccess world, int posX, int posY, int posZ);
}
