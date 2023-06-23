/*
 * Copyright (c) 2023 FalsePattern, Ven
 * This work is licensed under the Creative Commons Attribution-NonCommercial-NoDerivatives 4.0 International License.
 * To view a copy of this license, visit http://creativecommons.org/licenses/by-nc-nd/4.0/
 * or send a letter to Creative Commons, PO Box 1866, Mountain View, CA 94042, USA.
 */

package com.falsepattern.rple.internal.config.container;

import com.google.gson.annotations.SerializedName;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.experimental.Accessors;
import org.jetbrains.annotations.UnmodifiableView;

import java.util.Collections;
import java.util.HashMap;
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
    @SerializedName("brightness")
    private final Map<BlockReference, ColorReference> lightBlocks;
    @SerializedName("translucency")
    private final Map<BlockReference, ColorReference> translucentBlocks;

    public ColorConfig() {
        this.palette = new Palette();
        this.lightBlocks = new HashMap<>();
        this.translucentBlocks = new HashMap<>();
    }

    public @UnmodifiableView Map<BlockReference, ColorReference> lightBlocks() {
        return Collections.unmodifiableMap(lightBlocks);
    }

    public @UnmodifiableView Map<BlockReference, ColorReference> translucentBlocks() {
        return Collections.unmodifiableMap(translucentBlocks);
    }
}
