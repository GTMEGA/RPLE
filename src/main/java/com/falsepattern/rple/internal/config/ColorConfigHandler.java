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
import lombok.experimental.UtilityClass;
import lombok.val;

@UtilityClass
public final class ColorConfigHandler {
    private static final Gson GSON;

    static {
        GSON = new GsonBuilder()
                .registerTypeAdapter(Palette.class, new PaletteJSONAdapter())
                .registerTypeAdapter(HexColor.class, new HexColorJSONAdapter())
                .registerTypeAdapter(BlockReference.class, new BlockReferenceJSONAdapter())
                .registerTypeAdapter(ColorReference.class, new ColorReferenceJSONAdapter())
                .setPrettyPrinting()
                .create();
    }

    public static Gson colorConfigGSON() {
        return GSON;
    }

    public static void main(String[] args) {
        val config = new ColorConfig();

        config.setPaletteColor("red", "0xF00")
              .setPaletteColor("blue", "0x00C")
              .setPaletteColor("dim_blue", "0x004")
              .setBlockBrightness("minecraft:glowstone", "0xF0B")
              .setBlockBrightness("minecraft:lit_furnace", "red")
              .setBlockBrightness("chisel:futuraCircuit", "dim_blue")
              .setBlockBrightness("chisel:futuraCircuit:0", "0xFFF")
              .setBlockTranslucency("minecraft:stained_glass", "blue");

        val generatedJSON = colorConfigGSON().toJson(config);
        System.out.println(generatedJSON);

        val parsedConfig = colorConfigGSON().fromJson(generatedJSON, ColorConfig.class);
        val regeneratedJSON = colorConfigGSON().toJson(parsedConfig);

        System.out.println(regeneratedJSON);
        System.out.println(generatedJSON.equals(regeneratedJSON));
    }
}
