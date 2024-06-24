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

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public enum Lamps {
    // @formatter:off
    BLACK        ( 3,  0,  3),
    DARK_GRAY    ( 5,  5,  5),
    GRAY         ( 8,  8,  8),
    LIGHT_GRAY   (11, 11, 11),
    WHITE        (15, 15, 15),
    DARK_RED     (11,  0,  0),
    RED          (15,  0,  0),
    MAGENTA      (14,  0, 14),
    BLUE_PURPLE  ( 5,  0, 10),
    BLUE_GRAY    ( 7,  7, 11),
    DARK_BLUE    ( 0,  0, 11),
    BLUE         ( 0,  0, 15),
    LIGHT_BLUE   ( 7,  7, 15),
    CYAN         ( 0, 15, 14),
    PALE_BLUE    (12, 12, 14),
    PINK         ( 9,  4,  9),
    BRIGHT_PINK  (15,  8, 15),
    PURPLE       ( 8,  0, 15),
    DARK_PURPLE  ( 5,  0,  8),
    SEA_GREEN    (10, 14, 11),
    GREEN        ( 0, 14,  0),
    LIME         ( 2, 15,  0),
    YELLOW       (14, 14,  0),
    DARK_YELLOW  ( 9,  9,  0),
    BROWN        ( 8,  4,  0),
    ORANGE       (13,  6,  0),
    BRIGHT_ORANGE(14,  9,  1),
    RED_ORANGE   (15,  6,  0),
    PALE_GREEN   (12, 14, 12),
    LIGHT_GREEN  ( 9, 14,  9),
    YELLOW_GREEN (10, 12,  1),
    LIGHT_PURPLE (12,  8, 15),
    // @formatter:on
    ;

    public final int r;
    public final int g;
    public final int b;
}
