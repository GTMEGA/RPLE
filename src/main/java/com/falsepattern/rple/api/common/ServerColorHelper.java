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

package com.falsepattern.rple.api.common;

import com.falsepattern.lib.StableAPI;
import com.falsepattern.lib.util.MathUtil;

@SuppressWarnings("unused")
@StableAPI(since = "1.0.0")
public final class ServerColorHelper {
    public static final int COLOR_MIN = 0;
    public static final int COLOR_MAX = 15;

    public static final int CHANNEL_4BIT_MASK = 0xF;
    public static final int CHANNEL_4BIT_TO_RGB16_RED = 8;
    public static final int CHANNEL_4BIT_TO_RGB16_GREEN = 4;
    public static final int CHANNEL_4BIT_TO_RGB16_BLUE = 0;

    private ServerColorHelper() {
        throw new UnsupportedOperationException("This is a utility class and cannot be instantiated");
    }

    @StableAPI.Expose
    public static short RGB16OpacityTranslucentSwap(short val) {
        if (val == -1)
            return -1;
        int red = invertColorComponent(red(val));
        int green = invertColorComponent(green(val));
        int blue = invertColorComponent(blue(val));

        return RGB16FromRGBChannel4Bit(red, green, blue);
    }

    @StableAPI.Expose
    public static short RGB16FromRGBChannel4Bit(int red, int green, int blue) {
        return (short) ((red & CHANNEL_4BIT_MASK) << CHANNEL_4BIT_TO_RGB16_RED |
                (green & CHANNEL_4BIT_MASK) << CHANNEL_4BIT_TO_RGB16_GREEN |
                (blue & CHANNEL_4BIT_MASK) << CHANNEL_4BIT_TO_RGB16_BLUE);
    }

    @StableAPI.Expose
    public static int red(short rgb16) {
        return ((rgb16 >> CHANNEL_4BIT_TO_RGB16_RED) & CHANNEL_4BIT_MASK);
    }

    @StableAPI.Expose
    public static int green(short rgb16) {
        return ((rgb16 >> CHANNEL_4BIT_TO_RGB16_GREEN) & CHANNEL_4BIT_MASK);
    }

    @StableAPI.Expose
    public static int blue(short rgb16) {
        return ((rgb16 >> CHANNEL_4BIT_TO_RGB16_BLUE) & CHANNEL_4BIT_MASK);
    }

    @StableAPI.Expose
    public static int lightValueFromRGB16(short rgb16) {
        return maxColorComponent(rgb16);
    }

    @StableAPI.Expose
    public static int lightOpacityFromRGB16(short rgb16) {
        return invertColorComponent(maxColorComponent(rgb16));
    }

    @StableAPI.Expose
    public static int clampColorComponent(int component) {
        return MathUtil.clamp(component, COLOR_MIN, COLOR_MAX);
    }

    @StableAPI.Expose
    public static int invertColorComponent(int component) {
        return COLOR_MAX - clampColorComponent(component);
    }

    @StableAPI.Expose
    public static int minColorComponent(short rgb16) {
        return minColorComponent(red(rgb16), green(rgb16), blue(rgb16));
    }

    @StableAPI.Expose
    public static int maxColorComponent(short rgb16) {
        return maxColorComponent(red(rgb16), green(rgb16), blue(rgb16));
    }

    @StableAPI.Expose
    public static int minColorComponent(int red, int green, int blue) {
        return Math.min(red, Math.min(green, blue));
    }

    @StableAPI.Expose
    public static int maxColorComponent(int red, int green, int blue) {
        return Math.max(red, Math.max(green, blue));
    }
}
