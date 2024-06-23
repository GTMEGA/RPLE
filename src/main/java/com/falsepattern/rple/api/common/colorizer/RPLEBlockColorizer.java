/*
 * Copyright (c) 2023 FalsePattern, Ven
 * This work is licensed under the Creative Commons Attribution-NonCommercial-NoDerivatives 4.0 International License.
 * To view a copy of this license, visit http://creativecommons.org/licenses/by-nc-nd/4.0/
 * or send a letter to Creative Commons, PO Box 1866, Mountain View, CA 94042, USA.
 */

package com.falsepattern.rple.api.common.colorizer;

import com.falsepattern.rple.api.common.color.IPaletteColor;
import org.jetbrains.annotations.NotNull;

public interface RPLEBlockColorizer {
    default RPLEBlockColorizer brightness(int rgb16) {
        return brightness((short)rgb16);
    }
    @NotNull RPLEBlockColorizer brightness(short color);

    @NotNull RPLEBlockColorizer brightness(@NotNull IPaletteColor color);

    default RPLEBlockColorizer translucency(int rgb16) {
        return translucency((short)rgb16);
    }
    @NotNull RPLEBlockColorizer translucency(short color);

    @NotNull RPLEBlockColorizer translucency(@NotNull IPaletteColor color);

    void apply();
}
