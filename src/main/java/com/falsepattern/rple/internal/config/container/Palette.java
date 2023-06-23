/*
 * Copyright (c) 2023 FalsePattern, Ven
 * This work is licensed under the Creative Commons Attribution-NonCommercial-NoDerivatives 4.0 International License.
 * To view a copy of this license, visit http://creativecommons.org/licenses/by-nc-nd/4.0/
 * or send a letter to Creative Commons, PO Box 1866, Mountain View, CA 94042, USA.
 */

package com.falsepattern.rple.internal.config.container;

import com.falsepattern.rple.api.color.RPLEColour;
import lombok.AllArgsConstructor;
import lombok.val;
import org.jetbrains.annotations.UnmodifiableView;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;

@AllArgsConstructor
public final class Palette {
    private final LinkedHashMap<String, HexColor> colors;

    public Palette() {
        this.colors = new LinkedHashMap<>();
    }

    public @UnmodifiableView Map<String, HexColor> colors() {
        return Collections.unmodifiableMap(colors);
    }

    public Optional<RPLEColour> colour(String colorName) {
        return Optional.ofNullable(colors.get(colorName));
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
}
