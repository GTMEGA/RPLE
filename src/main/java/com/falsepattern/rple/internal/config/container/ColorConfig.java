/*
 * Copyright (c) 2023 FalsePattern, Ven
 * This work is licensed under the Creative Commons Attribution-NonCommercial-NoDerivatives 4.0 International License.
 * To view a copy of this license, visit http://creativecommons.org/licenses/by-nc-nd/4.0/
 * or send a letter to Creative Commons, PO Box 1866, Mountain View, CA 94042, USA.
 */

package com.falsepattern.rple.internal.config.container;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.experimental.Accessors;
import lombok.val;
import org.jetbrains.annotations.UnmodifiableView;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;


/*
{
    palette: {
      "red": "0xF00",
      "blue": "0x00C",
      "dim_blue": "0x004",
    },
    brightness: {
        "minecraft:glowstone" : "0xF0B",
        "minecraft:lit_furnace" : "red",
        "chisel:futuraCircuit" : "dim_blue",
        "chisel:futuraCircuit:0" : "0xFFF"
    },
    translucency: {
        "minecraft:stained_glass" : "blue"
    }
}
 */
@Getter
@Accessors(fluent = true, chain = false)
@AllArgsConstructor
public final class ColorConfig {
    private final Palette palette;
    private final LinkedHashMap<BlockReference, ColorReference> brightness;
    private final LinkedHashMap<BlockReference, ColorReference> translucency;

    public ColorConfig() {
        this.palette = new Palette();
        this.brightness = new LinkedHashMap<>();
        this.translucency = new LinkedHashMap<>();
    }

    public ColorConfig setPaletteColor(String name, String colorHex) {
        palette.setColor(name, colorHex);
        return this;
    }

    public ColorConfig setBlockBrightness(String blockName, String color) {
        val blockReference = new BlockReference(blockName);
        val colorReference = new ColorReference(color);
        brightness.put(blockReference, colorReference);
        return this;
    }

    public ColorConfig setBlockTranslucency(String blockName, String color) {
        val blockReference = new BlockReference(blockName);
        val colorReference = new ColorReference(color);
        translucency.put(blockReference, colorReference);
        return this;
    }

    public @UnmodifiableView Map<BlockReference, ColorReference> brightness() {
        return Collections.unmodifiableMap(brightness);
    }

    public @UnmodifiableView Map<BlockReference, ColorReference> translucency() {
        return Collections.unmodifiableMap(translucency);
    }
}
