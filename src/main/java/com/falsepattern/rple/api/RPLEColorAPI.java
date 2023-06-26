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
import com.falsepattern.rple.api.color.RPLEColour;
import com.falsepattern.rple.api.color.RPLENamedColour;
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
    public static RPLEColour vanillaBlockMetaColor(int meta) {
        if (meta < COLOR_MIN || meta > COLOR_MAX)
            return errorColor();

        return DefaultColor.values()[meta];
    }

    public static RPLEColour errorColor() {
        return ErrorColor.errorColor();
    }

    public static int clampColorComponent(int component) {
        return MathUtil.clamp(component, COLOR_MIN, COLOR_MAX);
    }

    public static int colorHashCode(RPLEColour colour) {
        if (colour == null)
            return 0;

        return (colour.red() << 8) | (colour.green() << 4) | colour.blue();
    }

    public static boolean colorEquals(Object valueA, Object valueB) {
        if (!(valueA instanceof RPLEColour))
            return false;
        if (!(valueB instanceof RPLEColour))
            return false;

        val colorA = (RPLEColour) valueA;
        val colorB = (RPLEColour) valueB;

        if (colorA.red() != colorB.red())
            return false;
        if (colorA.green() != colorB.green())
            return false;
        if (colorA.blue() != colorB.blue())
            return false;

        return true;
    }

    public static boolean namedColorEquals(Object valueA, Object valueB) {
        if (!(valueA instanceof RPLENamedColour))
            return false;
        if (!(valueB instanceof RPLENamedColour))
            return false;

        val colorA = (RPLENamedColour) valueA;
        val colorB = (RPLENamedColour) valueB;

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
