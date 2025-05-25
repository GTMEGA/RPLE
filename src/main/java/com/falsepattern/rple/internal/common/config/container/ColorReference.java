/*
 * Right Proper Lighting Engine
 *
 * Copyright (C) 2023-2025 FalsePattern, Ven
 * All Rights Reserved
 *
 * The above copyright notice and this permission notice
 * shall be included in all copies or substantial portions of the Software.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, only version 3 of the License.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program. If not, see <https://www.gnu.org/licenses/>.
 *
 * This program comes with additional permissions according to Section 7 of the
 * GNU Affero General Public License. See the full LICENSE file for details.
 */

package com.falsepattern.rple.internal.common.config.container;

import com.falsepattern.rple.api.common.color.DefaultColor;
import com.falsepattern.rple.api.common.color.RPLENamedColor;
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

    public static ColorReference paletteOrRaw(RPLENamedColor palette, short raw) {
        return palette == null ? new ColorReference(new HexColor(raw)) : new ColorReference(palette);
    }

    public ColorReference(RPLENamedColor color) {
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
