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

import com.falsepattern.lib.StableAPI;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Range;

@StableAPI(since = "1.0.0")
public interface RPLELightMapStrip {
    @StableAPI.Expose
    int LIGHT_MAP_STRIP_LENGTH = 16;
    @StableAPI.Expose
    int LIGHT_MAP_STRIP_LIMIT = LIGHT_MAP_STRIP_LENGTH - 1;

    @StableAPI.Expose
    float @NotNull [] lightMapRedData();

    @StableAPI.Expose
    float @NotNull [] lightMapGreenData();

    @StableAPI.Expose
    float @NotNull [] lightMapBlueData();

    @StableAPI.Expose
    @NotNull RPLELightMapStrip setLightMap(@Range(from = 0, to = LIGHT_MAP_STRIP_LIMIT) int index, float brightness);

    @StableAPI.Expose
    @NotNull RPLELightMapStrip setLightMapRGB(@Range(from = 0, to = LIGHT_MAP_STRIP_LIMIT) int index, float red, float green, float blue);

    @StableAPI.Expose
    @NotNull RPLELightMapStrip setLightMapRed(@Range(from = 0, to = LIGHT_MAP_STRIP_LIMIT) int index, float red);

    @StableAPI.Expose
    @NotNull RPLELightMapStrip setLightMapGreen(@Range(from = 0, to = LIGHT_MAP_STRIP_LIMIT) int index, float green);

    @StableAPI.Expose
    @NotNull RPLELightMapStrip setLightMapBlue(@Range(from = 0, to = LIGHT_MAP_STRIP_LIMIT) int index, float blue);

    @StableAPI.Expose
    @NotNull RPLELightMapStrip fillLightMap(float brightness);

    @StableAPI.Expose
    @NotNull RPLELightMapStrip fillLightMapRGB(float red, float green, float blue);

    @StableAPI.Expose
    @NotNull RPLELightMapStrip fillLightMapRed(float red);

    @StableAPI.Expose
    @NotNull RPLELightMapStrip fillLightMapGreen(float green);

    @StableAPI.Expose
    @NotNull RPLELightMapStrip fillLightMapBlue(float blue);

    @StableAPI.Expose
    @NotNull RPLELightMapStrip setLightMap(@NotNull RPLELightMapStrip strip);

    @StableAPI.Expose
    @NotNull RPLELightMapStrip addLightMap(@NotNull RPLELightMapStrip strip);

    @StableAPI.Expose
    @NotNull RPLELightMapStrip subLightMap(@NotNull RPLELightMapStrip strip);

    @StableAPI.Expose
    @NotNull RPLELightMapStrip mulLightMap(@NotNull RPLELightMapStrip strip);

    @StableAPI.Expose
    @NotNull RPLELightMapStrip divLightMap(@NotNull RPLELightMapStrip strip);

    @StableAPI.Expose
    @NotNull RPLELightMapStrip resetLightMap();
}
