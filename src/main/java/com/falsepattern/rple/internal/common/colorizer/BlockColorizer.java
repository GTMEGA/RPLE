/*
 * Right Proper Lighting Engine
 *
 * Copyright (C) 2023-2024 FalsePattern, Ven
 * All Rights Reserved
 *
 * The above copyright notice and this permission notice
 * shall be included in all copies or substantial portions of the Software.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program. If not, see <https://www.gnu.org/licenses/>.
 *
 * This program comes with additional permissions according to Section 7 of the
 * GNU Affero General Public License. See the full LICENSE file for details.
 */

package com.falsepattern.rple.internal.common.colorizer;

import com.falsepattern.rple.api.common.color.RPLENamedColor;
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

    private @Nullable RPLENamedColor paletteBrightness;
    private @Nullable RPLENamedColor paletteTranslucency;

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

    public Optional<RPLENamedColor> paletteBrightness() {
        return Optional.ofNullable(paletteBrightness);
    }

    public Optional<RPLENamedColor> paletteTranslucency() {
        return Optional.ofNullable(paletteTranslucency);
    }

    @Override
    public @NotNull RPLEBlockColorizer brightness(short color) {
        resetBrightness();
        this.brightness = color;
        return this;
    }

    @Override
    public @NotNull RPLEBlockColorizer brightness(@NotNull RPLENamedColor color) {
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
    public @NotNull RPLEBlockColorizer translucency(@NotNull RPLENamedColor color) {
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
