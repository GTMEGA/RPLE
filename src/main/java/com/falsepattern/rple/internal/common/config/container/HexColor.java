/*
 * Copyright (c) 2023 FalsePattern, Ven
 * All Rights Reserved
 *
 * The above copyright notice and this permission notice shall be included
 * in all copies or substantial portions of the Software.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package com.falsepattern.rple.internal.common.config.container;

import com.falsepattern.rple.api.RPLEColorAPI;
import com.falsepattern.rple.api.color.ColorChannel;
import com.falsepattern.rple.api.color.RPLEColor;
import lombok.Getter;
import lombok.experimental.Accessors;
import lombok.val;
import lombok.var;

import static com.falsepattern.rple.api.RPLEColorAPI.COLOR_MIN;
import static com.falsepattern.rple.api.color.ColorChannel.*;

@Getter
@Accessors(fluent = true, chain = false)
public final class HexColor implements RPLEColor {
    private static final String EXPECTED_REGEX = "0x[A-F0-9]{3}$";

    private final int red;
    private final int green;
    private final int blue;

    private final String asColorHex;
    private final boolean isValid;

    public HexColor(RPLEColor color) {
        this.red = RPLEColorAPI.clampColorComponent(color.red());
        this.green = RPLEColorAPI.clampColorComponent(color.green());
        this.blue = RPLEColorAPI.clampColorComponent(color.blue());

        this.asColorHex = colorHexFromColor(color);
        this.isValid = true;
    }

    public HexColor(int colour) {
        var red = COLOR_MIN;
        var green = COLOR_MIN;
        var blue = COLOR_MIN;

        var asColorHex = "";
        var isValid = false;

        try {
            if (colour < 0x000 || colour > 0xFFF)
                throw new IllegalArgumentException();

            red = colour >> 8 & 0xF;
            green = colour >> 4 & 0xF;
            blue = colour & 0xF;

            asColorHex = String.format("0x%1X%1X%1X", red, green, blue);
            isValid = true;
        } catch (IllegalArgumentException e) {
            val errorColor = RPLEColorAPI.errorColor();

            red = errorColor.red();
            green = errorColor.green();
            blue = errorColor.blue();

            asColorHex = colorHexFromColor(errorColor);
            isValid = false;
        }

        this.red = red;
        this.green = green;
        this.blue = blue;

        this.asColorHex = asColorHex;
        this.isValid = isValid;
    }

    public HexColor(String colorHex) {
        var red = COLOR_MIN;
        var green = COLOR_MIN;
        var blue = COLOR_MIN;

        var asColorHex = "";
        var isValid = false;

        try {
            if (!isValidColorHex(colorHex))
                throw new IllegalArgumentException();

            red = componentFromHex(RED_CHANNEL, colorHex);
            green = componentFromHex(GREEN_CHANNEL, colorHex);
            blue = componentFromHex(BLUE_CHANNEL, colorHex);

            asColorHex = colorHex;
            isValid = true;
        } catch (IllegalArgumentException e) {
            val errorColor = RPLEColorAPI.errorColor();

            red = errorColor.red();
            green = errorColor.green();
            blue = errorColor.blue();

            asColorHex = colorHexFromColor(errorColor);
            isValid = false;
        }

        this.red = red;
        this.green = green;
        this.blue = blue;

        this.asColorHex = asColorHex;
        this.isValid = isValid;
    }

    @Override
    public int hashCode() {
        return RPLEColorAPI.colorHashCode(this);
    }

    @Override
    @SuppressWarnings("EqualsWhichDoesntCheckParameterClass")
    public boolean equals(Object obj) {
        return RPLEColorAPI.colorEquals(this, obj);
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

    private static String colorHexFromColor(RPLEColor colour) {
        val red = RED_CHANNEL.componentFromColor(colour);
        val green = GREEN_CHANNEL.componentFromColor(colour);
        val blue = BLUE_CHANNEL.componentFromColor(colour);

        val redHex = hexCharFromNibble(red);
        val greenHex = hexCharFromNibble(green);
        val blueHex = hexCharFromNibble(blue);

        return String.format("0x%c%c%c", redHex, greenHex, blueHex);
    }

    private static char hexCharFromNibble(int nibble) {
        if (nibble < 0)
            return '0';

        switch (nibble) {
            case 0:
                return '0';
            case 1:
                return '1';
            case 2:
                return '2';
            case 3:
                return '3';
            case 4:
                return '4';
            case 5:
                return '5';
            case 6:
                return '6';
            case 7:
                return '7';
            case 8:
                return '8';
            case 9:
                return '9';
            case 10:
                return 'A';
            case 11:
                return 'B';
            case 12:
                return 'C';
            case 13:
                return 'D';
            case 14:
                return 'E';
            case 15:
            default:
                return 'F';
        }
    }
}
