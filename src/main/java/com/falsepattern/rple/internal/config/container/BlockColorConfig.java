/*
 * Copyright (c) 2023 FalsePattern, Ven
 * This work is licensed under the Creative Commons Attribution-NonCommercial-NoDerivatives 4.0 International License.
 * To view a copy of this license, visit http://creativecommons.org/licenses/by-nc-nd/4.0/
 * or send a letter to Creative Commons, PO Box 1866, Mountain View, CA 94042, USA.
 */

package com.falsepattern.rple.internal.config.container;

import com.falsepattern.rple.api.color.RPLENamedColor;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.experimental.Accessors;
import lombok.val;
import org.jetbrains.annotations.Unmodifiable;
import org.jetbrains.annotations.UnmodifiableView;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

@Getter
@Accessors(fluent = true, chain = false)
@AllArgsConstructor
public final class BlockColorConfig {
    private final ColorPalette palette;
    private final LinkedHashMap<BlockReference, ColorReference> brightness;
    private final LinkedHashMap<BlockReference, ColorReference> translucency;

    public BlockColorConfig() {
        this.palette = new ColorPalette();
        this.brightness = new LinkedHashMap<>();
        this.translucency = new LinkedHashMap<>();
    }

    public @Unmodifiable Map<BlockReference, ColorReference> brightness() {
        return Collections.unmodifiableMap(brightness);
    }

    public @UnmodifiableView Map<BlockReference, ColorReference> translucency() {
        return Collections.unmodifiableMap(translucency);
    }

    public void addPaletteColor(RPLENamedColor color) {
        palette.addPaletteColor(color);
    }

    public void setBlockBrightness(BlockReference block, ColorReference color) {
        if (brightness.containsKey(block))
            return;

        if (block.isValid() && color.isValid())
            brightness.put(block, color);
    }

    public void setBlockTranslucency(BlockReference block, ColorReference color) {
        if (translucency.containsKey(block))
            return;

        if (block.isValid() && color.isValid())
            translucency.put(block, color);
    }

    @Deprecated
    public BlockColorConfig setPaletteColor(String name, String colorHex) {
        palette.addColor(name, colorHex);
        return this;
    }

    @Deprecated
    public BlockColorConfig setBlockBrightness(String blockName, String color) {
        val blockReference = new BlockReference(blockName);
        val colorReference = new ColorReference(color);
        brightness.put(blockReference, colorReference);
        return this;
    }

    @Deprecated
    public BlockColorConfig setBlockTranslucency(String blockName, String color) {
        val blockReference = new BlockReference(blockName);
        val colorReference = new ColorReference(color);
        translucency.put(blockReference, colorReference);
        return this;
    }

    public void reset() {
        palette.reset();
        brightness.clear();
        translucency.clear();
    }
}
