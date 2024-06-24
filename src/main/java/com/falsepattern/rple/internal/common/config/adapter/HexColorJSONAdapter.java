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

package com.falsepattern.rple.internal.common.config.adapter;

import com.falsepattern.rple.internal.common.config.container.HexColor;
import com.google.gson.JsonSyntaxException;
import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import lombok.NoArgsConstructor;
import lombok.val;

import static com.falsepattern.rple.internal.common.config.ColorConfigLoader.colorConfigGSON;
import static com.falsepattern.rple.internal.common.config.ColorConfigLoader.logParsingError;
import static com.falsepattern.rple.internal.common.config.container.HexColor.INVALID_HEX_COLOR;

@NoArgsConstructor
public final class HexColorJSONAdapter extends TypeAdapter<HexColor> {
    @Override
    public void write(JsonWriter out, HexColor value) {
        colorConfigGSON().toJson(value.asColorHex(), String.class, out);
    }

    @Override
    public HexColor read(JsonReader in) {
        final String colorHex;
        try {
            colorHex = colorConfigGSON().fromJson(in, String.class);
        } catch (JsonSyntaxException e) {
            logParsingError("Failed parsing hex color: {}", e.getMessage());
            return INVALID_HEX_COLOR;
        }

        val hexColor = new HexColor(colorHex);
        if (!hexColor.isValid())
            logParsingError("Invalid hex color: {}", hexColor);
        return hexColor;
    }
}
