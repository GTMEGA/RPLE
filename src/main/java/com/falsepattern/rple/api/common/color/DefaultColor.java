/*
 * Copyright (c) 2023 FalsePattern, Ven
 * This work is licensed under the Creative Commons Attribution-NonCommercial-NoDerivatives 4.0 International License.
 * To view a copy of this license, visit http://creativecommons.org/licenses/by-nc-nd/4.0/
 * or send a letter to Creative Commons, PO Box 1866, Mountain View, CA 94042, USA.
 */

package com.falsepattern.rple.api.common.color;

import lombok.val;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public enum DefaultColor implements IPaletteColor {
    // @formatter:off
    WHITE          (0xfff),
    ORANGE         (0xfca),
    MAGENTA        (0xf0f),
    LIGHT_BLUE     (0x08f),
    YELLOW         (0xff0),
    LIME           (0x8f0),
    PINK           (0xfad),
    GRAY           (0x555),
    LIGHT_GRAY     (0xaaa),
    CYAN           (0x0ff),
    PURPLE         (0xa0f),
    BLUE           (0x00f),
    BROWN          (0x830),
    GREEN          (0x0f0),
    RED            (0xf00),
    BLACK          (0x111),

    DIM_WHITE      (0x888),
    DIM_ORANGE     (0x865),
    DIM_MAGENTA    (0x808),
    DIM_LIGHT_BLUE (0x048),
    DIM_YELLOW     (0x880),
    DIM_LIME       (0x480),
    DIM_PINK       (0x856),
    DIM_GRAY       (0x333),
    DIM_LIGHT_GRAY (0x555),
    DIM_CYAN       (0x088),
    DIM_PURPLE     (0x508),
    DIM_BLUE       (0x008),
    DIM_BROWN      (0x420),
    DIM_GREEN      (0x080),
    DIM_RED        (0x800),
    DIM_BLACK      (0x111),

    TORCH_LIGHT    (0xda8),
    // @formatter:on
    ;
    private static final DefaultColor[] VALUES = values();

    public static final String DEFAULT_COLOR_DOMAIN = "default";

    private final short rgb16;

    private final String colorName;

    DefaultColor(int rgb16) {
        this.rgb16 = (short) rgb16;
        this.colorName = name().toLowerCase();
    }

    public static DefaultColor fromVanillaBlockMeta(int blockMeta) {
        blockMeta &= 15;
        return VALUES[blockMeta];
    }

    public static @Nullable DefaultColor attemptMapToEnum(short rgb16) {
        for (val otherColor: VALUES) {
            if (otherColor.rgb16 == rgb16)
                return otherColor;
        }
        return null;
    }

    @Override
    public short rgb16() {
        return rgb16;
    }

    @Override
    public String colorName() {
        return colorName;
    }

    @Override
    public String colorDomain() {
        return DEFAULT_COLOR_DOMAIN;
    }
}
