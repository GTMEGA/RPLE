/*
 * Copyright (c) 2023 FalsePattern, Ven
 * This work is licensed under the Creative Commons Attribution-NonCommercial-NoDerivatives 4.0 International License.
 * To view a copy of this license, visit http://creativecommons.org/licenses/by-nc-nd/4.0/
 * or send a letter to Creative Commons, PO Box 1866, Mountain View, CA 94042, USA.
 */

package com.falsepattern.rple.internal.common.colorizer;

import com.falsepattern.rple.api.common.color.RPLEColor;
import com.falsepattern.rple.api.common.color.RPLENamedColor;
import com.falsepattern.rple.api.common.colorizer.RPLEBlockColorizer;
import com.falsepattern.rple.internal.common.config.container.BlockReference;
import com.falsepattern.rple.internal.common.config.container.ColorReference;
import com.falsepattern.rple.internal.common.config.container.HexColor;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.experimental.Accessors;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;
import java.util.function.Consumer;

import static lombok.AccessLevel.PRIVATE;

@Accessors(fluent = true, chain = false)
public final class BlockColorizer implements RPLEBlockColorizer {
    private final BlockReference block;
    private final Consumer<BlockColorizer> applyCallback;

    private @Nullable ColorReference brightness;
    private @Nullable ColorReference translucency;

    private @Nullable RPLENamedColor paletteBrightness;
    private @Nullable RPLENamedColor paletteTranslucency;

    private boolean hasApplied = false;

    public BlockColorizer(BlockReference block, Consumer<BlockColorizer> applyCallback) {
        this.block = block;
        this.applyCallback = applyCallback;

        this.brightness = null;
        this.translucency = null;

        this.paletteBrightness = null;
        this.paletteTranslucency = null;

        this.hasApplied = false;
    }

    public Optional<BlockColorReference> brightness() {
        if (brightness == null)
            return Optional.empty();
        return Optional.of(wrappedBlockColor(block, brightness));
    }

    public Optional<BlockColorReference> translucency() {
        if (translucency == null)
            return Optional.empty();
        return Optional.of(wrappedBlockColor(block, translucency));
    }

    public Optional<RPLENamedColor> paletteBrightness() {
        return Optional.ofNullable(paletteBrightness);
    }

    public Optional<RPLENamedColor> paletteTranslucency() {
        return Optional.ofNullable(paletteTranslucency);
    }

    @Override
    public @NotNull RPLEBlockColorizer brightness(int color) {
        resetBrightness();
        this.brightness = new ColorReference(new HexColor(color));
        return this;
    }

    @Override
    public @NotNull RPLEBlockColorizer brightness(@NotNull RPLEColor color) {
        resetBrightness();
        if (color != null)
            this.brightness = new ColorReference(new HexColor(color));
        return this;
    }

    @Override
    public @NotNull RPLEBlockColorizer brightness(@NotNull RPLENamedColor color) {
        resetBrightness();
        if (color != null) {
            this.brightness = new ColorReference(color);
            this.paletteBrightness = color;
        }
        return this;
    }

    @Override
    public @NotNull RPLEBlockColorizer translucency(int color) {
        resetTranslucency();
        this.translucency = new ColorReference(new HexColor(color));
        return this;
    }

    @Override
    public @NotNull RPLEBlockColorizer translucency(@NotNull RPLEColor color) {
        resetTranslucency();
        if (color != null)
            this.translucency = new ColorReference(new HexColor(color));
        return this;
    }

    @Override
    public @NotNull RPLEBlockColorizer translucency(@NotNull RPLENamedColor color) {
        resetTranslucency();
        if (color != null) {
            this.translucency = new ColorReference(color);
            this.paletteTranslucency = color;
        }
        return this;
    }

    @Override
    public void apply() {
        if (hasApplied)
            return;

        applyCallback.accept(this);
        hasApplied = true;
    }

    private void resetBrightness() {
        this.brightness = null;
        this.paletteBrightness = null;
    }

    private void resetTranslucency() {
        this.translucency = null;
        this.paletteTranslucency = null;
    }

    @Getter
    @Accessors(fluent = true, chain = false)
    @AllArgsConstructor(access = PRIVATE)
    public static final class BlockColorReference {
        private final BlockReference block;
        private final ColorReference color;

        public boolean isValid() {
            return block.isValid() && color.isValid();
        }
    }

    private static BlockColorReference wrappedBlockColor(BlockReference block, ColorReference color) {
        return new BlockColorReference(block, color);
    }
}
