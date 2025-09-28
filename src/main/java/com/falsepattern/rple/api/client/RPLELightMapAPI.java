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

package com.falsepattern.rple.api.client;

import com.falsepattern.rple.internal.client.lightmap.LightMapPipeline;
import lombok.val;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;

@ApiStatus.AvailableSince("2.1.0")
public final class RPLELightMapAPI {
    /**
     * Provides the last updated lightmap data, packed into ints as ARGB.
     *
     * @param out Mixed lightmap data output
     */
    public static void getMixedLightMapData(int @NotNull [] out) {
        if (out.length != 256) {
            throw new IllegalArgumentException("Output must be 16x16");
        }
        val data = LightMapPipeline.lightMapPipeline().mixedLightMapData();
        System.arraycopy(data, 0, out, 0, 256);
    }
}
