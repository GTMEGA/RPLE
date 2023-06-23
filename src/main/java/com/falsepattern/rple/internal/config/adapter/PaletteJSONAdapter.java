/*
 * Copyright (c) 2023 FalsePattern, Ven
 * This work is licensed under the Creative Commons Attribution-NonCommercial-NoDerivatives 4.0 International License.
 * To view a copy of this license, visit http://creativecommons.org/licenses/by-nc-nd/4.0/
 * or send a letter to Creative Commons, PO Box 1866, Mountain View, CA 94042, USA.
 */

package com.falsepattern.rple.internal.config.adapter;

import com.falsepattern.rple.internal.config.container.HexColor;
import com.falsepattern.rple.internal.config.container.Palette;
import com.google.gson.TypeAdapter;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import lombok.NoArgsConstructor;

import java.lang.reflect.Type;
import java.util.LinkedHashMap;

import static com.falsepattern.rple.internal.config.ColorConfigHandler.colorConfigGSON;

@NoArgsConstructor
public final class PaletteJSONAdapter extends TypeAdapter<Palette> {
    private static final Type COLOR_MAP_TYPE = new TypeToken<LinkedHashMap<String, HexColor>>() {}.getType();

    @Override
    public void write(JsonWriter out, Palette value) {
        colorConfigGSON().toJson(value.colors(), COLOR_MAP_TYPE, out);
    }

    @Override
    public Palette read(JsonReader in) {
        return new Palette(colorConfigGSON().fromJson(in, COLOR_MAP_TYPE));
    }
}
