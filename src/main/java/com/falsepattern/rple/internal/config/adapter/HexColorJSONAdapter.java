/*
 * Copyright (c) 2023 FalsePattern, Ven
 * This work is licensed under the Creative Commons Attribution-NonCommercial-NoDerivatives 4.0 International License.
 * To view a copy of this license, visit http://creativecommons.org/licenses/by-nc-nd/4.0/
 * or send a letter to Creative Commons, PO Box 1866, Mountain View, CA 94042, USA.
 */

package com.falsepattern.rple.internal.config.adapter;

import com.falsepattern.rple.internal.config.container.HexColor;
import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import lombok.NoArgsConstructor;

import static com.falsepattern.rple.internal.config.ColorConfigLoader.colorConfigGSON;

@NoArgsConstructor
public final class HexColorJSONAdapter extends TypeAdapter<HexColor> {
    @Override
    public void write(JsonWriter out, HexColor value) {
        colorConfigGSON().toJson(value.asColorHex(), String.class, out);
    }

    @Override
    public HexColor read(JsonReader in) {
        return new HexColor(colorConfigGSON().<String>fromJson(in, String.class));
    }
}
