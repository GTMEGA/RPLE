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
