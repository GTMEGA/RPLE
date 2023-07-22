/*
 * Copyright (c) 2023 FalsePattern, Ven
 * This work is licensed under the Creative Commons Attribution-NonCommercial-NoDerivatives 4.0 International License.
 * To view a copy of this license, visit http://creativecommons.org/licenses/by-nc-nd/4.0/
 * or send a letter to Creative Commons, PO Box 1866, Mountain View, CA 94042, USA.
 */

package com.falsepattern.rple.api.common.block;

import com.falsepattern.rple.api.common.color.RPLEColor;
import com.falsepattern.rple.api.common.color.RPLENamedColor;
import org.jetbrains.annotations.NotNull;

public interface RPLEBlockColorizer {
    @NotNull RPLEBlockColorizer brightness(int color);

    @NotNull RPLEBlockColorizer brightness(@NotNull RPLEColor color);

    @NotNull RPLEBlockColorizer brightness(@NotNull RPLENamedColor color);

    @NotNull RPLEBlockColorizer translucency(int color);

    @NotNull RPLEBlockColorizer translucency(@NotNull RPLEColor color);

    @NotNull RPLEBlockColorizer translucency(@NotNull RPLENamedColor color);

    void apply();
}
