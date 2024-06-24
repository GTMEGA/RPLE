/*
 * Right Proper Lighting Engine
 *
 * Copyright (C) 2023-2024 FalsePattern, Ven
 * All Rights Reserved
 *
 * The above copyright notice and this permission notice
 * shall be included in all copies or substantial portions of the Software.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
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

import com.falsepattern.rple.api.common.color.RPLEBlockColor;
import lombok.val;
import org.jetbrains.annotations.UnmodifiableView;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;

public final class ColorPalette {
    private final LinkedHashMap<String, HexColor> colors;

    public ColorPalette() {
        this(new LinkedHashMap<>());
    }

    public ColorPalette(LinkedHashMap<String, HexColor> colors) {
        this.colors = colors;
        this.colors.entrySet()
                   .removeIf(c -> !isValidPaletteColor(c.getKey(), c.getValue()));
    }

    public void addPaletteColor(RPLEBlockColor color) {
        if (color == null)
            return;

        val paletteColorName = color.paletteColorName();
        if (colors.containsKey(paletteColorName))
            return;

        val paletteColor = new HexColor(color.rgb16());
        if (isValidPaletteColor(paletteColorName, paletteColor))
            colors.put(paletteColorName, paletteColor);
    }

    public void addColor(String name, String colorHex) {
        val color = new HexColor(colorHex);

        if (isValidPaletteColor(name, color))
            colors.put(name, new HexColor(colorHex));
    }

    public @UnmodifiableView Map<String, HexColor> colors() {
        return Collections.unmodifiableMap(colors);
    }

    public Optional<HexColor> colour(String colorName) {
        return Optional.ofNullable(colors.get(colorName));
    }

    public void reset() {
        colors.clear();
    }

    @Override
    public int hashCode() {
        return colors.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof ColorPalette))
            return false;

        val other = (ColorPalette) obj;
        return colors.equals(other.colors);
    }

    @Override
    public String toString() {
        val sb = new StringBuilder();
        for (val color : colors.entrySet()) {
            sb.append(color.getKey())
              .append(": ")
              .append(color.getValue())
              .append("\n");
        }

        return sb.toString();
    }

    public static boolean isValidPaletteColor(String colorName, HexColor color) {
        if (colorName == null)
            return false;
        if (color == null)
            return false;

        if (!isValidPaletteColorName(colorName))
            return false;

        return color.isValid();
    }

    public static boolean isValidPaletteColorName(String colorName) {
        if (colorName == null)
            return false;
        return !colorName.toLowerCase().startsWith("0x");
    }
}
