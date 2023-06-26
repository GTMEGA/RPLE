/*
 * Copyright (c) 2023 FalsePattern, Ven
 * This work is licensed under the Creative Commons Attribution-NonCommercial-NoDerivatives 4.0 International License.
 * To view a copy of this license, visit http://creativecommons.org/licenses/by-nc-nd/4.0/
 * or send a letter to Creative Commons, PO Box 1866, Mountain View, CA 94042, USA.
 */

package com.falsepattern.rple.api;

import com.falsepattern.lib.util.MathUtil;
import com.falsepattern.rple.api.color.DefaultColor;
import com.falsepattern.rple.api.color.ErrorColor;
import com.falsepattern.rple.api.color.RPLEColor;
import com.falsepattern.rple.api.color.RPLENamedColor;
import com.falsepattern.rple.internal.Tags;
import lombok.experimental.UtilityClass;
import lombok.val;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Objects;

@UtilityClass
public final class RPLEColorAPI {
    private static final Logger LOG = LogManager.getLogger(Tags.MODNAME + "|ColorAPI");

    public static final int COLOR_MIN = 0;
    public static final int COLOR_MAX = 15;

    @Deprecated
    public static RPLEColor vanillaBlockMetaColor(int meta) {
        if (meta < COLOR_MIN || meta > COLOR_MAX)
            return errorColor();

        return DefaultColor.values()[meta];
    }

    public static RPLEColor errorColor() {
        return ErrorColor.errorColor();
    }

    public static int clampColorComponent(int component) {
        return MathUtil.clamp(component, COLOR_MIN, COLOR_MAX);
    }

    public static int invertColorComponent(int component) {
        return COLOR_MAX - clampColorComponent(component);
    }

    public static int minColorComponent(RPLEColor color) {
        return Math.min(color.red(), Math.min(color.green(), color.blue()));
    }

    public static int maxColorComponent(RPLEColor color) {
        return Math.max(color.red(), Math.max(color.green(), color.blue()));
    }

    public static int colorHashCode(RPLEColor colour) {
        if (colour == null)
            return 0;

        return (colour.red() << 8) | (colour.green() << 4) | colour.blue();
    }

    public static boolean colorEquals(Object valueA, Object valueB) {
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

    public static boolean namedColorEquals(Object valueA, Object valueB) {
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
