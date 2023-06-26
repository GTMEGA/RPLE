/*
 * Copyright (c) 2023 FalsePattern, Ven
 * This work is licensed under the Creative Commons Attribution-NonCommercial-NoDerivatives 4.0 International License.
 * To view a copy of this license, visit http://creativecommons.org/licenses/by-nc-nd/4.0/
 * or send a letter to Creative Commons, PO Box 1866, Mountain View, CA 94042, USA.
 */

package com.falsepattern.rple.internal.common.block;

import com.falsepattern.rple.api.block.RPLEBlockColorizer;
import com.falsepattern.rple.api.color.RPLEColor;
import com.falsepattern.rple.api.color.RPLENamedColor;
import com.falsepattern.rple.internal.config.container.BlockReference;
import com.falsepattern.rple.internal.config.container.ColorReference;
import com.falsepattern.rple.internal.config.container.HexColor;
import lombok.Getter;
import lombok.experimental.Accessors;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;
import java.util.function.Consumer;

@Accessors(fluent = true, chain = false)
public final class BlockColorizer implements RPLEBlockColorizer {
    @Getter
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

    public Optional<ColorReference> brightness() {
        return Optional.ofNullable(brightness);
    }

    public Optional<ColorReference> translucency() {
        return Optional.ofNullable(translucency);
    }

    public Optional<RPLENamedColor> paletteBrightness() {
        return Optional.ofNullable(paletteBrightness);
    }

    public Optional<RPLENamedColor> paletteTranslucency() {
        return Optional.ofNullable(paletteTranslucency);
    }

    @Override
    public RPLEBlockColorizer brightness(int color) {
        resetBrightness();
        this.brightness = new ColorReference(new HexColor(color));
        return this;
    }

    @Override
    public RPLEBlockColorizer brightness(RPLEColor color) {
        resetBrightness();
        if (color != null)
            this.brightness = new ColorReference(new HexColor(color));
        return this;
    }

    @Override
    public RPLEBlockColorizer brightness(RPLENamedColor color) {
        resetBrightness();
        if (color != null) {
            this.brightness = new ColorReference(new HexColor(color));
            this.paletteBrightness = color;
        }
        return this;
    }

    @Override
    public RPLEBlockColorizer translucency(int color) {
        resetTranslucency();
        this.translucency = new ColorReference(new HexColor(color));
        return this;
    }

    @Override
    public RPLEBlockColorizer translucency(RPLEColor color) {
        resetTranslucency();
        if (color != null)
            this.translucency = new ColorReference(new HexColor(color));
        return this;
    }

    @Override
    public RPLEBlockColorizer translucency(RPLENamedColor color) {
        resetTranslucency();
        if (color != null) {
            this.translucency = new ColorReference(new HexColor(color));
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
}
