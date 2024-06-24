/*
 * Copyright (c) 2023 FalsePattern, Ven
 * This work is licensed under the Creative Commons Attribution-NonCommercial-NoDerivatives 4.0 International License.
 * To view a copy of this license, visit http://creativecommons.org/licenses/by-nc-nd/4.0/
 * or send a letter to Creative Commons, PO Box 1866, Mountain View, CA 94042, USA.
 */

package com.falsepattern.rple.internal.common.colorizer;

import com.falsepattern.rple.api.common.color.RPLEBlockColor;
import com.falsepattern.rple.api.common.colorizer.RPLEBlockColorizer;
import com.falsepattern.rple.internal.common.config.container.BlockReference;
import com.falsepattern.rple.internal.common.config.container.ColorReference;
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

    private short brightness;
    private short translucency;

    private @Nullable RPLEBlockColor paletteBrightness;
    private @Nullable RPLEBlockColor paletteTranslucency;

    private boolean hasApplied = false;

    public BlockColorizer(BlockReference block, Consumer<BlockColorizer> applyCallback) {
        this.block = block;
        this.applyCallback = applyCallback;

        this.brightness = -1;
        this.translucency = -1;

        this.paletteBrightness = null;
        this.paletteTranslucency = null;

        this.hasApplied = false;
    }

    public Optional<BlockColorReference> brightness() {
        if (brightness == -1)
            return Optional.empty();
        return Optional.of(wrappedBlockColor(block, ColorReference.paletteOrRaw(paletteBrightness, brightness)));
    }

    public Optional<BlockColorReference> translucency() {
        if (translucency == -1)
            return Optional.empty();
        return Optional.of(wrappedBlockColor(block, ColorReference.paletteOrRaw(paletteTranslucency, translucency)));
    }

    public Optional<RPLEBlockColor> paletteBrightness() {
        return Optional.ofNullable(paletteBrightness);
    }

    public Optional<RPLEBlockColor> paletteTranslucency() {
        return Optional.ofNullable(paletteTranslucency);
    }

    @Override
    public @NotNull RPLEBlockColorizer brightness(short color) {
        resetBrightness();
        this.brightness = color;
        return this;
    }

    @Override
    public @NotNull RPLEBlockColorizer brightness(@NotNull RPLEBlockColor color) {
        resetBrightness();
        this.brightness = color.rgb16();
        this.paletteBrightness = color;
        return this;
    }

    @Override
    public @NotNull RPLEBlockColorizer translucency(short color) {
        resetTranslucency();
        this.translucency = color;
        return this;
    }

    @Override
    public @NotNull RPLEBlockColorizer translucency(@NotNull RPLEBlockColor color) {
        resetTranslucency();
        this.translucency = color.rgb16();
        this.paletteTranslucency = color;
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
        this.brightness = -1;
        this.paletteBrightness = null;
    }

    private void resetTranslucency() {
        this.translucency = -1;
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
