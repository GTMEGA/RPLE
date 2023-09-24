/*
 * Copyright (c) 2023 FalsePattern, Ven
 * This work is licensed under the Creative Commons Attribution-NonCommercial-NoDerivatives 4.0 International License.
 * To view a copy of this license, visit http://creativecommons.org/licenses/by-nc-nd/4.0/
 * or send a letter to Creative Commons, PO Box 1866, Mountain View, CA 94042, USA.
 */

package com.falsepattern.rple.api.common.color;

import org.jetbrains.annotations.NotNull;

public enum DefaultColor implements RPLENamedColor {
    // @formatter:off
    WHITE          (15, 15, 15),
    ORANGE         (15, 12, 10),
    MAGENTA        (15,  0, 15),
    LIGHT_BLUE     ( 0,  8, 15),
    YELLOW         (15, 15,  0),
    LIME           ( 8, 15,  0),
    PINK           (15, 10, 13),
    GRAY           ( 5,  5,  5),
    LIGHT_GRAY     (10, 10, 10),
    CYAN           ( 0, 15, 15),
    PURPLE         (10,  0, 15),
    BLUE           ( 0,  0, 15),
    BROWN          ( 8,  3,  0),
    GREEN          ( 0, 15,  0),
    RED            (15,  0,  0),
    BLACK          ( 1,  1,  1),

    DIM_WHITE      ( 8,  8,  8),
    DIM_ORANGE     ( 8,  6,  5),
    DIM_MAGENTA    ( 8,  0,  8),
    DIM_LIGHT_BLUE ( 0,  4,  8),
    DIM_YELLOW     ( 8,  8,  0),
    DIM_LIME       ( 4,  8,  0),
    DIM_PINK       ( 8,  5,  6),
    DIM_GRAY       ( 3,  3,  3),
    DIM_LIGHT_GRAY ( 5,  5,  5),
    DIM_CYAN       ( 0,  8,  8),
    DIM_PURPLE     ( 5,  0,  8),
    DIM_BLUE       ( 0,  0,  8),
    DIM_BROWN      ( 4,  2,  0),
    DIM_GREEN      ( 0,  8,  0),
    DIM_RED        ( 8,  0,  0),
    DIM_BLACK      ( 1,  1,  1),

    TORCH_LIGHT    (13, 10,  8),
    // @formatter:on
    ;
    private static final DefaultColor[] VALUES = values();

    public static final String DEFAULT_COLOR_DOMAIN = "default";

    private final int red;
    private final int green;
    private final int blue;

    private final String colorName;

    DefaultColor(int red, int green, int blue) {
        this.red = red;
        this.green = green;
        this.blue = blue;

        this.colorName = name().toLowerCase();
    }

    public static DefaultColor fromVanillaBlockMeta(int blockMeta) {
        blockMeta &= 15;
        return VALUES[blockMeta];
    }

    public @NotNull String colorName() {
        return colorName;
    }

    @Override
    public @NotNull String colorDomain() {
        return DEFAULT_COLOR_DOMAIN;
    }

    public int red() {
        return red;
    }

    public int green() {
        return green;
    }

    public int blue() {
        return blue;
    }
}
