/*
 * Copyright (c) 2023 FalsePattern, Ven
 * This work is licensed under the Creative Commons Attribution-NonCommercial-NoDerivatives 4.0 International License.
 * To view a copy of this license, visit http://creativecommons.org/licenses/by-nc-nd/4.0/
 * or send a letter to Creative Commons, PO Box 1866, Mountain View, CA 94042, USA.
 */

package com.falsepattern.rple.api;

import com.falsepattern.lib.util.MathUtil;
import lombok.Getter;
import lombok.experimental.Accessors;
import lombok.val;

import java.awt.*;

@Getter
@Accessors(fluent = true, chain = false)
public enum VanillaLightColor implements RPLELightColor {
    // @formatter:off
    WHITE      ( 0,  0,  0),
    ORANGE     (15, 12, 10),
    MAGENTA    (15,  0, 15),
    LIGHT_BLUE ( 0,  8, 15),
    YELLOW     (15, 15,  0),
    LIME       ( 8, 15,  0),
    PINK       (15, 10, 13),
    GRAY       ( 5,  5,  5),
    LIGHT_GRAY (10, 10, 10),
    CYAN       ( 0, 15, 15),
    PURPLE     (10,  0, 15),
    BLUE       ( 0,  0, 15),
    BROWN      ( 8,  3,  0),
    GREEN      ( 0, 15,  0),
    RED        (15,  0,  0),
    BLACK      ( 0,  0,  0),
    // @formatter:on
    ;

    private final int red;
    private final int green;
    private final int blue;
    private final Color awtColor;

    VanillaLightColor(int red, int green, int blue) {
        this.red = red;
        this.green = green;
        this.blue = blue;

        this.awtColor = rpleToAWTColor(red, green, blue);
    }

    public static VanillaLightColor ofBlockMeta(int meta) {
        if (meta < RPLE_LIGHT_COLOR_MIN || meta > RPLE_LIGHT_COLOR_MAX)
            return RED;

        return values()[meta];
    }

    private static Color rpleToAWTColor(int red, int green, int blue) {
        val redFloat = channelToFloat(red);
        val greenFloat = channelToFloat(green);
        val blueFloat = channelToFloat(blue);

        return new Color(redFloat, greenFloat, blueFloat);
    }

    private static float channelToFloat(int colorChannel) {
        val floatComponent = (float) clampColorChannel(colorChannel) / (float) RPLE_LIGHT_COLOR_MAX;
        return MathUtil.clamp(floatComponent, 0F, 1F);
    }

    private static int clampColorChannel(int colorChannel) {
        return MathUtil.clamp(colorChannel, RPLE_LIGHT_COLOR_MIN, RPLE_LIGHT_COLOR_MAX);
    }
}
