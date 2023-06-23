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
import com.falsepattern.rple.internal.config.container.ColorReference;
import com.falsepattern.rple.internal.config.container.HexColor;
import com.falsepattern.rple.internal.config.container.Palette;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import lombok.experimental.UtilityClass;

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

    }
}
