/*
 * Copyright (c) 2023 FalsePattern, Ven
 * This work is licensed under the Creative Commons Attribution-NonCommercial-NoDerivatives 4.0 International License.
 * To view a copy of this license, visit http://creativecommons.org/licenses/by-nc-nd/4.0/
 * or send a letter to Creative Commons, PO Box 1866, Mountain View, CA 94042, USA.
 */

package com.falsepattern.rple.api.common.color;

public class CustomBlockColor implements RPLEBlockColor {
    protected final String paletteColorName;
    protected final short rgb16;

    public CustomBlockColor(String paletteColorName, short rgb16) {
        this.paletteColorName = paletteColorName;
        this.rgb16 = rgb16;
    }

    @Override
    public String paletteColorName() {
        return paletteColorName;
    }

    @Override
    public short rgb16() {
        return rgb16;
    }
}
