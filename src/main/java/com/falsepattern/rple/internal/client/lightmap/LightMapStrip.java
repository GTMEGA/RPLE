/*
 * Copyright (c) 2023 FalsePattern, Ven
 * This work is licensed under the Creative Commons Attribution-NonCommercial-NoDerivatives 4.0 International License.
 * To view a copy of this license, visit http://creativecommons.org/licenses/by-nc-nd/4.0/
 * or send a letter to Creative Commons, PO Box 1866, Mountain View, CA 94042, USA.
 */

package com.falsepattern.rple.internal.client.lightmap;

import com.falsepattern.rple.api.client.lightmap.RPLELightMapStrip;
import lombok.Getter;
import lombok.experimental.Accessors;
import lombok.val;
import lombok.var;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;

@Getter
@Accessors(fluent = true, chain = true)
@SuppressWarnings("MismatchedReadAndWriteOfArray")
public final class LightMapStrip implements RPLELightMapStrip {
    private final float[] lightMapRedData;
    private final float[] lightMapGreenData;
    private final float[] lightMapBlueData;

    public LightMapStrip() {
        lightMapRedData = new float[LightMapConstants.LIGHT_MAP_1D_SIZE];
        lightMapGreenData = new float[LightMapConstants.LIGHT_MAP_1D_SIZE];
        lightMapBlueData = new float[LightMapConstants.LIGHT_MAP_1D_SIZE];

        resetLightMap();
    }

    @Override
    public @NotNull RPLELightMapStrip setLightMap(int index, float brightness) {
        lightMapRedData[index] = brightness;
        lightMapGreenData[index] = brightness;
        lightMapBlueData[index] = brightness;

        return this;
    }

    @Override
    public @NotNull RPLELightMapStrip setLightMapRed(int index, float red) {
        lightMapRedData[index] = red;
        return this;
    }

    @Override
    public @NotNull RPLELightMapStrip setLightMapGreen(int index, float green) {
        lightMapGreenData[index] = green;
        return this;
    }

    @Override
    public @NotNull RPLELightMapStrip setLightMapBlue(int index, float blue) {
        lightMapBlueData[index] = blue;
        return this;
    }

    @Override
    public @NotNull RPLELightMapStrip setLightMapRGB(int index, float red, float green, float blue) {
        lightMapRedData[index] = red;
        lightMapGreenData[index] = green;
        lightMapBlueData[index] = blue;

        return this;
    }

    @Override
    public @NotNull RPLELightMapStrip fillLightMap(float brightness) {
        Arrays.fill(lightMapRedData, brightness);
        Arrays.fill(lightMapGreenData, brightness);
        Arrays.fill(lightMapBlueData, brightness);

        return this;
    }

    @Override
    public @NotNull RPLELightMapStrip fillLightMapRGB(float red, float green, float blue) {
        Arrays.fill(lightMapRedData, red);
        Arrays.fill(lightMapGreenData, green);
        Arrays.fill(lightMapBlueData, blue);

        return this;
    }

    @Override
    public @NotNull RPLELightMapStrip fillLightMapRed(float red) {
        Arrays.fill(lightMapRedData, red);

        return this;
    }

    @Override
    public @NotNull RPLELightMapStrip fillLightMapGreen(float green) {
        Arrays.fill(lightMapGreenData, green);

        return this;
    }

    @Override
    public @NotNull RPLELightMapStrip fillLightMapBlue(float blue) {
        Arrays.fill(lightMapBlueData, blue);
        return this;
    }

    @Override
    public @NotNull RPLELightMapStrip setLightMap(@NotNull RPLELightMapStrip strip) {
        val otherLightMapRed = strip.lightMapRedData();
        val otherLightMapGreen = strip.lightMapGreenData();
        val otherLightMapBlue = strip.lightMapBlueData();

        for (var i = 0; i < LightMapConstants.LIGHT_MAP_1D_SIZE; i++) {
            lightMapRedData[i] /= otherLightMapRed[i];
            lightMapGreenData[i] /= otherLightMapGreen[i];
            lightMapBlueData[i] /= otherLightMapBlue[i];
        }

        return this;
    }

    @Override
    public @NotNull RPLELightMapStrip addLightMap(@NotNull RPLELightMapStrip strip) {
        val otherLightMapRed = strip.lightMapRedData();
        val otherLightMapGreen = strip.lightMapGreenData();
        val otherLightMapBlue = strip.lightMapBlueData();

        for (var i = 0; i < LightMapConstants.LIGHT_MAP_1D_SIZE; i++) {
            lightMapRedData[i] += otherLightMapRed[i];
            lightMapGreenData[i] += otherLightMapGreen[i];
            lightMapBlueData[i] += otherLightMapBlue[i];
        }

        return this;
    }

    @Override
    public @NotNull RPLELightMapStrip subLightMap(@NotNull RPLELightMapStrip strip) {
        val otherLightMapRed = strip.lightMapRedData();
        val otherLightMapGreen = strip.lightMapGreenData();
        val otherLightMapBlue = strip.lightMapBlueData();

        for (var i = 0; i < LightMapConstants.LIGHT_MAP_1D_SIZE; i++) {
            lightMapRedData[i] -= otherLightMapRed[i];
            lightMapGreenData[i] -= otherLightMapGreen[i];
            lightMapBlueData[i] -= otherLightMapBlue[i];
        }

        return this;
    }

    @Override
    public @NotNull RPLELightMapStrip multLightMap(@NotNull RPLELightMapStrip strip) {
        val otherLightMapRed = strip.lightMapRedData();
        val otherLightMapGreen = strip.lightMapGreenData();
        val otherLightMapBlue = strip.lightMapBlueData();

        for (var i = 0; i < LightMapConstants.LIGHT_MAP_1D_SIZE; i++) {
            lightMapRedData[i] *= otherLightMapRed[i];
            lightMapGreenData[i] *= otherLightMapGreen[i];
            lightMapBlueData[i] *= otherLightMapBlue[i];
        }

        return this;
    }

    @Override
    public @NotNull RPLELightMapStrip divLightMap(@NotNull RPLELightMapStrip strip) {
        val otherLightMapRed = strip.lightMapRedData();
        val otherLightMapGreen = strip.lightMapGreenData();
        val otherLightMapBlue = strip.lightMapBlueData();

        for (var i = 0; i < LightMapConstants.LIGHT_MAP_1D_SIZE; i++) {
            lightMapRedData[i] /= otherLightMapRed[i];
            lightMapGreenData[i] /= otherLightMapGreen[i];
            lightMapBlueData[i] /= otherLightMapBlue[i];
        }

        return this;
    }

    @Override
    public @NotNull RPLELightMapStrip resetLightMap() {
        Arrays.fill(lightMapRedData, 1F);
        Arrays.fill(lightMapGreenData, 1F);
        Arrays.fill(lightMapBlueData, 1F);

        return this;
    }
}
