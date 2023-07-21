/*
 * Copyright (c) 2023 FalsePattern, Ven
 * This work is licensed under the Creative Commons Attribution-NonCommercial-NoDerivatives 4.0 International License.
 * To view a copy of this license, visit http://creativecommons.org/licenses/by-nc-nd/4.0/
 * or send a letter to Creative Commons, PO Box 1866, Mountain View, CA 94042, USA.
 */

package com.falsepattern.rple.internal.common.config.adapter;

import com.falsepattern.rple.internal.common.config.ColorConfigLoader;
import com.falsepattern.rple.internal.common.config.container.ColorPalette;
import com.falsepattern.rple.internal.common.config.container.HexColor;
import com.google.gson.TypeAdapter;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import lombok.NoArgsConstructor;

import java.lang.reflect.Type;
import java.util.LinkedHashMap;

@NoArgsConstructor
public final class PaletteJSONAdapter extends TypeAdapter<ColorPalette> {
    private static final Type COLOR_MAP_TYPE = new TypeToken<LinkedHashMap<String, HexColor>>() {}.getType();

    @Override
    public void write(JsonWriter out, ColorPalette value) {
        ColorConfigLoader.colorConfigGSON().toJson(value.colors(), COLOR_MAP_TYPE, out);
    }

    @Override
    public ColorPalette read(JsonReader in) {
        return new ColorPalette(ColorConfigLoader.colorConfigGSON().fromJson(in, COLOR_MAP_TYPE));
    }
}
