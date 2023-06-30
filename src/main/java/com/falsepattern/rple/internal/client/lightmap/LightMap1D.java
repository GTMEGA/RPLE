/*
 * Copyright (c) 2023 FalsePattern, Ven
 * This work is licensed under the Creative Commons Attribution-NonCommercial-NoDerivatives 4.0 International License.
 * To view a copy of this license, visit http://creativecommons.org/licenses/by-nc-nd/4.0/
 * or send a letter to Creative Commons, PO Box 1866, Mountain View, CA 94042, USA.
 */

package com.falsepattern.rple.internal.client.lightmap;

import com.falsepattern.rple.api.lightmap.LightMapStrip;
import lombok.Getter;
import lombok.experimental.Accessors;
import lombok.val;
import lombok.var;

import java.util.Arrays;

import static com.falsepattern.rple.internal.Common.LIGHT_MAP_1D_SIZE;

@Getter
@Accessors(fluent = true, chain = true)
@SuppressWarnings("MismatchedReadAndWriteOfArray")
public final class LightMap1D implements LightMapStrip {
    private final float[] lightMapRedData;
    private final float[] lightMapGreenData;
    private final float[] lightMapBlueData;

    public LightMap1D() {
        lightMapRedData = new float[LIGHT_MAP_1D_SIZE];
        lightMapGreenData = new float[LIGHT_MAP_1D_SIZE];
        lightMapBlueData = new float[LIGHT_MAP_1D_SIZE];

        resetLightMap();
    }

    @Override
    public LightMapStrip setLightMap(int index, float brightness) {
        lightMapRedData[index] = brightness;
        lightMapGreenData[index] = brightness;
        lightMapBlueData[index] = brightness;

        return this;
    }

    @Override
    public LightMapStrip setLightMapRed(int index, float red) {
        lightMapRedData[index] = red;
        return this;
    }

    @Override
    public LightMapStrip setLightMapGreen(int index, float green) {
        lightMapGreenData[index] = green;
        return this;
    }

    @Override
    public LightMapStrip setLightMapBlue(int index, float blue) {
        lightMapBlueData[index] = blue;
        return this;
    }

    @Override
    public LightMapStrip setLightMapRGB(int index, float red, float green, float blue) {
        lightMapRedData[index] = red;
        lightMapGreenData[index] = green;
        lightMapBlueData[index] = blue;

        return this;
    }

    @Override
    public LightMapStrip fillLightMap(float brightness) {
        Arrays.fill(lightMapRedData, brightness);
        Arrays.fill(lightMapGreenData, brightness);
        Arrays.fill(lightMapBlueData, brightness);

        return this;
    }

    @Override
    public LightMapStrip fillLightMapRGB(float red, float green, float blue) {
        Arrays.fill(lightMapRedData, red);
        Arrays.fill(lightMapGreenData, green);
        Arrays.fill(lightMapBlueData, blue);

        return this;
    }

    @Override
    public LightMapStrip fillLightMapRed(float red) {
        Arrays.fill(lightMapRedData, red);

        return this;
    }

    @Override
    public LightMapStrip fillLightMapGreen(float green) {
        Arrays.fill(lightMapGreenData, green);

        return this;
    }

    @Override
    public LightMapStrip fillLightMapBlue(float blue) {
        Arrays.fill(lightMapBlueData, blue);
        return this;
    }

    @Override
    public LightMapStrip setLightMap(LightMapStrip strip) {
        val otherLightMapRed = strip.lightMapRedData();
        val otherLightMapGreen = strip.lightMapGreenData();
        val otherLightMapBlue = strip.lightMapBlueData();

        for (var i = 0; i < LIGHT_MAP_1D_SIZE; i++) {
            lightMapRedData[i] /= otherLightMapRed[i];
            lightMapGreenData[i] /= otherLightMapGreen[i];
            lightMapBlueData[i] /= otherLightMapBlue[i];
        }

        return this;
    }

    @Override
    public LightMapStrip addLightMap(LightMapStrip strip) {
        val otherLightMapRed = strip.lightMapRedData();
        val otherLightMapGreen = strip.lightMapGreenData();
        val otherLightMapBlue = strip.lightMapBlueData();

        for (var i = 0; i < LIGHT_MAP_1D_SIZE; i++) {
            lightMapRedData[i] += otherLightMapRed[i];
            lightMapGreenData[i] += otherLightMapGreen[i];
            lightMapBlueData[i] += otherLightMapBlue[i];
        }

        return this;
    }

    @Override
    public LightMapStrip subLightMap(LightMapStrip strip) {
        val otherLightMapRed = strip.lightMapRedData();
        val otherLightMapGreen = strip.lightMapGreenData();
        val otherLightMapBlue = strip.lightMapBlueData();

        for (var i = 0; i < LIGHT_MAP_1D_SIZE; i++) {
            lightMapRedData[i] -= otherLightMapRed[i];
            lightMapGreenData[i] -= otherLightMapGreen[i];
            lightMapBlueData[i] -= otherLightMapBlue[i];
        }

        return this;
    }

    @Override
    public LightMapStrip multLightMap(LightMapStrip strip) {
        val otherLightMapRed = strip.lightMapRedData();
        val otherLightMapGreen = strip.lightMapGreenData();
        val otherLightMapBlue = strip.lightMapBlueData();

        for (var i = 0; i < LIGHT_MAP_1D_SIZE; i++) {
            lightMapRedData[i] *= otherLightMapRed[i];
            lightMapGreenData[i] *= otherLightMapGreen[i];
            lightMapBlueData[i] *= otherLightMapBlue[i];
        }

        return this;
    }

    @Override
    public LightMapStrip divLightMap(LightMapStrip strip) {
        val otherLightMapRed = strip.lightMapRedData();
        val otherLightMapGreen = strip.lightMapGreenData();
        val otherLightMapBlue = strip.lightMapBlueData();

        for (var i = 0; i < LIGHT_MAP_1D_SIZE; i++) {
            lightMapRedData[i] /= otherLightMapRed[i];
            lightMapGreenData[i] /= otherLightMapGreen[i];
            lightMapBlueData[i] /= otherLightMapBlue[i];
        }

        return this;
    }

    @Override
    public LightMapStrip resetLightMap() {
        Arrays.fill(lightMapRedData, 1F);
        Arrays.fill(lightMapGreenData, 1F);
        Arrays.fill(lightMapBlueData, 1F);

        return this;
    }
}
