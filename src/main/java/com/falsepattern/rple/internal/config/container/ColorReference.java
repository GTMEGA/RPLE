/*
 * Copyright (c) 2023 FalsePattern, Ven
 * This work is licensed under the Creative Commons Attribution-NonCommercial-NoDerivatives 4.0 International License.
 * To view a copy of this license, visit http://creativecommons.org/licenses/by-nc-nd/4.0/
 * or send a letter to Creative Commons, PO Box 1866, Mountain View, CA 94042, USA.
 */

package com.falsepattern.rple.internal.config.container;

import com.falsepattern.rple.api.RPLEColorAPI;
import com.falsepattern.rple.api.color.RPLEColour;
import lombok.val;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public final class ColorReference {
    private final @Nullable String paletteColorName;
    private final @Nullable HexColor uniqueColor;

    public ColorReference(@NotNull String paletteColorName) {
        this.paletteColorName = paletteColorName;
        this.uniqueColor = null;
    }

    public ColorReference(@NotNull HexColor uniqueColor) {
        this.paletteColorName = null;
        this.uniqueColor = uniqueColor;
    }

    public RPLEColour color(Palette palette) {
        if (uniqueColor != null)
            return uniqueColor;

        if (paletteColorName != null) {
            val paletteColor = palette.colour(paletteColorName);
            if (paletteColor.isPresent())
                return paletteColor.get();
        }

        return RPLEColorAPI.errorColor();
    }

    @Override
    public String toString() {
        if (uniqueColor != null)
            return uniqueColor.toString();
        if (paletteColorName != null)
            return paletteColorName;

        return "invalid_color";
    }
}
