/*
 * Copyright (c) 2023 FalsePattern
 * This work is licensed under the Creative Commons Attribution-NonCommercial-NoDerivatives 4.0 International License.
 * To view a copy of this license, visit http://creativecommons.org/licenses/by-nc-nd/4.0/
 * or send a letter to Creative Commons, PO Box 1866, Mountain View, CA 94042, USA.
 */

package com.falsepattern.rple.api.lightmap;

public interface LightMapStrip {
    int LIGHT_MAP_STRIP_LENGTH = 16;

    float[] lightMapRedData();

    float[] lightMapGreenData();

    float[] lightMapBlueData();

    LightMapStrip setLightMap(int index, float brightness);

    LightMapStrip setLightMapRGB(int index, float red, float green, float blue);

    LightMapStrip setLightMapRed(int index, float red);

    LightMapStrip setLightMapGreen(int index, float green);

    LightMapStrip setLightMapBlue(int index, float blue);

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
