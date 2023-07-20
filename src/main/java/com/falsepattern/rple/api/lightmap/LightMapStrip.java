/*
 * Copyright (c) 2023 FalsePattern, Ven
 * This work is licensed under the Creative Commons Attribution-NonCommercial-NoDerivatives 4.0 International License.
 * To view a copy of this license, visit http://creativecommons.org/licenses/by-nc-nd/4.0/
 * or send a letter to Creative Commons, PO Box 1866, Mountain View, CA 94042, USA.
 */

package com.falsepattern.rple.api.lightmap;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Range;

public interface LightMapStrip {
    int LIGHT_MAP_STRIP_LENGTH = 16;
    int LIGHT_MAP_STRIP_LIMIT = LIGHT_MAP_STRIP_LENGTH - 1;

    float @NotNull [] lightMapRedData();

    float @NotNull [] lightMapGreenData();

    float @NotNull [] lightMapBlueData();

    @NotNull LightMapStrip setLightMap(@Range(from = 0, to = LIGHT_MAP_STRIP_LIMIT) int index, float brightness);

    @NotNull LightMapStrip setLightMapRGB(@Range(from = 0, to = LIGHT_MAP_STRIP_LIMIT) int index, float red, float green, float blue);

    @NotNull LightMapStrip setLightMapRed(@Range(from = 0, to = LIGHT_MAP_STRIP_LIMIT) int index, float red);

    @NotNull LightMapStrip setLightMapGreen(@Range(from = 0, to = LIGHT_MAP_STRIP_LIMIT) int index, float green);

    @NotNull LightMapStrip setLightMapBlue(@Range(from = 0, to = LIGHT_MAP_STRIP_LIMIT) int index, float blue);

    @NotNull LightMapStrip fillLightMap(float brightness);

    @NotNull LightMapStrip fillLightMapRGB(float red, float green, float blue);

    @NotNull LightMapStrip fillLightMapRed(float red);

    @NotNull LightMapStrip fillLightMapGreen(float green);

    @NotNull LightMapStrip fillLightMapBlue(float blue);

    @NotNull LightMapStrip setLightMap(@NotNull LightMapStrip strip);

    @NotNull LightMapStrip addLightMap(@NotNull LightMapStrip strip);

    @NotNull LightMapStrip subLightMap(@NotNull LightMapStrip strip);

    @NotNull LightMapStrip multLightMap(@NotNull LightMapStrip strip);

    @NotNull LightMapStrip divLightMap(@NotNull LightMapStrip strip);

    @NotNull LightMapStrip resetLightMap();
}
