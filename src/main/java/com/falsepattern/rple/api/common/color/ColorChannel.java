/*
 * Copyright (c) 2023 FalsePattern, Ven
 * This work is licensed under the Creative Commons Attribution-NonCommercial-NoDerivatives 4.0 International License.
 * To view a copy of this license, visit http://creativecommons.org/licenses/by-nc-nd/4.0/
 * or send a letter to Creative Commons, PO Box 1866, Mountain View, CA 94042, USA.
 */

package com.falsepattern.rple.api.common.color;

import org.jetbrains.annotations.NotNull;

import static com.falsepattern.rple.internal.common.color.ColorPackingUtil.*;

public enum ColorChannel {
    RED_CHANNEL(CACHE_ENTRY_RED_OFFSET),
    GREEN_CHANNEL(CACHE_ENTRY_GREEN_OFFSET),
    BLUE_CHANNEL(CACHE_ENTRY_BLUE_OFFSET),
    ;

    private final String name;
    private final int bitShift;

    ColorChannel(int bitShift) {
        this.name = name().split("_")[0].toLowerCase();
        this.bitShift = bitShift;
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

    public int componentFromColor(short color) {
        return ((int) color >>> bitShift) & CACHE_CHANNEL_BITMASK;
    }

    @Override
    public String toString() {
        return name;
    }
}
