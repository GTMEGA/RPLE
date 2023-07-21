/*
 * Copyright (c) 2023 FalsePattern, Ven
 * This work is licensed under the Creative Commons Attribution-NonCommercial-NoDerivatives 4.0 International License.
 * To view a copy of this license, visit http://creativecommons.org/licenses/by-nc-nd/4.0/
 * or send a letter to Creative Commons, PO Box 1866, Mountain View, CA 94042, USA.
 */

package com.falsepattern.rple.api.color;

import static com.falsepattern.rple.api.RPLEColorAPI.*;

public class MutableColor implements RPLEColor {
    protected int red;
    protected int green;
    protected int blue;

    public MutableColor() {
        this.red = COLOR_MIN;
        this.green = COLOR_MIN;
        this.blue = COLOR_MIN;
    }

    public void set(int red, int green, int blue) {
        this.red = red;
        this.green = green;
        this.blue = blue;
    }

    public void red(int red) {
        this.red = red;
    }

    public int red() {
        return red;
    }

    public void green(int green) {
        this.green = green;
    }

    public int green() {
        return green;
    }

    public void blue(int blue) {
        this.blue = blue;
    }

    public int blue() {
        return blue;
    }

    @Override
    public int hashCode() {
        return colorHashCode(this);
    }

    @Override
    @SuppressWarnings("EqualsWhichDoesntCheckParameterClass")
    public boolean equals(Object obj) {
        return colorEquals(this, obj);
    }
}
