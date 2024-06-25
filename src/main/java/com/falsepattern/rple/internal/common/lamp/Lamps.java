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

package com.falsepattern.rple.internal.common.lamp;

import com.falsepattern.rple.internal.Tags;
import lombok.val;

import net.minecraft.block.Block;

import cpw.mods.fml.common.registry.GameRegistry;

public enum Lamps {
    // @formatter:off
    BLACK        ( 3,  0,  3, "dyeBlack"),
    DARK_GRAY    ( 5,  5,  5, "dyeBlack", "dyeGray"),
    GRAY         ( 8,  8,  8, "dyeGray"),
    LIGHT_GRAY   (11, 11, 11, "dyeLightGray"),
    WHITE        (15, 15, 15, "dyeWhite"),
    DARK_RED     (11,  0,  0, "dyeBlack", "dyeRed"),
    RED          (15,  0,  0, "dyeRed"),
    MAGENTA      (14,  0, 14, "dyeMagenta"),
    BLUE_PURPLE  ( 5,  0, 10, "dyeBlue", "dyePurple"),
    BLUE_GRAY    ( 7,  7, 11, "dyeBlue", "dyeGray"),
    DARK_BLUE    ( 0,  0, 11, "dyeBlue", "dyeBlue"),
    BLUE         ( 0,  0, 15, "dyeBlue"),
    LIGHT_BLUE   ( 7,  7, 15, "dyeLightBlue"),
    CYAN         ( 0, 15, 14, "dyeCyan"),
    PALE_BLUE    (12, 12, 14, "dyeLightBlue", "dyeLightGray"),
    PINK         ( 9,  4,  9, "dyePink"),
    BRIGHT_PINK  (15,  8, 15, "dyePink", "dyeWhite"),
    PURPLE       ( 8,  0, 15, "dyePurple"),
    DARK_PURPLE  ( 5,  0,  8, "dyePurple", "dyeBlack"),
    SEA_GREEN    (10, 14, 11, "dyeGreen", "dyeWhite"),
    GREEN        ( 0, 14,  0, "dyeGreen"),
    LIME         ( 2, 15,  0, "dyeLime"),
    YELLOW       (14, 14,  0, "dyeYellow"),
    DARK_YELLOW  ( 9,  9,  0, "dyeYellow", "dyeBlack"),
    BROWN        ( 8,  4,  0, "dyeBrown"),
    ORANGE       (13,  6,  0, "dyeOrange"),
    BRIGHT_ORANGE(14,  9,  1, "dyeOrange", "dyeWhite"),
    RED_ORANGE   (15,  6,  0, "dyeRed", "dyeOrange"),
    PALE_GREEN   (12, 14, 12, "dyeGreen", "dyeLightGray"),
    LIGHT_GREEN  ( 9, 14,  9, "dyeGreen", "dyeWhite"),
    YELLOW_GREEN (10, 12,  1, "dyeYellow", "dyeGreen"),
    LIGHT_PURPLE (12,  8, 15, "dyePurple", "dyeWhite"),
    // @formatter:on
    ;

    public final int r;
    public final int g;
    public final int b;
    public final String[] dyes;

    Lamps(int r, int g, int b, String... dyes) {
        this.r = r;
        this.g = g;
        this.b = b;
        this.dyes = dyes;
    }

    public Block findBlock() {
        val name = name().toLowerCase();
        return GameRegistry.findBlock(Tags.MOD_ID, "lamp." + name);
    }
}
