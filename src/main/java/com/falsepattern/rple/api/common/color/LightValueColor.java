/*
 * Right Proper Lighting Engine
 *
 * Copyright (C) 2023-2025 FalsePattern, Ven
 * All Rights Reserved
 *
 * The above copyright notice and this permission notice
 * shall be included in all copies or substantial portions of the Software.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, only version 3 of the License.
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

package com.falsepattern.rple.api.common.color;

import com.falsepattern.lib.StableAPI;
import com.falsepattern.rple.api.common.ServerColorHelper;
import org.jetbrains.annotations.NotNull;

@StableAPI(since = "1.0.0")
public enum LightValueColor implements RPLENamedColor {
    LIGHT_VALUE_0,
    LIGHT_VALUE_1,
    LIGHT_VALUE_2,
    LIGHT_VALUE_3,
    LIGHT_VALUE_4,
    LIGHT_VALUE_5,
    LIGHT_VALUE_6,
    LIGHT_VALUE_7,
    LIGHT_VALUE_8,
    LIGHT_VALUE_9,
    LIGHT_VALUE_10,
    LIGHT_VALUE_11,
    LIGHT_VALUE_12,
    LIGHT_VALUE_13,
    LIGHT_VALUE_14,
    LIGHT_VALUE_15;

    private static final LightValueColor[] VALUES = values();
    @StableAPI.Internal
    public static final String LIGHT_LEVEL_COLOR_DOMAIN = "light_value";

    private final String paletteColorName;
    private final short rgb16;

    LightValueColor() {
        final int lightValue = ordinal();

        this.rgb16 = ServerColorHelper.RGB16FromRGBChannel4Bit(lightValue, lightValue, lightValue);
        this.paletteColorName = LIGHT_LEVEL_COLOR_DOMAIN + ":" + lightValue;
    }

    @Override
    public String paletteColorName() {
        return paletteColorName;
    }

    @Override
    public short rgb16() {
        return rgb16;
    }

    @StableAPI.Expose
    public static @NotNull LightValueColor fromVanillaLightValue(int vanillaLightValue) {
        final int index = vanillaLightValue & 15;
        return VALUES[index];
    }

    @StableAPI.Expose
    public static @NotNull LightValueColor fromVanillaLightOpacity(int vanillaLightOpacity) {
        final int index = 15 - (vanillaLightOpacity & 15);
        return VALUES[index];
    }
}
