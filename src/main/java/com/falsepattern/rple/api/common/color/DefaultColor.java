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

package com.falsepattern.rple.api.common.color;

import com.falsepattern.lib.StableAPI;
import org.jetbrains.annotations.ApiStatus;

@StableAPI(since = "1.0.0")
public enum DefaultColor implements RPLENamedColor {
    // @formatter:off
    WHITE         (0xFFF),
    ORANGE        (0xFCA),
    MAGENTA       (0xF0F),
    LIGHT_BLUE    (0x08F),
    YELLOW        (0xFF0),
    LIME          (0x8F0),
    PINK          (0xFAD),
    GRAY          (0x555),
    LIGHT_GRAY    (0xAAA),
    CYAN          (0x0FF),
    PURPLE        (0xA0F),
    BLUE          (0x00F),
    BROWN         (0x830),
    GREEN         (0x0F0),
    RED           (0xF00),
    BLACK         (0x111),

    DIM_WHITE     (0x888),
    DIM_ORANGE    (0x865),
    DIM_MAGENTA   (0x808),
    DIM_LIGHT_BLUE(0x048),
    DIM_YELLOW    (0x880),
    DIM_LIME      (0x480),
    DIM_PINK      (0x856),
    DIM_GRAY      (0x333),
    DIM_LIGHT_GRAY(0x555),
    DIM_CYAN      (0x088),
    DIM_PURPLE    (0x508),
    DIM_BLUE      (0x008),
    DIM_BROWN     (0x420),
    DIM_GREEN     (0x080),
    DIM_RED       (0x800),
    DIM_BLACK     (0x111),

    TORCH_LIGHT   (0xDA8),

    ERROR         (0xF00),
    // @formatter:on
    ;
    private static final DefaultColor[] VALUES = values();

    @ApiStatus.Internal
    public static final String DEFAULT_COLOR_DOMAIN = "default";

    private final short rgb16;

    private final String paletteColorName;

    DefaultColor(int rgb16) {
        this.rgb16 = (short) rgb16;
        this.paletteColorName = DEFAULT_COLOR_DOMAIN + ":" + name().toLowerCase();
    }

    @Override
    public String paletteColorName() {
        return paletteColorName;
    }

    @Override
    public short rgb16() {
        return rgb16;
    }

    @StableAPI.Expose
    public static DefaultColor fromVanillaBlockMeta(int blockMeta) {
        blockMeta &= 15;
        return VALUES[blockMeta];
    }

    @StableAPI.Expose
    public static DefaultColor dimFromVanillaBlockMeta(int blockMeta) {
        blockMeta &= 15;
        return VALUES[blockMeta + 16];
    }
}
