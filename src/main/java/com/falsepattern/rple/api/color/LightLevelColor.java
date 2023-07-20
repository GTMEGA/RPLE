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
import org.jetbrains.annotations.NotNull;

@Getter
@Accessors(fluent = true, chain = false)
public enum LightLevelColor implements RPLENamedColor {
    LIGHT_LEVEL_0,
    LIGHT_LEVEL_1,
    LIGHT_LEVEL_2,
    LIGHT_LEVEL_3,
    LIGHT_LEVEL_4,
    LIGHT_LEVEL_5,
    LIGHT_LEVEL_6,
    LIGHT_LEVEL_7,
    LIGHT_LEVEL_8,
    LIGHT_LEVEL_9,
    LIGHT_LEVEL_10,
    LIGHT_LEVEL_11,
    LIGHT_LEVEL_12,
    LIGHT_LEVEL_13,
    LIGHT_LEVEL_14,
    LIGHT_LEVEL_15,
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
    public @NotNull String colorDomain() {
        return LIGHT_LEVEL_COLOR_DOMAIN;
    }

    public static @NotNull LightLevelColor fromVanillaLightValue(int vanillaLightValue) {
        val ordinal = RPLEColorAPI.clampColorComponent(vanillaLightValue);
        return values()[ordinal];
    }

    public static @NotNull LightLevelColor fromVanillaLightOpacity(int vanillaLightOpacity) {
        val ordinal = RPLEColorAPI.invertColorComponent(vanillaLightOpacity);
        return values()[ordinal];
    }
}
