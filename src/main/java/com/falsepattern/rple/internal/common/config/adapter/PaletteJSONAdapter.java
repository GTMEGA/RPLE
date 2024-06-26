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

import com.falsepattern.rple.internal.common.config.container.ColorPalette;
import com.falsepattern.rple.internal.common.config.container.HexColor;
import com.google.gson.TypeAdapter;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import lombok.NoArgsConstructor;

import java.lang.reflect.Type;
import java.util.LinkedHashMap;

import static com.falsepattern.rple.internal.common.config.ColorConfigLoader.colorConfigGSON;

@NoArgsConstructor
public final class PaletteJSONAdapter extends TypeAdapter<ColorPalette> {
    private static final Type COLOR_MAP_TYPE = new TypeToken<LinkedHashMap<String, HexColor>>() {}.getType();

    @Override
    public void write(JsonWriter out, ColorPalette value) {
        colorConfigGSON().toJson(value.colors(), COLOR_MAP_TYPE, out);
    }

    @Override
    public ColorPalette read(JsonReader in) {
        return new ColorPalette(colorConfigGSON().fromJson(in, COLOR_MAP_TYPE));
    }
}
