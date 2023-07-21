/*
 * Copyright (c) 2023 FalsePattern, Ven
 * All Rights Reserved
 *
 * The above copyright notice and this permission notice shall be included
 * in all copies or substantial portions of the Software.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package com.falsepattern.rple.internal.common.config.container;

import com.falsepattern.rple.api.RPLEColorAPI;
import com.falsepattern.rple.api.color.RPLEColor;
import com.falsepattern.rple.api.color.RPLENamedColor;
import lombok.Getter;
import lombok.experimental.Accessors;
import lombok.val;
import org.jetbrains.annotations.Nullable;

@Accessors(fluent = true, chain = false)
public final class ColorReference {
    public static final String INVALID_COLOR = "invalid_color";

    private final @Nullable String paletteColorName;
    private final @Nullable HexColor uniqueColor;

    @Getter
    private final boolean isValid;

    public ColorReference(RPLENamedColor color) {
        this.uniqueColor = null;

        nameCheck:
        {
            val colorDomain = color.colorDomain();
            val colorName = color.colorName();

            if (colorDomain == null || colorDomain.isEmpty())
                break nameCheck;
            if (colorName == null || colorName.isEmpty())
                break nameCheck;

            val paletteColorName = colorDomain + ":" + colorName;
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

    public RPLEColor color(ColorPalette palette) {
        if (!isValid)
            return RPLEColorAPI.errorColor();

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
