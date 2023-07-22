/*
 * Copyright (c) 2023 FalsePattern, Ven
 * This work is licensed under the Creative Commons Attribution-NonCommercial-NoDerivatives 4.0 International License.
 * To view a copy of this license, visit http://creativecommons.org/licenses/by-nc-nd/4.0/
 * or send a letter to Creative Commons, PO Box 1866, Mountain View, CA 94042, USA.
 */

package com.falsepattern.rple.api.common;

import com.falsepattern.lib.util.MathUtil;
import com.falsepattern.rple.api.common.color.ErrorColor;
import com.falsepattern.rple.api.common.color.RPLEColor;
import com.falsepattern.rple.api.common.color.RPLENamedColor;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

@SuppressWarnings("unused")
public final class RPLEColorUtil {
    public static final int COLOR_MIN = 0;
    public static final int COLOR_MAX = 15;

    private RPLEColorUtil() {
        throw new UnsupportedOperationException("This is a utility class and cannot be instantiated");
    }

    public static @NotNull RPLEColor errorColor() {
        return ErrorColor.errorColor();
    }

    public static int lightValueFromColor(@NotNull RPLEColor color) {
        return maxColorComponent(color);
    }

    public static int lightOpacityFromColor(@NotNull RPLEColor color) {
        return invertColorComponent(maxColorComponent(color));
    }

    public static int clampColorComponent(int component) {
        return MathUtil.clamp(component, COLOR_MIN, COLOR_MAX);
    }

    public static int invertColorComponent(int component) {
        return COLOR_MAX - clampColorComponent(component);
    }

    public static int minColorComponent(@NotNull RPLEColor color) {
        return minColorComponent(color.red(), color.green(), color.blue());
    }

    public static int maxColorComponent(@NotNull RPLEColor color) {
        return maxColorComponent(color.red(), color.green(), color.blue());
    }

    public static int minColorComponent(int red, int green, int blue) {
        return Math.min(red, Math.min(green, blue));
    }

    public static int maxColorComponent(int red, int green, int blue) {
        return Math.max(red, Math.max(green, blue));
    }

    public static int colorHashCode(@NotNull RPLEColor colour) {
        return (colour.red() << 8) | (colour.green() << 4) | colour.blue();
    }

    public static boolean colorEquals(@NotNull Object valueA, @NotNull Object valueB) {
        if (!(valueA instanceof RPLEColor))
            return false;
        if (!(valueB instanceof RPLEColor))
            return false;

        final RPLEColor colorA = (RPLEColor) valueA;
        final RPLEColor colorB = (RPLEColor) valueB;

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

        final RPLENamedColor colorA = (RPLENamedColor) valueA;
        final RPLENamedColor colorB = (RPLENamedColor) valueB;

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
