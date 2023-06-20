/*
 * Copyright (c) 2023 FalsePattern, Ven
 * This work is licensed under the Creative Commons Attribution-NonCommercial-NoDerivatives 4.0 International License.
 * To view a copy of this license, visit http://creativecommons.org/licenses/by-nc-nd/4.0/
 * or send a letter to Creative Commons, PO Box 1866, Mountain View, CA 94042, USA.
 *
 */

package com.falsepattern.rple.api.lightmap;

import org.jetbrains.annotations.Range;

public interface LightMapStrip {
    int LIGHT_MAP_STRIP_LENGTH = 16;
    int LIGHT_MAP_STRIP_LIMIT = LIGHT_MAP_STRIP_LENGTH - 1;

    float[] lightMapRedData();

    float[] lightMapGreenData();

    float[] lightMapBlueData();

    LightMapStrip setLightMap(@Range(from = 0, to = LIGHT_MAP_STRIP_LIMIT) int index, float brightness);

    LightMapStrip setLightMapRGB(@Range(from = 0, to = LIGHT_MAP_STRIP_LIMIT) int index, float red, float green, float blue);

    LightMapStrip setLightMapRed(@Range(from = 0, to = LIGHT_MAP_STRIP_LIMIT) int index, float red);

    LightMapStrip setLightMapGreen(@Range(from = 0, to = LIGHT_MAP_STRIP_LIMIT) int index, float green);

    LightMapStrip setLightMapBlue(@Range(from = 0, to = LIGHT_MAP_STRIP_LIMIT) int index, float blue);

    LightMapStrip fillLightMap(float brightness);

    LightMapStrip fillLightMapRGB(float red, float green, float blue);

    LightMapStrip fillLightMapRed(float red);

    LightMapStrip fillLightMapGreen(float green);

    LightMapStrip fillLightMapBlue(float blue);

    LightMapStrip setLightMap(LightMapStrip strip);

    LightMapStrip addLightMap(LightMapStrip strip);

    LightMapStrip subLightMap(LightMapStrip strip);

    LightMapStrip multLightMap(LightMapStrip strip);

    LightMapStrip divLightMap(LightMapStrip strip);

    LightMapStrip resetLightMap();
}
