/*
 * Copyright (c) 2023 FalsePattern, Ven
 * This work is licensed under the Creative Commons Attribution-NonCommercial-NoDerivatives 4.0 International License.
 * To view a copy of this license, visit http://creativecommons.org/licenses/by-nc-nd/4.0/
 * or send a letter to Creative Commons, PO Box 1866, Mountain View, CA 94042, USA.
 */

package com.falsepattern.rple.api.common.color;

public enum DefaultColor implements RPLEBlockColor {
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

    public static DefaultColor fromVanillaBlockMeta(int blockMeta) {
        blockMeta &= 15;
        return VALUES[blockMeta];
    }

    public static DefaultColor dimFromVanillaBlockMeta(int blockMeta) {
        blockMeta &= 15;
        return VALUES[blockMeta + 16];
    }
}
