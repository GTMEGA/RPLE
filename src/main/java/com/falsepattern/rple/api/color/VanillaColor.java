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
    WHITE      (15, 15, 15),
    ORANGE     (15, 12, 10),
    MAGENTA    (15,  0, 15),
    LIGHT_BLUE ( 0,  8, 15),
    YELLOW     (15, 15,  0),
    LIME       ( 8, 15,  0),
    PINK       (15, 10, 13),
    GRAY       ( 5,  5,  5),
    LIGHT_GRAY (10, 10, 10),
    CYAN       ( 0, 15, 15),
    PURPLE     (10,  0, 15),
    BLUE       ( 0,  0, 15),
    BROWN      ( 8,  3,  0),
    GREEN      ( 0, 15,  0),
    RED        (15,  0,  0),
    BLACK      ( 0,  0,  0),
    // @formatter:on
    ;

    private final int red;
    private final int green;
    private final int blue;
}
