/*
 * Copyright (c) 2023 FalsePattern, Ven
 * This work is licensed under the Creative Commons Attribution-NonCommercial-NoDerivatives 4.0 International License.
 * To view a copy of this license, visit http://creativecommons.org/licenses/by-nc-nd/4.0/
 * or send a letter to Creative Commons, PO Box 1866, Mountain View, CA 94042, USA.
 */

package com.falsepattern.rple.internal.common.config.container;

import com.falsepattern.rple.api.common.color.RPLENamedColor;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.experimental.Accessors;
import lombok.val;
import lombok.var;
import org.jetbrains.annotations.Unmodifiable;
import org.jetbrains.annotations.UnmodifiableView;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;

@Accessors(fluent = true, chain = false)
@AllArgsConstructor
public final class BlockColorConfig {
    @Getter
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

    public Optional<ColorReference> setBlockBrightness(BlockReference block, ColorReference color) {
        return Optional.ofNullable(brightness.put(block, color));
    }

    public Optional<ColorReference> setBlockTranslucency(BlockReference block, ColorReference color) {
        return Optional.ofNullable(translucency.put(block, color));
    }

    @Override
    public int hashCode() {
        var hashCode = palette.hashCode();
        hashCode = (31 * hashCode) + brightness.hashCode();
        hashCode = (31 * hashCode) + translucency.hashCode();
        return hashCode;
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof BlockColorConfig))
            return false;

        val other = (BlockColorConfig) obj;
        return palette.equals(other.palette) &&
               brightness.equals(other.brightness) &&
               translucency.equals(other.translucency);
    }
}
