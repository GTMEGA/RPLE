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
public enum LightLevelColor implements RPLENamedColor {
    VALUE_0,
    VALUE_1,
    VALUE_2,
    VALUE_3,
    VALUE_4,
    VALUE_5,
    VALUE_6,
    VALUE_7,
    VALUE_8,
    VALUE_9,
    VALUE_10,
    VALUE_11,
    VALUE_12,
    VALUE_13,
    VALUE_14,
    VALUE_15,
    ;

    public static final String LIGHT_LEVEL_COLOR_DOMAIN = "light_level";

    private final int red;
    private final int green;
    private final int blue;

    private final String colorName;

    LightLevelColor() {
        val level = ordinal();

        this.red = level;
        this.green = level;
        this.blue = level;

        this.colorName = String.valueOf(level);
    }

    @Override
    public String colorDomain() {
        return LIGHT_LEVEL_COLOR_DOMAIN;
    }

    public static LightLevelColor fromVanillaLightValue(int vanillaLightValue) {
        val ordinal = RPLEColorAPI.clampColorComponent(vanillaLightValue);
        return values()[ordinal];
    }

    public static LightLevelColor fromVanillaLightOpacity(int vanillaLightOpacity) {
        val ordinal = RPLEColorAPI.invertColorComponent(vanillaLightOpacity);
        return values()[ordinal];
    }
}
