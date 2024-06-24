/*
 * Right Proper Lighting Engine
 *
 * Copyright (C) 2023-2024 FalsePattern, Ven
 * All Rights Reserved
 *
 * The above copyright notice and this permission notice
 * shall be included in all copies or substantial portions of the Software.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program. If not, see <https://www.gnu.org/licenses/>.
 *
 * This program comes with additional permissions according to Section 7 of the
 * GNU Affero General Public License. See the full LICENSE file for details.
 */

package com.falsepattern.rple.internal.common.config.container;

import com.falsepattern.rple.api.common.ServerColorHelper;
import com.falsepattern.rple.api.common.color.ColorChannel;
import com.falsepattern.rple.api.common.color.DefaultColor;
import lombok.Getter;
import lombok.experimental.Accessors;
import lombok.val;
import lombok.var;

import static com.falsepattern.rple.api.common.ServerColorHelper.COLOR_MIN;
import static com.falsepattern.rple.api.common.color.ColorChannel.*;

@Getter
@Accessors(fluent = true, chain = false)
public final class HexColor {
    public static final HexColor INVALID_HEX_COLOR = new HexColor(DefaultColor.ERROR.rgb16(), false);

    private static final String EXPECTED_REGEX = "0x[A-F0-9]{3}$";

    private final short rgb16;

    private final String asColorHex;
    private final boolean isValid;

    public HexColor(short rgb16) {
        this(rgb16, true);
    }

    public HexColor(short rgb16, boolean valid) {
        this.rgb16 = rgb16;

        this.asColorHex = colorHexFromColor(rgb16);
        this.isValid = valid;
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
            val errorColor = DefaultColor.ERROR.rgb16();

            red = ServerColorHelper.red(errorColor);
            green = ServerColorHelper.green(errorColor);
            blue = ServerColorHelper.blue(errorColor);

            asColorHex = colorHexFromColor(errorColor);
            isValid = false;
        }

        this.rgb16 = ServerColorHelper.RGB16FromRGBChannel4Bit(red, green, blue);

        this.asColorHex = asColorHex;
        this.isValid = isValid;
    }

    @Override
    public int hashCode() {
        return rgb16;
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof HexColor))
            return false;
        return rgb16 == ((HexColor)obj).rgb16;
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

    private static String colorHexFromColor(short rgb16) {
        val red = RED_CHANNEL.componentFromColor(rgb16);
        val green = GREEN_CHANNEL.componentFromColor(rgb16);
        val blue = BLUE_CHANNEL.componentFromColor(rgb16);

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
