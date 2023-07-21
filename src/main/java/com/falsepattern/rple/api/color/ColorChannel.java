/*
 * Copyright (c) 2023 FalsePattern, Ven
 * This work is licensed under the Creative Commons Attribution-NonCommercial-NoDerivatives 4.0 International License.
 * To view a copy of this license, visit http://creativecommons.org/licenses/by-nc-nd/4.0/
 * or send a letter to Creative Commons, PO Box 1866, Mountain View, CA 94042, USA.
 */

package com.falsepattern.rple.api.color;

import org.jetbrains.annotations.NotNull;

public enum ColorChannel {
    RED_CHANNEL, GREEN_CHANNEL, BLUE_CHANNEL;

    private final String name;

    ColorChannel() {
        this.name = name().split("_")[0].toLowerCase();
    }

    public int componentFromColor(@NotNull RPLEColor color) {
        switch (this) {
            default:
            case RED_CHANNEL:
                return color.red();
            case GREEN_CHANNEL:
                return color.green();
            case BLUE_CHANNEL:
                return color.blue();
        }
    }

    @Override
    public String toString() {
        return name;
    }
}
