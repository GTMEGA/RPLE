/*
 * Copyright (c) 2023 FalsePattern, Ven
 * This work is licensed under the Creative Commons Attribution-NonCommercial-NoDerivatives 4.0 International License.
 * To view a copy of this license, visit http://creativecommons.org/licenses/by-nc-nd/4.0/
 * or send a letter to Creative Commons, PO Box 1866, Mountain View, CA 94042, USA.
 */

package com.falsepattern.rple.api.color;

import com.falsepattern.rple.api.RPLEColorAPI;
import lombok.Getter;
import lombok.experimental.Accessors;
import lombok.val;

@Getter
@Accessors(fluent = true, chain = false)
public enum GreyscaleColor implements RPLENamedColour {
    LEVEL_0,
    LEVEL_1,
    LEVEL_2,
    LEVEL_3,
    LEVEL_4,
    LEVEL_5,
    LEVEL_6,
    LEVEL_7,
    LEVEL_8,
    LEVEL_9,
    LEVEL_10,
    LEVEL_11,
    LEVEL_12,
    LEVEL_13,
    LEVEL_14,
    LEVEL_15,
    ;

    public static final String GREYSCALE_COLOR_DOMAIN = "greyscale";

    private final int red;
    private final int green;
    private final int blue;

    private final String colorName;

    GreyscaleColor() {
        val level = ordinal();

        this.red = level;
        this.green = level;
        this.blue = level;

        this.colorName = String.valueOf(level);
    }

    @Override
    public String colorDomain() {
        return GREYSCALE_COLOR_DOMAIN;
    }

    public static GreyscaleColor fromVanillaLightValue(int vanillaLightValue) {
        val ordinal = RPLEColorAPI.clampColorComponent(vanillaLightValue);
        return values()[ordinal];
    }

    public static GreyscaleColor fromVanillaLightOpacity(int vanillaLightOpacity) {
        val ordinal = RPLEColorAPI.COLOR_MAX - RPLEColorAPI.clampColorComponent(vanillaLightOpacity);
        return values()[ordinal];
    }
}
