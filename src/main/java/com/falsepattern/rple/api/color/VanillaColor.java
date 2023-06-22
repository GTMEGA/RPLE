/*
 * Copyright (c) 2023 FalsePattern, Ven
 * This work is licensed under the Creative Commons Attribution-NonCommercial-NoDerivatives 4.0 International License.
 * To view a copy of this license, visit http://creativecommons.org/licenses/by-nc-nd/4.0/
 * or send a letter to Creative Commons, PO Box 1866, Mountain View, CA 94042, USA.
 */

package com.falsepattern.rple.api.color;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.experimental.Accessors;

@Getter
@Accessors(fluent = true, chain = false)
@AllArgsConstructor
public enum VanillaColor implements RPLEColour {
    // @formatter:off
    WHITE_COLOR      (15, 15, 15),
    ORANGE_COLOR     (15, 12, 10),
    MAGENTA_COLOR    (15,  0, 15),
    LIGHT_BLUE_COLOR ( 0,  8, 15),
    YELLOW_COLOR     (15, 15,  0),
    LIME_COLOR       ( 8, 15,  0),
    PINK_COLOR       (15, 10, 13),
    GRAY_COLOR       ( 5,  5,  5),
    LIGHT_GRAY_COLOR (10, 10, 10),
    CYAN_COLOR       ( 0, 15, 15),
    PURPLE_COLOR     (10,  0, 15),
    BLUE_COLOR       ( 0,  0, 15),
    BROWN_COLOR      ( 8,  3,  0),
    GREEN_COLOR      ( 0, 15,  0),
    RED_COLOR        (15,  0,  0),
    BLACK_COLOR      ( 0,  0,  0),
    // @formatter:on
    ;

    private final int red;
    private final int green;
    private final int blue;
}
