/*
 * Copyright (c) 2023 FalsePattern, Ven
 * This work is licensed under the Creative Commons Attribution-NonCommercial-NoDerivatives 4.0 International License.
 * To view a copy of this license, visit http://creativecommons.org/licenses/by-nc-nd/4.0/
 * or send a letter to Creative Commons, PO Box 1866, Mountain View, CA 94042, USA.
 */

package com.falsepattern.rple.api;

import com.falsepattern.lib.util.MathUtil;
import com.falsepattern.rple.api.color.CustomColor;
import com.falsepattern.rple.api.color.RPLEColour;
import com.falsepattern.rple.api.color.VanillaColor;
import com.falsepattern.rple.internal.Tags;
import lombok.experimental.UtilityClass;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@UtilityClass
public final class RPLEColorAPI {
    private static final Logger LOG = LogManager.getLogger(Tags.MODNAME + "|ColorAPI");
    private static final RPLEColour ERROR_COLOR = new CustomColor(15, 0, 0);

    public static final int COLOR_MIN = 0;
    public static final int COLOR_MAX = 15;

    public static RPLEColour vanillaBlockMetaColor(int meta) {
        if (meta < COLOR_MIN || meta > COLOR_MAX)
            return errorColor();

        return VanillaColor.values()[meta];
    }

    public static RPLEColour errorColor() {
        return ERROR_COLOR;
    }

    public static int clampColorComponent(int component) {
        return MathUtil.clamp(component, COLOR_MIN, COLOR_MAX);
    }
}
