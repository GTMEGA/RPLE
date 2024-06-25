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
