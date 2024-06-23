/*
 * Copyright (c) 2023 FalsePattern, Ven
 * This work is licensed under the Creative Commons Attribution-NonCommercial-NoDerivatives 4.0 International License.
 * To view a copy of this license, visit http://creativecommons.org/licenses/by-nc-nd/4.0/
 * or send a letter to Creative Commons, PO Box 1866, Mountain View, CA 94042, USA.
 */

package com.falsepattern.rple.api.common.color;

import static com.falsepattern.rple.api.common.ServerColorHelper.*;

public enum ColorChannel {
    RED_CHANNEL(CHANNEL_4BIT_TO_RGB16_RED),
    GREEN_CHANNEL(CHANNEL_4BIT_TO_RGB16_GREEN),
    BLUE_CHANNEL(CHANNEL_4BIT_TO_RGB16_BLUE),
    ;

    private final String name;
    private final int bitShift;

    ColorChannel(int bitShift) {
        this.name = name().split("_")[0].toLowerCase();
        this.bitShift = bitShift;
    }

    public int componentFromColor(short color) {
        return ((int) color >>> bitShift) & CHANNEL_4BIT_MASK;
    }

    @Override
    public String toString() {
        return name;
    }
}
