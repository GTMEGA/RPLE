/*
 * Copyright (c) 2023 FalsePattern, Ven
 * This work is licensed under the Creative Commons Attribution-NonCommercial-NoDerivatives 4.0 International License.
 * To view a copy of this license, visit http://creativecommons.org/licenses/by-nc-nd/4.0/
 * or send a letter to Creative Commons, PO Box 1866, Mountain View, CA 94042, USA.
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
