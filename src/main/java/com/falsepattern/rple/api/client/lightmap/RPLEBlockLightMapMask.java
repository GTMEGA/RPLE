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

package com.falsepattern.rple.api.client.lightmap;

import com.falsepattern.lib.DeprecationDetails;
import com.falsepattern.lib.StableAPI;
import org.jetbrains.annotations.NotNull;

@StableAPI(since = "1.0.0")
public interface RPLEBlockLightMapMask extends RPLELightMapProvider {
    @Deprecated
    @DeprecationDetails(deprecatedSince = "1.3.0")
    default boolean generateBlockLightMapMask(@NotNull RPLELightMapStrip output, float partialTick) {
        return false;
    }

    default void mutateBlockLightMap(@NotNull RPLELightMapStrip output, float partialTick) {

    }
}
