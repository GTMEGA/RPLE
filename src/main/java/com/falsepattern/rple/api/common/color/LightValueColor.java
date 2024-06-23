/*
 * Copyright (c) 2023 FalsePattern, Ven
 * This work is licensed under the Creative Commons Attribution-NonCommercial-NoDerivatives 4.0 International License.
 * To view a copy of this license, visit http://creativecommons.org/licenses/by-nc-nd/4.0/
 * or send a letter to Creative Commons, PO Box 1866, Mountain View, CA 94042, USA.
 */

package com.falsepattern.rple.api.common.color;

import com.falsepattern.rple.api.common.ServerColorHelper;
import lombok.val;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public enum LightValueColor implements IPaletteColor {
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
    LIGHT_VALUE_15,
    ;

    private static final LightValueColor[] VALUES = values();
    public static final String LIGHT_LEVEL_COLOR_DOMAIN = "light_value";

    private final short rgb16;
    private final String colorName;

    LightValueColor() {
        final int lightValue = ordinal();

        this.rgb16 = ServerColorHelper.RGB16FromRGBChannel4Bit(lightValue, lightValue, lightValue);

        this.colorName = String.valueOf(lightValue);
    }

    public static @NotNull LightValueColor fromVanillaLightValue(int vanillaLightValue) {
        final int index = vanillaLightValue & 15;
        return VALUES[index];
    }

    public static @NotNull LightValueColor fromVanillaLightOpacity(int vanillaLightOpacity) {
        final int index = 15 - (vanillaLightOpacity & 15);
        return VALUES[index];
    }

    public static @Nullable LightValueColor attemptMapToEnum(short rgb16) {
        for (val otherColor : VALUES) {
            if (otherColor.rgb16 == rgb16)
                return otherColor;
        }
        return null;
    }

    @Override
    public short rgb16() {
        return rgb16;
    }

    @Override
    public @NotNull String colorName() {
        return colorName;
    }

    @Override
    public @NotNull String colorDomain() {
        return LIGHT_LEVEL_COLOR_DOMAIN;
    }
}
