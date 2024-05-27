/*
 * Copyright (c) 2023 FalsePattern, Ven
 * This work is licensed under the Creative Commons Attribution-NonCommercial-NoDerivatives 4.0 International License.
 * To view a copy of this license, visit http://creativecommons.org/licenses/by-nc-nd/4.0/
 * or send a letter to Creative Commons, PO Box 1866, Mountain View, CA 94042, USA.
 *
 */

package com.falsepattern.rple.internal.client.render;

public class RGBHelper {
    public static final int RGB_RED_OFFSET_SKY = 12;
    public static final int RGB_GREEN_OFFSET_SKY = 16;
    public static final int RGB_BLUE_OFFSET_SKY = 20;
    public static final int RGB_RED_OFFSET_BLOCK = 0;
    public static final int RGB_GREEN_OFFSET_BLOCK = 4;
    public static final int RGB_BLUE_OFFSET_BLOCK = 8;

    private static final int SHIFT_MAX = 20;

    public static final int RGB_MAX_SKYLIGHT_NO_BLOCKLIGHT = (0xF << RGB_RED_OFFSET_SKY) |
                                                             (0xF << RGB_GREEN_OFFSET_SKY) |
                                                             (0xF << RGB_BLUE_OFFSET_SKY);

    public static final int RGB_NO_SKYLIGHT_NO_BLOCKLIGHT = 0x000_000;

    public static int createRGB(int skyR, int skyG, int skyB, int blockR, int blockG, int blockB) {
        skyR = (skyR & 0xF) << RGB_RED_OFFSET_SKY;
        skyG = (skyG & 0xF) << RGB_GREEN_OFFSET_SKY;
        skyB = (skyB & 0xF) << RGB_BLUE_OFFSET_SKY;
        blockR = (blockR & 0xF) << RGB_RED_OFFSET_BLOCK;
        blockG = (blockG & 0xF) << RGB_GREEN_OFFSET_BLOCK;
        blockB = (blockB & 0xF) << RGB_BLUE_OFFSET_BLOCK;

        return skyR | skyG | skyB | blockR | blockG | blockB;
    }

    public static int createRGBBlock(int blockR, int blockG, int blockB) {
        blockR = (blockR & 0xF) << RGB_RED_OFFSET_BLOCK;
        blockG = (blockG & 0xF) << RGB_GREEN_OFFSET_BLOCK;
        blockB = (blockB & 0xF) << RGB_BLUE_OFFSET_BLOCK;

        return blockR | blockG | blockB;
    }

    private static final int BLOCK_REMOVE_BITMASK = ~((0xF << RGB_RED_OFFSET_BLOCK) | (0xF << RGB_GREEN_OFFSET_BLOCK) | (0xF << RGB_BLUE_OFFSET_BLOCK));

    public static int weaveMinBlockLightLevels(int packed, int minRedBlockLight, int minGreenBlockLight, int minBlueBlockLight) {
        //extract
        int containedRedBlockLight = (packed >>> RGB_RED_OFFSET_BLOCK) & 0xF;
        int containedGreenBlockLight = (packed >>> RGB_GREEN_OFFSET_BLOCK) & 0xF;
        int containedBlueBlockLight = (packed >>> RGB_BLUE_OFFSET_BLOCK) & 0xF;

        //align
        minRedBlockLight &= 0xF;
        minGreenBlockLight &= 0xF;
        minBlueBlockLight &= 0xF;

        //max
        int redBlockLight = Math.max(minRedBlockLight, containedRedBlockLight);
        int greenBlockLight = Math.max(minGreenBlockLight, containedGreenBlockLight);
        int blueBlockLight = Math.max(minBlueBlockLight, containedBlueBlockLight);

        //mask out bits
        packed &= BLOCK_REMOVE_BITMASK;
        //weave
        packed |= redBlockLight << RGB_RED_OFFSET_BLOCK;
        packed |= greenBlockLight << RGB_GREEN_OFFSET_BLOCK;
        packed |= blueBlockLight << RGB_BLUE_OFFSET_BLOCK;

        return packed;
    }


    public static int rgbMax(int lightValueA, int lightValueB, int lightValueC, int lightValueD, int lightValueE) {
        int result = 0;
        for (int shift = 0; shift <= SHIFT_MAX; shift += 4) {
            int a = (lightValueA >>> shift) & 0xF;
            int b = (lightValueB >>> shift) & 0xF;
            int c = (lightValueC >>> shift) & 0xF;
            int d = (lightValueD >>> shift) & 0xF;
            int e = (lightValueE >>> shift) & 0xF;

            int max = 0;
            //noinspection DataFlowIssue
            max = Math.max(max, a);
            max = Math.max(max, b);
            max = Math.max(max, c);
            max = Math.max(max, d);
            max = Math.max(max, e);

            result |= max << shift;
        }
        return result;
    }
}
