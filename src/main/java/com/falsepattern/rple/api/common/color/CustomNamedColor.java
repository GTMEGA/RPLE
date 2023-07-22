/*
 * Copyright (c) 2023 FalsePattern, Ven
 * This work is licensed under the Creative Commons Attribution-NonCommercial-NoDerivatives 4.0 International License.
 * To view a copy of this license, visit http://creativecommons.org/licenses/by-nc-nd/4.0/
 * or send a letter to Creative Commons, PO Box 1866, Mountain View, CA 94042, USA.
 */

package com.falsepattern.rple.api.common.color;

import com.falsepattern.rple.api.common.RPLEColorAPI;
import org.jetbrains.annotations.NotNull;

public class CustomNamedColor implements RPLENamedColor {
    protected final int red;
    protected final int green;
    protected final int blue;

    protected final String colorDomain;
    protected final String colorName;

    public CustomNamedColor(int red, int green, int blue, String colorDomain, String colorName) {
        this.red = red;
        this.green = green;
        this.blue = blue;
        this.colorDomain = colorDomain;
        this.colorName = colorName;
    }

    public @NotNull String colorDomain() {
        return colorDomain;
    }

    public @NotNull String colorName() {
        return colorName;
    }

    public int red() {
        return red;
    }

    public int green() {
        return green;
    }

    public int blue() {
        return blue;
    }

    @Override
    public int hashCode() {
        return RPLEColorAPI.colorHashCode(this);
    }

    @Override
    @SuppressWarnings("EqualsWhichDoesntCheckParameterClass")
    public boolean equals(Object obj) {
        return RPLEColorAPI.namedColorEquals(this, obj);
    }
}
