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

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Range;

public interface RPLELightMapStrip {
    int LIGHT_MAP_STRIP_LENGTH = 16;
    int LIGHT_MAP_STRIP_LIMIT = LIGHT_MAP_STRIP_LENGTH - 1;

    float @NotNull [] lightMapRedData();

    float @NotNull [] lightMapGreenData();

    float @NotNull [] lightMapBlueData();

    @NotNull RPLELightMapStrip setLightMap(@Range(from = 0, to = LIGHT_MAP_STRIP_LIMIT) int index, float brightness);

    @NotNull RPLELightMapStrip setLightMapRGB(@Range(from = 0, to = LIGHT_MAP_STRIP_LIMIT) int index, float red, float green, float blue);

    @NotNull RPLELightMapStrip setLightMapRed(@Range(from = 0, to = LIGHT_MAP_STRIP_LIMIT) int index, float red);

    @NotNull RPLELightMapStrip setLightMapGreen(@Range(from = 0, to = LIGHT_MAP_STRIP_LIMIT) int index, float green);

    @NotNull RPLELightMapStrip setLightMapBlue(@Range(from = 0, to = LIGHT_MAP_STRIP_LIMIT) int index, float blue);

    @NotNull RPLELightMapStrip fillLightMap(float brightness);

    @NotNull RPLELightMapStrip fillLightMapRGB(float red, float green, float blue);

    @NotNull RPLELightMapStrip fillLightMapRed(float red);

    @NotNull RPLELightMapStrip fillLightMapGreen(float green);

    @NotNull RPLELightMapStrip fillLightMapBlue(float blue);

    @NotNull RPLELightMapStrip setLightMap(@NotNull RPLELightMapStrip strip);

    @NotNull RPLELightMapStrip addLightMap(@NotNull RPLELightMapStrip strip);

    @NotNull RPLELightMapStrip subLightMap(@NotNull RPLELightMapStrip strip);

    @NotNull RPLELightMapStrip multLightMap(@NotNull RPLELightMapStrip strip);

    @NotNull RPLELightMapStrip divLightMap(@NotNull RPLELightMapStrip strip);

    @NotNull RPLELightMapStrip resetLightMap();
}
