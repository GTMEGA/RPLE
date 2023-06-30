/*
 * Copyright (c) 2023 FalsePattern, Ven
 * This work is licensed under the Creative Commons Attribution-NonCommercial-NoDerivatives 4.0 International License.
 * To view a copy of this license, visit http://creativecommons.org/licenses/by-nc-nd/4.0/
 * or send a letter to Creative Commons, PO Box 1866, Mountain View, CA 94042, USA.
 */

package com.falsepattern.rple.internal.config.container;

import com.falsepattern.rple.api.color.RPLEColor;
import com.falsepattern.rple.api.color.RPLENamedColor;
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

    public void addPaletteColor(RPLENamedColor color) {
        if (color == null)
            return;

        val colorDomain = color.colorDomain();
        val colorName = color.colorName();

        if (colorDomain == null || colorDomain.isEmpty())
            return;
        if (colorName == null || colorName.isEmpty())
            return;

        val paletteColorName = colorDomain + ":" + colorName;
        if (colors.containsKey(paletteColorName))
            return;

        val paletteColor = new HexColor(color);
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

    public Optional<RPLEColor> colour(String colorName) {
        return Optional.ofNullable(colors.get(colorName));
    }

    public void reset() {
        colors.clear();
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
