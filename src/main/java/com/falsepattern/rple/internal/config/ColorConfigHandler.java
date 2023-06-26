/*
 * Copyright (c) 2023 FalsePattern, Ven
 * This work is licensed under the Creative Commons Attribution-NonCommercial-NoDerivatives 4.0 International License.
 * To view a copy of this license, visit http://creativecommons.org/licenses/by-nc-nd/4.0/
 * or send a letter to Creative Commons, PO Box 1866, Mountain View, CA 94042, USA.
 */

package com.falsepattern.rple.internal.config;

import com.falsepattern.rple.internal.config.adapter.BlockReferenceJSONAdapter;
import com.falsepattern.rple.internal.config.adapter.ColorReferenceJSONAdapter;
import com.falsepattern.rple.internal.config.adapter.HexColorJSONAdapter;
import com.falsepattern.rple.internal.config.adapter.PaletteJSONAdapter;
import com.falsepattern.rple.internal.config.container.BlockReference;
import com.falsepattern.rple.internal.config.container.ColorPalette;
import com.falsepattern.rple.internal.config.container.ColorReference;
import com.falsepattern.rple.internal.config.container.HexColor;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.stream.JsonWriter;
import lombok.experimental.UtilityClass;
import lombok.val;

import java.io.StringWriter;

@UtilityClass
public final class ColorConfigHandler {
    private static final Gson GSON;

    static {
        GSON = new GsonBuilder()
                .registerTypeAdapter(ColorPalette.class, new PaletteJSONAdapter())
                .registerTypeAdapter(HexColor.class, new HexColorJSONAdapter())
                .registerTypeAdapter(BlockReference.class, new BlockReferenceJSONAdapter())
                .registerTypeAdapter(ColorReference.class, new ColorReferenceJSONAdapter())
                .create();
    }

    public static Gson colorConfigGSON() {
        return GSON;
    }

    public static String fromObjectToPrettyPrintJson(Object source) {
        val stringWriter = new StringWriter();
        val jsonWriter = new JsonWriter(stringWriter);
        jsonWriter.setIndent("    ");

        GSON.toJson(source, source.getClass(), jsonWriter);
        return stringWriter.toString();
    }
}
