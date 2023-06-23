/*
 * Copyright (c) 2023 FalsePattern, Ven
 * This work is licensed under the Creative Commons Attribution-NonCommercial-NoDerivatives 4.0 International License.
 * To view a copy of this license, visit http://creativecommons.org/licenses/by-nc-nd/4.0/
 * or send a letter to Creative Commons, PO Box 1866, Mountain View, CA 94042, USA.
 */

package com.falsepattern.rple.internal.config.container;

import com.falsepattern.rple.api.RPLEColorAPI;
import com.falsepattern.rple.api.color.ColorChannel;
import com.falsepattern.rple.api.color.RPLEColour;
import lombok.Getter;
import lombok.experimental.Accessors;
import lombok.val;
import lombok.var;

import static com.falsepattern.rple.api.RPLEColorAPI.COLOR_MIN;
import static com.falsepattern.rple.api.color.ColorChannel.*;

@Getter
@Accessors(fluent = true, chain = false)
public final class HexColor implements RPLEColour {
    private static final String EXPECTED_REGEX = "0x[A-F0-9]{3}$";

    private final int red;
    private final int green;
    private final int blue;

    private final String asColorHex;

    public HexColor(String colorHex) {
        var red = COLOR_MIN;
        var green = COLOR_MIN;
        var blue = COLOR_MIN;

        var asColorHex = "";

        try {
            if (!isValidColorHex(colorHex))
                throw new IllegalArgumentException();

            red = componentFromHex(RED_CHANNEL, colorHex);
            green = componentFromHex(GREEN_CHANNEL, colorHex);
            blue = componentFromHex(BLUE_CHANNEL, colorHex);

            asColorHex = colorHex;
        } catch (IllegalArgumentException e) {
            val errorColor = RPLEColorAPI.errorColor();

            red = errorColor.red();
            green = errorColor.green();
            blue = errorColor.blue();

            asColorHex = colorHexFromColor(errorColor);
        }

        this.red = red;
        this.green = green;
        this.blue = blue;

        this.asColorHex = asColorHex;
    }

    @Override
    public int hashCode() {
        return (red << 8) | (green << 4) | blue;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null)
            return false;
        if (!(obj instanceof RPLEColour))
            return false;

        val otherColor = (RPLEColour) obj;
        if (this.red != otherColor.red())
            return false;
        if (this.green != otherColor.green())
            return false;
        if (this.blue != otherColor.blue())
            return false;

        return true;
    }

    @Override
    public String toString() {
        return asColorHex;
    }

    public static boolean isValidColorHex(String colorHex) {
        if (colorHex == null)
            return false;
        return colorHex.matches(EXPECTED_REGEX);
    }

    private static int componentFromHex(ColorChannel channel, String colorHex) {
        val position = channel.ordinal() + 2;
        val hexChar = colorHex.charAt(position);
        val hexString = String.valueOf(hexChar);

        return Integer.parseUnsignedInt(hexString, 16);
    }

    private static String colorHexFromColor(RPLEColour colour) {
        val redHex = Integer.toHexString(colour.red());
        val greenHex = Integer.toHexString(colour.green());
        val blueHex = Integer.toHexString(colour.blue());

        val colorHex = "0x" + redHex + greenHex + blueHex;
        return colorHex.toUpperCase();
    }
}
