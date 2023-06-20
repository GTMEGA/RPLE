/*
 * Copyright (c) 2023 FalsePattern, Ven
 * This work is licensed under the Creative Commons Attribution-NonCommercial-NoDerivatives 4.0 International License.
 * To view a copy of this license, visit http://creativecommons.org/licenses/by-nc-nd/4.0/
 * or send a letter to Creative Commons, PO Box 1866, Mountain View, CA 94042, USA.
 */

package com.falsepattern.rple.api.color;

public enum ColorChannel {
    RED, GREEN, BLUE;

    public int component(RPLEColour coloured) {
        switch (this) {
            case RED:
                return coloured.red();
            case GREEN:
                return coloured.green();
            case BLUE:
                return coloured.blue();
            default:
                return ColorAPI.COLOR_MIN;
        }
    }
}
