/*
 * Copyright (c) 2023 FalsePattern, Ven
 * This work is licensed under the Creative Commons Attribution-NonCommercial-NoDerivatives 4.0 International License.
 * To view a copy of this license, visit http://creativecommons.org/licenses/by-nc-nd/4.0/
 * or send a letter to Creative Commons, PO Box 1866, Mountain View, CA 94042, USA.
 */

package com.falsepattern.rple.api.common.color;

import com.falsepattern.rple.api.common.RPLEColorAPI;
import org.jetbrains.annotations.NotNull;

public final class ErrorColor implements RPLENamedColor {
    private static final ErrorColor INSTANCE = new ErrorColor();

    private ErrorColor() {
    }

    public static @NotNull RPLENamedColor errorColor() {
        return INSTANCE;
    }

    @Override
    public @NotNull String colorDomain() {
        return "invalid_domain";
    }

    @Override
    public @NotNull String colorName() {
        return "invalid_name";
    }

    @Override
    public int red() {
        return RPLEColorAPI.COLOR_MAX;
    }

    @Override
    public int green() {
        return RPLEColorAPI.COLOR_MIN;
    }

    @Override
    public int blue() {
        return RPLEColorAPI.COLOR_MIN;
    }
}
