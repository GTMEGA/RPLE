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
import com.falsepattern.rple.internal.config.container.*;
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
                .registerTypeAdapter(Palette.class, new PaletteJSONAdapter())
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

    public static void main(String[] args) {
        val config = new ColorConfig();

        config.setPaletteColor("white", "0xFFF");
        config.setPaletteColor("light_white", "0x888");
        config.setPaletteColor("medium_gray", "0xAAA");
        config.setPaletteColor("gray", "0x555");
        config.setPaletteColor("dark_gray", "0x333");
        config.setPaletteColor("black", "0x111");

        config.setPaletteColor("orange", "0xFCA");
        config.setPaletteColor("dark_orange", "0x865");

        config.setPaletteColor("magenta", "0xF0F");
        config.setPaletteColor("dark_magenta", "0x808");

        config.setPaletteColor("light_blue", "0x08F");
        config.setPaletteColor("dark_light_blue", "0x048");

        config.setPaletteColor("yellow", "0xFF0");
        config.setPaletteColor("dark_yellow", "0x880");

        config.setPaletteColor("lime", "0x8F0");
        config.setPaletteColor("dark_lime", "0x480");

        config.setPaletteColor("pink", "0xFAD");
        config.setPaletteColor("dark_pink", "0x856");

        config.setPaletteColor("cyan", "0x0FF");
        config.setPaletteColor("dark_cyan", "0x088");

        config.setPaletteColor("purple", "0xA0F");
        config.setPaletteColor("dark_purple", "0x508");

        config.setPaletteColor("blue", "0x00F");
        config.setPaletteColor("dark_blue", "0x008");

        config.setPaletteColor("brown", "0x830");
        config.setPaletteColor("dark_brown", "0x420");

        config.setPaletteColor("green", "0x0F0");
        config.setPaletteColor("dark_green", "0x080");

        config.setPaletteColor("red", "0xF00");
        config.setPaletteColor("dark_red", "0x800");

        val generatedJSON = fromObjectToPrettyPrintJson(config);
        System.out.println(generatedJSON);
    }
}
