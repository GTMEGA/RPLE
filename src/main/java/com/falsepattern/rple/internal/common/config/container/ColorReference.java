/*
 * Copyright (c) 2023 FalsePattern, Ven
 * This work is licensed under the Creative Commons Attribution-NonCommercial-NoDerivatives 4.0 International License.
 * To view a copy of this license, visit http://creativecommons.org/licenses/by-nc-nd/4.0/
 * or send a letter to Creative Commons, PO Box 1866, Mountain View, CA 94042, USA.
 */

package com.falsepattern.rple.internal.common.config.container;

import com.falsepattern.rple.api.common.color.DefaultColor;
import com.falsepattern.rple.api.common.color.RPLEBlockColor;
import lombok.Getter;
import lombok.experimental.Accessors;
import lombok.val;
import org.jetbrains.annotations.Nullable;

@Accessors(fluent = true, chain = false)
public final class ColorReference {
    public static final String INVALID_COLOR = "invalid_color";

    public static final ColorReference INVALID_COLOR_REFERENCE = new ColorReference(INVALID_COLOR);

    private final @Nullable String paletteColorName;
    private final @Nullable HexColor uniqueColor;

    @Getter
    private final boolean isValid;

    public static ColorReference paletteOrRaw(RPLEBlockColor palette, short raw) {
        return palette == null ? new ColorReference(new HexColor(raw)) : new ColorReference(palette);
    }

    public ColorReference(RPLEBlockColor color) {
        this.uniqueColor = null;

        nameCheck:
        {

            val paletteColorName = color.paletteColorName();
            if (!ColorPalette.isValidPaletteColorName(paletteColorName))
                break nameCheck;

            this.paletteColorName = paletteColorName;
            this.isValid = true;
            return;
        }

        this.paletteColorName = INVALID_COLOR;
        this.isValid = false;
    }

    public ColorReference(@Nullable HexColor uniqueColor) {
        this.paletteColorName = null;
        this.uniqueColor = uniqueColor;

        this.isValid = this.uniqueColor != null;
    }

    public ColorReference(@Nullable String color) {
        nameCheck:
        {
            if (color == null)
                break nameCheck;

            if (HexColor.isValidColorHex(color)) {
                this.paletteColorName = null;
                this.uniqueColor = new HexColor(color);

                this.isValid = true;
                return;
            }

            if (ColorPalette.isValidPaletteColorName(color)) {
                this.paletteColorName = color;
                this.uniqueColor = null;

                this.isValid = true;
                return;
            }
        }

        this.paletteColorName = null;
        this.uniqueColor = null;

        this.isValid = false;
    }

    public short color(ColorPalette palette) {
        if (!isValid)
            return DefaultColor.ERROR.rgb16();

        if (uniqueColor != null)
            return uniqueColor.rgb16();

        if (paletteColorName != null) {
            val paletteColor = palette.colour(paletteColorName);
            if (paletteColor.isPresent())
                return paletteColor.get().rgb16();
        }

        return DefaultColor.ERROR.rgb16();
    }

    @Override
    public int hashCode() {
        if (uniqueColor != null)
            return uniqueColor.hashCode();
        if (paletteColorName != null)
            return paletteColorName.hashCode();

        return INVALID_COLOR.hashCode();
    }

    @Override
    public String toString() {
        if (uniqueColor != null)
            return uniqueColor.toString();
        if (paletteColorName != null)
            return paletteColorName;

        return INVALID_COLOR;
    }
}
