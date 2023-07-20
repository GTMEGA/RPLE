/*
 * Copyright (c) 2023 FalsePattern, Ven
 * This work is licensed under the Creative Commons Attribution-NonCommercial-NoDerivatives 4.0 International License.
 * To view a copy of this license, visit http://creativecommons.org/licenses/by-nc-nd/4.0/
 * or send a letter to Creative Commons, PO Box 1866, Mountain View, CA 94042, USA.
 */

package com.falsepattern.rple.api;

import com.falsepattern.lib.util.MathUtil;
import com.falsepattern.rple.api.color.ErrorColor;
import com.falsepattern.rple.api.color.RPLEColor;
import com.falsepattern.rple.api.color.RPLENamedColor;
import lombok.val;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

@SuppressWarnings("unused")
public final class RPLEColorAPI {
    public static final int COLOR_MIN = 0;
    public static final int COLOR_MAX = 15;

    private RPLEColorAPI() {
        throw new UnsupportedOperationException("This is a utility class and cannot be instantiated");
    }

    public static @NotNull RPLEColor errorColor() {
        return ErrorColor.errorColor();
    }

    public static int clampColorComponent(int component) {
        return MathUtil.clamp(component, COLOR_MIN, COLOR_MAX);
    }

    public static int invertColorComponent(int component) {
        return COLOR_MAX - clampColorComponent(component);
    }

    public static int minColorComponent(@NotNull RPLEColor color) {
        return Math.min(color.red(), Math.min(color.green(), color.blue()));
    }

    public static int maxColorComponent(@NotNull RPLEColor color) {
        return Math.max(color.red(), Math.max(color.green(), color.blue()));
    }

    public static int colorHashCode(@NotNull RPLEColor colour) {
        return (colour.red() << 8) | (colour.green() << 4) | colour.blue();
    }

    public static boolean colorEquals(@NotNull Object valueA, @NotNull Object valueB) {
        if (!(valueA instanceof RPLEColor))
            return false;
        if (!(valueB instanceof RPLEColor))
            return false;

        val colorA = (RPLEColor) valueA;
        val colorB = (RPLEColor) valueB;

        if (colorA.red() != colorB.red())
            return false;
        if (colorA.green() != colorB.green())
            return false;
        if (colorA.blue() != colorB.blue())
            return false;

        return true;
    }

    public static boolean namedColorEquals(@NotNull Object valueA, @NotNull Object valueB) {
        if (!(valueA instanceof RPLENamedColor))
            return false;
        if (!(valueB instanceof RPLENamedColor))
            return false;

        val colorA = (RPLENamedColor) valueA;
        val colorB = (RPLENamedColor) valueB;

        if (colorA.red() != colorB.red())
            return false;
        if (colorA.green() != colorB.green())
            return false;
        if (colorA.blue() != colorB.blue())
            return false;

        if (!Objects.equals(colorA.colorDomain(), colorB.colorDomain()))
            return false;
        if (!Objects.equals(colorA.colorName(), colorB.colorName()))
            return false;

        return true;
    }
}
