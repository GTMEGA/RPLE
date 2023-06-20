/*
 * Copyright (c) 2023 FalsePattern, Ven
 * This work is licensed under the Creative Commons Attribution-NonCommercial-NoDerivatives 4.0 International License.
 * To view a copy of this license, visit http://creativecommons.org/licenses/by-nc-nd/4.0/
 * or send a letter to Creative Commons, PO Box 1866, Mountain View, CA 94042, USA.
 */

package com.falsepattern.rple.api;

import java.awt.*;

public interface RPLELightColor {
    int RPLE_LIGHT_COLOR_MIN = 0;
    int RPLE_LIGHT_COLOR_MAX = 15;

    int red();

    int green();

    int blue();

    default int rgbChannel(int colorChannel) {
        switch (colorChannel) {
            case 0:
                return red();
            case 1:
                return green();
            case 2:
                return blue();
            default:
                return RPLE_LIGHT_COLOR_MAX;
        }
    }

    Color awtColor();

    default int[] asArray() {
        return new int[]{red(), green(), blue()};
    }
}
