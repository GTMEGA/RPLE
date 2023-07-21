/*
 * Copyright (c) 2023 FalsePattern, Ven
 * This work is licensed under the Creative Commons Attribution-NonCommercial-NoDerivatives 4.0 International License.
 * To view a copy of this license, visit http://creativecommons.org/licenses/by-nc-nd/4.0/
 * or send a letter to Creative Commons, PO Box 1866, Mountain View, CA 94042, USA.
 */

package com.falsepattern.rple.internal.common.config.adapter;

import com.falsepattern.rple.internal.common.config.ColorConfigLoader;
import com.falsepattern.rple.internal.common.config.container.ColorReference;
import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
public final class ColorReferenceJSONAdapter extends TypeAdapter<ColorReference> {
    @Override
    public void write(JsonWriter out, ColorReference value) {
        ColorConfigLoader.colorConfigGSON().toJson(value.toString(), String.class, out);
    }

    @Override
    public ColorReference read(JsonReader in) {
        return new ColorReference(ColorConfigLoader.colorConfigGSON().<String>fromJson(in, String.class));
    }
}
